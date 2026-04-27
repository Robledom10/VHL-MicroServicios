-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: auth_service
-- ------------------------------------------------------
-- Server version	8.0.44
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
-- Table structure for table `permission`
--
DROP TABLE IF EXISTS `permission`;

/*!40101 SET @saved_cs_client     = @@character_set_client */;

/*!50503 SET character_set_client = utf8mb4 */;

CREATE TABLE
  `permission` (
    `id_permission` int NOT NULL AUTO_INCREMENT,
    `description` varchar(150) DEFAULT NULL,
    `module` varchar(255) NOT NULL,
    `status` bit (1) NOT NULL,
    PRIMARY KEY (`id_permission`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--
LOCK TABLES `permission` WRITE;

/*!40000 ALTER TABLE `permission` DISABLE KEYS */;

/*!40000 ALTER TABLE `permission` ENABLE KEYS */;

UNLOCK TABLES;

--
-- Table structure for table `refresh_token`
--
DROP TABLE IF EXISTS `refresh_token`;

/*!40101 SET @saved_cs_client     = @@character_set_client */;

/*!50503 SET character_set_client = utf8mb4 */;

CREATE TABLE
  `refresh_token` (
    `id_refresh_token` int NOT NULL AUTO_INCREMENT,
    `token` varchar(500) NOT NULL,
    `expiry_date` datetime NOT NULL,
    `fk_id_user` int NOT NULL,
    PRIMARY KEY (`id_refresh_token`),
    UNIQUE KEY `token` (`token`),
    UNIQUE KEY `fk_id_user` (`fk_id_user`),
    CONSTRAINT `refresh_token_ibfk_1` FOREIGN KEY (`fk_id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refresh_token`
--
LOCK TABLES `refresh_token` WRITE;

/*!40000 ALTER TABLE `refresh_token` DISABLE KEYS */;

/*!40000 ALTER TABLE `refresh_token` ENABLE KEYS */;

UNLOCK TABLES;

--
-- Table structure for table `role`
--
DROP TABLE IF EXISTS `role`;

/*!40101 SET @saved_cs_client     = @@character_set_client */;

/*!50503 SET character_set_client = utf8mb4 */;

CREATE TABLE
  `role` (
    `id_rol` int NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `status` bit (1) NOT NULL,
    PRIMARY KEY (`id_rol`)
  ) ENGINE = InnoDB AUTO_INCREMENT = 3 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--
LOCK TABLES `role` WRITE;

/*!40000 ALTER TABLE `role` DISABLE KEYS */;

INSERT INTO
  `role`
VALUES
  (1, 'CLIENT', _binary ''),
  (2, 'ADMIN', _binary '');

/*!40000 ALTER TABLE `role` ENABLE KEYS */;

UNLOCK TABLES;

--
-- Table structure for table `role_permission`
--
DROP TABLE IF EXISTS `role_permission`;

/*!40101 SET @saved_cs_client     = @@character_set_client */;

/*!50503 SET character_set_client = utf8mb4 */;

CREATE TABLE
  `role_permission` (
    `fk_id_rol` int NOT NULL,
    `fk_id_permission` int NOT NULL,
    PRIMARY KEY (`fk_id_rol`, `fk_id_permission`),
    KEY `FK9kses3lyc38lrsfch5drqur53` (`fk_id_permission`),
    CONSTRAINT `FK2t4ulnw7voqwhp1d2ft9j0o3h` FOREIGN KEY (`fk_id_rol`) REFERENCES `role` (`id_rol`),
    CONSTRAINT `FK9kses3lyc38lrsfch5drqur53` FOREIGN KEY (`fk_id_permission`) REFERENCES `permission` (`id_permission`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permission`
--
LOCK TABLES `role_permission` WRITE;

/*!40000 ALTER TABLE `role_permission` DISABLE KEYS */;

/*!40000 ALTER TABLE `role_permission` ENABLE KEYS */;

UNLOCK TABLES;

--
-- Table structure for table `user`
--
DROP TABLE IF EXISTS `user`;

/*!40101 SET @saved_cs_client     = @@character_set_client */;

/*!50503 SET character_set_client = utf8mb4 */;

CREATE TABLE
  `user` (
    `id_user` int NOT NULL AUTO_INCREMENT,
    `address` varchar(255) DEFAULT NULL,
    `birth_date` date DEFAULT NULL,
    `city` varchar(255) DEFAULT NULL,
    `document_number` varchar(255) NOT NULL,
    `document_type` enum (
      'CEDULA_CIUDADANIA',
      'CEDULA_EXTRANJERIA',
      'PASAPORTE',
      'TARJETA_IDENTIDAD',
      'VISA'
    ) DEFAULT NULL,
    `email` varchar(255) NOT NULL,
    `email_verified` bit (1) NOT NULL,
    `first_name` varchar(255) NOT NULL,
    `last_name` varchar(255) NOT NULL,
    `password_hash` varchar(255) NOT NULL,
    `phone` varchar(255) DEFAULT NULL,
    `phone_verified` bit (1) NOT NULL,
    `state` varchar(255) DEFAULT NULL,
    `fk_id_rol` int NOT NULL,
    PRIMARY KEY (`id_user`),
    UNIQUE KEY `UKckltqnf47mr90fw56edpewrk8` (`document_number`),
    UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`),
    KEY `FK3gd57fmfubx2birsarn5fjbdw` (`fk_id_rol`),
    CONSTRAINT `FK3gd57fmfubx2birsarn5fjbdw` FOREIGN KEY (`fk_id_rol`) REFERENCES `role` (`id_rol`)
  ) ENGINE = InnoDB AUTO_INCREMENT = 3 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--
LOCK TABLES `user` WRITE;

/*!40000 ALTER TABLE `user` DISABLE KEYS */;

INSERT INTO
  `user`
VALUES
  (
    1,
    NULL,
    NULL,
    NULL,
    '12345678',
    'CEDULA_CIUDADANIA',
    'juan@test.com',
    _binary '\0',
    'Juan',
    'Perez',
    '$2a$10$FgWmVYmlvdDW2NGcbMsCd.pvnqHOiUZq4pKIBTQ8CAOmHudbClyNu',
    NULL,
    _binary '\0',
    NULL,
    1
  ),
  (
    2,
    NULL,
    NULL,
    NULL,
    '123498',
    'CEDULA_CIUDADANIA',
    'admin@test.com',
    _binary '\0',
    'Admin',
    'test',
    '$2a$10$8PKyreyL1IdjYr.k76b1vel9It.UxjBXpVfrrTLo/niz/Y6z.kTNi',
    NULL,
    _binary '\0',
    NULL,
    2
  );

/*!40000 ALTER TABLE `user` ENABLE KEYS */;

UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;

/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;

/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-27 12:27:48