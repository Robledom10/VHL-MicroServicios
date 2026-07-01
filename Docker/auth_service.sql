-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: auth_db
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
-- Table structure for table `blacklisted_token`
--

DROP TABLE IF EXISTS `blacklisted_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blacklisted_token` (
  `id_blacklisted_token` int NOT NULL AUTO_INCREMENT,
  `token` text NOT NULL,
  `logout_at` datetime NOT NULL,
  `expires_at` datetime NOT NULL,
  PRIMARY KEY (`id_blacklisted_token`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blacklisted_token`
--

LOCK TABLES `blacklisted_token` WRITE;
/*!40000 ALTER TABLE `blacklisted_token` DISABLE KEYS */;
INSERT INTO `blacklisted_token` VALUES (1,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnRAdGVzdC5jb20iLCJ1c2VySWQiOjMsInJvbGUiOiJST0xFX0NMSUVOVCIsImlhdCI6MTc3ODcwNDAyNywiZXhwIjoxNzc4NzA0OTI3fQ.hEijSMHfV4KjJ6I2aHaJNdljs80LvCrGNwEnsbVdXV4','2026-05-13 20:27:21','2026-05-13 20:42:07'),(2,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnRAdGVzdC5jb20iLCJ1c2VySWQiOjMsInJvbGUiOiJDTElFTlQiLCJpYXQiOjE3Nzg3MjAyNjUsImV4cCI6MTc3ODcyMTE2NX0.WAGAy63VbyNk_wXOh9o3T64mCZB17_In7K7aU2vkC1U','2026-05-14 00:58:27','2026-05-14 01:12:45'),(3,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsInVzZXJJZCI6Miwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzc4NzIwODI3LCJleHAiOjE3Nzg3MjE3Mjd9.4IGsHXBOwbQsJ3eQZOqSE4GGdJOCIjClErEH5_Cti9g','2026-05-14 01:08:10','2026-05-14 01:22:07'),(4,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsInVzZXJJZCI6Miwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzgwMDA0NDQ3LCJleHAiOjE3ODAwMDUzNDd9.ILHrI1gmHVqc2cuYNrIJXhwsjFmVw-kcyuSD7oKrd8k','2026-05-28 21:42:05','2026-05-28 21:55:47'),(5,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnRAdGVzdC5jb20iLCJ1c2VySWQiOjMsInJvbGUiOiJDTElFTlQiLCJpYXQiOjE3ODAwMDQ1NDgsImV4cCI6MTc4MDAwNTQ0OH0.OGPvFNDVhpldEUXkj6Muf2jUbC3OUZp-3eEMcejqchc','2026-05-28 21:43:56','2026-05-28 21:57:28'),(6,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsInVzZXJJZCI6Miwicm9sZSI6IkFETUlOIiwiZmlyc3ROYW1lIjoiQWRtaW4iLCJsYXN0TmFtZSI6InRlc3QiLCJpYXQiOjE3ODIzNjM2NjgsImV4cCI6MTc4MjM2NDU2OH0.EFDHjkKsKsfmvS7vSm3XMPmGCBDkwE6KtlQCl3mOmHQ','2026-06-25 05:04:39','2026-06-25 05:16:08'),(7,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsInVzZXJJZCI6Miwicm9sZSI6IkFETUlOIiwiZmlyc3ROYW1lIjoiQWRtaW4iLCJsYXN0TmFtZSI6InRlc3QiLCJpYXQiOjE3ODIzNjM4OTQsImV4cCI6MTc4MjM2NDc5NH0.7V9pwoRY2SVYingSFx2sQeWa0Zt9XPLE3eTid2F0zPk','2026-06-25 05:07:42','2026-06-25 05:19:54'),(8,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsInVzZXJJZCI6Miwicm9sZSI6IkFETUlOIiwiZmlyc3ROYW1lIjoiQWRtaW4iLCJsYXN0TmFtZSI6InRlc3QiLCJpYXQiOjE3ODI4ODY3OTIsImV4cCI6MTc4Mjg4NzY5Mn0.Aq4phCzqEbpA3scFohaqRBnEoQOalSF3rV7qTIGnASE','2026-07-01 06:19:58','2026-07-01 06:34:52'),(9,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsInVzZXJJZCI6Miwicm9sZSI6IkFETUlOIiwiZmlyc3ROYW1lIjoiQWRtaW4iLCJsYXN0TmFtZSI6InRlc3QiLCJpYXQiOjE3ODI4ODc0NjUsImV4cCI6MTc4Mjg4ODM2NX0.IUIQXDdwyUrxWDfc2qZVrHBeZVdmAPJK1LimUdQYuV0','2026-07-01 06:32:09','2026-07-01 06:46:05'),(10,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsInVzZXJJZCI6Miwicm9sZSI6IkFETUlOIiwiZmlyc3ROYW1lIjoiQWRtaW4iLCJsYXN0TmFtZSI6InRlc3QiLCJpYXQiOjE3ODI4ODc2NDgsImV4cCI6MTc4Mjg4ODU0OH0.aRPcsPrQf5UK_68CzcmjziJ3QwxQJG5MF3sJ11LjYR4','2026-07-01 06:34:40','2026-07-01 06:49:08'),(11,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsInVzZXJJZCI6Miwicm9sZSI6IkFETUlOIiwiZmlyc3ROYW1lIjoiQWRtaW4iLCJsYXN0TmFtZSI6InRlc3QiLCJpYXQiOjE3ODI4ODc3NTQsImV4cCI6MTc4Mjg4ODY1NH0.1iQQqUcW7gVGDohDSUVt4EOr8aWC80b12FcTE2eDdyo','2026-07-01 06:36:14','2026-07-01 06:50:54'),(12,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkYW5vbmlubzEyM0BnbWFpbC5jb20iLCJ1c2VySWQiOjYsInJvbGUiOiJDTElFTlQiLCJmaXJzdE5hbWUiOiJLaW0gIiwibGFzdE5hbWUiOiJEYW4iLCJpYXQiOjE3ODI4ODc3ODUsImV4cCI6MTc4Mjg4ODY4NX0.rSWpwoXYO8CwF8vjicKNsqWm9XcgswZh7TKg8blLMo0','2026-07-01 06:38:31','2026-07-01 06:51:25'),(13,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsInVzZXJJZCI6Miwicm9sZSI6IkFETUlOIiwiZmlyc3ROYW1lIjoiQWRtaW4iLCJsYXN0TmFtZSI6InRlc3QiLCJpYXQiOjE3ODI4ODc5MTQsImV4cCI6MTc4Mjg4ODgxNH0.e-cFO-oNxcyOSdlC1DWp7uadHT8OboJZOqVcSCN-YDY','2026-07-01 06:44:46','2026-07-01 06:53:34'),(14,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsInVzZXJJZCI6Miwicm9sZSI6IkFETUlOIiwiZmlyc3ROYW1lIjoiQWRtaW4iLCJsYXN0TmFtZSI6InRlc3QiLCJpYXQiOjE3ODI5MjU2ODIsImV4cCI6MTc4MjkyNjU4Mn0.IsppNXw6zYqBFvUt3y3tRDfA3tsxuFmxKCZc6TT8hx8','2026-07-01 17:08:13','2026-07-01 17:23:02'),(15,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGFuZ2FicmllbGd1dGllcnJlenByYWRhQGdtYWlsLmNvbSIsInVzZXJJZCI6NCwicm9sZSI6IkNMSUVOVCIsImZpcnN0TmFtZSI6IkFsYW4iLCJsYXN0TmFtZSI6Ikd1dGnDqXJyZXoiLCJpYXQiOjE3ODI5MjU1ODYsImV4cCI6MTc4MjkyNjQ4Nn0.Fu0J1h1NMTOFkKO57vBUMZDqADs1-wprfY1mduyccxg','2026-07-01 17:09:16','2026-07-01 17:21:26'),(16,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsInVzZXJJZCI6Miwicm9sZSI6IkFETUlOIiwiZmlyc3ROYW1lIjoiQWRtaW4iLCJsYXN0TmFtZSI6InRlc3QiLCJpYXQiOjE3ODI5MjY2MjcsImV4cCI6MTc4MjkyNzUyN30.Bk2N_f-lLZBYhTRxcmcA-YvlUFuenEg1bUuWGsyelAc','2026-07-01 17:23:56','2026-07-01 17:38:47'),(17,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdGVmYW5pYWNvYmFsb25kb25AZ21haWwuY29tIiwidXNlcklkIjo5LCJyb2xlIjoiQ0xJRU5UIiwiZmlyc3ROYW1lIjoiU3RlZmFuaWEiLCJsYXN0TmFtZSI6ImxvbmRvbiIsImlhdCI6MTc4MjkyNzM5MiwiZXhwIjoxNzgyOTI4MjkyfQ.5TUOatKiN5yo0RvJqAnqoZ5mFnqtVuw9tpdwGiGokTo','2026-07-01 17:43:42','2026-07-01 17:51:32'),(18,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyb2JsZWRvbWlndWVsYW5nZWwyMDA3QGdtYWlsLmNvbSIsInVzZXJJZCI6MTAsInJvbGUiOiJDTElFTlQiLCJmaXJzdE5hbWUiOiJNaWd1ZWwiLCJsYXN0TmFtZSI6IlJvYmxlZG8iLCJpYXQiOjE3ODI5Mjc3ODAsImV4cCI6MTc4MjkyODY4MH0.1mdCcYiLceoJE16XWzqkGgNJSNHpE4KhIa4oC2nQgbU','2026-07-01 17:44:39','2026-07-01 17:58:00');
/*!40000 ALTER TABLE `blacklisted_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_token`
--

DROP TABLE IF EXISTS `password_reset_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_token` (
  `id` int NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(6) NOT NULL,
  `token` varchar(255) NOT NULL,
  `used` bit(1) NOT NULL,
  `fk_id_user` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKg0guo4k8krgpwuagos61oc06j` (`token`),
  KEY `FKls9tujsdxw9lcy8jjrnpo0t6x` (`fk_id_user`),
  CONSTRAINT `FKls9tujsdxw9lcy8jjrnpo0t6x` FOREIGN KEY (`fk_id_user`) REFERENCES `user` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_token`
--

LOCK TABLES `password_reset_token` WRITE;
/*!40000 ALTER TABLE `password_reset_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refresh_token`
--

DROP TABLE IF EXISTS `refresh_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refresh_token` (
  `id_refresh_token` int NOT NULL AUTO_INCREMENT,
  `token` varchar(500) NOT NULL,
  `expiry_date` datetime NOT NULL,
  `fk_id_user` int NOT NULL,
  PRIMARY KEY (`id_refresh_token`),
  UNIQUE KEY `token` (`token`),
  UNIQUE KEY `fk_id_user` (`fk_id_user`),
  CONSTRAINT `refresh_token_ibfk_1` FOREIGN KEY (`fk_id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refresh_token`
--

LOCK TABLES `refresh_token` WRITE;
/*!40000 ALTER TABLE `refresh_token` DISABLE KEYS */;
INSERT INTO `refresh_token` VALUES (57,'460cf364-5e69-4959-852e-3f78bd4ad9c7','2026-05-22 17:48:03',1),(62,'802c54a0-0267-4752-9299-0a630c9e1307','2026-07-08 18:07:49',2),(64,'874a29b8-dcf9-4a14-81b8-60a433de9604','2026-07-08 17:08:42',7),(65,'48251da2-ea80-4021-b4b1-f64f25aa8951','2026-07-08 17:24:11',8),(67,'174dd955-0247-49bd-9a0e-817d7b86a1d7','2026-07-08 17:43:00',10),(68,'2f537581-27aa-4122-bbdb-27580bc2ded4','2026-07-08 18:17:17',11);
/*!40000 ALTER TABLE `refresh_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id_rol` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `status` bit(1) NOT NULL,
  PRIMARY KEY (`id_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'CLIENT',_binary ''),(2,'ADMIN',_binary ''),(3,'GUIDE',_binary '');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id_user` int NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `document_number` varchar(255) DEFAULT NULL,
  `document_type` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `email_verified` bit(1) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `phone_verified` bit(1) NOT NULL,
  `state` varchar(255) DEFAULT NULL,
  `fk_id_rol` int NOT NULL,
  `failed_attempts` int NOT NULL DEFAULT '0',
  `account_non_locked` tinyint(1) NOT NULL DEFAULT '1',
  `lock_time` datetime DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `profile_completed` bit(1) NOT NULL,
  `failed_attemps` int NOT NULL,
  `provider` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`),
  UNIQUE KEY `UKckltqnf47mr90fw56edpewrk8` (`document_number`),
  KEY `FK3gd57fmfubx2birsarn5fjbdw` (`fk_id_rol`),
  CONSTRAINT `FK3gd57fmfubx2birsarn5fjbdw` FOREIGN KEY (`fk_id_rol`) REFERENCES `role` (`id_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,NULL,NULL,NULL,'12345678','CEDULA_CIUDADANIA','juan@test.com',_binary '\0','Juan','Perez','$2a$10$FgWmVYmlvdDW2NGcbMsCd.pvnqHOiUZq4pKIBTQ8CAOmHudbClyNu',NULL,_binary '\0',NULL,3,0,1,NULL,1,_binary '\0',0,'LOCAL',NULL),(2,'Barrio Centro','2005-10-15','Armenia','123498','CEDULA_CIUDADANIA','admin@test.com',_binary '\0','Admin','test','$2a$10$8PKyreyL1IdjYr.k76b1vel9It.UxjBXpVfrrTLo/niz/Y6z.kTNi','3001234567',_binary '\0','Quindio',2,0,1,NULL,1,_binary '',0,'LOCAL',NULL),(3,'Barrio Centro','2005-10-15','Armenia','1234567','CEDULA_CIUDADANIA','client@test.com',_binary '\0','client','test','$2a$10$.DWrJez7m38.Dx1/oIc1w.j1nkPM9xvylXTff5rU1D48KJdMOrv2m','3001234567',_binary '\0','Quindio',1,0,1,NULL,1,_binary '',0,'LOCAL',NULL),(7,NULL,NULL,'Armenia','104646465','Cedula Ciudadania','stmiguelangelgarciarobledo@gmail.com',_binary '','Miguel Angel','Garcia Robledo',NULL,'6465463232',_binary '\0',NULL,1,0,1,NULL,1,_binary '',0,'GOOGLE','https://lh3.googleusercontent.com/a/ACg8ocKexiAD0-J6kAY2GgnqBfEVG-fQYA8a_2xI3ldnwTkyTT9V=s96-c'),(8,NULL,NULL,NULL,NULL,NULL,'alangabrielgutierrezprada@gmail.com',_binary '','Alan','Gutiérrez',NULL,NULL,_binary '\0',NULL,1,0,1,NULL,1,_binary '\0',0,'GOOGLE','https://lh3.googleusercontent.com/a/ACg8ocLLP9164mPXsasEODwwCbgIVXoevdZqtWub0YTCztPelVUquareiQ=s96-c'),(10,NULL,NULL,NULL,NULL,NULL,'robledomiguelangel2007@gmail.com',_binary '','Miguel','Robledo',NULL,NULL,_binary '\0',NULL,1,0,1,NULL,1,_binary '\0',0,'GOOGLE','https://lh3.googleusercontent.com/a/ACg8ocJ31OKlN7Ph_fK52GcOqKy2wpgK3nDPrenxYhft4hw4zzbBuA=s96-c'),(11,NULL,NULL,NULL,NULL,NULL,'hlviajes2026@gmail.com',_binary '','hlviajes','',NULL,NULL,_binary '\0',NULL,2,0,1,NULL,1,_binary '\0',0,'GOOGLE','https://lh3.googleusercontent.com/a/ACg8ocJbgCE_vLzdZF2QVOeWBpw7SignBv6DWyCt91DTI8Cn9GIDpKo=s96-c');
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

-- Dump completed on 2026-07-01 13:19:15
