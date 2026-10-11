# Finlytics

Finlytics is a personal finance tracker REST API built with Spring Boot. Users register, log in, and record their own income and expenses, then see where their money goes through totals, category breakdowns and monthly summaries. Every user can only see and change their own data. I built it to practice layered backend design, JWT authentication, validation and clean error handling.

## Features

- User registration and login with JWT (Bearer token) authentication
- Passwords stored as BCrypt hashes, never as plain text
- Per-user data isolation: each user can only access their own transactions
- Add, view, update and delete income and expense transactions
- Input validation with clear, field-level error messages
- Global exception handling that returns consistent JSON errors
- Overall summary: total income, total expense and balance
- Spending breakdown by category (largest first)
- Monthly summary for any year and month
- Automated tests that run without MySQL or any local setup (in-memory H2)

## Tech Stack

- Java 17+
- Spring Boot 4 (Spring Web, Spring Data JPA, Spring Security, Validation)
- MySQL (application) and H2 in-memory database (tests only)
- Hibernate (JPA)
- JWT with the jjwt library
- JUnit 5, Mockito and AssertJ
- Maven
- Tested manually with Postman

## Project Structure

```
com.finlyticsltd.finlytics
├── config        # Security configuration and the JWT authentication filter
├── controller    # REST endpoints, handle HTTP requests and responses
├── service       # Business logic (summaries, ownership checks, JWT creation)
├── repository    # Database access via Spring Data JPA
├── entity        # JPA entities (User, Transaction, TransactionType)
├── dto           # Request and response objects (login, summaries)
└── exception     # Custom exceptions and global error handler
```

Request flow: **Filter (checks token) → Controller → Service → Repository → MySQL**, and the response travels back the same way.

## Getting Started

### Prerequisites

- JDK 17 or higher
- MySQL 8 or higher
- Maven (or use the included `mvnw` wrapper)

### Setup

1. Clone the repository:

```bash
   git clone https://github.com/rkpraj1536/finlytics.git
   cd finlytics
```

2. Create the database:

```sql
   CREATE DATABASE finlytics_db;
```

   Tables are created automatically by Hibernate on first run.

3. Set these environment variables:

   | Variable | Description | Default |
   |---|---|---|
   | `DB_USERNAME` | MySQL username | `root` |
   | `DB_PASSWORD` | MySQL password | none (required) |
   | `JWT_SECRET` | Secret used to sign tokens, at least 32 characters | none (required) |

   Generate a random secret with:

```bash
   openssl rand -base64 48
```

   On Windows (Command Prompt):

```bash
   set DB_PASSWORD=your_password
   set JWT_SECRET=your_long_random_secret
```

   On Linux/macOS:

```bash
   export DB_PASSWORD=your_password
   export JWT_SECRET=your_long_random_secret
```

   In Eclipse: *Run → Run Configurations → Environment tab → New*.

4. Run the application:

```bash
   ./mvnw spring-boot:run
```

   Or run `FinlyticsApplication` from your IDE. The API starts at `http://localhost:8080`.

### Run the tests

```bash
./mvnw test
```

Tests use an in-memory H2 database and a test-only secret, so they need no MySQL and no environment variables.

## Authentication

Only `/api/auth/**` is open. Every other endpoint needs a token.

1. **Register** an account:

```bash
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username": "rahul", "password": "MyPass@123"}'
```

2. **Log in** to get a token:

```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username": "rahul", "password": "MyPass@123"}'
```

   Response:

```json
   {
     "token": "eyJhbGciOiJIUzM4NCJ9...",
     "tokenType": "Bearer"
   }
```

3. **Send the token** on every other request:

```bash
   curl http://localhost:8080/api/transactions \
     -H "Authorization: Bearer <your-token>"
```

Tokens expire after 1 hour. When one expires, log in again.

Rules for new accounts: username 3 to 30 characters, password 8 to 72 characters.

## API Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | No | Create an account |
| POST | `/api/auth/login` | No | Log in and receive a token |
| POST | `/api/transactions` | Yes | Add a transaction |
| GET | `/api/transactions` | Yes | List your transactions |
| GET | `/api/transactions/{id}` | Yes | Get one of your transactions |
| PUT | `/api/transactions/{id}` | Yes | Update one of your transactions |
| DELETE | `/api/transactions/{id}` | Yes | Delete one of your transactions |
| GET | `/api/transactions/summary` | Yes | Your total income, expense and balance |
| GET | `/api/transactions/summary/by-category?type=EXPENSE` | Yes | Your totals per category (`INCOME` or `EXPENSE`) |
| GET | `/api/transactions/summary/monthly?year=2026&month=10` | Yes | Your summary for one month |

### Example: add a transaction

`POST /api/transactions`

```json
{
  "amount": 450.50,
  "type": "EXPENSE",
  "category": "Food",
  "description": "Weekly groceries",
  "transactionDate": "2026-10-07"
}
```

Response: `201 Created` with the saved transaction including its `id`.

### Example: summary

`GET /api/transactions/summary`

```json
{
  "totalIncome": 50000.00,
  "totalExpense": 2000.50,
  "balance": 47999.50
}
```

### Example errors

Validation failure (`400 Bad Request`):

```json
{
  "category": "Category is required"
}
```

Wrong login (`401 Unauthorized`):

```json
{
  "error": "Invalid username or password"
}
```

No or invalid token (`401 Unauthorized`):

```json
{
  "error": "Authentication required"
}
```

Transaction not found, or it belongs to another user (`404 Not Found`):

```json
{
  "error": "Transaction not found with id 999"
}
```

Username already registered (`409 Conflict`):

```json
{
  "error": "Username already taken: rahul"
}
```

## Security Design

- **BCrypt password hashing**: passwords are hashed with a salt before they are stored, so a leaked database does not reveal them.
-
