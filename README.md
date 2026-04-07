# FinTrack — Personal Finance Manager

A full-stack personal finance management application built with **Spring Boot** and **React**. Track income, manage expenses, set monthly budgets, and gain actionable financial insights through interactive charts and analytics.

---

## Features

- **User Authentication** — Signup, login with JWT tokens, BCrypt password hashing
- **Dashboard** — At-a-glance view with balance, income, expenses, savings, and charts
- **Income Management** — CRUD operations with categories (Salary, Freelance, Investments, Other)
- **Expense Tracking** — Categorized expenses with payment method tracking
- **Budget Management** — Monthly budget limits with progress bars and overspending warnings
- **Financial Analytics** — Pie charts, bar charts, line charts for spending trends & breakdowns
- **Transaction History** — Combined view with search, filters, and pagination
- **User Profile** — Update name, change password, view account summary
- **Dark Mode** — Toggle between light and dark themes
- **Responsive Design** — Fully responsive layout with collapsible sidebar

---

## Tech Stack

| Layer     | Technology                                        |
|-----------|---------------------------------------------------|
| Frontend  | React 18, Vite 5, TailwindCSS 3, Recharts, Axios |
| Backend   | Java 17, Spring Boot 3.2, Spring Security, JPA    |
| Database  | MySQL 8+                                          |
| Auth      | JWT (jjwt 0.12.x), BCrypt                         |
| Build     | Maven (backend), npm (frontend)                   |

---

## Architecture

```
personal-finance-manager/
├── backend/               # Spring Boot REST API
│   ├── controller/        # REST endpoints
│   ├── service/           # Business logic
│   ├── repository/        # Data access (Spring Data JPA)
│   ├── model/             # JPA entities
│   ├── dto/               # Request/response objects
│   ├── security/          # JWT provider, filter, UserDetailsService
│   ├── config/            # Security & CORS config
│   ├── exception/         # Global exception handler
│   └── util/              # Auth utilities
│
├── frontend/              # React SPA
│   ├── src/
│   │   ├── components/    # Reusable UI (Sidebar, Navbar, Charts)
│   │   ├── pages/         # Route-level components
│   │   ├── context/       # AuthContext (global state)
│   │   ├── services/      # Axios API layer
│   │   └── App.jsx        # Router config
│   └── index.html
│
└── README.md
```

---

## Database Design

| Table      | Key Columns                                                    |
|------------|----------------------------------------------------------------|
| `users`    | id, name, email (unique), password, created_at                 |
| `income`   | id, user_id (FK), amount, category, source, date, notes        |
| `expenses` | id, user_id (FK), amount, category, date, payment_method, desc |
| `budgets`  | id, user_id (FK), monthly_limit, month, year (unique per user) |

---

## API Endpoints

### Authentication
| Method | Endpoint            | Description       |
|--------|---------------------|--------------------|
| POST   | `/api/auth/signup`  | Register new user  |
| POST   | `/api/auth/login`   | Login & get JWT    |

### Income
| Method | Endpoint            | Description         |
|--------|---------------------|----------------------|
| GET    | `/api/income`       | List all income      |
| POST   | `/api/income`       | Create income entry  |
| PUT    | `/api/income/{id}`  | Update income entry  |
| DELETE | `/api/income/{id}`  | Delete income entry  |

### Expenses
| Method | Endpoint              | Description           |
|--------|-----------------------|------------------------|
| GET    | `/api/expenses`       | List all expenses      |
| POST   | `/api/expenses`       | Create expense entry   |
| PUT    | `/api/expenses/{id}`  | Update expense entry   |
| DELETE | `/api/expenses/{id}`  | Delete expense entry   |

### Budget
| Method | Endpoint             | Description            |
|--------|----------------------|-------------------------|
| GET    | `/api/budget`        | List all budgets        |
| GET    | `/api/budget/current`| Get current month budget|
| POST   | `/api/budget`        | Create budget           |
| PUT    | `/api/budget/{id}`   | Update budget           |

### Analytics & Dashboard
| Method | Endpoint                 | Description                |
|--------|--------------------------|----------------------------|
| GET    | `/api/dashboard`         | Dashboard summary          |
| GET    | `/api/analytics/monthly` | Monthly trends & analytics |
| GET    | `/api/analytics/category`| Category breakdown         |
| GET    | `/api/analytics/trend`   | Spending trend data        |
| GET    | `/api/transactions`      | Combined transaction list  |

### User
| Method | Endpoint             | Description        |
|--------|----------------------|---------------------|
| GET    | `/api/user/profile`  | Get user profile    |
| PUT    | `/api/user/profile`  | Update profile      |
| PUT    | `/api/user/password` | Change password     |

---

## Setup Instructions

### Prerequisites
- Java 17+
- Node.js 18+
- MySQL 8+
- Maven 3.8+

### 1. Database Setup
```sql
CREATE DATABASE finance_manager;
```
> Optionally run `backend/schema.sql` for explicit table creation (Hibernate auto-creates too).

### 2. Backend Setup
```bash
cd backend

# Update database credentials in src/main/resources/application.properties
# Default: root/root on localhost:3306

mvn clean install
mvn spring-boot:run
```
Backend starts on **http://localhost:8080**

### 3. Frontend Setup
```bash
cd frontend
cp .env.example .env   # Already configured for localhost:8080
npm install
npm run dev
```
Frontend starts on **http://localhost:5173**

---

## Environment Variables

### Backend (`application.properties`)
| Property                          | Default                            |
|-----------------------------------|------------------------------------|
| `spring.datasource.url`          | `jdbc:mysql://localhost:3306/finance_manager` |
| `spring.datasource.username`     | `root`                             |
| `spring.datasource.password`     | `root`                             |
| `app.jwt.secret`                 | (64-char hex string)               |
| `app.jwt.expiration-ms`          | `86400000` (24 hours)              |
| `app.cors.allowed-origins`       | `http://localhost:5173`            |

### Frontend (`.env`)
| Variable              | Default                          |
|-----------------------|-----------------------------------|
| `VITE_API_BASE_URL`  | `http://localhost:8080/api`       |

---

## Future Improvements

- [ ] Export transactions as CSV/PDF
- [ ] Email notifications for overspending
- [ ] Recurring income/expense entries
- [ ] Multi-currency support
- [ ] Mobile app (React Native)
- [ ] OAuth 2.0 (Google, GitHub login)
- [ ] Two-factor authentication
- [ ] Financial goal tracking
- [ ] Bill reminders & scheduling

---

## License

This project is for educational and personal use.
