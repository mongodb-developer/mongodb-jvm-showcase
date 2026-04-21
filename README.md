# kotlin-driver-atlas-search
This repository demonstrates how to integrate MongoDB Search with the Kotlin Sync driver using a Spring Boot REST application. The goal is to create a solution for exploring and discovering properties similar to Airbnb, leveraging the advanced search capabilities of MongoDB.

You can read more on:
- [`Discover Your Ideal Airbnb: Implementing a Spring Boot & Atlas Search with Kotlin Sync Driver`](https://www.mongodb.com/developer/products/atlas/kotlin-driver-sync-with-atlas-search/)

# Demonstration
![Demonstration](./demonstration/demonstration.gif)

## Prerequisites

Before you start, make sure you have the following:

1. **MongoDB Atlas Account**
    - Get started with MongoDB Atlas for free! If you don’t already have an account, MongoDB offers a free-forever Atlas cluster. You can sign up [here](https://www.mongodb.com/cloud/atlas).

2. **Java 21+**
    - Ensure that you have Java 21 or higher installed. You can download the latest version from the [Oracle website](https://www.oracle.com/java/technologies/javase-downloads.html)

3. **Gradle 8.8+**
    - This project uses Gradle for build automation. Make sure you have Gradle version 8.8 or later. You can download Gradle from the [official website](https://gradle.org/install/).

4. **IDE of Your Choice**
    - Use any Java IDE that you prefer. Popular options include [IntelliJ IDEA](https://www.jetbrains.com/idea/), [Eclipse](https://www.eclipse.org/), and [Visual Studio Code](https://code.visualstudio.com/). Ensure that your IDE is set up to support Java 21 and integrate with MongoDB.

## Getting Started

1. **Clone the Repository**

   ```bash
   git clone https://github.com/mongodb-developer/kotlin-driver-atlas-search.git
   cd kotlin-driver-atlas-search

2. **Build the Project**

   ```bash
   ./gradlew build

3. **Run the Application**

   ```bash
   ./gradlew bootRun

4. **Access the Endpoint**

- Once the application is running, you can access the search endpoint at http://localhost:8080/airbnb/search.

  - Endpoint Parameters:

    - query: A text string to perform a full-text search with fuzzy matching on the summary field.
    - minNumberReviews: The minimum number of reviews required for each Airbnb listing to be included in the results.
  
  
   ```bash
        curl --location 'http://localhost:8080/airbnb/search?query=Istambun&minNumberReviews=50'
