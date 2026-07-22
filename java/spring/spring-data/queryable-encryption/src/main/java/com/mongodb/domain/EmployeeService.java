package com.mongodb.domain;

import com.mongodb.db.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService  {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
         this.employeeRepository = employeeRepository;
    }

    public Employee createEmployee(Employee employee) {
        logger.info("Creating employee {}", employee);
         return employeeRepository.save(employee);
     }

    public List<Employee> findAll() {
       logger.info("Finding all employees ");
       return employeeRepository.findAll();
    }

    public Optional<Employee> findBySsn(int ssn) {
        logger.info("Finding employee with ssn equals {}", ssn);
        return employeeRepository.findBySsn(ssn);
    }

    public List<Employee> findByAgeLessThan(int age) {
        Assert.isTrue(age > 0, "Age must be greater than 0");
        Assert.isTrue(age < 150, "Age must be less than 150");

        logger.info("Finding all employees where age is less than {} ", age);
        return employeeRepository.findByAgeLessThan(age);
    }

    public List<Employee> findBySalaryGreaterThan(double salary) {
        Assert.isTrue(salary >= 1500, "Salary must be at least 1500");
        Assert.isTrue(salary < 100000, "Salary must be less than 100000");

        logger.info("Finding all employees where salary is greater than {}", salary);
        return employeeRepository.findBySalaryGreaterThan(salary);
    }


}


