-- ─────────────────────────────────────────────────────
-- Personal Finance Manager — MySQL Schema
-- Run this script after creating the database:
--   CREATE DATABASE finance_manager;
--   USE finance_manager;
-- ─────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    email       VARCHAR(150)  NOT NULL UNIQUE,
    password    VARCHAR(255)  NOT NULL,
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS income (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT        NOT NULL,
    amount      DECIMAL(12,2) NOT NULL,
    category    VARCHAR(50)   NOT NULL,
    source      VARCHAR(100)  NOT NULL,
    date        DATE          NOT NULL,
    notes       TEXT,

    CONSTRAINT fk_income_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_income_user_date (user_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS expenses (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    amount          DECIMAL(12,2) NOT NULL,
    category        VARCHAR(50)   NOT NULL,
    date            DATE          NOT NULL,
    payment_method  VARCHAR(50),
    description     VARCHAR(255),
    notes           TEXT,

    CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_expense_user_date (user_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS budgets (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    monthly_limit   DECIMAL(12,2) NOT NULL,
    month           INT           NOT NULL,
    year            INT           NOT NULL,

    CONSTRAINT fk_budget_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_budget_user_month (user_id, month, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
