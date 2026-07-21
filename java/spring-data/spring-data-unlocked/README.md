
# Spring Data Unlocked - Series
This repository showcases how to integrate MongoDB with Spring Data, covering essential practices for creating complex queries and optimizing performance. It includes sample code that guides you through working with Spring Data to effectively handle data operations and improve query efficiency.

You can read more on:
- [`Spring Data Unlocked: Getting Started With Java and MongoDB`](https://www.mongodb.com/developer/products/mongodb/springdata-getting-started-with-java-mongodb/)

- [`Spring Data Unlocked: Advanced Queries With MongoDB`](https://www.mongodb.com/developer/products/mongodb/springdata-advanced-queries-with-mongodb)

- [`Spring Data Unlocked: Performance Optimization Techniques With MongoDB`](https://www.mongodb.com/developer/products/mongodb/springdata-performance-optimization-with-mongodb/)

## Getting Started

### Prerequisites

- Java 17 or higher (tested with Corretto 21)
- Gradle
- MongoDB running locally or in the cloud (e.g., Atlas)

---

## Running the Application

You can run the application in two ways:

### 1. Using IntelliJ or your IDE

- Open the project.
- Run the `SpringShopApplication.java` class.

### 2. Using terminal Gradle
 
```bash
export MONGODB_URI="<YOUR_CONNECTION_STRING>" 
./gradlew bootRun

```

## Sample Data
When the application starts, it automatically loads:

- 15 Customers with realistic names and global cities

- 10 Transactions including different types (Credit, Debit), statuses (PENDING, COMPLETED, ERROR), and account details

Data is only inserted if the collections are empty to avoid duplicates.

## Swagger UI (API Documentation)
Once the application is running, access:

```bash
http://localhost:8080/swagger-ui.html
```
Or (for some versions):

```bash
http://localhost:8080/swagger-ui/index.html
```
It includes endpoints for:

- /customers → Manage customer records
- /transactions → Manage financial transactions

## Example Endpoints
Create a Customer
```bash
POST /customers
```

Search Transactions

```bash
GET /transactions/search?type=Credit&status=PENDING
```

Export Error Transactions
```bash
POST /transactions/actions/export-errors
```

## Running the API with .http Files
This project includes ready-to-use .http files to test all API endpoints easily from your IDE (such as IntelliJ IDEA, Rider, or VSCode with REST Client plugin).

### Files included:
- customers.http → Test all endpoints under /customers
- transactions.http → Test all endpoints under /transactions

### How to use:
1. Open the .http file in IntelliJ.
2. Click on the green "Run" icon next to any request.
3. You’ll see the response in a built-in HTTP client tab.

## Running Tests & Code Coverage
This project uses JUnit for testing and JaCoCo for code coverage.

### Run All Tests
To execute all unit and integration tests in the project:

```bash
./gradlew test
```

After the command runs, open the HTML report:

```bash
/build/jacocoHtml/index.html
```