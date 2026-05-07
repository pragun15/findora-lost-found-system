# Lost & Found Management System — Complete Workflow Documentation

> **Academic DBMS Mini Project** | React + Tailwind CSS · Spring Boot · MySQL

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Tech Stack](#2-tech-stack)
3. [System Architecture](#3-system-architecture)
4. [Folder Structure](#4-folder-structure)
5. [Database Design](#5-database-design)
6. [Table Explanations](#6-table-explanations)
7. [Relationships Between Tables](#7-relationships-between-tables)
8. [ER Diagram Description](#8-er-diagram-description)
9. [Complete User Workflow](#9-complete-user-workflow)
10. [Authentication Workflow](#10-authentication-workflow)
11. [Item CRUD Workflow](#11-item-crud-workflow)
12. [Smart Matching Workflow](#12-smart-matching-workflow)
13. [Claim Workflow](#13-claim-workflow)
14. [Audit Log Workflow](#14-audit-log-workflow)
15. [Search & Filtering Workflow](#15-search--filtering-workflow)
16. [Export Workflow](#16-export-workflow)
17. [Backend API Structure](#17-backend-api-structure)
18. [REST API Endpoints](#18-rest-api-endpoints)
19. [Frontend Page Structure](#19-frontend-page-structure)
20. [React Component Structure](#20-react-component-structure)
21. [Spring Boot Architecture](#21-spring-boot-architecture)
22. [JPA/Hibernate Usage](#22-jpahibernate-usage)
23. [Validation Rules](#23-validation-rules)
24. [Soft Delete Logic](#24-soft-delete-logic)
25. [Pagination Logic](#25-pagination-logic)
26. [SQL Concepts Used](#26-sql-concepts-used)
27. [Important JOIN Queries](#27-important-join-queries)
28. [Aggregate Queries](#28-aggregate-queries)
29. [Constraints Used](#29-constraints-used)
30. [Indexing Suggestions](#30-indexing-suggestions)
31. [Future Scope](#31-future-scope)
32. [Viva Explanation Notes](#32-viva-explanation-notes)
33. [Key DBMS Concepts Demonstrated](#33-key-dbms-concepts-demonstrated)
34. [Development Phases](#34-development-phases)
35. [Step-by-Step Build Plan](#35-step-by-step-build-plan)

---

## 1. Project Overview

### What is the Lost & Found Management System?

The Lost & Found Management System is a web-based application designed for colleges and institutions. It provides a centralized platform where students and staff can:

- Register and log in securely.
- Report items they have **lost**.
- Report items they have **found**.
- Search and filter through all reported items.
- Submit **claims** to retrieve a found item they believe belongs to them.
- Track claim status (pending → approved/rejected).
- View a full **audit history** of all important actions.
- Export records as CSV for documentation.

### Purpose

This project is built as a **DBMS mini project** to demonstrate core database management concepts using a realistic, practical use case. The emphasis is on:

- Proper relational database design with normalization.
- CRUD operations through a Spring Boot REST API.
- Complex SQL queries — JOINs, aggregates, filtering, and pagination.
- Real-world workflows like claims management and soft deletes.
- Audit trails maintained through a dedicated log table.

### Scope

This is an academic project. It is intentionally kept simple and explainable. It does NOT involve microservices, JWT tokens, image uploads, cloud deployments, AI/ML, or real-time systems. Everything is standard, clean, and implementable within a few weeks.

---

## 2. Tech Stack

| Layer | Technology | Purpose |
|---|---|---|
| **Frontend** | React 18 | Component-based UI |
| **Styling** | Tailwind CSS | Utility-first responsive design |
| **HTTP Client** | Axios | API communication from React |
| **Backend** | Spring Boot 3 | REST API server |
| **ORM** | Spring Data JPA + Hibernate | Database interaction |
| **Database** | MySQL 8 | Relational data storage |
| **Build Tool** | Maven | Java dependency management |
| **Dev Tool** | Spring Boot DevTools | Hot reload during development |
| **Validation** | Jakarta Bean Validation | Input validation on backend |
| **Export** | Apache Commons CSV | CSV export |

---

## 3. System Architecture

```
┌─────────────────────────────────────────────────────┐
│                    BROWSER (User)                    │
└──────────────────────┬──────────────────────────────┘
                       │  HTTP Requests (JSON)
                       ▼
┌─────────────────────────────────────────────────────┐
│              REACT FRONTEND (Port 3000)              │
│                                                     │
│  Pages: Login · Register · Items · Report Item ·    │
│         Item Detail · Claims · Audit Logs           │
│                                                     │
│  State: useState / useEffect / Axios                │
└──────────────────────┬──────────────────────────────┘
                       │  REST API Calls (Axios)
                       ▼
┌─────────────────────────────────────────────────────┐
│           SPRING BOOT BACKEND (Port 8080)            │
│                                                     │
│  Controllers → Services → Repositories              │
│                                                     │
│  Layers:                                            │
│  ├── Controller  (REST endpoints)                   │
│  ├── Service     (Business logic)                   │
│  ├── Repository  (JPA/Hibernate queries)            │
│  └── Model       (Entity classes)                   │
└──────────────────────┬──────────────────────────────┘
                       │  JDBC / JPA
                       ▼
┌─────────────────────────────────────────────────────┐
│                 MYSQL DATABASE (Port 3306)           │
│                                                     │
│  Tables: users · categories · items · claims ·      │
│          audit_logs                                 │
└─────────────────────────────────────────────────────┘
```

### Request Flow (Example: Report a Lost Item)

```
User fills form in React
    → Axios POST /api/items
        → ItemController.createItem()
            → ItemService.createItem()
                → ItemRepository.save(item)       [INSERT into items]
                → AuditLogService.log(...)        [INSERT into audit_logs]
                → MatchingService.findMatches()   [SELECT from items WHERE ...]
            ← Returns saved item + matches as JSON
        ← ItemController returns ResponseEntity
    ← React shows success + match suggestions
```

---

## 4. Folder Structure

### Backend (Spring Boot)

```
lost-found-backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/lostandfound/
│       │       ├── LostFoundApplication.java          ← Main entry point
│       │       ├── config/
│       │       │   └── CorsConfig.java                ← Allow React to call API
│       │       ├── controller/
│       │       │   ├── AuthController.java
│       │       │   ├── ItemController.java
│       │       │   ├── ClaimController.java
│       │       │   ├── AuditLogController.java
│       │       │   ├── CategoryController.java
│       │       │   └── ExportController.java          ← CSV export only
│       │       ├── service/
│       │       │   ├── UserService.java
│       │       │   ├── ItemService.java
│       │       │   ├── ClaimService.java
│       │       │   ├── AuditLogService.java
│       │       │   ├── MatchingService.java
│       │       │   └── ExportService.java             ← CSV generation only
│       │       ├── repository/
│       │       │   ├── UserRepository.java
│       │       │   ├── ItemRepository.java
│       │       │   ├── ClaimRepository.java
│       │       │   ├── AuditLogRepository.java
│       │       │   └── CategoryRepository.java
│       │       ├── model/
│       │       │   ├── User.java
│       │       │   ├── Item.java
│       │       │   ├── Claim.java
│       │       │   ├── AuditLog.java
│       │       │   └── Category.java
│       │       └── dto/
│       │           ├── RegisterRequest.java
│       │           ├── LoginRequest.java
│       │           ├── ItemRequest.java
│       │           ├── ClaimRequest.java
│       │           └── ApiResponse.java
│       └── resources/
│           └── application.properties
├── pom.xml
```

### Frontend (React)

```
lost-found-frontend/
├── public/
│   └── index.html
├── src/
│   ├── index.js                          ← React entry point
│   ├── App.js                            ← Routes
│   ├── api/
│   │   └── axios.js                      ← Axios base config
│   ├── context/
│   │   └── AuthContext.js                ← Global user session state
│   ├── pages/
│   │   ├── LoginPage.jsx
│   │   ├── RegisterPage.jsx
│   │   ├── ItemListPage.jsx              ← Table of all items + search/filter/CSV export
│   │   ├── ItemDetailPage.jsx            ← Single item view + claim form
│   │   ├── ReportItemPage.jsx            ← Form to report lost/found item
│   │   ├── ClaimsPage.jsx                ← Claims table (submitted + received)
│   │   └── AuditLogPage.jsx              ← Audit log table
│   ├── components/
│   │   ├── Navbar.jsx
│   │   ├── ItemTable.jsx                 ← Tabular display of items
│   │   ├── ItemForm.jsx                  ← Shared form for report + edit
│   │   ├── ClaimForm.jsx                 ← Submit claim form
│   │   ├── MatchSuggestions.jsx          ← Simple list of matching FOUND items
│   │   ├── SearchBar.jsx
│   │   ├── FilterPanel.jsx
│   │   ├── Pagination.jsx
│   │   └── AuditLogTable.jsx
│   └── utils/
│       └── helpers.js
├── tailwind.config.js
└── package.json
```

---

## 5. Database Design

### Schema Creation SQL

```sql
CREATE DATABASE IF NOT EXISTS lost_found_db;
USE lost_found_db;

-- 1. Users table
CREATE TABLE users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. Categories table
CREATE TABLE categories (
    id    INT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100) NOT NULL UNIQUE
);

-- 3. Items table
CREATE TABLE items (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(200) NOT NULL,
    description   TEXT,
    category_id   INT NOT NULL,
    color         VARCHAR(50),
    keywords      VARCHAR(500),
    status        ENUM('LOST', 'FOUND', 'CLAIMED') NOT NULL,
    location      VARCHAR(200),
    date_reported DATE NOT NULL,
    reporter_id   INT NOT NULL,
    is_deleted    BOOLEAN DEFAULT FALSE,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (reporter_id) REFERENCES users(id)
);

-- 4. Claims table
CREATE TABLE claims (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    item_id      INT NOT NULL,
    claimant_id  INT NOT NULL,
    proof_text   TEXT,
    status       ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items(id),
    FOREIGN KEY (claimant_id) REFERENCES users(id)
);

-- 5. Audit Logs table
CREATE TABLE audit_logs (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT,
    action       VARCHAR(100) NOT NULL,
    entity_name  VARCHAR(100) NOT NULL,
    entity_id    INT,
    timestamp    DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);
```

### Sample Seed Data

```sql
-- Seed categories
INSERT INTO categories (name) VALUES
('Electronics'), ('Clothing'), ('Books'), ('Bags'), ('Keys'),
('Wallets'), ('Accessories'), ('Sports Equipment'), ('Documents'), ('Other');

-- Seed users
INSERT INTO users (name, email, password) VALUES
('Alice Sharma', 'alice@college.edu', 'hashed_password_1'),
('Bob Kumar',   'bob@college.edu',   'hashed_password_2');

-- Seed items
INSERT INTO items (title, description, category_id, color, keywords, status, location, date_reported, reporter_id)
VALUES
('Blue Water Bottle', 'Stainless steel bottle with college logo', 7, 'blue', 'bottle,steel,water', 'LOST', 'Library Block A', '2024-06-01', 1),
('Found Keys Bundle', 'Set of 3 keys on a red keychain', 5, 'red', 'keys,keychain,red', 'FOUND', 'Canteen', '2024-06-02', 2);
```

---

## 6. Table Explanations

### 6.1 `users`

Stores registered users of the system.

| Column | Type | Description |
|---|---|---|
| `id` | INT, PK, AI | Unique identifier for each user |
| `name` | VARCHAR(100) | Full name of the user |
| `email` | VARCHAR(150), UNIQUE | Login email; must be unique |
| `password` | VARCHAR(255) | Stored as plain text for this academic project (in real apps, use bcrypt) |
| `created_at` | DATETIME | Account creation timestamp |

### 6.2 `categories`

A lookup/reference table for item categories. Normalized to avoid repeating category names in every item row.

| Column | Type | Description |
|---|---|---|
| `id` | INT, PK, AI | Unique category identifier |
| `name` | VARCHAR(100), UNIQUE | Category label (e.g., Electronics, Books) |

### 6.3 `items`

The central table. Stores all reported lost and found items.

| Column | Type | Description |
|---|---|---|
| `id` | INT, PK, AI | Unique item identifier |
| `title` | VARCHAR(200) | Short name/title of item |
| `description` | TEXT | Detailed description |
| `category_id` | INT, FK → categories | Normalized category reference |
| `color` | VARCHAR(50) | Color of the item (used in matching) |
| `keywords` | VARCHAR(500) | Comma-separated keywords for search/matching |
| `status` | ENUM | `LOST`, `FOUND`, or `CLAIMED` |
| `location` | VARCHAR(200) | Where item was lost/found |
| `date_reported` | DATE | When the item was reported |
| `reporter_id` | INT, FK → users | Who reported the item |
| `is_deleted` | BOOLEAN | Soft delete flag (TRUE = deleted) |
| `created_at` | DATETIME | Row creation timestamp |
| `updated_at` | DATETIME | Auto-updated on any change |

### 6.4 `claims`

Tracks claims submitted by users who believe a found item belongs to them.

| Column | Type | Description |
|---|---|---|
| `id` | INT, PK, AI | Unique claim identifier |
| `item_id` | INT, FK → items | Which item is being claimed |
| `claimant_id` | INT, FK → users | Who is submitting the claim |
| `proof_text` | TEXT | Description/proof of ownership |
| `status` | ENUM | `PENDING`, `APPROVED`, or `REJECTED` |
| `created_at` | DATETIME | When the claim was submitted |

### 6.5 `audit_logs`

Immutable event log. Records every important system action for traceability.

| Column | Type | Description |
|---|---|---|
| `id` | INT, PK, AI | Unique log entry |
| `user_id` | INT, FK → users | Who performed the action (nullable if system action) |
| `action` | VARCHAR(100) | Action name (e.g., `ITEM_CREATED`, `CLAIM_APPROVED`) |
| `entity_name` | VARCHAR(100) | Which table/entity was affected |
| `entity_id` | INT | ID of the affected row |
| `timestamp` | DATETIME | Exact time of the action |

---

## 7. Relationships Between Tables

```
users ──────────────< items          (One user reports many items)
users ──────────────< claims         (One user submits many claims)
users ──────────────< audit_logs     (One user generates many log entries)
categories ─────────< items          (One category has many items)
items ───────────────< claims        (One item can have many claims)
```

### Summary Table

| Relationship | Type | Description |
|---|---|---|
| users → items | One-to-Many | A user can report multiple items |
| categories → items | One-to-Many | A category groups multiple items |
| items → claims | One-to-Many | One item can receive multiple claims |
| users → claims | One-to-Many | A user can claim multiple items |
| users → audit_logs | One-to-Many | User's actions generate multiple logs |

### Why These Relationships?

- **categories is separate**: Avoids data redundancy. If "Electronics" is a category, it is stored once in `categories`, not repeated in every item row. This is **1NF and 2NF normalization**.
- **claims is separate**: A claim is its own entity with its own lifecycle (status). It references both the item and the user.
- **audit_logs is decoupled**: Logs reference user_id with `ON DELETE SET NULL` so that deleting a user does not destroy the audit trail.

---

## 8. ER Diagram Description

```
+----------+       +----------+       +----------+
|  users   |       | categories|      |  items   |
+----------+       +----------+       +----------+
| id (PK)  |       | id (PK)  |       | id (PK)  |
| name     |       | name     |       | title    |
| email    |       +----------+       | desc     |
| password |           1              | color    |
| created_at|          |              | keywords |
+----------+           | 1:N          | status   |
     |                 |              | location |
     | 1:N (reporter)  +--------------| category_id (FK)
     |                                | reporter_id (FK)
     +--------------------------------| is_deleted|
     |                                | created_at|
     |                                | updated_at|
     |                                +----------+
     |                                     |
     | 1:N (claimant)                      | 1:N
     |                                     |
     v                                     v
+----------+                         +----------+
|  claims  |                         |audit_logs|
+----------+                         +----------+
| id (PK)  |                         | id (PK)  |
| item_id (FK)                       | user_id (FK)
| claimant_id (FK)                   | action   |
| proof_text|                        | entity_name|
| status   |                         | entity_id|
| created_at|                        | timestamp|
+----------+                         +----------+
```

### Cardinality

- `users` to `items`: **1 : N** (one user, many items)
- `categories` to `items`: **1 : N** (one category, many items)
- `items` to `claims`: **1 : N** (one item, many claims)
- `users` to `claims`: **1 : N** (one user, many claims)

---

## 9. Complete User Workflow

```
1. User visits the site
        ↓
2. User registers (name, email, password)
        ↓
3. User logs in (email + password checked against DB)
        ↓
4. User lands on ItemListPage
    ├── View all lost/found items in a table
    ├── Search by keyword
    └── Filter by status, category, date range
        ↓
5. User reports a LOST or FOUND item
    ├── Fills form (title, description, category, color, keywords, location, date)
    └── System checks for smart matches and shows a simple match list
        ↓
6. User sees a FOUND item that might be theirs
    └── Clicks into ItemDetailPage → submits a CLAIM with proof text
        ↓
7. Reporter reviews the claim on ClaimsPage
    ├── APPROVES → item status changes to CLAIMED
    └── REJECTS  → claim marked rejected
        ↓
8. Every action above is logged in audit_logs
        ↓
9. User exports item records as CSV directly from ItemListPage
```

---

## 10. Authentication Workflow

### Registration Flow

```
React RegisterPage
    → POST /api/auth/register
        → AuthController.register()
            → UserService.register()
                → userRepository.findByEmail(email)  [Check duplicate]
                → if exists → throw "Email already registered"
                → else → userRepository.save(newUser) [INSERT into users]
                → auditLogService.log(userId, "USER_REGISTERED", "users", userId)
            ← Return ApiResponse { success: true, userId: X }
    ← React stores userId + name in AuthContext (localStorage)
    ← Redirect to ItemListPage
```

### Login Flow

```
React LoginPage
    → POST /api/auth/login { email, password }
        → AuthController.login()
            → UserService.login()
                → userRepository.findByEmail(email)  [SELECT * FROM users WHERE email=?]
                → if not found → throw "User not found"
                → if password doesn't match → throw "Incorrect password"
                → Return user object (id, name, email)
        ← Return ApiResponse { success: true, user: { id, name, email } }
    ← React stores user in AuthContext
    ← Redirect to ItemListPage
```

### Logout Flow

```
User clicks Logout in Navbar
    → AuthContext.logout()
        → Clear user from state
        → Clear localStorage
    → Redirect to LoginPage
```

### Session Management (Simple Approach for Academic Project)

Since JWT is excluded, the project uses simple front-end state management:

```javascript
// AuthContext.js
const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(() => {
        const saved = localStorage.getItem('user');
        return saved ? JSON.parse(saved) : null;
    });

    const login = (userData) => {
        setUser(userData);
        localStorage.setItem('user', JSON.stringify(userData));
    };

    const logout = () => {
        setUser(null);
        localStorage.removeItem('user');
    };

    return (
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
```

---

## 11. Item CRUD Workflow

### Create Item

```
User fills ReportItemPage form
    → POST /api/items  { title, description, categoryId, color, keywords, status, location, dateReported }
        → ItemController.createItem()
            → ItemService.createItem(request, userId)
                → new Item() — map fields from request
                → item.setReporter(userRepository.findById(userId))
                → item.setCategory(categoryRepository.findById(categoryId))
                → itemRepository.save(item)               [INSERT into items]
                → auditLogService.log(userId, "ITEM_CREATED", "items", item.getId())
                → matchingService.findMatches(item)        [SELECT matching items]
            ← Return { item, matches }
    ← React shows success + match suggestions
```

### Read Items (List with Pagination)

```
ItemListPage loads
    → GET /api/items?page=0&size=10&status=LOST&categoryId=1&keyword=bottle
        → ItemController.getAllItems(pageable, filters)
            → ItemService.getAllItems(pageable, filters)
                → itemRepository.findAllByFilters(...)     [SELECT with WHERE + LIMIT OFFSET]
            ← Return Page<Item> (content, totalElements, totalPages)
    ← React renders list + pagination controls
```

### Read Item (Single)

```
User clicks "View" on a row in ItemTable
    → GET /api/items/{id}
        → ItemController.getItemById(id)
            → ItemService.getItemById(id)
                → itemRepository.findByIdAndIsDeletedFalse(id)
            ← Return Item with category and reporter details
    ← React renders ItemDetailPage
```

### Update Item

```
User clicks Edit on their own item
    → PUT /api/items/{id}  { updated fields }
        → ItemController.updateItem(id, request)
            → ItemService.updateItem(id, request, userId)
                → find item by id
                → verify reporter_id == userId
                → update fields
                → itemRepository.save(item)               [UPDATE items SET ... WHERE id=?]
                → auditLogService.log(userId, "ITEM_UPDATED", "items", id)
            ← Return updated item
    ← React shows success toast
```

### Soft Delete Item

```
User clicks Delete on their item
    → DELETE /api/items/{id}
        → ItemController.deleteItem(id)
            → ItemService.softDeleteItem(id, userId)
                → item.setIsDeleted(true)
                → itemRepository.save(item)               [UPDATE items SET is_deleted=TRUE WHERE id=?]
                → auditLogService.log(userId, "ITEM_DELETED", "items", id)
            ← Return success message
    ← React removes item from list
```

---

## 12. Smart Matching Workflow

When a user reports a **LOST** item, the system automatically searches for potential **FOUND** items that may be related.

### Matching Logic

```
User reports LOST item: { category=Electronics, color=black, keywords="charger,cable,usb", location="Library" }

MatchingService.findMatches(newItem):
    → Build SQL query to find FOUND items where:
        1. category_id = same category         (strong match)
        2. color = same color                  (medium match)
        3. keywords LIKE any of the keywords   (medium match)
        4. location LIKE same location         (medium match)
        5. date_reported BETWEEN (date-7) AND (date+7)  (weak match)
        6. status = 'FOUND'
        7. is_deleted = FALSE

    → Score each result (optional scoring logic):
        - category match: +3 points
        - color match:    +2 points
        - keyword match:  +2 points (per keyword match)
        - location match: +1 point
        - date proximity: +1 point

    → Return top 5 matches sorted by relevance score
```

### SQL Query for Matching

```sql
SELECT i.*, c.name AS category_name, u.name AS reporter_name,
       (
           (CASE WHEN i.category_id = :categoryId THEN 3 ELSE 0 END) +
           (CASE WHEN i.color = :color THEN 2 ELSE 0 END) +
           (CASE WHEN i.location LIKE CONCAT('%', :location, '%') THEN 1 ELSE 0 END)
       ) AS match_score
FROM items i
JOIN categories c ON i.category_id = c.id
JOIN users u ON i.reporter_id = u.id
WHERE i.status = 'FOUND'
  AND i.is_deleted = FALSE
  AND (
      i.category_id = :categoryId
      OR i.color = :color
      OR i.keywords LIKE CONCAT('%', :keyword1, '%')
      OR i.location LIKE CONCAT('%', :location, '%')
  )
ORDER BY match_score DESC
LIMIT 5;
```

### Frontend Display

After a LOST item is submitted, the `MatchSuggestions` component renders a plain list below the success message. Each entry shows the most useful fields at a glance; clicking a row navigates to that item's detail page where the user can submit a claim.

```
Possible Matches Found:
┌─────────────────────────────────────────────────────────────────┐
│ #  │ Title                    │ Location        │ Date       │   │
├─────────────────────────────────────────────────────────────────┤
│  1 │ Black Charger Found      │ Library Block A │ 2024-06-01 │ → │
│  2 │ USB Cable — Canteen      │ Canteen         │ 2024-05-30 │ → │
└─────────────────────────────────────────────────────────────────┘
```

If no matches are found, a plain message is shown: *"No potential matches found yet. Check back later."*

---

## 13. Claim Workflow

### Submit Claim

```
User views a FOUND item they believe is theirs
    → POST /api/claims  { itemId, proofText }
        → ClaimController.submitClaim()
            → ClaimService.submitClaim(itemId, userId, proofText)
                → Check: item exists and status is FOUND and is_deleted = FALSE
                → Check: user hasn't already claimed this item
                → new Claim(itemId, userId, proofText, PENDING)
                → claimRepository.save(claim)             [INSERT into claims]
                → auditLogService.log(userId, "CLAIM_SUBMITTED", "claims", claim.getId())
            ← Return claim details
    ← React shows "Claim submitted. Status: PENDING"
```

### Review Claim (By Reporter)

```
Reporter opens ClaimsPage for their item
    → GET /api/claims/item/{itemId}
        → ClaimController.getClaimsForItem(itemId)
            → Return all claims for that item with claimant info
    ← React shows list of claims with proof text

Reporter clicks Approve / Reject
    → PUT /api/claims/{claimId}/status  { status: "APPROVED" }
        → ClaimController.updateClaimStatus()
            → ClaimService.updateClaimStatus(claimId, status, userId)
                → update claim.setStatus(APPROVED/REJECTED)
                → claimRepository.save(claim)
                → if APPROVED:
                    → item.setStatus("CLAIMED")
                    → itemRepository.save(item)            [UPDATE items SET status='CLAIMED']
                → auditLogService.log(userId, "CLAIM_APPROVED"/"CLAIM_REJECTED", "claims", claimId)
            ← Return updated claim
    ← React shows updated status in the claims table row
```

### Claim Status Flow

```
[PENDING] ──── Reporter Approves ──→ [APPROVED] → Item marked CLAIMED
[PENDING] ──── Reporter Rejects ──→ [REJECTED]
```

---

## 14. Audit Log Workflow

### What is Logged

| Action | Trigger | Entity |
|---|---|---|
| `USER_REGISTERED` | New user registers | users |
| `ITEM_CREATED` | Item is reported | items |
| `ITEM_UPDATED` | Item is edited | items |
| `ITEM_DELETED` | Item is soft deleted | items |
| `CLAIM_SUBMITTED` | User submits a claim | claims |
| `CLAIM_APPROVED` | Reporter approves claim | claims |
| `CLAIM_REJECTED` | Reporter rejects claim | claims |

### AuditLogService Implementation

```java
@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void log(Integer userId, String action, String entityName, Integer entityId) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }
}
```

### Querying Audit Logs

```sql
-- Get all audit logs with user names
SELECT a.id, u.name AS user_name, a.action, a.entity_name, a.entity_id, a.timestamp
FROM audit_logs a
LEFT JOIN users u ON a.user_id = u.id
ORDER BY a.timestamp DESC;

-- Get audit logs for a specific item
SELECT a.*, u.name FROM audit_logs a
LEFT JOIN users u ON a.user_id = u.id
WHERE a.entity_name = 'items' AND a.entity_id = 42
ORDER BY a.timestamp DESC;
```

---

## 15. Search & Filtering Workflow

### Search Flow

```
User types "wallet" in SearchBar
    → GET /api/items?keyword=wallet&page=0&size=10
        → ItemController.getAllItems(keyword, filters, pageable)
            → ItemRepository custom query:
                SELECT * FROM items
                WHERE is_deleted = FALSE
                  AND (title LIKE '%wallet%' OR description LIKE '%wallet%' OR keywords LIKE '%wallet%')
                ORDER BY created_at DESC
                LIMIT 10 OFFSET 0
            ← Returns paginated results
    ← React re-renders ItemTable with filtered rows
```

### Filter Options

```
Filters available in FilterPanel:
    - Status: ALL | LOST | FOUND | CLAIMED
    - Category: dropdown of all categories
    - Color: text input
    - Location: text input
    - Date From / Date To: date pickers
    - Sort By: date_reported DESC | date_reported ASC | title ASC
```

### Combined Filter SQL

```sql
SELECT i.*, c.name AS category_name, u.name AS reporter_name
FROM items i
JOIN categories c ON i.category_id = c.id
JOIN users u ON i.reporter_id = u.id
WHERE i.is_deleted = FALSE
  AND (:status IS NULL OR i.status = :status)
  AND (:categoryId IS NULL OR i.category_id = :categoryId)
  AND (:keyword IS NULL OR i.title LIKE CONCAT('%',:keyword,'%')
                        OR i.description LIKE CONCAT('%',:keyword,'%')
                        OR i.keywords LIKE CONCAT('%',:keyword,'%'))
  AND (:color IS NULL OR i.color = :color)
  AND (:location IS NULL OR i.location LIKE CONCAT('%',:location,'%'))
  AND (:dateFrom IS NULL OR i.date_reported >= :dateFrom)
  AND (:dateTo IS NULL OR i.date_reported <= :dateTo)
ORDER BY i.created_at DESC
LIMIT :size OFFSET :offset;
```

### Spring Data JPA Specification (for dynamic filters)

```java
public class ItemSpecification {
    public static Specification<Item> buildSpec(String keyword, String status,
                                                Integer categoryId, String color) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), false));

            if (keyword != null && !keyword.isEmpty()) {
                String like = "%" + keyword + "%";
                predicates.add(cb.or(
                    cb.like(root.get("title"), like),
                    cb.like(root.get("description"), like),
                    cb.like(root.get("keywords"), like)
                ));
            }
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (categoryId != null) predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            if (color != null) predicates.add(cb.equal(root.get("color"), color));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
```

---

## 16. Export Workflow

### CSV Export

The CSV export button lives directly inside **ItemListPage**, above the item table. There is no separate export page. The current active filters (status, category, keyword) are passed as query parameters so the exported file always reflects what the user is currently viewing.

```
User clicks "Export CSV" button on ItemListPage
    → GET /api/export/csv?status=LOST&categoryId=2
        → ExportController.exportCsv(status, categoryId, response)
            → ExportService.generateCsv(status, categoryId)
                → itemRepository.findByFiltersAndIsDeletedFalse(status, categoryId)
                → Apache Commons CSV: write header + rows to ByteArrayOutputStream
                → return byte stream
        ← Response headers:
             Content-Type: text/csv
             Content-Disposition: attachment; filename="items_export.csv"
    ← Browser downloads CSV file automatically
```

### CSV Column Headers

```
ID, Title, Description, Category, Color, Keywords, Status, Location, Date Reported, Reporter Name
```

### ExportService Implementation

```java
@Service
public class ExportService {

    @Autowired
    private ItemRepository itemRepository;

    public byte[] generateCsv(String status, Integer categoryId) throws IOException {
        // Build specification to respect active filters
        Specification<Item> spec = ItemSpecification.buildSpec(null, status, categoryId, null);
        List<Item> items = itemRepository.findAll(spec);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
            "ID", "Title", "Category", "Color", "Status",
            "Location", "Date Reported", "Reporter"
        ));

        for (Item item : items) {
            printer.printRecord(
                item.getId(),
                item.getTitle(),
                item.getCategory().getName(),
                item.getColor(),
                item.getStatus(),
                item.getLocation(),
                item.getDateReported(),
                item.getReporter().getName()
            );
        }
        printer.flush();
        return out.toByteArray();
    }
}
```

### ExportController Implementation

```java
@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired private ExportService exportService;

    @GetMapping("/csv")
    public void exportCsv(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer categoryId,
            HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"items_export.csv\"");
        byte[] data = exportService.generateCsv(status, categoryId);
        response.getOutputStream().write(data);
    }
}
```

### Frontend Trigger (inside ItemListPage)

```jsx
const handleExport = () => {
    const params = new URLSearchParams();
    if (statusFilter) params.append('status', statusFilter);
    if (categoryFilter) params.append('categoryId', categoryFilter);
    window.open(`http://localhost:8080/api/export/csv?${params.toString()}`, '_blank');
};

// Render:
<button onClick={handleExport}
    className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700">
    Export CSV
</button>
```

---

## 17. Backend API Structure

### Package Responsibilities

| Package | Responsibility |
|---|---|
| `controller` | Receives HTTP requests, validates input, calls services, returns responses |
| `service` | Contains business logic, coordinates between repositories |
| `repository` | Data access layer; Spring Data JPA interfaces |
| `model` | JPA entity classes mapped to database tables |
| `dto` | Data Transfer Objects; shapes of request/response bodies |
| `config` | Configuration beans (CORS, etc.) |

### CORS Configuration

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
```

### application.properties

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lost_found_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
server.port=8080
```

---

## 18. REST API Endpoints

### Auth Endpoints

| Method | URL | Description | Request Body |
|---|---|---|---|
| POST | `/api/auth/register` | Register new user | `{ name, email, password }` |
| POST | `/api/auth/login` | Login user | `{ email, password }` |

### Category Endpoints

| Method | URL | Description |
|---|---|---|
| GET | `/api/categories` | Get all categories |

### Item Endpoints

| Method | URL | Description | Params / Body |
|---|---|---|---|
| GET | `/api/items` | Get all items (paginated + filtered) | `?keyword=&status=&categoryId=&page=&size=` |
| GET | `/api/items/{id}` | Get single item | — |
| POST | `/api/items` | Create new item | `{ title, description, categoryId, color, keywords, status, location, dateReported, reporterId }` |
| PUT | `/api/items/{id}` | Update item | Updated fields |
| DELETE | `/api/items/{id}` | Soft delete item | — |
| GET | `/api/items/my?userId={id}` | Get items reported by a user | — |
| GET | `/api/items/{id}/matches` | Get smart match suggestions | — |

### Claim Endpoints

| Method | URL | Description | Body |
|---|---|---|---|
| POST | `/api/claims` | Submit a claim | `{ itemId, claimantId, proofText }` |
| GET | `/api/claims/item/{itemId}` | Get all claims for an item | — |
| GET | `/api/claims/user/{userId}` | Get claims submitted by user | — |
| PUT | `/api/claims/{id}/status` | Update claim status | `{ status: "APPROVED"/"REJECTED" }` |

### Audit Log Endpoints

| Method | URL | Description |
|---|---|---|
| GET | `/api/audit-logs` | Get all logs (paginated) |
| GET | `/api/audit-logs?userId={id}` | Filter by user |
| GET | `/api/audit-logs?entity=items&entityId={id}` | Filter by entity |

### Export Endpoints

| Method | URL | Description |
|---|---|---|
| GET | `/api/export/csv` | Download CSV of items (supports `?status=&categoryId=` filters) |

### Example API Response

```json
{
  "success": true,
  "message": "Item created successfully",
  "data": {
    "id": 15,
    "title": "Blue Water Bottle",
    "status": "LOST",
    "category": { "id": 7, "name": "Accessories" },
    "reporter": { "id": 1, "name": "Alice Sharma" },
    "location": "Library Block A",
    "dateReported": "2024-06-01",
    "createdAt": "2024-06-01T10:30:00"
  },
  "matches": [
    { "id": 8, "title": "Water Bottle Found", "location": "Library Entrance", "matchScore": 5 }
  ]
}
```

---

## 19. Frontend Page Structure

| Page | Route | Purpose |
|---|---|---|
| `LoginPage` | `/login` | Email + password login form |
| `RegisterPage` | `/register` | New user registration |
| `ItemListPage` | `/items` | Table of all items with search, filter, pagination, CSV export |
| `ItemDetailPage` | `/items/:id` | Full item details + inline edit/delete + claim form |
| `ReportItemPage` | `/items/report` | Form to report a lost or found item + match suggestions |
| `ClaimsPage` | `/claims` | Table of claims submitted by or received by the logged-in user |
| `AuditLogPage` | `/audit-logs` | Paginated table of all system audit events |

> **Note on editing:** Item editing is handled inline on `ItemDetailPage` (a toggle between view mode and edit mode using the same `ItemForm` component) rather than a separate route. This keeps navigation simple.

---

## 20. React Component Structure

### UI Philosophy

This is a **DBMS project**, not a consumer app. The UI goal is clarity and data density — every page should feel like a clean database management interface. Use tables, not cards. Keep forms straightforward. Avoid animations and decorative widgets.

### Component Hierarchy

```
App
├── AuthProvider (context)
│   └── Router
│       ├── LoginPage
│       ├── RegisterPage
│       └── (Auth-guarded Routes)
│           ├── Navbar
│           ├── ItemListPage
│           │   ├── SearchBar
│           │   ├── FilterPanel
│           │   ├── ItemTable           ← tabular display of all items
│           │   └── Pagination
│           ├── ItemDetailPage
│           │   ├── ItemForm            ← reused for inline edit mode
│           │   ├── ClaimForm
│           │   └── MatchSuggestions    ← shown after LOST item created
│           ├── ReportItemPage
│           │   ├── ItemForm
│           │   └── MatchSuggestions
│           ├── ClaimsPage
│           │   └── (inline claims table)
│           └── AuditLogPage
│               └── AuditLogTable
```

### Key Components

#### `ItemTable.jsx`

The primary display component for `ItemListPage`. Shows all items as rows with sortable columns. The "Export CSV" button sits above the table.

```jsx
const ItemTable = ({ items, onDelete }) => (
  <table className="w-full text-sm border-collapse border border-gray-300">
    <thead className="bg-gray-100">
      <tr>
        <th className="border border-gray-300 px-3 py-2 text-left">ID</th>
        <th className="border border-gray-300 px-3 py-2 text-left">Title</th>
        <th className="border border-gray-300 px-3 py-2 text-left">Category</th>
        <th className="border border-gray-300 px-3 py-2 text-left">Status</th>
        <th className="border border-gray-300 px-3 py-2 text-left">Location</th>
        <th className="border border-gray-300 px-3 py-2 text-left">Date</th>
        <th className="border border-gray-300 px-3 py-2 text-left">Reporter</th>
        <th className="border border-gray-300 px-3 py-2 text-left">Actions</th>
      </tr>
    </thead>
    <tbody>
      {items.map(item => (
        <tr key={item.id} className="hover:bg-gray-50">
          <td className="border border-gray-300 px-3 py-2">{item.id}</td>
          <td className="border border-gray-300 px-3 py-2">{item.title}</td>
          <td className="border border-gray-300 px-3 py-2">{item.category.name}</td>
          <td className="border border-gray-300 px-3 py-2">
            <span className={item.status === 'LOST' ? 'text-red-600 font-semibold' :
                             item.status === 'FOUND' ? 'text-green-600 font-semibold' :
                             'text-gray-500'}>
              {item.status}
            </span>
          </td>
          <td className="border border-gray-300 px-3 py-2">{item.location}</td>
          <td className="border border-gray-300 px-3 py-2">{item.dateReported}</td>
          <td className="border border-gray-300 px-3 py-2">{item.reporter.name}</td>
          <td className="border border-gray-300 px-3 py-2">
            <Link to={`/items/${item.id}`} className="text-blue-600 mr-2">View</Link>
            <button onClick={() => onDelete(item.id)} className="text-red-600">Delete</button>
          </td>
        </tr>
      ))}
    </tbody>
  </table>
);
```

#### `SearchBar.jsx`

```jsx
const SearchBar = ({ onSearch }) => {
  const [query, setQuery] = useState('');
  return (
    <input
      type="text"
      className="border border-gray-300 rounded px-3 py-2 w-full"
      placeholder="Search by title, keyword, or description..."
      value={query}
      onChange={(e) => { setQuery(e.target.value); onSearch(e.target.value); }}
    />
  );
};
```

#### `FilterPanel.jsx`

```jsx
const FilterPanel = ({ categories, onFilter }) => {
  const [status, setStatus] = useState('');
  const [categoryId, setCategoryId] = useState('');

  const apply = () => onFilter({ status, categoryId });

  return (
    <div className="flex gap-3 items-end flex-wrap">
      <div>
        <label className="block text-xs text-gray-600 mb-1">Status</label>
        <select className="border border-gray-300 rounded px-2 py-2"
                value={status} onChange={e => setStatus(e.target.value)}>
          <option value="">All</option>
          <option value="LOST">LOST</option>
          <option value="FOUND">FOUND</option>
          <option value="CLAIMED">CLAIMED</option>
        </select>
      </div>
      <div>
        <label className="block text-xs text-gray-600 mb-1">Category</label>
        <select className="border border-gray-300 rounded px-2 py-2"
                value={categoryId} onChange={e => setCategoryId(e.target.value)}>
          <option value="">All</option>
          {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
        </select>
      </div>
      <button onClick={apply}
              className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700">
        Apply
      </button>
    </div>
  );
};
```

#### `Pagination.jsx`

```jsx
const Pagination = ({ currentPage, totalPages, onPageChange }) => (
  <div className="flex gap-2 justify-center mt-4">
    <button onClick={() => onPageChange(currentPage - 1)}
            disabled={currentPage === 0}
            className="px-3 py-1 border rounded disabled:opacity-40">
      ← Prev
    </button>
    <span className="px-3 py-1 text-sm text-gray-600">
      Page {currentPage + 1} of {totalPages}
    </span>
    <button onClick={() => onPageChange(currentPage + 1)}
            disabled={currentPage === totalPages - 1}
            className="px-3 py-1 border rounded disabled:opacity-40">
      Next →
    </button>
  </div>
);
```

#### `MatchSuggestions.jsx`

Shown below the success message after a LOST item is submitted. Renders a minimal table of potential matching FOUND items.

```jsx
const MatchSuggestions = ({ matches }) => {
  if (!matches || matches.length === 0)
    return <p className="text-sm text-gray-500 mt-4">No potential matches found yet.</p>;

  return (
    <div className="mt-6">
      <h3 className="font-semibold text-gray-700 mb-2">Possible Matches</h3>
      <table className="w-full text-sm border-collapse border border-gray-300">
        <thead className="bg-yellow-50">
          <tr>
            <th className="border border-gray-300 px-3 py-2 text-left">Title</th>
            <th className="border border-gray-300 px-3 py-2 text-left">Location</th>
            <th className="border border-gray-300 px-3 py-2 text-left">Date</th>
            <th className="border border-gray-300 px-3 py-2 text-left">Score</th>
            <th className="border border-gray-300 px-3 py-2"></th>
          </tr>
        </thead>
        <tbody>
          {matches.map(m => (
            <tr key={m.id}>
              <td className="border border-gray-300 px-3 py-2">{m.title}</td>
              <td className="border border-gray-300 px-3 py-2">{m.location}</td>
              <td className="border border-gray-300 px-3 py-2">{m.dateReported}</td>
              <td className="border border-gray-300 px-3 py-2">{m.matchScore}</td>
              <td className="border border-gray-300 px-3 py-2">
                <Link to={`/items/${m.id}`} className="text-blue-600 text-xs">View →</Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};
```

---

## 21. Spring Boot Architecture

### Layer-by-Layer Explanation

```
HTTP Request
     ↓
@RestController (ItemController)
     ↓
@Service (ItemService)          ← Business logic lives here
     ↓
@Repository (ItemRepository)   ← JPA query lives here
     ↓
JPA / Hibernate
     ↓
MySQL Database
```

### ItemController (Example)

```java
@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired private ItemService itemService;

    @GetMapping
    public ResponseEntity<?> getAllItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Item> result = itemService.getAllItems(keyword, status, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> createItem(@Valid @RequestBody ItemRequest request) {
        Item item = itemService.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Integer id, @RequestBody ItemRequest request) {
        Item updated = itemService.updateItem(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Integer id, @RequestParam Integer userId) {
        itemService.softDeleteItem(id, userId);
        return ResponseEntity.ok(Map.of("message", "Item deleted"));
    }
}
```

---

## 22. JPA/Hibernate Usage

### Entity Example: Item.java

```java
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private String color;
    private String keywords;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus status;   // LOST, FOUND, CLAIMED

    private String location;
    private LocalDate dateReported;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Getters and Setters
}
```

### Repository Example

```java
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer>,
                                         JpaSpecificationExecutor<Item> {

    // Find active items by status
    List<Item> findByStatusAndIsDeletedFalse(ItemStatus status);

    // Find items by reporter
    Page<Item> findByReporterIdAndIsDeletedFalse(Integer reporterId, Pageable pageable);

    // Find single active item
    Optional<Item> findByIdAndIsDeletedFalse(Integer id);

    // Custom JPQL query for matching
    @Query("SELECT i FROM Item i WHERE i.status = 'FOUND' AND i.isDeleted = false " +
           "AND (i.category.id = :catId OR i.color = :color OR i.location LIKE %:loc%)")
    List<Item> findPotentialMatches(@Param("catId") Integer catId,
                                   @Param("color") String color,
                                   @Param("loc") String location);
}
```

### JPA Relationship Annotations

```java
// In User.java
@OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<Item> reportedItems;

// In Item.java
@OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
private List<Claim> claims;

// In Claim.java
@ManyToOne
@JoinColumn(name = "item_id")
private Item item;

@ManyToOne
@JoinColumn(name = "claimant_id")
private User claimant;
```

---

## 23. Validation Rules

### Backend Validation (Jakarta Bean Validation)

```java
// ItemRequest.java DTO
public class ItemRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be 3-200 characters")
    private String title;

    @Size(max = 2000)
    private String description;

    @NotNull(message = "Category is required")
    private Integer categoryId;

    @NotBlank(message = "Status must be LOST or FOUND")
    @Pattern(regexp = "LOST|FOUND", message = "Status must be LOST or FOUND")
    private String status;

    @NotNull(message = "Reporter ID is required")
    private Integer reporterId;

    @NotNull(message = "Date reported is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate dateReported;
}

// RegisterRequest.java DTO
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
```

### Frontend Validation (React)

```javascript
const validateItemForm = (form) => {
    const errors = {};
    if (!form.title || form.title.trim().length < 3)
        errors.title = "Title must be at least 3 characters";
    if (!form.categoryId)
        errors.categoryId = "Please select a category";
    if (!form.status)
        errors.status = "Please select Lost or Found";
    if (!form.dateReported)
        errors.dateReported = "Date is required";
    return errors;
};
```

---

## 24. Soft Delete Logic

### What is Soft Delete?

Instead of physically removing a row from the database with `DELETE FROM items WHERE id=?`, soft delete sets a flag `is_deleted = TRUE`. The record remains in the database but is hidden from all normal queries.

### Why Use Soft Delete?

- **Audit trail**: The item still exists for reference in audit_logs, claims, etc.
- **Data recovery**: Accidental deletes can be reversed.
- **Referential integrity**: Claims referencing a "deleted" item won't break.

### Implementation

```java
// Service method
public void softDeleteItem(Integer itemId, Integer userId) {
    Item item = itemRepository.findByIdAndIsDeletedFalse(itemId)
        .orElseThrow(() -> new RuntimeException("Item not found"));
    item.setIsDeleted(true);
    itemRepository.save(item);
    auditLogService.log(userId, "ITEM_DELETED", "items", itemId);
}
```

```sql
-- What happens in the database
UPDATE items SET is_deleted = TRUE, updated_at = NOW() WHERE id = 15;
```

```java
// All queries exclude deleted items
Optional<Item> findByIdAndIsDeletedFalse(Integer id);
Page<Item> findAllByIsDeletedFalse(Pageable pageable);
```

---

## 25. Pagination Logic

### Why Pagination?

When there are hundreds of items, loading all at once is slow and wasteful. Pagination loads a fixed number per page (e.g., 10 items per page).

### Spring Boot Pageable

```java
// Controller
@GetMapping
public ResponseEntity<?> getAllItems(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<Item> result = itemRepository.findAllByIsDeletedFalse(pageable);
    return ResponseEntity.ok(result);
}
```

### SQL Equivalent

```sql
-- Page 0 (first page), 10 items per page
SELECT * FROM items WHERE is_deleted = FALSE ORDER BY created_at DESC LIMIT 10 OFFSET 0;

-- Page 1 (second page)
SELECT * FROM items WHERE is_deleted = FALSE ORDER BY created_at DESC LIMIT 10 OFFSET 10;

-- Page 2 (third page)
SELECT * FROM items WHERE is_deleted = FALSE ORDER BY created_at DESC LIMIT 10 OFFSET 20;
```

### Frontend Pagination

```javascript
const [page, setPage] = useState(0);
const [totalPages, setTotalPages] = useState(0);

useEffect(() => {
    axios.get(`/api/items?page=${page}&size=10`)
        .then(res => {
            setItems(res.data.content);
            setTotalPages(res.data.totalPages);
        });
}, [page]);
```

---

## 26. SQL Concepts Used

| SQL Concept | Where Used |
|---|---|
| `CREATE TABLE` | Schema setup |
| `INSERT INTO` | Save new records |
| `SELECT` | Retrieve records |
| `UPDATE` | Edit records |
| `ENUM` | Restrict status values |
| `DEFAULT` | Auto-set timestamps, is_deleted |
| `FOREIGN KEY` | Enforce referential integrity |
| `UNIQUE` | Prevent duplicate emails, category names |
| `NOT NULL` | Enforce required fields |
| `AUTO_INCREMENT` | Auto-generate primary keys |
| `JOIN` | Combine data from multiple tables |
| `LEFT JOIN` | Include records with no match (audit logs with deleted users) |
| `LIKE` | Pattern-based keyword search |
| `BETWEEN` | Date range filtering |
| `ORDER BY` | Sort results |
| `LIMIT / OFFSET` | Pagination |
| `GROUP BY` | Aggregate queries (stats by category) |
| `COUNT`, `MAX`, `MIN` | Aggregate functions |
| `ON UPDATE CURRENT_TIMESTAMP` | Auto-update timestamps |
| `ON DELETE SET NULL` | Preserve audit logs when user is deleted |
| `CASE WHEN` | Conditional scoring in matching query |
| `CONCAT` | Build LIKE patterns dynamically |

---

## 27. Important JOIN Queries

### 1. Get all items with category name and reporter name

```sql
SELECT i.id, i.title, i.status, i.location, i.date_reported,
       c.name AS category_name,
       u.name AS reporter_name
FROM items i
JOIN categories c ON i.category_id = c.id
JOIN users u ON i.reporter_id = u.id
WHERE i.is_deleted = FALSE
ORDER BY i.created_at DESC;
```

### 2. Get all claims with claimant name and item title

```sql
SELECT cl.id, cl.proof_text, cl.status, cl.created_at,
       u.name AS claimant_name,
       i.title AS item_title,
       i.status AS item_status
FROM claims cl
JOIN users u ON cl.claimant_id = u.id
JOIN items i ON cl.item_id = i.id
ORDER BY cl.created_at DESC;
```

### 3. Get all audit logs with user names (LEFT JOIN to handle deleted users)

```sql
SELECT a.id, a.action, a.entity_name, a.entity_id, a.timestamp,
       COALESCE(u.name, 'Deleted User') AS performed_by
FROM audit_logs a
LEFT JOIN users u ON a.user_id = u.id
ORDER BY a.timestamp DESC;
```

### 4. Get items with their total claim counts

```sql
SELECT i.id, i.title, i.status, COUNT(cl.id) AS total_claims
FROM items i
LEFT JOIN claims cl ON i.id = cl.item_id
WHERE i.is_deleted = FALSE
GROUP BY i.id, i.title, i.status
ORDER BY total_claims DESC;
```

### 5. Get a user's reported items and their claim status

```sql
SELECT i.id, i.title, i.status AS item_status,
       cl.id AS claim_id, cl.status AS claim_status,
       u2.name AS claimant_name
FROM items i
LEFT JOIN claims cl ON i.id = cl.item_id
LEFT JOIN users u2 ON cl.claimant_id = u2.id
WHERE i.reporter_id = 1
  AND i.is_deleted = FALSE;
```

---

## 28. Aggregate Queries

### System Statistics

These queries power the summary counts shown on `ItemListPage` (above the table, as plain text such as "Total: 85 items — Lost: 42 · Found: 28 · Claimed: 15") and can also be used to verify data during viva.

```sql
-- Total items by status
SELECT status, COUNT(*) AS total
FROM items
WHERE is_deleted = FALSE
GROUP BY status;

-- Result:
-- LOST    | 42
-- FOUND   | 28
-- CLAIMED | 15
```

```sql
-- Items per category
SELECT c.name AS category, COUNT(i.id) AS total_items
FROM categories c
LEFT JOIN items i ON c.id = i.category_id AND i.is_deleted = FALSE
GROUP BY c.id, c.name
ORDER BY total_items DESC;
```

```sql
-- Claims by status
SELECT status, COUNT(*) AS count
FROM claims
GROUP BY status;
```

```sql
-- Most active reporters
SELECT u.name, COUNT(i.id) AS items_reported
FROM users u
JOIN items i ON u.id = i.reporter_id
WHERE i.is_deleted = FALSE
GROUP BY u.id, u.name
ORDER BY items_reported DESC
LIMIT 5;
```

```sql
-- Items reported in the last 7 days
SELECT COUNT(*) AS recent_reports
FROM items
WHERE date_reported >= CURDATE() - INTERVAL 7 DAY
  AND is_deleted = FALSE;
```

```sql
-- Claim approval rate
SELECT
  COUNT(*) AS total_claims,
  SUM(CASE WHEN status = 'APPROVED' THEN 1 ELSE 0 END) AS approved,
  SUM(CASE WHEN status = 'REJECTED' THEN 1 ELSE 0 END) AS rejected,
  SUM(CASE WHEN status = 'PENDING'  THEN 1 ELSE 0 END) AS pending
FROM claims;
```

---

## 29. Constraints Used

| Constraint | Table | Column | Purpose |
|---|---|---|---|
| `PRIMARY KEY` | All | `id` | Unique identifier for each row |
| `AUTO_INCREMENT` | All | `id` | Auto-generate IDs |
| `NOT NULL` | items | `title`, `status`, `reporter_id`, `date_reported` | Mandatory fields |
| `NOT NULL` | users | `name`, `email`, `password` | Mandatory registration fields |
| `UNIQUE` | users | `email` | One account per email |
| `UNIQUE` | categories | `name` | No duplicate category names |
| `FOREIGN KEY` | items | `category_id` → categories(id) | Referential integrity |
| `FOREIGN KEY` | items | `reporter_id` → users(id) | Referential integrity |
| `FOREIGN KEY` | claims | `item_id` → items(id) | Referential integrity |
| `FOREIGN KEY` | claims | `claimant_id` → users(id) | Referential integrity |
| `FOREIGN KEY` | audit_logs | `user_id` → users(id) ON DELETE SET NULL | Preserve logs even if user deleted |
| `ENUM` | items | `status` | Only LOST, FOUND, CLAIMED allowed |
| `ENUM` | claims | `status` | Only PENDING, APPROVED, REJECTED allowed |
| `DEFAULT FALSE` | items | `is_deleted` | New items not deleted by default |
| `DEFAULT CURRENT_TIMESTAMP` | All | `created_at` | Auto-timestamp on insert |
| `ON UPDATE CURRENT_TIMESTAMP` | items | `updated_at` | Auto-update on row change |

---

## 30. Indexing Suggestions

### Why Indexing?

Without indexes, MySQL does a full table scan for every query. Indexes let MySQL jump directly to relevant rows, making searches much faster.

### Recommended Indexes

```sql
-- Speed up filtering by status (very common query)
CREATE INDEX idx_items_status ON items(status);

-- Speed up soft-delete filtering
CREATE INDEX idx_items_is_deleted ON items(is_deleted);

-- Speed up lookup of items by reporter
CREATE INDEX idx_items_reporter ON items(reporter_id);

-- Speed up category filtering
CREATE INDEX idx_items_category ON items(category_id);

-- Speed up date-based filtering
CREATE INDEX idx_items_date ON items(date_reported);

-- Composite index for most common query pattern
CREATE INDEX idx_items_status_deleted ON items(status, is_deleted);

-- Speed up claim lookups by item
CREATE INDEX idx_claims_item ON claims(item_id);

-- Speed up claim lookups by user
CREATE INDEX idx_claims_claimant ON claims(claimant_id);

-- Speed up audit log queries by entity
CREATE INDEX idx_audit_entity ON audit_logs(entity_name, entity_id);

-- Speed up audit log queries by user
CREATE INDEX idx_audit_user ON audit_logs(user_id);
```

### Index Explanation for Viva

> An index is like a book's index — instead of reading every page, you jump directly to the right page. MySQL builds a B-tree data structure for indexed columns. The trade-off is slightly slower INSERT/UPDATE (index must be updated too) but much faster SELECT. We add indexes on columns that appear in WHERE, JOIN ON, and ORDER BY clauses.

---

## 31. Future Scope

| Feature | Description |
|---|---|
| **Image Upload** | Allow users to attach photos of lost/found items |
| **Role-based Access** | Admin role to manage all items and approve/reject claims |
| **Email Notifications** | Notify claimant when claim is approved/rejected |
| **JWT Authentication** | Stateless, secure auth instead of session-based |
| **Advanced Matching** | Natural Language Processing to improve smart matching |
| **QR Code Generation** | Print a QR code for each found item |
| **Mobile App** | React Native app for mobile access |
| **Real-time Alerts** | WebSocket-based notifications for new matches |
| **Analytics Dashboard** | Charts and graphs for item trends over time |
| **Cloud Deployment** | Host on AWS/GCP with Docker containers |

---

## 32. Viva Explanation Notes

### Common Viva Questions and Answers

**Q: What is normalization? How have you applied it?**

> Normalization is the process of organizing a database to reduce data redundancy and improve integrity. In this project:
> - **1NF**: All columns hold atomic values. `keywords` is a comma-separated string (acceptable simplification).
> - **2NF**: No partial dependencies. `categories` is a separate table — the category name is not repeated in every item row.
> - **3NF**: No transitive dependencies. User details are in the `users` table, not duplicated in `items` or `claims`.

**Q: What is a foreign key? Where have you used it?**

> A foreign key is a column in one table that references the primary key of another table, enforcing referential integrity. Example: `items.category_id` references `categories.id`. This means you cannot insert an item with a `category_id` that doesn't exist in the `categories` table.

**Q: What is soft delete? Why not use hard delete?**

> Soft delete sets a boolean flag (`is_deleted = TRUE`) instead of physically removing the row. We use it because: (1) the item may still be referenced by claims or audit logs, (2) data can be recovered if deleted accidentally, and (3) it preserves the audit trail.

**Q: Explain the claim workflow.**

> A user sees a found item that might be theirs. They submit a claim with proof text. The claim status starts as PENDING. The person who reported the found item reviews it and either APPROVES or REJECTS the claim. If approved, the item's status automatically changes to CLAIMED. Every step is logged in audit_logs.

**Q: What is an ENUM type? Where have you used it?**

> ENUM is a MySQL data type that restricts a column to a predefined set of values. We use it for `items.status` (LOST, FOUND, CLAIMED) and `claims.status` (PENDING, APPROVED, REJECTED). This prevents invalid data at the database level.

**Q: What is pagination? How is it implemented?**

> Pagination divides large result sets into pages. SQL uses `LIMIT` and `OFFSET`. Spring Boot uses the `Pageable` interface and `Page<T>` return type. The frontend sends `?page=0&size=10` as query parameters, and the backend returns metadata like `totalPages` and `totalElements` along with the current page's data.

**Q: What is an audit log? Why is it important?**

> An audit log is a record of every important action in the system — who did what, when, and to which record. It provides accountability and traceability. In our project, every item creation, update, deletion, and claim action is logged in the `audit_logs` table. We use `LEFT JOIN` to query logs even if the user was later deleted.

**Q: How does smart matching work?**

> When a LOST item is reported, the system queries the database for FOUND items that share similar attributes: same category, same color, similar keywords, or same location. Results are scored — category match scores highest, location match scores less — and the top 5 matches are returned to the user as suggestions.

---

## 33. Key DBMS Concepts Demonstrated

| Concept | Implementation in Project |
|---|---|
| **Relational Model** | 5 normalized tables with defined relationships |
| **Primary Keys** | Auto-increment `id` in all tables |
| **Foreign Keys** | items→categories, items→users, claims→items, claims→users |
| **Referential Integrity** | FK constraints prevent orphan records |
| **Normalization (1NF, 2NF, 3NF)** | Categories separated, no repeated data |
| **CRUD Operations** | Full Create, Read, Update, Delete on all entities |
| **Soft Delete** | `is_deleted` flag instead of physical DELETE |
| **Transactions** | Service methods that update multiple tables (claim approval updates both claims and items) |
| **ENUM Constraint** | status columns restricted to valid values |
| **Indexes** | Applied on frequently queried columns |
| **Pagination** | LIMIT/OFFSET for large result sets |
| **Aggregate Functions** | COUNT, SUM, GROUP BY for summary counts on ItemListPage |
| **JOINs** | INNER JOIN, LEFT JOIN across multiple tables |
| **Pattern Matching** | LIKE operator for keyword search |
| **Date Functions** | CURDATE(), INTERVAL, date range filtering |
| **Audit Trail** | Dedicated audit_logs table for event history |
| **ORM Mapping** | JPA/Hibernate entity-to-table mapping |
| **Cascade Operations** | JPA cascade on relationships |
| **Lazy/Eager Loading** | FetchType control in JPA |

---

## 34. Development Phases

> This plan is designed for **2 students** working part-time. Each phase is focused and manageable. Backend and DBMS work is the priority throughout.

### Phase 1: Setup (Week 1)

- Create MySQL database, run all `CREATE TABLE` statements, insert seed data.
- Initialize Spring Boot project via [start.spring.io](https://start.spring.io) with: Spring Web, Spring Data JPA, MySQL Driver, Validation, Lombok.
- Initialize React project, install Tailwind CSS, Axios, React Router DOM.
- Configure CORS in Spring Boot (`CorsConfig.java`).
- Verify DB connection via `spring.jpa.show-sql=true` — confirm Hibernate logs queries.

### Phase 2: Auth + Users (Week 1–2)

- Build `User` entity, `UserRepository`, `UserService`.
- Build `POST /api/auth/register` and `POST /api/auth/login` endpoints.
- Build `LoginPage` and `RegisterPage` in React.
- Implement `AuthContext.js` with `localStorage` for session persistence.
- Test registration and login with Postman before wiring to React.

### Phase 3: Item Management (Week 2–3)

- Build `Category` and `Item` entities, repositories, services.
- Build all item CRUD endpoints: GET list (paginated), GET single, POST, PUT, DELETE (soft).
- Build `ItemListPage` with `ItemTable`, `SearchBar`, `FilterPanel`, `Pagination`.
- Build `ReportItemPage` with `ItemForm`.
- Build `ItemDetailPage` with inline edit toggle and soft delete button.
- Verify soft delete: deleted rows disappear from list but remain in DB.

### Phase 4: Search & Filtering (Week 3)

- Implement `ItemSpecification` (JPA Specification) for dynamic multi-filter queries.
- Wire up `SearchBar` and `FilterPanel` to pass query parameters to `GET /api/items`.
- Verify that LIMIT/OFFSET pagination works correctly with filters active.
- Run the combined filter SQL query manually in MySQL Workbench to demonstrate understanding.

### Phase 5: Smart Matching + Claims (Week 4)

- Build `MatchingService` with the CASE WHEN scoring query.
- After LOST item is submitted, call `GET /api/items/{id}/matches` and show `MatchSuggestions` table.
- Build `Claim` entity, `ClaimRepository`, `ClaimService`.
- Build claim endpoints: POST submit, GET by item, GET by user, PUT status update.
- Build `ClaimsPage` as a table showing both submitted claims and claims on the user's reported items.
- Build `ClaimForm` on `ItemDetailPage`.
- Verify that approving a claim atomically updates both `claims.status` and `items.status`.

### Phase 6: Audit Logs + CSV Export (Week 5)

- Build `AuditLog` entity, `AuditLogRepository`, `AuditLogService`.
- Wire `auditLogService.log(...)` into every service method that creates/updates/deletes data.
- Build `GET /api/audit-logs` endpoint with pagination.
- Build `AuditLogPage` using `AuditLogTable`.
- Build `GET /api/export/csv` endpoint using Apache Commons CSV.
- Add the **Export CSV** button to `ItemListPage` — it must respect active filters.

### Phase 7: Testing & Viva Prep (Week 6)

- End-to-end test: register → report LOST → view matches → claim FOUND item → approve → verify CLAIMED status.
- Run and review all important SQL queries directly in MySQL Workbench.
- Review audit log table to confirm all events are logged.
- Polish table styling with Tailwind for a clean, professional look.
- Prepare viva answers from Section 32 of this document.

---

## 35. Step-by-Step Build Plan

### Step 1: Database Setup

```bash
mysql -u root -p
CREATE DATABASE lost_found_db;
USE lost_found_db;
-- Run the CREATE TABLE statements from Section 5
-- Run the INSERT seed statements
```

### Step 2: Spring Boot Initialization

```bash
# Go to start.spring.io or use IntelliJ
# Add dependencies: Spring Web, Spring Data JPA, MySQL Driver, Validation, Lombok
# Set application.properties (Section 17)
```

### Step 3: Create Entity Classes

```
User.java → map to users table
Category.java → map to categories table
Item.java → map to items table (with @ManyToOne for category and reporter)
Claim.java → map to claims table
AuditLog.java → map to audit_logs table
```

### Step 4: Create Repositories

```
UserRepository extends JpaRepository<User, Integer>
ItemRepository extends JpaRepository<Item, Integer>, JpaSpecificationExecutor<Item>
ClaimRepository extends JpaRepository<Claim, Integer>
AuditLogRepository extends JpaRepository<AuditLog, Integer>
CategoryRepository extends JpaRepository<Category, Integer>
```

### Step 5: Create Service Classes

```
UserService → register(), login()
ItemService → createItem(), getAllItems(), getItemById(), updateItem(), softDeleteItem()
ClaimService → submitClaim(), getClaimsForItem(), updateClaimStatus()
AuditLogService → log()
MatchingService → findMatches()
ExportService → generateCsv()
```

### Step 6: Create Controllers

```
AuthController → POST /api/auth/register, POST /api/auth/login
ItemController → GET/POST/PUT/DELETE /api/items
ClaimController → POST/GET/PUT /api/claims
AuditLogController → GET /api/audit-logs
CategoryController → GET /api/categories
ExportController → GET /api/export/csv
```

### Step 7: React Setup

```bash
npx create-react-app lost-found-frontend
cd lost-found-frontend
npm install tailwindcss axios react-router-dom
npx tailwindcss init
# Configure tailwind.config.js and index.css
```

### Step 8: Build React Pages

```
LoginPage     → form + POST /api/auth/login
RegisterPage  → form + POST /api/auth/register
ItemListPage  → GET /api/items with search/filter/pagination + Export CSV button
ReportItemPage→ POST /api/items + show MatchSuggestions table below form
ItemDetailPage→ GET /api/items/:id + inline edit (ItemForm toggle) + ClaimForm
ClaimsPage    → GET /api/claims/user/:id (submitted) + GET /api/claims/item/:id (received)
AuditLogPage  → GET /api/audit-logs with pagination
```

### Step 9: Wire Up Search + Filter

```
SearchBar → onChange triggers GET /api/items?keyword=...
FilterPanel → dropdowns/inputs trigger GET /api/items?status=&categoryId=...
Pagination → page buttons trigger GET /api/items?page=N
```

### Step 10: Final Polish

```
- Add consistent error handling (try/catch in React, @ExceptionHandler in Spring Boot)
- Add loading states (useState isLoading) on all data-fetching pages
- Add form validation error messages in ItemForm and ClaimForm
- Test the complete claim workflow end-to-end
- Run audit log queries and verify all actions are being logged
- Test CSV export with and without active filters
- Prepare project demo and run sample SQL queries for viva
```

---

*This workflow document covers the complete design, implementation, and database concepts for the Lost & Found Management System DBMS mini project. Use it as the primary reference during development and as a study guide for viva explanations.*
