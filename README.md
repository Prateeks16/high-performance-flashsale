# ⚡ High-Performance Flash Sale API

A robust backend system designed to handle high-concurrency traffic for limited-inventory events (Flash Sales). Built with **Spring Boot** and **PostgreSQL**, focusing on data consistency and race condition handling.

## 📖 Overview
In high-demand e-commerce scenarios (like a Black Friday sale), thousands of users may attempt to purchase the same item simultaneously. Without proper concurrency control, this leads to **overselling** (selling stock that doesn't exist).

This project demonstrates:
1.  **The Vulnerability:** How a standard "Read-Modify-Write" operation fails under load.
2.  **The Fix:** Implementing **Optimistic Locking** to ensure 100% data integrity.
3.  **The Proof:** Load testing verification using **Apache JMeter**.

## 🛑 The Engineering Problem (Race Conditions)
When multiple threads try to buy the last item at the exact same millisecond:
1.  **Thread A** reads stock: `10`.
2.  **Thread B** reads stock: `10` (before Thread A saves).
3.  **Thread A** updates stock to `9`.
4.  **Thread B** updates stock to `9`.
* **Result:** Two items were sold, but the database only recorded a decrement of 1.
* **Real-world Consequence:** Financial loss, negative inventory, and customer frustration.

## ✅ The Solution: Optimistic Locking
I resolved this using JPA's `@Version` mechanism:
- A version number is added to the `products` table.
- Before updating, the database checks if the version matches the one read at the start of the transaction.
- If the version has changed (meaning another user bought the item), the transaction is rejected with `ObjectOptimisticLockingFailureException`.
- **Outcome:** The system prioritizes **Consistency** over Availability, ensuring we never sell what we don't have.

## 🛠️ Tech Stack
- **Language:** Java 21
- **Framework:** Spring Boot 3 (Web, Data JPA)
- **Database:** PostgreSQL
- **Testing:** Apache JMeter (for Stress/Load Testing)
- **Tools:** Postman, Maven, Lombok

## 📊 Performance & Stress Testing Results
I simulated a traffic spike of **300 concurrent users** attempting to buy an item with only **100 units** in stock.

| Metric | Phase 1: Without Locking (Vulnerable) | Phase 2: With Optimistic Locking (Fixed) |
| :--- | :--- | :--- |
| **Initial Stock** | 100 | 100 |
| **Concurrent Users** | 300 | 300 |
| **Total Orders Recorded** | **140** (Oversold by 40) ❌ | **91** (Safe) ✅ |
| **Final Inventory** | **-40** (Data Corruption) | **9** (Data Consistent) |
| **Result** | ⚠️ Critical Failure | 🛡️ Success |

*> Note: The "Fixed" scenario resulted in 91 sales because strictly conflicting transactions were rejected to preserve data integrity.*

## 🚀 How to Run Locally

### 1. Prerequisites
- Java 17+
- PostgreSQL
- Maven

### 2. Database Setup
Create a PostgreSQL database named `flashsale`.
```sql
CREATE DATABASE flashsale;

```

### 3. Clone & Configure

```bash
git clone https://github.com/Prateeks16/high-performance-flashsale
cd high-performance-flashsale

```

Open `src/main/resources/application.properties` and update your database credentials:

```properties
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

```

### 4. Run the Application

```bash
./mvnw spring-boot:run

```

## 🔌 API Endpoints

### 1. Add a Product (Admin)

**POST** `/api/products`

```json
{
    "name": "Rolex Watch",
    "price": 5000.00,
    "quantity": 100
}

```

### 2. Buy a Product (User)

**POST** `/api/purchase/{productId}?userId={userId}`

* **Example:** `http://localhost:8080/api/purchase/1?userId=101`
* **Response (Success):** `Purchase Successful! Order ID: 1`
* **Response (Fail):** `Sold Out!` or `Transaction Failed` (under load)
