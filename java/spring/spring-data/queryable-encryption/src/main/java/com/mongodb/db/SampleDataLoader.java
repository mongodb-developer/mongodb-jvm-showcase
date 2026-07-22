package com.mongodb.db;

import com.mongodb.domain.Employee;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SampleDataLoader {

    @Bean
    public CommandLineRunner loadSampleEmployees(EmployeeRepository employeeRepository) {
        return args -> {
            if (employeeRepository.count() == 0) {
                List<Employee> employees = List.of(
                        new Employee(null, "Ricardo", "001", 1, 36, 1501),
                        new Employee(null, "Maria",   "002", 2, 28, 4200),
                        new Employee(null, "Karen",   "003", 3, 42, 2800),
                        new Employee(null, "Mark",    "004", 4, 22, 2100),
                        new Employee(null, "Pedro",   "005", 5, 50, 4000),
                        new Employee(null, "Joana",   "006", 5, 50, 99000)
                    );
                employeeRepository.saveAll(employees);
                System.out.println("Sample employees inserted.");
            } else {
                System.out.println("Sample data already exists. Skipping insert.");
            }
        };
    }
}