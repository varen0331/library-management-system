CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

CREATE TABLE IF NOT EXISTS users (
                                     user_id INT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN','STUDENT') NOT NULL
    );

CREATE TABLE IF NOT EXISTS books (
                                     book_id INT AUTO_INCREMENT PRIMARY KEY,
                                     title VARCHAR(150) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    total_copies INT NOT NULL,
    available_copies INT NOT NULL
    );

CREATE TABLE IF NOT EXISTS members (
                                       member_id INT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    joined_date DATE DEFAULT (CURRENT_DATE)
    );

CREATE TABLE IF NOT EXISTS transactions (
                                            txn_id INT AUTO_INCREMENT PRIMARY KEY,
                                            book_id INT NOT NULL,
                                            member_id INT NOT NULL,
                                            issue_date DATE NOT NULL,
                                            due_date DATE NOT NULL,
                                            return_date DATE NULL,
                                            fine DECIMAL(8,2) DEFAULT 0,
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (member_id) REFERENCES members(member_id)
    );