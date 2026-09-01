# Hibernate + MongoDB Dialect CRUD Example

A simple project designed to explore **CRUD operations** using **Hibernate** with the **MongoDB Dialect**.  
The goal is to understand how Hibernate interacts with MongoDB and how traditional JPA operations map to BSON documents.

## Versions

This repository is organized into four tagged versions, each representing a stage of the learning series:

| Tag      | Description                                                                                                                                                                                                                                                                                                                                                                      |
|----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **v1.0** | Part 1 – Basic CRUD operations with the `Book` entity (insert, list, update, delete). Focuses on Hibernate setup, MongoDB connection, and simple persistence using the MongoDB Dialect.                                                                                                                                                                                          |
| **v2.0** | Part 2 - Introduces the Review model and embeds reviews directly inside the Book document. Focuses on exploring the embedded document, showing how books can store their reviews as part of the same document in MongoDB.                                                                                                                                                        |
| **v3.0** | Part 3 - Extracts the Review model into its own collection to prevent unbounded array growth inside the Book document. Introduces a new apporach, where each review stores the bookId it belongs to.                            
| **v4.0** | Part 4 – Implements the subset pattern, keeping all reviews in a separate reviews collection while storing only the three most recent reviews inside each Book document under a recentReview field. This design combines the benefits of both embedded and referenced data |

 
You can check out each version with:

```bash
# Part 1
git checkout v1.0

# Part 2
git checkout v2.0

# Part 3
git checkout v3.0

# Part 4
git checkout v4.0

```
## Prerequisites

Before running the project, make sure you have:

- Java 17 or newer
- Apache Maven 3.8+
- MongoDB 6.0+ configured as a replica set
(required by the MongoDB Hibernate Dialect for transaction support)

## Running the Project

1. Clone the repository and enter the project folder:
```
git clone https://github.com/mongodb-developer/mongodb-hibernate-crud
cd mongodb-hibernate-crud
```

2. Edit the MongoDB connection URL
   
Open the file src/main/resources/hibernate.cfg.xml
and replace the value of the jakarta.persistence.jdbc.url property with your own MongoDB URI:

```
<property name="jakarta.persistence.jdbc.url">
    mongodb+srv://<username>:<password>@<cluster-url>/mydb?appName=devrel-mongodb-hibernate
</property>
```

3. Build the project with Maven:
```
mvn clean package
```
3. Run the application:
```
mvn exec:java -Dexec.mainClass="com.mongodb.MyApplication"
```

## Interactive CLI

When you run the project, you'll see an interactive CLI menu:
```
=== BOOK MENU ===
1 - Add Book
2 - List Books
3 - Update Book Title
4 - Delete Book
5 - Find Books by Minimum Pages
6 - Add Review
7 - List Books and Reviews by Id
0 - Exit
```

