# Hibernate + MongoDB Dialect CRUD Example

A simple project designed to explore CRUD operations using Hibernate with MongoDB.
The project uses MongoDB Hibernate 1.0.0-alpha1, which adds support for associations, joins, embedded objects, cascading operations, optimistic locking, and additional query capabilities.

You can read more on:
- [`Getting Started With Hibernate ORM and MongoDB`](https://foojay.io/today/getting-started-with-hibernate-orm-and-mongodb/)

- [`Modeling Relationships With Hibernate ORM and MongoDB`](https://foojay.io/today/modeling-relationships-with-hibernate-orm-and-mongodb/)

## Prerequisites

Before running the project, make sure you have:
- Java 21 or newer
- Apache Maven 3.8+
- MongoDB configured as a replica set, which is required for transaction support

## Running the Project

1. Edit the MongoDB connection URL
   
Open the file src/main/resources/hibernate.cfg.xml
and replace the value of the jakarta.persistence.jdbc.url property with your own MongoDB URI:

```
<property name="jakarta.persistence.jdbc.url">
    mongodb+srv://<username>:<password>@<cluster-url>/mydb?appName=devrel-mongodb-hibernate
</property>
```

2. Build the project with Maven:
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
1 - Manage Books
2 - Manage Orders
0 - Exit
```

