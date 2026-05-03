-- MySQL dump 10.13  Distrib 9.6.0, for macos26.3 (arm64)
--
-- Host: localhost    Database: require4testing
-- ------------------------------------------------------
-- Server version	9.6.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '59d5c2a6-2d1a-11f1-95fc-8701702d904d:1-97';

--
-- Table structure for table `Requirement`
--

DROP TABLE IF EXISTS `Requirement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Requirement` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Priority` varchar(1) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`),
  CONSTRAINT `requirement_chk_1` CHECK ((`Priority` in (_utf8mb4'M',_utf8mb4'S',_utf8mb4'C',_utf8mb4'W'))),
  CONSTRAINT `requirement_chk_2` CHECK ((`Status` in (_utf8mb4'new',_utf8mb4'in_progress',_utf8mb4'done')))
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Task`
--

DROP TABLE IF EXISTS `Task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Task` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Description` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `Status` varchar(13) COLLATE utf8mb4_unicode_ci NOT NULL,
  `User_id` int NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `fk_task_user` (`User_id`),
  CONSTRAINT `fk_task_user` FOREIGN KEY (`User_id`) REFERENCES `User` (`ID`),
  CONSTRAINT `task_chk_1` CHECK ((`Status` in (_utf8mb4'open',_utf8mb4'in_progress',_utf8mb4'done')))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Test`
--

DROP TABLE IF EXISTS `Test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Test` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Status` varchar(12) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Created_by` int DEFAULT NULL,
  `Assigned_tester` int DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `fk_test_user` (`Created_by`),
  KEY `fk_test_assigned_tester` (`Assigned_tester`),
  CONSTRAINT `fk_test_assigned_tester` FOREIGN KEY (`Assigned_tester`) REFERENCES `User` (`ID`),
  CONSTRAINT `fk_test_user` FOREIGN KEY (`Created_by`) REFERENCES `User` (`ID`),
  CONSTRAINT `test_chk_1` CHECK ((`Status` in (_utf8mb4'open',_utf8mb4'in_progress',_utf8mb4'done')))
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TestCase`
--

DROP TABLE IF EXISTS `TestCase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `TestCase` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Description` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `ExpectedResult` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `Requirement_id` int NOT NULL,
  `Test_id` int DEFAULT NULL,
  `TestSteps` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`ID`),
  KEY `fk_testcase_requirement` (`Requirement_id`),
  KEY `fk_testcase_test` (`Test_id`),
  CONSTRAINT `fk_testcase_requirement` FOREIGN KEY (`Requirement_id`) REFERENCES `Requirement` (`ID`),
  CONSTRAINT `fk_testcase_test` FOREIGN KEY (`Test_id`) REFERENCES `Test` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TestReport`
--

DROP TABLE IF EXISTS `TestReport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `TestReport` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Status` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Test_id` int NOT NULL,
  `User_id` int NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `fk_testreport_test` (`Test_id`),
  KEY `fk_testreport_user` (`User_id`),
  CONSTRAINT `fk_testreport_test` FOREIGN KEY (`Test_id`) REFERENCES `Test` (`ID`),
  CONSTRAINT `fk_testreport_user` FOREIGN KEY (`User_id`) REFERENCES `User` (`ID`),
  CONSTRAINT `testreport_chk_1` CHECK ((`Status` in (_utf8mb4'passed',_utf8mb4'failed')))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `User` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Passwort` varchar(5) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Role` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Email` (`Email`),
  CONSTRAINT `user_chk_1` CHECK ((`Role` in (_utf8mb4'RE',_utf8mb4'TM',_utf8mb4'TFE',_utf8mb4'T')))
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-03 20:45:12
