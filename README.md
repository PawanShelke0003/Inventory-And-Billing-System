# 📦 Inventory Billing System

A modern Point of Sale (POS) and Inventory Management System built with Spring Boot, Spring Security, and Thymeleaf. 

This project is designed to streamline retail operations. It features secure role-based access control (Admin/Staff), real-time stock management with low-stock alerts, and a Point of Sale (POS) module for generating customer invoices.

## 🚀 Features
* **Role-Based Access:** Isolated dashboards and capabilities for Administrators and Staff members.
* **Inventory Tracking:** Real-time stock adjustments, active/inactive toggles, and visual low-stock warnings.
* **Billing Module:** Quick invoice generation with automatic subtotal, tax (GST), and grand total calculations.
* **Responsive UI:** Clean, modern frontend powered by Thymeleaf and Bootstrap CSS with a reusable component architecture.

## 🛠️ Tech Stack
* **Backend:** Java 17, Spring Boot 3
* **Security:** Spring Security 6
* **Database:** MySQL, Spring Data JPA, Hibernate
* **Frontend:** Thymeleaf, HTML5, CSS3, Bootstrap

## ⚙️ How to Run Locally

### 1. Database Setup
Create a MySQL database on your local machine named `inventory_db`:
```sql
CREATE DATABASE inventory_db;
```

### 2. Configure Credentials
Update your `src/main/resources/application.properties` file with your local database credentials, or set them as environment variables:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. Run the Application
Open a terminal in the root directory and run the Maven wrapper:
```bash
./mvnw spring-boot:run
```
*(On Windows, you can use `mvnw.cmd spring-boot:run`)*

The application will start on `http://localhost:9090`.

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
