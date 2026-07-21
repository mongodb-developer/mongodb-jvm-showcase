# Queryable-Encryption-BankAccount

You can learn more at the MongoDB Developer Center:
- [`Java Meets Queryable Encryption: Developing a Secure Bank Account Application`](https://dev.to/ricardohsmello/java-meets-queryable-encryption-developing-a-secure-bank-account-application-40im)

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
- Java 21+
- [MongoDB Crypt Shared library](https://www.mongodb.com/docs/manual/core/queryable-encryption/reference/shared-library/) (Enterprise/Atlas) downloaded locally

> Maven is **not** required — the project ships with the Maven Wrapper (`mvnw`).

## Configuration

The application reads two values from environment variables (see `src/main/resources/application.properties`):

| Variable | Description |
| --- | --- |
| `MONGO_URI` | MongoDB connection string |
| `CRYPT_SHARED_LIB_PATH` | Absolute path to the `mongo_crypt_v1` shared library |

Export them in your shell before running:

**macOS / Linux**
```bash
export MONGO_URI="mongodb+srv://<user>:<password>@<cluster>.mongodb.net/"
export CRYPT_SHARED_LIB_PATH="/path/to/mongo_crypt_v1.dylib"
```

**Windows (PowerShell)**
```powershell
$env:MONGO_URI = "mongodb+srv://<user>:<password>@<cluster>.mongodb.net/"
$env:CRYPT_SHARED_LIB_PATH = "C:\path\to\mongo_crypt_v1.dll"
```

## Running the application

**macOS / Linux**
```bash
./mvnw spring-boot:run
```

**Windows**
```cmd
mvnw.cmd spring-boot:run
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

