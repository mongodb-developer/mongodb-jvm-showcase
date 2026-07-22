package com.mongodb.db;

import com.mongodb.domain.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    List<Employee> findByAgeLessThan(int age);
    Optional<Employee> findBySsn(int ssn);
    List<Employee> findBySalaryGreaterThan(double salary);
}


