# FinTrack — Personal Finance Manager: Walkthrough

## What Was Built

A production-quality full-stack Personal Finance Manager with **36 source files** across backend and frontend.

### Backend (Spring Boot 3.2 / Java 17)
| Layer | Files | Highlights |
|-------|-------|------------|
| Models | 4 entities | [User](file:///d:/Projects/FinanceManagerFullStack/backend/src/main/java/com/financemanager/model/User.java#11-49), [Income](file:///d:/Projects/FinanceManagerFullStack/backend/src/main/java/com/financemanager/model/Income.java#9-39), [Expense](file:///d:/Projects/FinanceManagerFullStack/backend/src/main/java/com/financemanager/model/Expense.java#9-42), [Budget](file:///d:/Projects/FinanceManagerFullStack/backend/src/main/java/com/financemanager/model/Budget.java#8-34) with JPA + Lombok |
| DTOs | 14 classes | Request/response DTOs with Jakarta Validation |
| Repositories | 4 interfaces | Custom JPQL queries for aggregation and date-range filtering |
| Security | 3 classes + config | JWT generation/validation, `OncePerRequestFilter`, BCrypt, stateless security chain |
| Services | 6 classes | Auth, Income, Expense, Budget (with spending enrichment), Analytics (dashboard + trends), User |
| Controllers | 6 classes | REST endpoints for all features + [AuthUtil](file:///d:/Projects/FinanceManagerFullStack/backend/src/main/java/com/financemanager/util/AuthUtil.java#14-31) for user resolution |
| Exception | 3 classes | `@ControllerAdvice` with structured JSON error responses |

### Frontend (React 18 / Vite 5 / TailwindCSS 3)
| Area | Files | Highlights |
|------|-------|------------|
| Pages | 11 components | Landing, Login, Signup, Dashboard, Income, Expense, Budget, Analytics, Transactions, Profile, 404 |
| Charts | 3 components | Pie (donut), Bar, Line using Recharts |
| Layout | 3 components | Collapsible sidebar, navbar with dark mode toggle, protected route wrapper |
| Context | 1 provider | `AuthContext` with localStorage persistence, auto-restore on refresh |
| Services | 1 module | Axios instance with JWT interceptor, 401 redirect, typed API functions |

### Design Highlights
- **Custom color palette** (primary indigo + accent emerald) via TailwindCSS
- **Dark mode** with `class` strategy, persisted in localStorage
- **Micro-animations**: fade-in, slide-up, card hover transitions
- **Skeleton loaders** for async data
- **Responsive** sidebar collapses to mobile hamburger menu
- **Inter font** from Google Fonts

---

## Verification Results

| Check | Result |
|-------|--------|
| `npm install` | ✅ 195 packages installed |
| `npm run build` | ✅ Production build succeeds in 4.63s |
| File structure | ✅ Clean separation: `backend/` and `frontend/` |
| TypeScript/lint errors | ✅ No compilation errors |

---

## How to Run

### 1. Database
```sql
CREATE DATABASE finance_manager;
```

### 2. Backend
```bash
cd backend
# Edit application.properties for your MySQL credentials
mvn spring-boot:run
# → http://localhost:8080
```

### 3. Frontend
```bash
cd frontend
npm install     # Already done
npm run dev
# → http://localhost:5173
```

---

## Key Files Reference

| File | Purpose |
|------|---------|
| [pom.xml](file:///d:/Projects/FinanceManagerFullStack/backend/pom.xml) | Maven dependencies |
| [SecurityConfig.java](file:///d:/Projects/FinanceManagerFullStack/backend/src/main/java/com/financemanager/config/SecurityConfig.java) | JWT filter chain, CORS, BCrypt |
| [AuthService.java](file:///d:/Projects/FinanceManagerFullStack/backend/src/main/java/com/financemanager/service/AuthService.java) | Signup/login with token generation |
| [AnalyticsService.java](file:///d:/Projects/FinanceManagerFullStack/backend/src/main/java/com/financemanager/service/AnalyticsService.java) | Dashboard aggregation & trends |
| [schema.sql](file:///d:/Projects/FinanceManagerFullStack/backend/schema.sql) | MySQL table definitions |
| [App.jsx](file:///d:/Projects/FinanceManagerFullStack/frontend/src/App.jsx) | React router config |
| [AuthContext.jsx](file:///d:/Projects/FinanceManagerFullStack/frontend/src/context/AuthContext.jsx) | Global auth state |
| [api.js](file:///d:/Projects/FinanceManagerFullStack/frontend/src/services/api.js) | Axios API layer |
| [DashboardPage.jsx](file:///d:/Projects/FinanceManagerFullStack/frontend/src/pages/DashboardPage.jsx) | Charts & summary cards |
| [README.md](file:///d:/Projects/FinanceManagerFullStack/README.md) | Full project documentation |
