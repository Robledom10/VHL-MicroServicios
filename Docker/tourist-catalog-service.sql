-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: catalogo_turistico_db
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
-- Table structure for table `itinerarie`
--

DROP TABLE IF EXISTS `itinerarie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `itinerarie` (
  `id_itinerary` int NOT NULL AUTO_INCREMENT,
  `day_number` int NOT NULL,
  `title` varchar(150) NOT NULL,
  `fk_id_package` int NOT NULL,
  PRIMARY KEY (`id_itinerary`),
  KEY `fk_id_package` (`fk_id_package`),
  CONSTRAINT `itinerarie_ibfk_1` FOREIGN KEY (`fk_id_package`) REFERENCES `package` (`id_package`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `itinerarie`
--

LOCK TABLES `itinerarie` WRITE;
/*!40000 ALTER TABLE `itinerarie` DISABLE KEYS */;
INSERT INTO `itinerarie` VALUES (5,1,'Llegada y descanso.',2),(6,2,'Vuelta a la isla.',2),(7,3,'Johnny Cay y Acuario Natural.',2),(8,4,'Día libre para compras y playa.',2),(9,5,'Regreso.',2),(14,1,'Salida desde Armenia, llegada a Riohacha, registro en el hotel,',7),(15,2,'Desayuno, visita a una ranchería Wayúu, recorrido por el Santuario de Fauna y Flora Los Flamencos',7),(16,3,'Desayuno, tiempo libre para compras de artesanías, salida de Riohacha y regreso a Armenia.',7);
/*!40000 ALTER TABLE `itinerarie` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `package`
--

DROP TABLE IF EXISTS `package`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `package` (
  `id_package` int NOT NULL AUTO_INCREMENT,
  `title` varchar(150) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `destination` varchar(150) NOT NULL,
  `duration_days` int NOT NULL,
  `price` decimal(15,2) NOT NULL,
  `quota` int NOT NULL,
  `departure_place` varchar(150) DEFAULT NULL,
  `transport_type` varchar(80) DEFAULT 'Bus de turismo',
  `vertical_photo_url` varchar(500) DEFAULT NULL,
  `horizontal_photo_url` varchar(500) DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_package`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `package`
--

LOCK TABLES `package` WRITE;
/*!40000 ALTER TABLE `package` DISABLE KEYS */;
INSERT INTO `package` VALUES (2,'Mar de los 7 colores','Vive una experiencia inolvidable en el Mar de los 7 Colores, un destino paradisíaco con playas de arena blanca, aguas cristalinas y paisajes únicos. Disfruta de un ambiente tropical, actividades recreativas, gastronomía local y momentos de descanso','San Andrés',5,149997.00,30,'Aeropuerto Internacional el Edén','Bus de turismo, Avion','https://res.cloudinary.com/dqcviyp18/image/upload/v1782929136/vhl/packages/babgrkoqxpqloam2jwz4.jpg','https://res.cloudinary.com/dqcviyp18/image/upload/v1782929135/vhl/packages/lxb1zpe2su0mhehuptcp.jpg',1),(7,'Magia Cafetera','Descubre la riqueza cultural y los paisajes únicos de La Guajira en una excursión llena de aventura y tradición. Recorre las principales playas de Riohacha, conoce la cultura Wayúu','Riohacha',3,980000.00,30,'Terminal de Transportes de Armenia','Bus de turismo','https://res.cloudinary.com/dqcviyp18/image/upload/v1782930031/vhl/packages/w7k8xz8a9pgry48co5u4.webp','https://res.cloudinary.com/dqcviyp18/image/upload/v1782930031/vhl/packages/ghyx0gcdnplbee0kigia.webp',1);
/*!40000 ALTER TABLE `package` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `package_cancellation_policy`
--

DROP TABLE IF EXISTS `package_cancellation_policy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `package_cancellation_policy` (
  `fk_id_package` int NOT NULL,
  `description` varchar(255) NOT NULL,
  KEY `fk_id_package` (`fk_id_package`),
  CONSTRAINT `package_cancellation_policy_ibfk_1` FOREIGN KEY (`fk_id_package`) REFERENCES `package` (`id_package`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `package_cancellation_policy`
--

LOCK TABLES `package_cancellation_policy` WRITE;
/*!40000 ALTER TABLE `package_cancellation_policy` DISABLE KEYS */;
INSERT INTO `package_cancellation_policy` VALUES (2,'Cancelación gratuita hasta 5 días antes del viaje.'),(2,'50% de reembolso si cancela 48 horas antes.'),(2,'No hay devolución el mismo día del viaje.'),(7,'Cancelación gratuita hasta 5 días antes del viaje.'),(7,'50% de reembolso si cancela 48 horas antes.'),(7,'No hay devolución el mismo día del viaje.');
/*!40000 ALTER TABLE `package_cancellation_policy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `package_comment`
--

DROP TABLE IF EXISTS `package_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `package_comment` (
  `id_comment` int NOT NULL AUTO_INCREMENT,
  `fk_id_package` int NOT NULL,
  `comment` varchar(2000) NOT NULL,
  `score` int NOT NULL,
  `author` varchar(100) DEFAULT NULL,
  `author_email` varchar(150) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_comment`),
  KEY `fk_id_package` (`fk_id_package`),
  CONSTRAINT `package_comment_ibfk_1` FOREIGN KEY (`fk_id_package`) REFERENCES `package` (`id_package`),
  CONSTRAINT `package_comment_chk_1` CHECK ((`score` between 1 and 5))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `package_comment`
--

LOCK TABLES `package_comment` WRITE;
/*!40000 ALTER TABLE `package_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `package_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `package_destination`
--

DROP TABLE IF EXISTS `package_destination`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `package_destination` (
  `fk_id_package` int NOT NULL,
  `destination` varchar(150) NOT NULL,
  KEY `fk_id_package` (`fk_id_package`),
  CONSTRAINT `package_destination_ibfk_1` FOREIGN KEY (`fk_id_package`) REFERENCES `package` (`id_package`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `package_destination`
--

LOCK TABLES `package_destination` WRITE;
/*!40000 ALTER TABLE `package_destination` DISABLE KEYS */;
INSERT INTO `package_destination` VALUES (2,'San Andrés'),(7,'Riohacha'),(7,'La Guajira');
/*!40000 ALTER TABLE `package_destination` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `package_exclusion`
--

DROP TABLE IF EXISTS `package_exclusion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `package_exclusion` (
  `fk_id_package` int NOT NULL,
  `description` varchar(150) NOT NULL,
  KEY `fk_id_package` (`fk_id_package`),
  CONSTRAINT `package_exclusion_ibfk_1` FOREIGN KEY (`fk_id_package`) REFERENCES `package` (`id_package`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `package_exclusion`
--

LOCK TABLES `package_exclusion` WRITE;
/*!40000 ALTER TABLE `package_exclusion` DISABLE KEYS */;
INSERT INTO `package_exclusion` VALUES (2,'Gastos personales'),(2,'Bebidas alcohólicas'),(2,'Compras personales'),(7,'Gastos personales'),(7,'Bebidas alcohólicas'),(7,'Compras personales');
/*!40000 ALTER TABLE `package_exclusion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `package_inclusion`
--

DROP TABLE IF EXISTS `package_inclusion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `package_inclusion` (
  `fk_id_package` int NOT NULL,
  `description` varchar(150) NOT NULL,
  KEY `fk_id_package` (`fk_id_package`),
  CONSTRAINT `package_inclusion_ibfk_1` FOREIGN KEY (`fk_id_package`) REFERENCES `package` (`id_package`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `package_inclusion`
--

LOCK TABLES `package_inclusion` WRITE;
/*!40000 ALTER TABLE `package_inclusion` DISABLE KEYS */;
INSERT INTO `package_inclusion` VALUES (2,'Transporte terrestre ida y regreso'),(2,'Hospedaje en hotel 3 estrellas'),(2,'Alimentación (desayuno y cena)'),(2,'Tours turísticos guiados'),(7,'Transporte terrestre ida y regreso'),(7,'Hospedaje en hotel 3 estrellas'),(7,'Alimentación (desayuno y cena)'),(7,'Tours turísticos guiados');
/*!40000 ALTER TABLE `package_inclusion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `package_provider`
--

DROP TABLE IF EXISTS `package_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `package_provider` (
  `id_package_provider` int NOT NULL AUTO_INCREMENT,
  `fk_id_package` int NOT NULL,
  `fk_id_provider` int NOT NULL,
  PRIMARY KEY (`id_package_provider`),
  KEY `fk_id_package` (`fk_id_package`),
  KEY `fk_id_provider` (`fk_id_provider`),
  CONSTRAINT `package_provider_ibfk_1` FOREIGN KEY (`fk_id_package`) REFERENCES `package` (`id_package`),
  CONSTRAINT `package_provider_ibfk_2` FOREIGN KEY (`fk_id_provider`) REFERENCES `provider` (`id_provider`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `package_provider`
--

LOCK TABLES `package_provider` WRITE;
/*!40000 ALTER TABLE `package_provider` DISABLE KEYS */;
/*!40000 ALTER TABLE `package_provider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `package_requirement`
--

DROP TABLE IF EXISTS `package_requirement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `package_requirement` (
  `fk_id_package` int NOT NULL,
  `description` varchar(255) NOT NULL,
  KEY `fk_id_package` (`fk_id_package`),
  CONSTRAINT `package_requirement_ibfk_1` FOREIGN KEY (`fk_id_package`) REFERENCES `package` (`id_package`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `package_requirement`
--

LOCK TABLES `package_requirement` WRITE;
/*!40000 ALTER TABLE `package_requirement` DISABLE KEYS */;
INSERT INTO `package_requirement` VALUES (2,'Cédula de ciudadanía obligatoria.'),(2,'Edad mínima: 5 años.'),(2,'Pago completo antes del viaje.'),(7,'Cédula de ciudadanía obligatoria.'),(7,'Edad mínima: 5 años.'),(7,'Pago completo antes del viaje.');
/*!40000 ALTER TABLE `package_requirement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `package_transport_type`
--

DROP TABLE IF EXISTS `package_transport_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `package_transport_type` (
  `fk_id_package` int NOT NULL,
  `transport_type` varchar(80) NOT NULL,
  KEY `fk_id_package` (`fk_id_package`),
  CONSTRAINT `package_transport_type_ibfk_1` FOREIGN KEY (`fk_id_package`) REFERENCES `package` (`id_package`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `package_transport_type`
--

LOCK TABLES `package_transport_type` WRITE;
/*!40000 ALTER TABLE `package_transport_type` DISABLE KEYS */;
INSERT INTO `package_transport_type` VALUES (2,'Bus de turismo'),(2,'Avion'),(7,'Bus de turismo');
/*!40000 ALTER TABLE `package_transport_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `provider`
--

DROP TABLE IF EXISTS `provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `provider` (
  `id_provider` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `type` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(150) DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  `capacidad` int DEFAULT NULL,
  `conductor` varchar(100) DEFAULT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `especialidad` varchar(100) DEFAULT NULL,
  `idioma` varchar(100) DEFAULT NULL,
  `notas` varchar(500) DEFAULT NULL,
  `placa` varchar(20) DEFAULT NULL,
  `telefono_conductor` varchar(20) DEFAULT NULL,
  `tipo_comida` varchar(100) DEFAULT NULL,
  `tipo_vehiculo` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_provider`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `provider`
--

LOCK TABLES `provider` WRITE;
/*!40000 ALTER TABLE `provider` DISABLE KEYS */;
/*!40000 ALTER TABLE `provider` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-01 13:20:39
