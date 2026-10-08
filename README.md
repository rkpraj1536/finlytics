# Finlytics

Finlytics is a personal finance tracker REST API built with Spring Boot. It lets you record income and expenses, then see where your money goes through totals, category breakdowns and monthly summaries. I built it to practice layered backend design, validation, and clean error handling with Spring Boot and MySQL.

## Features

- Add, view, update and delete income and expense transactions
- Input validation with clear, field-level error messages
- Global exception handling that returns consistent JSON errors
- Overall summary: total income, total expense and balance
- Spending breakdown by category (largest first)
- Monthly summary for any year and month

## Tech Stack

- Java 17
- Spring Boot 3 (Spring Web, Spring Data JPA, Validation)
- MySQL
- Hibernate (JPA)
- Maven
- Tested with Postman

## Project Structure

```
com.finlyticsltd.finlytics
├── controller    # REST endpoints, handle HTTP requests and responses
├── service       # Business logic (summaries, not-found checks)
├── repository    # Database access via Spring Data JPA
├── entity        # JPA entities (Transaction, TransactionType)
├── dto           # Response objects (SummaryResponse, CategoryTotal)
└── exception     # Custom exceptions and global error handler
```

Request flow: **Controller → Service → Repository → MySQL**, and the response travels back the same way.

## Getting Started

### Prerequisites

- JDK 17 or higher
- MySQL 8 or higher
- Maven (or use the included `mvnw` wrapper)

### Setup

1. Clone the repository:

```bash
   git clone https://github.com/<your-username>/finlytics.git
   cd finlytics
```

2. Create the database:

```sql
   CREATE DATABASE finlytics_db;
```

   Tables are created automatically by Hibernate on first run.

3. Set your database credentials as environment variables:

   | Variable | Description | Default |
   |---|---|---|
   | `DB_USERNAME` | MySQL username | `root` |
   | `DB_PASSWORD` | MySQL password | none (required) |

   On Windows (Command Prompt):

```bash
   set DB_PASSWORD=your_password
```

   On Linux/macOS:

```bash
   export DB_PASSWORD=your_password
```

   In Eclipse: *Run → Run Configurations → Environment tab → New*.

4. Run the application:

```bash
   ./mvnw spring-boot:run
```

   Or run `FinlyticsApplication` from your IDE. The API starts at `http://localhost:8080`.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/transactions` | Add a transaction |
| GET | `/api/transactions` | List all transactions |
| GET | `/api/transactions/{id}` | Get one transaction |
| PUT | `/api/transactions/{id}` | Update a transaction |
| DELETE | `/api/transactions/{id}` | Delete a transaction |
| GET | `/api/transactions/summary` | Total income, expense and balance |
| GET | `/api/transactions/summary/by-category?type=EXPENSE` | Totals per category (`INCOME` or `EXPENSE`) |
| GET | `/api/transactions/summary/monthly?year=2026&month=10` | Summary for one month |

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

### Example: validation error

Sending a request with no `category` returns `400 Bad Request`:

```json
{
  "category": "Category is required"
}
```

Requesting a transaction that doesn't exist returns `404 Not Found`:

```json
{
  "error": "Transaction not found with id 999"
}
```

## Design Decisions

- **BigDecimal for money**: floating point types like `double` cause rounding errors, so all amounts use `BigDecimal`.
- **Enum stored as a string**: `TransactionType` is saved as `INCOME` or `EXPENSE`, so data stays readable and survives enum reordering.
- **DTOs for calculated results**: summaries are not table rows, so they use dedicated response objects instead of entities.
- **Centralized error handling**: one `@RestControllerAdvice` class handles validation, bad input and not-found cases for every endpoint.
- **Credentials from environment variables**: no database password is stored in the repository.

## Planned Next

- Unit tests for the service layer
- User login and authentication with Spring Security
- Pagination and filtering on the transaction list
- A simple frontend to visualize spending

## Author

Rahul Prajapati
B.Tech Computer Science Engineering student
GitHub: [<your-username>](https://github.com/rkpraj1536)