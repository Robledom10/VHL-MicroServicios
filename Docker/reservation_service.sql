-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: reservation_db
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
-- Table structure for table `acompanante`
--

DROP TABLE IF EXISTS `acompanante`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `acompanante` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fk_id_reservation` int NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `tipo_documento` varchar(10) NOT NULL,
  `documento` varchar(50) DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_id_reservation` (`fk_id_reservation`),
  CONSTRAINT `acompanante_ibfk_1` FOREIGN KEY (`fk_id_reservation`) REFERENCES `reservation` (`id_reservation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acompanante`
--

LOCK TABLES `acompanante` WRITE;
/*!40000 ALTER TABLE `acompanante` DISABLE KEYS */;
/*!40000 ALTER TABLE `acompanante` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `archivos`
--

DROP TABLE IF EXISTS `archivos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `archivos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fecha_carga` datetime(6) NOT NULL,
  `nombre_archivo` varchar(255) NOT NULL,
  `ruta_archivo` varchar(255) NOT NULL,
  `tamanio_archivo` bigint DEFAULT NULL,
  `tipo_archivo` varchar(255) NOT NULL,
  `tipo_documento` varchar(255) NOT NULL,
  `verificado` bit(1) NOT NULL,
  `viajero_id` bigint DEFAULT NULL,
  `reserva_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archivos`
--

LOCK TABLES `archivos` WRITE;
/*!40000 ALTER TABLE `archivos` DISABLE KEYS */;
/*!40000 ALTER TABLE `archivos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cancellation`
--

DROP TABLE IF EXISTS `cancellation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cancellation` (
  `id_cancellation` int NOT NULL AUTO_INCREMENT,
  `fk_id_reservation` int NOT NULL,
  `reason` varchar(255) NOT NULL,
  `refund_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `status` enum('pendiente','aprobada','rechazada') NOT NULL DEFAULT 'pendiente',
  PRIMARY KEY (`id_cancellation`),
  KEY `fk_id_reservation` (`fk_id_reservation`),
  CONSTRAINT `cancellation_ibfk_1` FOREIGN KEY (`fk_id_reservation`) REFERENCES `reservation` (`id_reservation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cancellation`
--

LOCK TABLES `cancellation` WRITE;
/*!40000 ALTER TABLE `cancellation` DISABLE KEYS */;
/*!40000 ALTER TABLE `cancellation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `id_reservation` int NOT NULL AUTO_INCREMENT,
  `id_user` bigint DEFAULT NULL,
  `package_id` bigint DEFAULT NULL,
  `reservation_code` varchar(50) NOT NULL,
  `passenger_count` int DEFAULT NULL,
  `total_amount` decimal(15,2) NOT NULL,
  `travel_start_date` datetime DEFAULT NULL,
  `travel_end_date` datetime DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `payment_verified` tinyint(1) NOT NULL DEFAULT '0',
  `reservation_date` date NOT NULL,
  `expires_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `confirmation_date` datetime DEFAULT NULL,
  `notes` varchar(500) DEFAULT NULL,
  `viaje_id` bigint DEFAULT NULL,
  `paquete_nombre` varchar(150) DEFAULT NULL,
  `destino` varchar(150) DEFAULT NULL,
  `tipo_habitacion` varchar(50) DEFAULT NULL,
  `solicitud_especial` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id_reservation`),
  UNIQUE KEY `reservation_code` (`reservation_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (1,2,1,'RES-34BD3A82',1,1200000.00,'2026-06-25 00:00:00','2026-06-30 00:00:00','pendiente',0,'2026-06-25',NULL,NULL,NULL,'',2,'Caribe de Ensueño','Cartagena','No especificado','');
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation_emergency_contact`
--

DROP TABLE IF EXISTS `reservation_emergency_contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation_emergency_contact` (
  `id_contact` bigint NOT NULL AUTO_INCREMENT,
  `fk_id_reservation` int NOT NULL,
  `full_name` varchar(120) NOT NULL,
  `relationship` varchar(80) NOT NULL,
  `phone` varchar(30) NOT NULL,
  `email` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id_contact`),
  KEY `fk_id_reservation` (`fk_id_reservation`),
  CONSTRAINT `reservation_emergency_contact_ibfk_1` FOREIGN KEY (`fk_id_reservation`) REFERENCES `reservation` (`id_reservation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation_emergency_contact`
--

LOCK TABLES `reservation_emergency_contact` WRITE;
/*!40000 ALTER TABLE `reservation_emergency_contact` DISABLE KEYS */;
/*!40000 ALTER TABLE `reservation_emergency_contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `traveler`
--

DROP TABLE IF EXISTS `traveler`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `traveler` (
  `id_traveler` bigint NOT NULL AUTO_INCREMENT,
  `fk_id_reservation` int NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `document_number` varchar(50) NOT NULL,
  `document_type` varchar(50) NOT NULL,
  `birth_date` date NOT NULL,
  `email` varchar(150) NOT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `gender` varchar(50) DEFAULT NULL,
  `nationality` varchar(80) DEFAULT NULL,
  `data_completed` tinyint(1) NOT NULL DEFAULT '1',
  `documents_verified` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id_traveler`),
  KEY `fk_id_reservation` (`fk_id_reservation`),
  CONSTRAINT `traveler_ibfk_1` FOREIGN KEY (`fk_id_reservation`) REFERENCES `reservation` (`id_reservation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `traveler`
--

LOCK TABLES `traveler` WRITE;
/*!40000 ALTER TABLE `traveler` DISABLE KEYS */;
/*!40000 ALTER TABLE `traveler` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `voucher`
--

DROP TABLE IF EXISTS `voucher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `voucher` (
  `id_voucher` int NOT NULL AUTO_INCREMENT,
  `fk_id_reservation` int NOT NULL,
  `voucher_code` varchar(50) NOT NULL,
  `file_url` varchar(255) NOT NULL,
  PRIMARY KEY (`id_voucher`),
  UNIQUE KEY `voucher_code` (`voucher_code`),
  KEY `fk_id_reservation` (`fk_id_reservation`),
  CONSTRAINT `voucher_ibfk_1` FOREIGN KEY (`fk_id_reservation`) REFERENCES `reservation` (`id_reservation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `voucher`
--

LOCK TABLES `voucher` WRITE;
/*!40000 ALTER TABLE `voucher` DISABLE KEYS */;
/*!40000 ALTER TABLE `voucher` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-25  0:49:36
