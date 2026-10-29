# Finlytics

Finlytics is a personal finance tracker with a Spring Boot REST API and a dark glassmorphism web interface. You record income and expenses, import them from a CSV file, then see where your money goes through charts, budgets, spending insights and downloadable PDF reports. It works in INR, USD and EUR using daily exchange rates.

I built it as a portfolio project to practice layered backend design, JWT security, validation, testing, and connecting a plain JavaScript frontend to a real API.

## Screenshots

| Dashboard | Transactions |
|---|---|
| ![Dashboard](docs/screenshots/dashboard.png) | ![Transactions](docs/screenshots/transactions.png) |

| Analytics and budgets | Insights |
|---|---|
| ![Analytics](docs/screenshots/analytics.png) | ![Insights](docs/screenshots/insights.png) |

## Features

**Accounts**
- Sign up with first name, last name, email, an auto-suggested unique username and a strong password (live rule checker)
- Sign in with email or username, or continue with Google
- JWT authentication, passwords hashed with BCrypt, change password from Settings

**Money tracking**
- Add, edit and delete income and expense transactions
- Search by type, category and date range, with pagination and category suggestions
- Import many transactions at once from a CSV file, with per-row error messages and duplicate protection
- Dashboard cards: balance, income, expenses and savings rate
- Charts: six-month income vs expenses and expenses by category

**Analytics and insights**
- 12-month income vs expense chart and a month-vs-month category comparison
- Monthly category budgets with progress bars (amber near the limit, red when over)
- Unusual spending alerts, recurring payment detection and a next-month forecast

**Reports and currency**
- Monthly PDF report generated on the server
- Currency switcher (INR, USD, EUR) using daily exchange rates, cached on the server

## Tech Stack

- **Backend:** Java 17, Spring Boot 4.1.1 (Web MVC, Data JPA, Security, Validation), Hibernate, MySQL 8
- **Security:** Spring Security, JWT (jjwt), BCrypt, Google Identity Services
- **Reports:** OpenPDF
- **Frontend:** plain HTML, CSS and JavaScript, Chart.js (no framework)
- **Testing:** JUnit 5, Mockito, AssertJ
- **Tools:** Maven, Eclipse, Postman, Git

## Architecture

Request flow: **Controller → Service → Repository → MySQL**, and the response travels back the same way.

```
com.finlyticsltd.finlytics
├── config        # Security setup, JWT filter
├── controller    # REST endpoints
├── service       # Business logic (auth, insights, import, reports, rates, budgets)
├── repository    # Spring Data JPA repositories
├── entity        # JPA entities (User, Transaction, Budget)
├── dto           # Request and response records
└── exception     # Custom exceptions and global error handler

src/main/resources/static
├── *.html        # login, register, dashboard, transactions, analytics, insights, reports, settings
├── css/          # design tokens, layout, components
└── js/           # API wrapper, page scripts, shared layout
```

The frontend is served by Spring Boot from the `static` folder, and every data request goes through one `apiFetch` function that attaches the JWT.

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

2. Create the database (tables are created automatically on first run):

```sql
CREATE DATABASE finlytics_db;
```

3. Set the environment variables:

| Variable | Description | Required |
|---|---|---|
| `DB_USERNAME` | MySQL username | yes |
| `DB_PASSWORD` | MySQL password | yes |
| `JWT_SECRET` | Long random string (32+ characters) used to sign tokens | yes |
| `GOOGLE_CLIENT_ID` | Google OAuth Client ID, only for Google sign-in | no |

Windows (Command Prompt):

```bash
set DB_USERNAME=root
set DB_PASSWORD=your_password
set JWT_SECRET=replace_with_a_long_random_string_of_32_or_more_characters
```

Linux/macOS:

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=replace_with_a_long_random_string_of_32_or_more_characters
```

In Eclipse: *Run → Run Configurations → Environment tab → New*.

4. Run the application:

```bash
./mvnw spring-boot:run
```

Or run `FinlyticsApplication` from your IDE, then open `http://localhost:8080/login.html`.

### Try it with demo data

Register an account, open **Transactions → Import CSV**, and upload `docs/demo-transactions.csv`. The dashboard, charts, budgets and Insights fill up with six months of sample data.

### Optional: Google sign-in

1. In the Google Cloud Console, create an OAuth client of type **Web application** and add `http://localhost:8080` under **Authorized JavaScript origins**.
2. Set the `GOOGLE_CLIENT_ID` environment variable.
3. Put the same Client ID in `src/main/resources/static/js/config.js`.

Without these steps the app works normally and only the Google button is disabled.

### Run the tests

```bash
./mvnw test
```

On Windows use `mvnw.cmd test`. The service tests use Mockito, so they run in seconds without a database.

## CSV Import Format

The first row must be a header. Columns can be in any order:

| Column | Required | Accepted values |
|---|---|---|
| `date` | yes | `2026-10-03`, `03/10/2026` or `03-10-2026` (day first) |
| `category` | yes | any text up to 100 characters |
| `type` | yes | `INCOME` or `EXPENSE` (also `credit` / `debit`) |
| `amount` | yes | a number above zero; currency symbols and commas are ignored |
| `description` | no | any text up to 255
