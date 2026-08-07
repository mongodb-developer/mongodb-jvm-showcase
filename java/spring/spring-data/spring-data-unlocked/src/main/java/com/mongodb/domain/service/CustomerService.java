package com.mongodb.domain.service;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.domain.model.Customer;
import com.mongodb.domain.model.CustomersByCity;
import org.bson.Document;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.*;

@Service
public class CustomerService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final MongoTemplate mongoTemplate;

    CustomerService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public Customer insert(Customer customer) {
        return mongoTemplate.insert(customer);
    }

    public List<Customer> findAll() {
        return this.mongoTemplate.findAll(Customer.class);
    }

    public Customer findCustomerByEmail(String email) {

        return mongoTemplate.query(Customer.class)
                .matching(query(where("email").is(email)))
                .one()
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
    }

    public List<CustomersByCity> totalCustomerByCity() {

        TypedAggregation<Customer> aggregation = newAggregation(Customer.class,
                group("address.city")
                        .count().as("total"),
                Aggregation.sort(Sort.Direction.ASC, "_id"),
                project(Fields.fields("total", "_id")));

        AggregationResults<CustomersByCity> result = mongoTemplate.aggregate(aggregation, CustomersByCity.class);
        return result.getMappedResults();
    }

    public String getCustomerIndexExplanation() {
        MongoCollection<Document> collection = mongoTemplate.getCollection("customer");
        Document query = new Document("email", "ricardo.mello@mongodb.com");
        Document explanation = collection.find(query).explain();

        logger.info(explanation.toJson());
        return explanation.toJson();
    }

    public Customer updatePhoneByEmail(String email, String newPhone) {
        Query query = new Query(Criteria.where("email").is(email));
        Update update = new Update().set("phone", newPhone);

        mongoTemplate.updateFirst(query, update, Customer.class);

        // Return the updated customer (optional)
        return findCustomerByEmail(email);
    }

    public void deleteByEmail(String email) {
        mongoTemplate.remove(
                query(where("email").is(email)),
                Customer.class
        );
    }

    public int bulkCustomerSample(List<Customer> customerList) {
        if (findAll().isEmpty()) {
            BulkWriteResult result = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, Customer.class)
                    .insert(customerList)
                    .execute();


            return result.getInsertedCount();
        }

        return 0;
    }

    @Transactional
    public String deleteAndRecreateCustomerWithTransaction(String accountNumber, boolean failBeforeRecreating) {
        log.info("=== WITH @Transactional - MongoTransactionManager opened a ClientSession ===");
        deleteAndRecreate(accountNumber, failBeforeRecreating);
        return "Committed: customer " + accountNumber + " was deleted and recreated atomically.";
    }

    public String deleteAndRecreateCustomerWithoutTransaction(String accountNumber, boolean failBeforeRecreating) {
        log.info("=== WITHOUT @Transactional - every write is its own atomic unit ===");
        deleteAndRecreate(accountNumber, failBeforeRecreating);
        return "Finished: customer " + accountNumber + " was deleted and recreated, but in two independent writes.";
    }

    private void deleteAndRecreate(String accountNumber, boolean failBeforeRecreating) {
        Query byAccountNumber = query(where("accountNumber").is(accountNumber));

        Customer customer = mongoTemplate.findOne(byAccountNumber, Customer.class);
        if (customer == null) {
            throw new IllegalArgumentException("No customer found with accountNumber: " + accountNumber);
        }
        log.info("STEP 1/3 - loaded customer '{}'", customer.name());

        mongoTemplate.remove(byAccountNumber, Customer.class);
        log.info("STEP 2/3 - customer deleted");

        if (failBeforeRecreating) {
            log.info("STEP 3/3 - throwing on purpose, only a transaction can undo the delete above");
            throw new IllegalStateException("Simulated failure before recreating the customer");
        }

        mongoTemplate.insert(customer);
        log.info("STEP 3/3 - customer recreated");
    }
}
