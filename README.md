# ⚡ High-Performance Flash Sale API

A scalable backend system designed to handle high-concurrency traffic for limited-inventory "Flash Sale" events. Built with **Spring Boot** and **PostgreSQL**.

## 🛑 The Problem (Race Conditions)
In a traditional e-commerce system, when thousands of users click "Buy" simultaneously on the last item, standard logic fails.
- **Scenario:** 2 users buy the last item at the exact same millisecond.
- **Result:** The database records 2 sales for 1 item. Inventory becomes negative.
- **Impact:** Financial loss and overselling.

## ✅ The Solution (Optimistic Locking)
This project implements **Optimistic Locking** using JPA's `@Version` annotation.
- The database tracks a version number for every product.
- If two threads try to update the same row, the database rejects the second transaction with `ObjectOptimisticLockingFailureException`.
- **Outcome:** Data consistency is preserved at the cost of rejecting conflicting requests.

## 🛠️ Tech Stack
- **Language:** Java 17
- **Framework:** Spring Boot 3
- **Database:** PostgreSQL
- **Testing:** Apache JMeter (for Load Testing)

## 📊 Performance Results
I simulated a traffic spike of **300 concurrent users** buying an item with only **100 stock**.

| Metric | Without Locking (Buggy) | With Optimistic Locking (Fixed) |
| :--- | :--- | :--- |
| **Initial Stock** | 100 | 100 |
| **Concurrent Users** | 300 | 300 |
| **Items Sold** | **140 (Oversold!)** ❌ | **91 (Safe)** ✅ |
| **Data Integrity** | FAILED | PASS |

## 🚀 How to Run

1. **Clone the repo**
   ```bash
   git clone https://github.com/Prateeks16/high-performance-flashsale
