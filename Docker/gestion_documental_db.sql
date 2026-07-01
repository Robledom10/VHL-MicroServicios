-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: gestion_documental_db
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

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
-- Table structure for table `contract_acceptances`
--

DROP TABLE IF EXISTS `contract_acceptances`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contract_acceptances` (
  `id_acceptance` int(11) NOT NULL AUTO_INCREMENT,
  `id_reservation` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `contract_version` varchar(255) DEFAULT NULL,
  `electronic_signature` varchar(255) DEFAULT NULL,
  `accepted_at` datetime NOT NULL,
  PRIMARY KEY (`id_acceptance`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contract_acceptances`
--

LOCK TABLES `contract_acceptances` WRITE;
/*!40000 ALTER TABLE `contract_acceptances` DISABLE KEYS */;
/*!40000 ALTER TABLE `contract_acceptances` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `document_validation`
--

DROP TABLE IF EXISTS `document_validation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `document_validation` (
  `id_validation` int(11) NOT NULL AUTO_INCREMENT,
  `fk_id_document` int(11) NOT NULL,
  `result` enum('valido','invalido','pendiente') NOT NULL DEFAULT 'pendiente',
  `source` enum('IA','admin') NOT NULL,
  `observations` varchar(255) DEFAULT NULL,
  `validation_date` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id_validation`),
  KEY `fk_id_document` (`fk_id_document`),
  CONSTRAINT `document_validation_ibfk_1` FOREIGN KEY (`fk_id_document`) REFERENCES `traveler_document` (`id_document`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `document_validation`
--

LOCK TABLES `document_validation` WRITE;
/*!40000 ALTER TABLE `document_validation` DISABLE KEYS */;
/*!40000 ALTER TABLE `document_validation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `traveler_document`
--

DROP TABLE IF EXISTS `traveler_document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `traveler_document` (
  `id_document` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `reservation_id` int(11) DEFAULT NULL,
  `package_id` bigint(20) DEFAULT NULL,
  `trip_id` bigint(20) DEFAULT NULL,
  `document_type` enum('cedula','pasaporte','visa','permiso_menor','vacuna','otro') NOT NULL,
  `file_url` varchar(255) NOT NULL,
  `status` enum('pendiente','en_proceso','aprobado','rechazado') NOT NULL DEFAULT 'pendiente',
  `created_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id_document`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `traveler_document`
--

LOCK TABLES `traveler_document` WRITE;
/*!40000 ALTER TABLE `traveler_document` DISABLE KEYS */;
/*!40000 ALTER TABLE `traveler_document` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-03  7:17:43
