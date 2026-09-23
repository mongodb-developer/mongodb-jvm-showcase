# library-quarkus

A hands-on **Quarkus + MongoDB** laboratory built around a small library domain
(books, authors, reviews).

## Project Overview

This project is a practical lab for learning how to work with MongoDB from a
Quarkus application. Using a simple library domain as context, it demonstrates:

- MongoDB CRUD operations
- Quarkus MongoDB Panache
- Repository **and** Active Record patterns (side by side)
- The MongoDB Aggregation Framework
- DTO-based API design
- REST endpoints
- MongoDB Java Driver integration for advanced aggregations

The goal is to show **when** to reach for Panache versus the raw MongoDB Java
Driver, and how to keep a clean, layered architecture while doing so.

## Features Demonstrated

- Create, read, update, and delete books
- Manage authors and reviews
- Query data with Panache (simple finds, sorting, paging)
- Execute aggregation pipelines with the MongoDB Java Driver
- Calculate the average rating per book
- Demonstrate MongoDB schema design patterns (Subset, embedding vs. referencing)
- Expose REST APIs with Quarkus, validated and DTO-based
- Centralized error handling with consistent JSON error payloads

## Project Structure

```
src/main/java/com/example
├── resource      → REST endpoints (HTTP only)
├── service       → business logic / orchestration
├── repository    → data access (Panache + aggregation pipelines)
├── model/entity  → persistence entities
├── dto/request   → inbound request models (Bean Validation)
├── dto/response  → outbound response models
├── mapper        → entity ↔ DTO conversion
├── exception     → custom exceptions + JAX-RS exception mappers
└── config        → MongoDB configuration and data seeding
```

| Package | Responsibility |
|---|---|
| `resource` | REST endpoints; HTTP concerns only |
| `service` | Business logic; throws domain exceptions, never touches HTTP/Mongo types |
| `repository` | Data access; Panache for CRUD, MongoDB driver for aggregations |
| `model/entity` | Persistence models (`Book`, `Review`, `Author`) |
| `dto` | Request/response contracts, decoupled from entities |
| `mapper` | Entity ↔ DTO translation |
| `exception` | `ResourceNotFoundException` + mappers for 404 / 400 / 500 |

> **Two persistence styles on purpose:** `Book` and `Review` use the
> **Repository pattern** (`PanacheMongoRepositoryBase`), while `Author` uses the
> **Active Record pattern** (`PanacheMongoEntityBase`). This is a deliberate
> teaching contrast — in production, pick one style per project.

## Prerequisites

- **Java 21**
- **Maven 3.9+**
- **MongoDB** — MongoDB Atlas or a local instance

> A **replica set** is recommended: the atomic `$push`/`$slice` review embedding
> and the connection-pool settings are best observed against one.
>
> For a quick local replica set you can use the companion CLI:
> ```bash
> npm install -g @ricardohsmello/mongodb-cli-lab
> mongodb-cli-lab up --topology replica-set --replicas 3 --mongodb-version 8.2 --port 28000
> ```

## Configuration

The MongoDB connection string is read from the `MONGODB_URI` environment
variable. All other settings live in `src/main/resources/application.properties`:

```properties
quarkus.mongodb.connection-string=${MONGODB_URI}
quarkus.mongodb.database=library

# Application name reported to MongoDB (visible in server logs / profiler)
mongodb.application-name=devrel-tutorial-java-quarkus-library

# ReadPreference: primary | primaryPreferred | secondary | secondaryPreferred | nearest
mongodb.read-preference=primary

# Maximum number of reviews embedded on a book document (Subset Pattern)
library.reviews.max-embedded=5

# Connection pool
mongodb.pool.max-size=100
mongodb.pool.min-size=0
mongodb.pool.max-connection-idle-time-ms=0
mongodb.pool.max-connection-life-time-ms=0
```

Set the connection string before starting the app:

```bash
export MONGODB_URI="mongodb://localhost:28000,localhost:28001,localhost:28002/?replicaSet=rs0"
```

## Running the Application

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd library-quarkus
   ```

2. **Configure MongoDB** — export your connection string (see [Configuration](#configuration)):

   ```bash
   export MONGODB_URI="<your-mongodb-connection-string>"
   ```

3. **Start the application in dev mode** (live reload):

   ```bash
   ./mvnw quarkus:dev
   ```

   > Use `mvn` instead of `./mvnw` if you don't have the Maven wrapper set up.

The app starts on `http://localhost:8080`. Dev UI: `http://localhost:8080/q/dev/`.
On first start, a seeder inserts sample books, authors, and reviews.

**Build a production artifact:**

```bash
./mvnw clean package
```

**Run the tests:**

```bash
./mvnw test
```

> Tests are `@QuarkusTest`-based and boot the application with **MongoDB Dev
> Services**, so a container runtime (Docker or Podman) must be running. No
> manual MongoDB setup or `MONGODB_URI` is needed for tests.

Ready-to-use HTTP request files are available under `src/test/http/`.

## API Endpoints

### Books

| Method | Path | Description |
|---|---|---|
| GET | `/books` | List books, paginated (`?page=&size=`) |
| GET | `/books/{id}` | Find a book by ID |
| GET | `/books/min-pages/{minPages}` | Books with more than N pages (Panache) |
| GET | `/books/year/{year}` | Books published in a given year (Panache) |
| GET | `/books/sort/year?order=asc\|desc` | Books sorted by year |
| GET | `/books/top/{limit}` | The N longest books by page count |
| GET | `/books/classify` | Classify books as short/medium/long (`$switch`), paginated |
| GET | `/books/per-author` | Count books per author (`$unwind`/`$group`), paginated |
| GET | `/books/with-reviews` | Books joined with their reviews (`$lookup`), paginated |
| POST | `/books` | Create a book |
| PUT | `/books/{id}` | Update a book |
| DELETE | `/books/{id}` | Delete a book |

### Authors

| Method | Path | Description |
|---|---|---|
| GET | `/authors` | List authors, paginated (`?page=&size=`) |
| GET | `/authors/{id}` | Find an author by ID |
| GET | `/authors/nationality/{nationality}` | Filter authors by nationality |
| POST | `/authors` | Create an author |
| PUT | `/authors/{id}` | Update an author |
| DELETE | `/authors/{id}` | Delete an author |

### Reviews

| Method | Path | Description |
|---|---|---|
| GET | `/reviews` | List reviews, paginated (`?page=&size=`) |
| GET | `/reviews/avgRating/{bookId}` | Average rating and review count for a book (`$group`) |
| POST | `/reviews` | Create a review (persists + embeds it in the book) |
| PUT | `/reviews/{id}` | Update a review |
| DELETE | `/reviews/{id}` | Delete a review |

### Pagination

All list and aggregation endpoints accept `page` (zero-based, default `0`) and
`size` (default `20`, max `100`) query parameters, validated at the API boundary.
The aggregation endpoints paginate server-side with a single `$facet` stage
(page data + total count in one round trip). Every paged endpoint returns a
`PageResponse` envelope:

```bash
curl "http://localhost:8080/books?page=0&size=5"
```

```json
{
  "content": [
    {
      "id": "6a35533f1b897121f927ad55",
      "title": "Clean Code",
      "pages": 464,
      "year": 2008,
      "authors": ["Robert C. Martin"],
      "reviews": []
    }
  ],
  "page": 0,
  "size": 5,
  "totalElements": 50,
  "totalPages": 10,
  "first": true,
  "last": false
}
```

### Error Responses

Errors are handled centrally by JAX-RS exception mappers and always return the
same `ApiError` shape — internal exceptions are never exposed to clients.

- **400** — request validation failed (Bean Validation), with per-field `details`
- **404** — resource not found
- **500** — unexpected error (logged server-side, generic message returned)

```json
{
  "timestamp": "2026-06-19T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "details": ["title: title is required", "pages: pages must be greater than zero"]
}
```

## Aggregation Examples

All aggregation pipelines are encapsulated inside the repositories, so no
MongoDB-specific type leaks into the service or REST layers. Each one is
paginated server-side with `$facet` (`$skip`/`$limit` for the page, `$count` for
the total) and returns a `PageResponse`.

| Example | Endpoint | Concepts |
|---|---|---|
| **Average rating per book** | `/reviews/avgRating/{bookId}` | `$match` + `$group` with `$avg` and `$sum` |
| **Books per author** | `/books/per-author` | `$unwind` an array, `$group`, then `$sort` |
| **Classify by page count** | `/books/classify` | `$set` + `$switch` — data transformation |
| **Books with reviews** | `/books/with-reviews` | `$lookup` — joining two collections |

## MongoDB Patterns Demonstrated

- **Subset Pattern** — when a review is created it is written in full to the
  `reviews` collection **and** a denormalized copy is embedded on the book,
  capped to the 5 most recent via `$push` + `$sort` + `$slice`. This keeps the
  common "show a book with its latest reviews" read to a single document, with no
  `$lookup`.
- **Embedding vs. Referencing** — the same data is modeled both ways: a bounded
  embedded subset on the book (fast reads) and a referenced full collection
  (complete history, joined on demand with `$lookup` via `/books/with-reviews`).
- **Computed Pattern (contrast)** — the average rating is *computed at query
  time* with an aggregation (`/reviews/avgRating/{bookId}`) rather than stored on
  the book. This illustrates the trade-off the Computed Pattern addresses: when
  reads dominate, you would precompute and persist that value instead.

## Learning Goals

After working through this lab, you should understand:

- **When to use Panache** — CRUD and simple queries/sorting/paging
- **When to use the MongoDB Java Driver** — genuine aggregation pipelines
- **Repository vs. Active Record** — the two Panache styles and their trade-offs
- **Designing MongoDB schemas** — embedding vs. referencing, and the Subset Pattern
- **Building aggregation pipelines** — `$match`, `$group`, `$unwind`, `$lookup`, `$switch`
- **Building REST APIs with Quarkus** — DTOs, Bean Validation, and centralized error handling
