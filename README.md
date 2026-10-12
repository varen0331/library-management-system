# Library Management System

A console-based Library Management System built with **Java, JDBC and MySQL**.
It manages books and members, handles issuing and returning books with automatic
late fines, and supports role-based login (Admin / Student).

## Features
- Role-based login (Admin and Student) with **PBKDF2 salted password hashing**
- Add, view, search, update and delete books
- Add and view members
- Issue and return books with a 14-day loan period
- Automatic late fine (Rs. 5 per day) on return
- Reports for currently issued and overdue books
- Input validation and friendly error handling

## Tech Stack
- Java 25
- JDBC with MySQL 8
- Maven
- JUnit 5

## Concepts Demonstrated
- OOP with a layered design: model, DAO and util packages
- `PreparedStatement` to prevent SQL injection
- JDBC transactions (`commit` / `rollback`) so issue and return stay consistent
- Password hashing with a random salt and constant-time comparison
- Credentials kept out of source code in a config file

## Setup
1. Install JDK 17+, MySQL 8 and Maven (or use IntelliJ IDEA).
2. Create the database: run `schema.sql` in MySQL Workbench.
3. Copy `db.properties.example` to `src/main/resources/db.properties` and enter your MySQL password.
4. Run `com.library.Main`.
5. To create the first admin, call `new UserDAO().register("admin", "YourPassword", "ADMIN")` once.

## Run Tests
Run `mvn test`, or right-click the test folder in IntelliJ and choose Run.

## Screenshots
(Screenshots coming soon)

## Author
Varenya Palaka