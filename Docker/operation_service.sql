-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: trip_operations_db
-- ------------------------------------------------------
-- Server version	8.4.9

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

--
-- Table structure for table `email_respuestas`
--

DROP TABLE IF EXISTS `email_respuestas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `email_respuestas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `notificacion_id` bigint NOT NULL,
  `remitente_email` varchar(255) DEFAULT NULL,
  `asunto` varchar(500) DEFAULT NULL,
  `contenido` mediumtext,
  `fecha_recibida` datetime DEFAULT NULL,
  `leida` tinyint(1) NOT NULL DEFAULT '0',
  `incoming_message_id` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `incoming_message_id` (`incoming_message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `email_respuestas`
--

LOCK TABLES `email_respuestas` WRITE;
/*!40000 ALTER TABLE `email_respuestas` DISABLE KEYS */;
/*!40000 ALTER TABLE `email_respuestas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emergency_contact`
--

DROP TABLE IF EXISTS `emergency_contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `emergency_contact` (
  `id_contact` bigint NOT NULL AUTO_INCREMENT,
  `fk_id_trip` bigint NOT NULL,
  `id_traveler` bigint NOT NULL,
  `full_name` varchar(120) NOT NULL,
  `relationship` varchar(80) NOT NULL,
  `phone` varchar(30) NOT NULL,
  `email` varchar(150) DEFAULT NULL,
  `traveler_name` varchar(150) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_contact`),
  KEY `fk_id_trip` (`fk_id_trip`),
  CONSTRAINT `emergency_contact_ibfk_1` FOREIGN KEY (`fk_id_trip`) REFERENCES `trip_departure` (`id_trip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emergency_contact`
--

LOCK TABLES `emergency_contact` WRITE;
/*!40000 ALTER TABLE `emergency_contact` DISABLE KEYS */;
/*!40000 ALTER TABLE `emergency_contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `guide_assignment`
--

DROP TABLE IF EXISTS `guide_assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `guide_assignment` (
  `id_guide` bigint NOT NULL AUTO_INCREMENT,
  `fk_id_trip` bigint NOT NULL,
  `guide_name` varchar(150) NOT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `email` varchar(150) DEFAULT NULL,
  `specialty` varchar(200) DEFAULT NULL,
  `language` varchar(100) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_guide`),
  KEY `fk_id_trip` (`fk_id_trip`),
  CONSTRAINT `guide_assignment_ibfk_1` FOREIGN KEY (`fk_id_trip`) REFERENCES `trip_departure` (`id_trip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guide_assignment`
--

LOCK TABLES `guide_assignment` WRITE;
/*!40000 ALTER TABLE `guide_assignment` DISABLE KEYS */;
/*!40000 ALTER TABLE `guide_assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lodging_assignment`
--

DROP TABLE IF EXISTS `lodging_assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lodging_assignment` (
  `id_lodging` bigint NOT NULL AUTO_INCREMENT,
  `fk_id_trip` bigint NOT NULL,
  `id_traveler` bigint DEFAULT NULL,
  `traveler_name` varchar(150) DEFAULT NULL,
  `hotel_name` varchar(150) NOT NULL,
  `address` varchar(255) NOT NULL,
  `room_number` varchar(60) DEFAULT NULL,
  `check_in_date` date NOT NULL,
  `check_out_date` date NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_lodging`),
  KEY `fk_id_trip` (`fk_id_trip`),
  CONSTRAINT `lodging_assignment_ibfk_1` FOREIGN KEY (`fk_id_trip`) REFERENCES `trip_departure` (`id_trip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lodging_assignment`
--

LOCK TABLES `lodging_assignment` WRITE;
/*!40000 ALTER TABLE `lodging_assignment` DISABLE KEYS */;
/*!40000 ALTER TABLE `lodging_assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification_history`
--

DROP TABLE IF EXISTS `notification_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification_history` (
  `id_notification` bigint NOT NULL AUTO_INCREMENT,
  `fk_id_trip` bigint NOT NULL,
  `subject` varchar(150) NOT NULL,
  `message` varchar(1000) NOT NULL,
  `channel` varchar(30) NOT NULL,
  `recipient_count` int NOT NULL,
  `recipients` tinytext,
  `sent_at` datetime NOT NULL,
  `status` varchar(30) NOT NULL,
  `message_ids` tinytext,
  PRIMARY KEY (`id_notification`),
  KEY `fk_id_trip` (`fk_id_trip`),
  CONSTRAINT `notification_history_ibfk_1` FOREIGN KEY (`fk_id_trip`) REFERENCES `trip_departure` (`id_trip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_history`
--

LOCK TABLES `notification_history` WRITE;
/*!40000 ALTER TABLE `notification_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `restaurant_assignment`
--

DROP TABLE IF EXISTS `restaurant_assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurant_assignment` (
  `id_restaurant` bigint NOT NULL AUTO_INCREMENT,
  `fk_id_trip` bigint NOT NULL,
  `restaurant_name` varchar(150) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `meal_type` varchar(100) DEFAULT NULL,
  `notes` varchar(500) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_restaurant`),
  KEY `fk_id_trip` (`fk_id_trip`),
  CONSTRAINT `restaurant_assignment_ibfk_1` FOREIGN KEY (`fk_id_trip`) REFERENCES `trip_departure` (`id_trip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restaurant_assignment`
--

LOCK TABLES `restaurant_assignment` WRITE;
/*!40000 ALTER TABLE `restaurant_assignment` DISABLE KEYS */;
/*!40000 ALTER TABLE `restaurant_assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transport_assignment`
--

DROP TABLE IF EXISTS `transport_assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transport_assignment` (
  `id_transport` bigint NOT NULL AUTO_INCREMENT,
  `fk_id_trip` bigint NOT NULL,
  `company` varchar(100) NOT NULL,
  `vehicle_type` varchar(100) NOT NULL,
  `plate` varchar(20) NOT NULL,
  `driver_name` varchar(120) DEFAULT NULL,
  `driver_phone` varchar(30) DEFAULT NULL,
  `capacity` int NOT NULL DEFAULT '1',
  `traveler_count` int NOT NULL DEFAULT '1',
  `departure_time` datetime NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_transport`),
  KEY `fk_id_trip` (`fk_id_trip`),
  CONSTRAINT `transport_assignment_ibfk_1` FOREIGN KEY (`fk_id_trip`) REFERENCES `trip_departure` (`id_trip`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transport_assignment`
--

LOCK TABLES `transport_assignment` WRITE;
/*!40000 ALTER TABLE `transport_assignment` DISABLE KEYS */;
INSERT INTO `transport_assignment` VALUES (1,2,'Buses Granada','Bus','ABC-123','Luis carlos moreno','30125364753',50,30,'2026-07-16 00:00:00','2026-07-01 18:10:00'),(2,1,'Buses Granada','Bus ','ADD-1234','Fernando Morales','3124568365',50,45,'2026-07-02 00:00:00','2026-07-01 18:11:23');
/*!40000 ALTER TABLE `transport_assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `traveler_check_in`
--

DROP TABLE IF EXISTS `traveler_check_in`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `traveler_check_in` (
  `id_check_in` bigint NOT NULL AUTO_INCREMENT,
  `fk_id_trip` bigint NOT NULL,
  `id_traveler` bigint NOT NULL,
  `qr_code` varchar(180) NOT NULL,
  `id_reservation` bigint DEFAULT NULL,
  `check_in_date` datetime NOT NULL,
  PRIMARY KEY (`id_check_in`),
  UNIQUE KEY `uk_check_in_trip_traveler` (`fk_id_trip`,`id_traveler`),
  CONSTRAINT `traveler_check_in_ibfk_1` FOREIGN KEY (`fk_id_trip`) REFERENCES `trip_departure` (`id_trip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `traveler_check_in`
--

LOCK TABLES `traveler_check_in` WRITE;
/*!40000 ALTER TABLE `traveler_check_in` DISABLE KEYS */;
/*!40000 ALTER TABLE `traveler_check_in` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `traveler_medical_info`
--

DROP TABLE IF EXISTS `traveler_medical_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `traveler_medical_info` (
  `id_medical_info` bigint NOT NULL AUTO_INCREMENT,
  `fk_id_trip` bigint NOT NULL,
  `id_traveler` bigint NOT NULL,
  `blood_type` varchar(10) NOT NULL,
  `allergies` varchar(500) DEFAULT NULL,
  `medications` varchar(500) DEFAULT NULL,
  `medical_conditions` varchar(500) DEFAULT NULL,
  `medical_phone` varchar(30) DEFAULT NULL,
  `traveler_name` varchar(150) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_medical_info`),
  KEY `fk_id_trip` (`fk_id_trip`),
  CONSTRAINT `traveler_medical_info_ibfk_1` FOREIGN KEY (`fk_id_trip`) REFERENCES `trip_departure` (`id_trip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `traveler_medical_info`
--

LOCK TABLES `traveler_medical_info` WRITE;
/*!40000 ALTER TABLE `traveler_medical_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `traveler_medical_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trip_departure`
--

DROP TABLE IF EXISTS `trip_departure`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trip_departure` (
  `id_trip` bigint NOT NULL AUTO_INCREMENT,
  `id_user` bigint NOT NULL,
  `id_package` bigint NOT NULL,
  `departure_date` date NOT NULL,
  `return_date` date NOT NULL,
  `status` varchar(30) NOT NULL,
  PRIMARY KEY (`id_trip`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trip_departure`
--

LOCK TABLES `trip_departure` WRITE;
/*!40000 ALTER TABLE `trip_departure` DISABLE KEYS */;
INSERT INTO `trip_departure` VALUES (1,2,1,'2026-07-02','2026-07-06','programado'),(2,2,2,'2026-07-16','2026-07-21','programado');
/*!40000 ALTER TABLE `trip_departure` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trip_incident`
--

DROP TABLE IF EXISTS `trip_incident`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trip_incident` (
  `id_incident` bigint NOT NULL AUTO_INCREMENT,
  `fk_id_trip` bigint NOT NULL,
  `id_traveler` bigint DEFAULT NULL,
  `type` varchar(100) NOT NULL,
  `description` varchar(255) NOT NULL,
  `severity` varchar(30) DEFAULT NULL,
  `reported_by` varchar(120) DEFAULT NULL,
  `incident_date` datetime NOT NULL,
  `status` varchar(30) NOT NULL,
  PRIMARY KEY (`id_incident`),
  KEY `fk_id_trip` (`fk_id_trip`),
  CONSTRAINT `trip_incident_ibfk_1` FOREIGN KEY (`fk_id_trip`) REFERENCES `trip_departure` (`id_trip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trip_incident`
--

LOCK TABLES `trip_incident` WRITE;
/*!40000 ALTER TABLE `trip_incident` DISABLE KEYS */;
/*!40000 ALTER TABLE `trip_incident` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-01 13:21:54
