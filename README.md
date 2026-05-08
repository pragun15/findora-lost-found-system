# 🔍 Findora - Lost and Found Management System

Findora is a web-based **Lost and Found Management System** designed to help users report, search, and recover lost items efficiently. The platform provides a centralized system where users can post details about lost or found items, making it easier to reconnect owners with their belongings.

The system supports secure authentication, item management, search functionality, and an admin panel for managing reports and users.

---

## 📌 Features

### 👤 User Authentication
- User Registration and Login
- Secure Authentication System
- User Profile Management

### 📦 Lost & Found Item Management
- Report Lost Items
- Report Found Items
- Upload Item Details
- Add Item Descriptions and Categories
- Update or Delete Posts

### 🔎 Search & Filter
- Search lost or found items
- Filter by:
  - Category
  - Item Name
  - Date
  - Location

### 🛠️ Admin Features
- Manage Users
- Manage Lost/Found Listings
- Remove Spam or Invalid Reports
- Monitor System Activity

### 📱 Responsive Design
- Mobile-friendly UI
- Easy Navigation
- Clean Dashboard Interface

---

## 🛠️ Tech Stack

### Frontend
- HTML5
- CSS3
- JavaScript

### Backend
- Node.js
- Express.js

### Database
- MySQL

### Other Tools
- REST API
- Authentication System
- CRUD Operations

---

## 📂 Project Structure

```bash
findora-lost-found-system/
│── backend/
│   ├── routes/
│   ├── controllers/
│   ├── models/
│   ├── config/
│   └── server.js
│
│── frontend/
│   ├── css/
│   ├── js/
│   ├── images/
│   └── index.html
│
│── database/
│   └── schema.sql
│
│── README.md
│── package.json
│── workflow.md
```

---

## ⚙️ System Workflow

### Step 1: User Registration/Login
Users create an account or log in to access system features.

### Step 2: Report Item
Users can:
- Post a **Lost Item**
- Post a **Found Item**

Each report includes:
- Item Name
- Description
- Category
- Date
- Location
- Contact Information
- Image Upload (Optional)

### Step 3: Search Items
Users search for lost/found items using keywords or filters.

### Step 4: Match & Recovery
If a matching item is found:
- Users can contact the owner/finder
- Verification takes place
- Item is returned

### Step 5: Admin Monitoring
Admin manages:
- Fake reports
- User accounts
- System records

---

## 🗄️ Database Design

The system uses **MySQL Database** for storing:

### Users Table
Stores:
- User ID
- Name
- Email
- Password
- Role

### Lost Items Table
Stores:
- Item Details
- Category
- Description
- Date Lost
- Location

### Found Items Table
Stores:
- Item Details
- Found Date
- Location
- Finder Information

---

## 🚀 Installation Guide

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/pragun15/findora-lost-found-system.git
```

### 2️⃣ Navigate to Project Directory

```bash
cd findora-lost-found-system
```

### 3️⃣ Install Dependencies

```bash
npm install
```

### 4️⃣ Setup Database

- Open MySQL
- Create a database

```sql
CREATE DATABASE findora_db;
```

- Import the schema:

```sql
source schema.sql;
```

### 5️⃣ Configure Environment Variables

Create a `.env` file:

```env
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=yourpassword
DB_NAME=findora_db
PORT=5000
JWT_SECRET=your_secret_key
```

### 6️⃣ Start Backend Server

```bash
npm start
```

### 7️⃣ Open Frontend

Open:

```bash
index.html
```

in your browser.

---

## 🧪 CRUD Operations

### Create
- Add lost item
- Add found item
- Register user

### Read
- View reports
- Search items
- View profiles

### Update
- Edit item details
- Update profile information

### Delete
- Remove item reports
- Delete user account
- Admin delete functionality

---

## 🎯 Objectives of the Project

- To simplify the process of reporting lost and found items.
- To reduce the time required to recover misplaced belongings.
- To provide a secure and organized tracking system.
- To improve communication between owners and finders.

---

## 🔐 Security Features

- Password Encryption
- Authentication & Authorization
- Protected Routes
- Data Validation

---

## 📸 Screenshots

Add screenshots here:

### Home Page
![Home](images/home.png)

### Login Page
![Login](images/login.png)

### Dashboard
![Dashboard](images/dashboard.png)

---

## 🔮 Future Enhancements

- AI-based item matching
- Email notifications
- OTP verification
- Chat system between users
- Mobile application support
- GPS-based lost item tracking

---

## 👨‍💻 Contributors

**Pragnya Meharwade**  
Project Developer

---

## 📜 License

This project is developed for educational and learning purposes.

---

## ⭐ Support

If you like this project, consider giving it a **star ⭐** on GitHub.
