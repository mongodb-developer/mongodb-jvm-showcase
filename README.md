# Queryable-Encryption-BankAccount

You can learn more at the MongoDB Developer Center:
- [`Java Meets Queryable Encryption: Developing a Secure Bank Account Application`](https://www.mongodb.com/developer/products/atlas/java-queryable-encryption/)

The BankAccount service project focuses on exploring queryable encryption to securely manage sensitive bank account information. The application will encrypt the following fields in the BankAccount entity:

- accountNumber 
- cardVerificationCode 
- accountBalance 

The goal is to enable secure querying of encrypted data. Specifically, the project will implement encryption mechanisms that support:

- Equality Queries: Searching by accountNumber.
- Range Queries: Such as performing greater-than comparisons on accountBalance, allowing secure range-based searches.

By encrypting these fields, the service maintains the confidentiality of sensitive information while supporting advanced querying capabilities. The system will automatically handle encryption and decryption, ensuring that secure and efficient data retrieval is possible.

## Demonstration
![Demonstration](/src/main/resources/images/demonstration-postman.gif)

## Prerequisites
- MongoDB 8.0 ReplicaSet
- Java 17+ 

## Running the application
```
mvn clean install

```


```
mvn spring-boot:run

```

## Endpoints
### Create new document

```
     curl --location 'http://localhost:8080/bank' \
        --header 'Content-Type: application/json' \
        --data '{
            "accountHolderName": "Ricas",
            "accountNumber": "9872334",
            "cardVerificationCode": "192",
            "accountBalance": 2100.2
        }'
```

### Find documents 

```
     curl --location 'http://localhost:8080/bank'
```


### Find by accountNumber

```
curl --location 'http://localhost:8080/bank/accountNumber/98732909855512398731'
```

### Find by balance greater than

```
curl --location 'http://localhost:8080/bank/balance/greaterThan/10'
```

# Author
Ricardo Mello
- Ricardo on [MongoDB Developer Community](https://www.mongodb.com/community/forums/u/ricardo_silva_de_mello/summary).
- Ricardo on [GitHub](https://github.com/ricardohsmello)

