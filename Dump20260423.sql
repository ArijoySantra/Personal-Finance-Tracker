CREATE DATABASE  IF NOT EXISTS `financetracker` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `financetracker`;
-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: financetracker
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accounts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `account_name` varchar(100) NOT NULL,
  `account_type` enum('Bank','Wallet','Cash','Other') NOT NULL,
  `balance` decimal(12,2) DEFAULT '0.00',
  `currency` varchar(10) DEFAULT 'INR',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `accounts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (17,1,'HDFC Savings','Bank',500700.00,'INR','2026-04-22 20:24:53'),(18,1,'GPay','Wallet',-556.00,'INR','2026-04-22 20:26:04'),(19,1,'Physical Cash','Cash',4100.00,'INR','2026-04-22 20:26:26');
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `budgets`
--

DROP TABLE IF EXISTS `budgets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `budgets` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `category_id` int NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `month_year` date NOT NULL,
  `period` enum('Monthly','Yearly') NOT NULL DEFAULT 'Monthly',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_budget` (`user_id`,`category_id`,`month_year`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `budgets_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `budgets_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `budgets`
--

LOCK TABLES `budgets` WRITE;
/*!40000 ALTER TABLE `budgets` DISABLE KEYS */;
INSERT INTO `budgets` VALUES (14,1,37,22000.00,'2026-04-23','Monthly','2026-04-22 20:05:58'),(15,1,25,10000.00,'2026-04-23','Monthly','2026-04-22 20:06:37'),(17,1,34,15000.00,'2026-01-01','Yearly','2026-04-22 20:07:04'),(18,1,52,10000.00,'2026-01-01','Yearly','2026-04-22 20:07:28');
/*!40000 ALTER TABLE `budgets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cards`
--

DROP TABLE IF EXISTS `cards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cards` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `card_name` varchar(100) NOT NULL,
  `card_type` enum('Credit','Debit','Prepaid') NOT NULL,
  `credit_limit` decimal(12,2) DEFAULT '0.00',
  `current_balance` decimal(12,2) DEFAULT '0.00',
  `due_date` date DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `associated_account_id` int DEFAULT NULL,
  `interest_rate` decimal(5,2) DEFAULT '0.00',
  `paid_off_monthly` tinyint(1) DEFAULT '0',
  `starting_day` int DEFAULT '1',
  `payment_day` int DEFAULT '5',
  `automatic_payment` tinyint(1) DEFAULT '0',
  `notes` text,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `associated_account_id` (`associated_account_id`),
  CONSTRAINT `cards_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `cards_ibfk_2` FOREIGN KEY (`associated_account_id`) REFERENCES `accounts` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cards`
--

LOCK TABLES `cards` WRITE;
/*!40000 ALTER TABLE `cards` DISABLE KEYS */;
INSERT INTO `cards` VALUES (5,1,'Milennial','Credit',20000.00,10000.00,'2026-05-23','2026-04-22 21:37:34',17,2.00,0,1,30,1,NULL);
/*!40000 ALTER TABLE `cards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `type` enum('INCOME','EXPENSE') NOT NULL,
  `icon` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_user_category` (`user_id`,`name`,`type`),
  CONSTRAINT `categories_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (22,NULL,'Food/Drinks','EXPENSE',NULL),(23,NULL,'Eating out','EXPENSE',NULL),(24,NULL,'Bar','EXPENSE',NULL),(25,NULL,'Shopping','EXPENSE',NULL),(26,NULL,'Clothing','EXPENSE',NULL),(27,NULL,'Shoes','EXPENSE',NULL),(28,NULL,'Technology','EXPENSE',NULL),(29,NULL,'Gifts','EXPENSE',NULL),(30,NULL,'Transportation','EXPENSE',NULL),(31,NULL,'Car','EXPENSE',NULL),(32,NULL,'Fuel','EXPENSE',NULL),(33,NULL,'Insurance','EXPENSE',NULL),(34,NULL,'Entertainment','EXPENSE',NULL),(35,NULL,'Books/Magazines','EXPENSE',NULL),(36,NULL,'Home','EXPENSE',NULL),(37,NULL,'Rent','EXPENSE',NULL),(38,NULL,'Energy bill','EXPENSE',NULL),(39,NULL,'Water bill','EXPENSE',NULL),(40,NULL,'Garbage bill','EXPENSE',NULL),(41,NULL,'Internet/Telephone','EXPENSE',NULL),(42,NULL,'Condominium fees','EXPENSE',NULL),(43,NULL,'Family','EXPENSE',NULL),(44,NULL,'Children','EXPENSE',NULL),(45,NULL,'Instruction','EXPENSE',NULL),(46,NULL,'Health/Sport','EXPENSE',NULL),(47,NULL,'Health','EXPENSE',NULL),(48,NULL,'Sport','EXPENSE',NULL),(49,NULL,'Pets','EXPENSE',NULL),(50,NULL,'Food - Pets','EXPENSE',NULL),(51,NULL,'Travels','EXPENSE',NULL),(52,NULL,'Accommodation','EXPENSE',NULL),(53,NULL,'Transportation - Travels','EXPENSE',NULL),(54,NULL,'Other (Expenses)','EXPENSE',NULL),(55,NULL,'Taxes','EXPENSE',NULL),(56,NULL,'Cigarettes','EXPENSE',NULL),(57,NULL,'Salary','INCOME',NULL),(58,NULL,'Freelance','INCOME',NULL),(59,NULL,'Business','INCOME',NULL),(60,NULL,'Investments','INCOME',NULL),(61,NULL,'Dividends','INCOME',NULL),(62,NULL,'Interest','INCOME',NULL),(63,NULL,'Rental Income','INCOME',NULL),(64,NULL,'Gifts','INCOME',NULL),(65,NULL,'Refunds/Reimbursements','INCOME',NULL),(66,NULL,'Side Hustle','INCOME',NULL),(67,NULL,'Bonus','INCOME',NULL),(68,NULL,'Other (Income)','INCOME',NULL),(69,NULL,'Debt Payment','EXPENSE',NULL);
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debts`
--

DROP TABLE IF EXISTS `debts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `type` enum('LENT','BORROWED') NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `remaining` decimal(12,2) NOT NULL,
  `due_date` date DEFAULT NULL,
  `interest_rate` decimal(5,2) DEFAULT '0.00',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `emi_amount` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `debts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debts`
--

LOCK TABLES `debts` WRITE;
/*!40000 ALTER TABLE `debts` DISABLE KEYS */;
INSERT INTO `debts` VALUES (2,1,'Home Loan','BORROWED',200000.00,150000.00,'2026-05-01',1.20,'2026-04-22 20:28:27',12000.00),(3,1,'Car Loan','BORROWED',300000.00,230000.00,'2026-05-02',2.50,'2026-04-22 20:41:01',20000.00),(4,1,'Bike Loan','BORROWED',100000.00,0.00,'2026-05-12',1.00,'2026-04-22 20:47:42',0.00);
/*!40000 ALTER TABLE `debts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scheduled_transactions`
--

DROP TABLE IF EXISTS `scheduled_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scheduled_transactions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `account_id` int DEFAULT NULL,
  `card_id` int DEFAULT NULL,
  `category_id` int DEFAULT NULL,
  `amount` decimal(12,2) NOT NULL,
  `type` enum('INCOME','EXPENSE') NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `frequency` enum('DAILY','WEEKLY','MONTHLY','YEARLY') NOT NULL,
  `next_date` date NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `account_id` (`account_id`),
  KEY `card_id` (`card_id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `scheduled_transactions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `scheduled_transactions_ibfk_2` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE SET NULL,
  CONSTRAINT `scheduled_transactions_ibfk_3` FOREIGN KEY (`card_id`) REFERENCES `cards` (`id`) ON DELETE SET NULL,
  CONSTRAINT `scheduled_transactions_ibfk_4` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scheduled_transactions`
--

LOCK TABLES `scheduled_transactions` WRITE;
/*!40000 ALTER TABLE `scheduled_transactions` DISABLE KEYS */;
INSERT INTO `scheduled_transactions` VALUES (3,1,NULL,NULL,34,900.00,'EXPENSE','Netflix','MONTHLY','2026-05-12','2026-04-22 20:22:02'),(4,1,NULL,NULL,22,50.00,'EXPENSE','Milk','DAILY','2026-04-24','2026-04-22 20:22:31'),(5,1,NULL,NULL,47,300.00,'EXPENSE','Gym Membership','MONTHLY','2026-05-01','2026-04-22 20:23:38'),(6,1,NULL,NULL,37,15000.00,'EXPENSE','Rent','MONTHLY','2026-05-02','2026-04-22 20:24:14');
/*!40000 ALTER TABLE `scheduled_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `account_id` int DEFAULT NULL,
  `card_id` int DEFAULT NULL,
  `category_id` int DEFAULT NULL,
  `amount` decimal(12,2) NOT NULL,
  `type` enum('INCOME','EXPENSE') NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `transaction_date` date NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `account_id` (`account_id`),
  KEY `card_id` (`card_id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `transactions_ibfk_2` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE SET NULL,
  CONSTRAINT `transactions_ibfk_3` FOREIGN KEY (`card_id`) REFERENCES `cards` (`id`) ON DELETE SET NULL,
  CONSTRAINT `transactions_ibfk_4` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (30,1,18,NULL,22,556.00,'EXPENSE','Zomato','2026-04-23','2026-04-22 20:49:07'),(31,1,17,NULL,54,9000.00,'INCOME','Gig work','2026-04-23','2026-04-22 20:50:28'),(32,1,19,NULL,64,5000.00,'INCOME','Gift','2026-04-21','2026-04-22 20:54:55'),(33,1,18,NULL,22,8000.00,'EXPENSE','Zomato','2026-04-22','2026-04-22 20:55:38'),(34,1,19,NULL,64,5000.00,'EXPENSE','Wedding Gift','2026-04-20','2026-04-22 20:56:40'),(35,1,19,NULL,23,1500.00,'EXPENSE','Dine-Out','2026-03-23','2026-04-22 21:21:49'),(36,1,NULL,NULL,57,75000.00,'EXPENSE','Salary','2026-04-23','2026-04-22 21:23:33'),(37,1,17,NULL,52,75000.00,'INCOME','Salary','2026-04-23','2026-04-22 21:36:23'),(38,1,17,NULL,57,75000.00,'INCOME','Salary','2026-03-01','2026-04-22 21:59:02'),(39,1,17,NULL,25,2000.00,'EXPENSE','Grocery','2026-04-21','2026-04-22 22:06:02'),(42,1,NULL,NULL,69,50000.00,'EXPENSE','Payment: Bike Loan','2026-04-23','2026-04-22 22:18:49'),(43,1,17,NULL,64,5000000.00,'INCOME','Lottery','2026-04-22','2026-04-22 22:27:08'),(44,1,NULL,NULL,69,20000.00,'EXPENSE','Payment: Car Loan','2026-04-23','2026-04-22 22:28:19'),(45,1,17,NULL,54,25000.00,'EXPENSE','Robbery','2026-04-22','2026-04-22 22:29:55'),(46,1,18,NULL,22,2000.00,'EXPENSE','Zomato','2026-04-23','2026-04-22 22:31:16');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_profiles`
--

DROP TABLE IF EXISTS `user_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_profiles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `monthly_income` decimal(12,2) DEFAULT '0.00',
  `occupation` varchar(100) DEFAULT NULL,
  `financial_goal` varchar(255) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_user_profile` (`user_id`),
  CONSTRAINT `user_profiles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_profiles`
--

LOCK TABLES `user_profiles` WRITE;
/*!40000 ALTER TABLE `user_profiles` DISABLE KEYS */;
INSERT INTO `user_profiles` VALUES (1,1,20.00,'Software Developer','Porche 9/11','India','2026-04-23 00:17:01','2026-04-23 00:31:10');
/*!40000 ALTER TABLE `user_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` int NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`phone`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Ajoy','Aj@gmail.com',1234568779,'65e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c5','2026-04-21 10:46:25'),(2,'AFG','asd',1234568999,'65e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c5','2026-04-22 12:55:11');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'financetracker'
--

--
-- Dumping routines for database 'financetracker'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-23  7:35:08
