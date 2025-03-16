-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: diploma_project_daily_exercise_health_management_system
-- ------------------------------------------------------
-- Server version	8.0.40

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
-- Table structure for table `diet_foods`
--

DROP TABLE IF EXISTS `diet_foods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diet_foods` (
  `food_id` int NOT NULL AUTO_INCREMENT COMMENT '食物ID',
  `food_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '食物名称（唯一）',
  `calories` decimal(8,2) NOT NULL COMMENT '每100g卡路里（单位：kcal）',
  `protein` decimal(8,2) NOT NULL COMMENT '每100g蛋白质含量（单位：g）',
  `fat` decimal(8,2) NOT NULL COMMENT '每100g脂肪含量（单位：g）',
  `carbohydrates` decimal(8,2) NOT NULL COMMENT '每100g碳水化合物含量（单位：g）',
  `water` decimal(8,2) DEFAULT '0.00' COMMENT '每100g水分含量（单位：ml）',
  `food_type` varchar(63) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '食物分类',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`food_id`),
  UNIQUE KEY `food_name` (`food_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标准化食物营养成分表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diet_foods`
--

LOCK TABLES `diet_foods` WRITE;
/*!40000 ALTER TABLE `diet_foods` DISABLE KEYS */;
/*!40000 ALTER TABLE `diet_foods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `diet_records`
--

DROP TABLE IF EXISTS `diet_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diet_records` (
  `diet_id` int NOT NULL AUTO_INCREMENT COMMENT '饮食记录ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `quantity` decimal(8,2) NOT NULL DEFAULT '100.00' COMMENT '食用量（单位：g）',
  `meal_type` varchar(63) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '进餐类型',
  `meal_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用餐具体时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`diet_id`),
  KEY `user_id` (`user_id`),
  INDEX `meal_time` (`meal_time`),
  CONSTRAINT `diet_records_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户的饮食记录表，记录每日的饮食情况';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diet_records`
--

LOCK TABLES `diet_records` WRITE;
/*!40000 ALTER TABLE `diet_records` DISABLE KEYS */;
INSERT INTO `diet_records` VALUES (26,57,100.00,'BREAKFAST','0000-00-00 00:00:00','2025-01-06 05:59:12','2025-01-06 05:59:12'),(27,57,100.00,'SNACK','0000-00-00 00:00:00','2025-01-06 06:00:26','2025-01-06 06:14:12'),(28,57,100.00,'BREAKFAST','0000-00-00 00:00:00','2025-01-06 06:02:33','2025-01-06 06:02:33'),(29,57,100.00,'LUNCH','0000-00-00 00:00:00','2025-01-06 06:02:47','2025-01-18 06:28:25'),(30,57,100.00,'LUNCH','0000-00-00 00:00:00','2025-01-06 06:13:52','2025-01-06 06:13:52'),(31,57,100.00,'早餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(32,57,100.00,'午餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(33,57,100.00,'晚餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(34,57,100.00,'加餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(35,57,100.00,'午餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(36,57,100.00,'早餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(37,57,100.00,'晚餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(38,57,100.00,'午餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(39,57,100.00,'加餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(40,57,100.00,'加餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(41,57,100.00,'早餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(42,57,100.00,'加餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(43,57,100.00,'午餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(44,57,100.00,'早餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(45,57,100.00,'晚餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(46,57,100.00,'午餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(48,57,100.00,'午餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(49,57,100.00,'晚餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(50,57,100.00,'加餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(51,57,100.00,'早餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(53,57,100.00,'早餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(54,57,100.00,'晚餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(55,57,100.00,'午餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(56,57,100.00,'晚餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(57,57,100.00,'早餐','0000-00-00 00:00:00','2025-01-06 06:17:11','2025-01-06 06:17:11'),(61,57,100.00,'SNACK','0000-00-00 00:00:00','2025-01-06 10:58:20','2025-01-06 10:58:20'),(62,57,100.00,'SNACK','0000-00-00 00:00:00','2025-01-07 06:03:05','2025-01-07 06:03:05'),(63,57,100.00,'SNACK','0000-00-00 00:00:00','2025-01-18 06:27:51','2025-01-18 06:27:51'),(64,57,100.00,'SNACK','0000-00-00 00:00:00','2025-01-18 06:38:33','2025-01-18 06:38:33');
/*!40000 ALTER TABLE `diet_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `diet_records_foods`
--

DROP TABLE IF EXISTS `diet_records_foods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diet_records_foods` (
  `diet_id` int NOT NULL COMMENT '饮食记录ID',
  `food_id` int NOT NULL COMMENT '食物ID',
  PRIMARY KEY (`diet_id`,`food_id`),
  KEY `food_id` (`food_id`),
  CONSTRAINT `diet_records_foods_ibfk_1` FOREIGN KEY (`diet_id`) REFERENCES `diet_records` (`diet_id`) ON DELETE CASCADE,
  CONSTRAINT `diet_records_foods_ibfk_2` FOREIGN KEY (`food_id`) REFERENCES `diet_foods` (`food_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='饮食记录与食物的多对多关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diet_records_foods`
--

LOCK TABLES `diet_records_foods` WRITE;
/*!40000 ALTER TABLE `diet_records_foods` DISABLE KEYS */;
/*!40000 ALTER TABLE `diet_records_foods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercise_records`
--

DROP TABLE IF EXISTS `exercise_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercise_records` (
  `exercise_record_id` int NOT NULL AUTO_INCREMENT COMMENT '运动记录ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `duration` int DEFAULT NULL COMMENT '运动时长（单位：分钟）',
  `distance` decimal(6,2) DEFAULT NULL COMMENT '运动距离（单位：km）',
  `calories_burned` decimal(6,2) DEFAULT NULL COMMENT '消耗的卡路里（单位：kcal）',
  `heart_rate` decimal(5,2) DEFAULT NULL COMMENT '运动中的平均心率（单位：bpm）',
  `exercise_date` date NOT NULL COMMENT '运动日期',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  `exercise_note` text COLLATE utf8mb4_unicode_ci COMMENT '运动记录的备注、随笔和详细内容',
  PRIMARY KEY (`exercise_record_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `exercise_records_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运动记录表，记录每次用户的运动数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise_records`
--

LOCK TABLES `exercise_records` WRITE;
/*!40000 ALTER TABLE `exercise_records` DISABLE KEYS */;
INSERT INTO `exercise_records` VALUES (33,57,50,10.00,356.00,154.00,'2024-12-01','2025-01-07 02:52:52','2025-01-07 07:06:08','OK了家人们'),(35,57,20,100.00,999.00,119.00,'2024-12-02','2025-01-07 04:52:33','2025-01-07 04:52:33',NULL),(37,57,125,10.23,309.81,139.37,'2024-12-09','2025-01-07 05:11:48','2025-01-07 05:11:48',NULL),(38,57,71,12.72,610.68,178.54,'2024-12-24','2025-01-07 05:11:48','2025-01-07 05:11:48',NULL),(39,57,37,13.65,264.19,67.11,'2024-12-13','2025-01-07 05:11:48','2025-01-07 05:11:48',NULL),(40,57,43,5.27,592.97,187.28,'2024-12-12','2025-01-07 05:11:48','2025-01-07 05:11:48',NULL),(41,57,135,12.37,225.99,67.15,'2025-01-05','2025-01-07 05:11:48','2025-01-07 05:11:48',NULL),(42,57,62,6.00,545.25,82.98,'2024-12-18','2025-01-07 05:11:48','2025-01-07 05:11:48',NULL),(43,57,31,14.79,628.97,112.88,'2025-01-02','2025-01-07 05:11:48','2025-01-07 05:11:48',NULL),(44,57,136,13.69,541.44,181.16,'2024-12-09','2025-01-07 05:11:48','2025-01-07 05:11:48',NULL),(45,57,93,11.67,577.27,175.54,'2024-12-21','2025-01-07 05:11:48','2025-01-07 05:11:48',NULL),(46,57,86,8.24,304.29,70.67,'2024-12-17','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(47,57,82,14.92,525.84,102.28,'2024-12-25','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(48,57,81,12.64,471.43,123.48,'2024-12-24','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(51,57,127,14.67,398.74,72.90,'2024-12-31','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(52,57,141,14.38,649.65,162.39,'2024-12-17','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(53,57,93,9.93,641.82,200.71,'2025-01-06','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(54,57,75,12.95,612.77,171.57,'2024-12-31','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(55,57,147,6.76,669.10,84.44,'2025-01-07','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(56,57,55,13.12,415.84,168.14,'2024-12-29','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(57,57,76,14.99,618.39,88.18,'2024-12-26','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(58,57,99,11.19,375.05,194.17,'2024-12-26','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(59,57,81,13.69,232.78,167.94,'2024-12-26','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(60,57,131,5.34,513.01,64.35,'2024-12-30','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(61,57,59,9.36,419.66,193.16,'2025-01-04','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(62,57,142,8.41,642.28,119.80,'2024-12-28','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(63,57,90,9.93,678.93,106.54,'2024-12-18','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(64,57,84,7.54,649.44,169.76,'2024-12-10','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(65,57,103,6.88,247.95,197.30,'2024-12-30','2025-01-07 05:12:02','2025-01-07 05:12:02',NULL),(66,57,50,10.00,454.00,154.00,'2024-12-01','2025-01-07 05:32:45','2025-01-07 05:32:45',NULL),(69,57,90,140.00,200.00,156.00,'2025-01-01','2025-01-07 05:48:11','2025-01-07 05:48:11','测试'),(70,57,50,10.00,150.00,156.00,'2025-01-07','2025-01-07 05:56:15','2025-01-07 05:56:15',''),(72,57,140,14.00,200.00,144.00,'2025-01-07','2025-01-07 06:10:00','2025-01-07 06:10:00',''),(73,57,1,1.00,1.00,178.00,'2025-01-07','2025-01-07 06:12:56','2025-01-07 06:12:56',''),(74,57,414,20.00,300.00,200.00,'2025-01-07','2025-01-07 06:15:59','2025-01-07 06:52:44','test_update'),(75,57,100,10.00,300.00,145.00,'2025-01-18','2025-01-18 06:29:08','2025-01-18 06:39:30',''),(76,57,60,15.00,300.00,160.00,'2025-01-18','2025-01-18 06:39:08','2025-01-18 06:39:08','');
/*!40000 ALTER TABLE `exercise_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercise_records_types`
--

DROP TABLE IF EXISTS `exercise_records_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercise_records_types` (
  `exercise_record_id` int NOT NULL COMMENT '运动记录ID',
  `exercise_type_id` int NOT NULL COMMENT '运动类型ID',
  PRIMARY KEY (`exercise_record_id`,`exercise_type_id`),
  KEY `exercise_type_id` (`exercise_type_id`),
  CONSTRAINT `exercise_records_types_ibfk_1` FOREIGN KEY (`exercise_record_id`) REFERENCES `exercise_records` (`exercise_record_id`) ON DELETE CASCADE,
  CONSTRAINT `exercise_records_types_ibfk_2` FOREIGN KEY (`exercise_type_id`) REFERENCES `exercise_types` (`exercise_type_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运动记录和运动类型之间的多对多关系表。一个运动记录可能关联多个运动类型，而一个运动类型也可能对应多个运动记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise_records_types`
--

LOCK TABLES `exercise_records_types` WRITE;
/*!40000 ALTER TABLE `exercise_records_types` DISABLE KEYS */;
INSERT INTO `exercise_records_types` VALUES (33,1),(66,1),(76,1),(33,2),(66,2),(75,2),(70,3),(46,5),(72,8),(40,9),(62,9),(65,11),(54,14),(74,14),(51,24),(73,27),(42,29),(53,31),(64,37),(74,41),(38,47),(48,75),(56,78),(60,80),(61,93),(44,95),(45,110),(57,113),(39,114),(55,120),(59,125),(58,138),(43,139),(74,141),(41,175),(47,184),(69,188),(69,189),(69,191),(69,193),(35,216),(52,220),(63,221),(37,223);
/*!40000 ALTER TABLE `exercise_records_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercise_types`
--

DROP TABLE IF EXISTS `exercise_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercise_types` (
  `exercise_type_id` int NOT NULL AUTO_INCREMENT COMMENT '运动类型ID',
  `exercise_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '运动类型名称',
  PRIMARY KEY (`exercise_type_id`),
  UNIQUE KEY `exercise_name` (`exercise_name`)
) ENGINE=InnoDB AUTO_INCREMENT=225 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运动类型表，定义所有可能的运动类型';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise_types`
--

LOCK TABLES `exercise_types` WRITE;
/*!40000 ALTER TABLE `exercise_types` DISABLE KEYS */;
INSERT INTO `exercise_types` VALUES (117,'BMX自行车'),(114,'Burpees'),(140,'CS:GO'),(136,'Dota 2'),(143,'FIFA足球系列'),(142,'Madden NFL'),(126,'MMA'),(161,'SUP冲浪'),(32,'举重'),(8,'乒乓球'),(13,'五人制足球'),(222,'人体旗帜'),(223,'人旗引体'),(144,'任天堂明星大乱斗'),(21,'体操'),(184,'体育舞蹈'),(217,'俄式挺身'),(59,'保龄球'),(178,'信任坠落'),(97,'俯卧撑'),(109,'健身轮'),(194,'冰上曲棍球'),(167,'冰上篮球'),(165,'冰上舞蹈'),(35,'冰壶'),(163,'冰壶不同变种'),(5,'冰球'),(41,'冲浪'),(88,'划船'),(220,'前水平'),(84,'力量运动'),(103,'动态拉伸'),(164,'北欧两项'),(192,'南美藤球'),(95,'卧推'),(170,'印度摔跤'),(111,'原地高抬腿'),(58,'台球'),(221,'后水平'),(146,'围棋'),(92,'固定器械训练'),(145,'国际象棋'),(149,'国际跳棋'),(200,'坐式排球'),(139,'堡垒之夜'),(69,'塔吉克的拔河比赛'),(57,'壁球'),(106,'壶铃训练'),(196,'夏威夷冲浪'),(138,'守望先锋'),(180,'定向越野'),(31,'射击'),(30,'射箭'),(123,'巴西柔术'),(77,'帆板'),(75,'帆船'),(159,'帆船级别比赛'),(108,'平衡球训练'),(96,'引体向上'),(44,'徒步旅行'),(105,'战绳训练'),(6,'手球'),(63,'手鼓球'),(150,'扑克'),(18,'拉丁舞比赛'),(131,'拉伸瑜伽'),(101,'拉伸训练'),(118,'拳击'),(3,'排球'),(62,'搏击操'),(125,'摔跤'),(33,'攀岩'),(119,'散打'),(195,'新西兰毛利人摔跤'),(155,'无人机竞速'),(141,'星际争霸II'),(61,'普拉提'),(133,'普拉提康复'),(14,'曲棍球'),(90,'有氧舞蹈'),(156,'机器人格斗'),(93,'杠铃深蹲'),(46,'极地探险'),(53,'极限单车'),(54,'极限滑雪'),(186,'极限跳伞'),(27,'极限运动'),(11,'极限飞盘'),(120,'柔道'),(188,'桌上篮球'),(187,'桌上足球'),(189,'桌上高尔夫'),(191,'桌面赛车'),(83,'桑巴舞'),(148,'桥牌'),(7,'棒球'),(89,'椭圆机训练'),(4,'橄榄球'),(16,'橄榄球七人制'),(22,'武术'),(201,'武术-咏春'),(209,'武术-太极'),(171,'毛里求斯卡里尤克球'),(64,'毽球'),(72,'水上摩托'),(74,'水上滑板'),(78,'水上球类运动'),(73,'水上足球'),(162,'水下曲棍球'),(130,'水中健身'),(218,'水平支撑'),(10,'水球'),(160,'水翼帆船'),(12,'沙滩排球'),(134,'泡沫轴放松'),(124,'泰拳'),(20,'游泳'),(127,'滑冰'),(26,'滑板'),(55,'滑板滑雪'),(76,'滑水'),(39,'滑翔伞'),(25,'滑雪'),(34,'滑雪板'),(51,'滑雪特技'),(56,'滑雪跳跃'),(38,'漂流'),(48,'潜水'),(193,'澳大利亚澳式足球'),(47,'热气球'),(87,'爬楼梯'),(132,'物理治疗训练'),(175,'狗拉雪橇训练'),(42,'独木舟'),(128,'独轮车'),(60,'瑜伽'),(19,'田径'),(154,'电子健身'),(190,'电子投篮机'),(212,'男女混合双打'),(43,'皮划艇'),(158,'皮划艇激流回旋'),(198,'盲人门球'),(94,'硬拉'),(122,'空手道'),(67,'立陶宛篮球'),(2,'篮球'),(137,'绝地求生'),(177,'绳索课程'),(116,'网球'),(80,'羊角舞'),(9,'羽毛球'),(112,'翻轮胎'),(100,'肩推'),(98,'肱二头肌弯举'),(99,'腿举'),(185,'自由式滑雪'),(91,'自由重量训练'),(50,'自行车特技'),(23,'自行车运动'),(102,'舞蹈拉伸'),(168,'舞龙舞狮'),(183,'艺术体操'),(182,'花样滑冰'),(135,'英雄联盟'),(65,'蒙古摔跤'),(169,'蒙古族那达慕大会项目'),(157,'虚拟马拉松'),(68,'蛇形舞'),(224,'街头健身'),(49,'街头滑板'),(151,'记忆运动'),(147,'象棋'),(107,'负重行走'),(17,'赛艇'),(24,'赛车'),(66,'赛马'),(172,'赛骆驼'),(1,'足球'),(121,'跆拳道'),(52,'跑酷'),(36,'跳伞'),(86,'跳绳'),(115,'跳绳HIIT'),(110,'跳跃训练'),(79,'踩高跷'),(37,'蹦极'),(199,'轮椅篮球'),(152,'速解魔方'),(28,'钓鱼'),(113,'间歇性跑步'),(82,'障碍跑'),(166,'雪地摩托'),(104,'静态拉伸'),(71,'风帆'),(40,'风筝冲浪'),(129,'飞盘'),(153,'飞盘高尔夫'),(81,'飞行器比赛'),(45,'马拉松'),(29,'马术'),(174,'马术障碍赛'),(219,'马耳他'),(85,'骑行'),(15,'高尔夫'),(216,'鹈鹕式俄挺俯卧撑'),(70,'龙舟');
/*!40000 ALTER TABLE `exercise_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `goal_exercise_types`
--

DROP TABLE IF EXISTS `goal_exercise_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goal_exercise_types` (
  `goal_id` int NOT NULL COMMENT '健康目标计划ID',
  `exercise_type_id` int NOT NULL COMMENT '运动类型ID',
  PRIMARY KEY (`goal_id`,`exercise_type_id`),
  KEY `exercise_type_id` (`exercise_type_id`),
  CONSTRAINT `goal_exercise_types_ibfk_1` FOREIGN KEY (`goal_id`) REFERENCES `health_goals` (`goal_id`) ON DELETE CASCADE,
  CONSTRAINT `goal_exercise_types_ibfk_2` FOREIGN KEY (`exercise_type_id`) REFERENCES `exercise_types` (`exercise_type_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康目标计划和运动类型之间的多对多关系表。目标计划与运动类型的多对多关系表，表示一个目标计划可能关联多个运动类型，而一个运动类型也可能对应多个目标计划';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goal_exercise_types`
--

LOCK TABLES `goal_exercise_types` WRITE;
/*!40000 ALTER TABLE `goal_exercise_types` DISABLE KEYS */;
INSERT INTO `goal_exercise_types` VALUES (14,1),(17,1),(20,1),(15,2),(16,2),(19,2),(24,2),(26,2),(32,2),(15,3),(16,3),(18,3),(20,3),(22,3),(23,3),(25,5),(28,5),(10,113),(11,217),(9,220),(9,221);
/*!40000 ALTER TABLE `goal_exercise_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `health_data`
--

DROP TABLE IF EXISTS `health_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `health_data` (
  `health_data_id` int NOT NULL AUTO_INCREMENT COMMENT '健康数据ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `heart_rate` decimal(5,2) DEFAULT NULL COMMENT '当前心率（单位：bpm）',
  `weight` decimal(5,2) DEFAULT NULL COMMENT '体重（单位：kg）',
  `bmi` decimal(5,2) DEFAULT NULL COMMENT '体质指数（BMI）',
  `measurement_date` date NOT NULL COMMENT '健康数据记录日期',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`health_data_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `health_data_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户健康数据表，包括体重、BMI、心率等监测数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `health_data`
--

LOCK TABLES `health_data` WRITE;
/*!40000 ALTER TABLE `health_data` DISABLE KEYS */;
INSERT INTO `health_data` VALUES (11,57,160.00,90.00,10.00,'2020-01-01','2025-01-05 11:28:56','2025-01-05 11:28:56'),(13,57,150.00,90.25,29.40,'2018-01-03','2025-01-05 11:32:55','2025-01-06 05:16:35'),(14,57,125.00,87.21,28.41,'2023-04-13','2025-01-05 11:41:57','2025-01-06 05:19:27'),(15,57,140.00,85.14,27.74,'2022-01-05','2025-01-05 11:52:33','2025-01-06 05:17:21'),(19,57,165.00,84.69,27.59,'2021-08-23','2025-01-06 05:24:09','2025-01-06 05:24:09'),(20,57,115.42,60.34,33.32,'2024-05-16','2025-01-06 05:39:33','2025-01-06 05:39:33'),(21,57,140.00,70.72,23.04,'2023-07-06','2025-01-06 05:39:33','2025-01-06 10:57:21'),(22,57,100.80,97.52,21.73,'2024-07-19','2025-01-06 05:39:33','2025-01-06 05:39:33'),(23,57,104.35,58.41,37.58,'2024-06-03','2025-01-06 05:39:33','2025-01-06 05:39:33'),(24,57,160.00,80.00,26.06,'2025-01-07','2025-01-06 05:39:33','2025-01-07 07:56:14'),(26,57,85.41,91.49,20.72,'2024-06-12','2025-01-06 05:39:33','2025-01-06 05:39:33'),(27,57,66.29,46.29,15.88,'2024-02-27','2025-01-06 05:39:33','2025-01-06 05:39:33'),(28,57,70.08,65.23,27.49,'2024-10-12','2025-01-06 05:39:33','2025-01-06 05:39:33'),(29,57,118.63,83.23,28.58,'2024-06-18','2025-01-06 05:39:33','2025-01-06 05:39:33'),(31,57,125.75,76.75,34.74,'2024-11-27','2025-01-06 05:39:33','2025-01-06 05:39:33'),(32,57,68.54,75.67,25.41,'2024-09-19','2025-01-06 05:39:33','2025-01-06 05:39:33'),(33,57,74.41,59.45,37.22,'2024-07-18','2025-01-06 05:39:33','2025-01-06 05:39:33'),(34,57,119.06,42.32,18.04,'2024-07-11','2025-01-06 05:39:33','2025-01-06 05:39:33'),(35,57,59.85,40.85,34.38,'2024-03-08','2025-01-06 05:39:33','2025-01-06 05:39:33'),(38,57,134.70,95.99,18.13,'2024-03-11','2025-01-06 05:39:33','2025-01-06 05:39:33'),(40,57,62.80,69.71,17.29,'2024-01-18','2025-01-06 05:39:33','2025-01-06 05:39:33'),(41,57,108.48,40.52,22.22,'2024-08-07','2025-01-06 05:39:33','2025-01-06 05:39:33'),(42,57,72.45,92.06,31.63,'2024-04-18','2025-01-06 05:39:33','2025-01-06 05:39:33'),(43,57,111.66,94.95,33.23,'2024-02-13','2025-01-06 05:39:33','2025-01-06 05:39:33'),(44,57,80.63,90.11,21.42,'2024-03-29','2025-01-06 05:39:33','2025-01-06 05:39:33'),(45,57,62.03,56.03,39.37,'2024-12-11','2025-01-06 05:39:33','2025-01-06 05:39:33'),(47,57,79.92,66.98,23.77,'2024-08-12','2025-01-06 05:39:33','2025-01-06 05:39:33'),(48,57,147.09,78.43,22.25,'2024-06-28','2025-01-06 05:39:33','2025-01-06 05:39:33'),(49,57,150.00,75.00,24.43,'2025-01-07','2025-01-07 07:57:48','2025-01-07 07:57:48'),(50,57,110.00,74.00,24.11,'2025-01-18','2025-01-18 06:24:06','2025-01-18 06:40:40'),(51,57,140.00,75.00,24.43,'2025-01-18','2025-01-18 06:25:07','2025-01-18 06:25:07');
/*!40000 ALTER TABLE `health_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `health_goals`
--

DROP TABLE IF EXISTS `health_goals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `health_goals` (
  `goal_id` int NOT NULL AUTO_INCREMENT COMMENT '健康目标计划ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `target_plan` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标计划',
  `target_date` date NOT NULL COMMENT '目标达成日期',
  `is_finished` tinyint DEFAULT '0' COMMENT '是否完成计划 - 未完成：0，完成：1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '目标创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '目标更新时间',
  PRIMARY KEY (`goal_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `health_goals_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户的健康目标表，记录用户的健康目标（如减脂、增肌等)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `health_goals`
--

LOCK TABLES `health_goals` WRITE;
/*!40000 ALTER TABLE `health_goals` DISABLE KEYS */;
INSERT INTO `health_goals` VALUES (9,57,'学会前水平和倒立俯卧撑','2025-05-01',0,'2025-01-07 08:26:53','2025-01-07 08:26:53'),(10,57,'减肥15公斤的脂肪。','2025-04-01',0,'2025-01-07 08:37:23','2025-01-07 08:37:23'),(11,57,'学会俄式挺身','2026-01-31',0,'2025-01-07 08:40:31','2025-01-07 08:40:31'),(14,57,'有氧运动计划','2025-02-14',1,'2025-01-07 09:01:07','2025-01-07 09:01:07'),(15,57,'耐力训练计划','2025-05-19',1,'2025-01-07 09:01:07','2025-01-07 09:01:07'),(16,57,'力量提升计划','2025-12-09',1,'2025-01-07 09:01:07','2025-01-07 09:01:07'),(17,57,'灵活性训练计划','2025-04-18',1,'2025-01-07 09:01:07','2025-01-07 09:01:07'),(18,57,'减压放松计划','2025-12-22',1,'2025-01-07 09:01:07','2025-01-07 09:01:07'),(19,57,'体重管理计划','2025-09-12',1,'2025-01-07 09:01:07','2025-01-07 09:01:07'),(20,57,'健康饮食计划','2025-05-05',1,'2025-01-07 09:01:07','2025-01-07 09:01:07'),(22,57,'减脂计划','2025-06-23',0,'2025-01-07 09:01:17','2025-01-07 09:01:17'),(23,57,'增肌计划','2026-01-05',1,'2025-01-07 09:01:17','2025-01-07 09:01:17'),(24,57,'有氧运动计划','2025-10-26',1,'2025-01-07 09:01:17','2025-01-07 09:01:17'),(25,57,'耐力训练计划','2025-04-09',0,'2025-01-07 09:01:17','2025-01-07 09:01:17'),(26,57,'力量提升计划','2025-08-16',1,'2025-01-07 09:01:17','2025-01-07 09:01:17'),(28,57,'减压放松计划','2025-04-29',0,'2025-01-07 09:01:17','2025-01-07 09:01:17'),(32,57,'练运球','2025-01-31',0,'2025-01-18 06:26:02','2025-01-18 06:26:44');
/*!40000 ALTER TABLE `health_goals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `health_profiles`
--

DROP TABLE IF EXISTS `health_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `health_profiles` (
  `profile_id` int NOT NULL AUTO_INCREMENT COMMENT '健康档案ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `medical_history` text COLLATE utf8mb4_unicode_ci COMMENT '用户的疾病史',
  `allergy_history` text COLLATE utf8mb4_unicode_ci COMMENT '用户的过敏史',
  `exercise_habits` text COLLATE utf8mb4_unicode_ci COMMENT '用户的运动习惯',
  `health_goals` text COLLATE utf8mb4_unicode_ci COMMENT '用户设定的健康目标',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '档案创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '档案更新时间',
  PRIMARY KEY (`profile_id`),
  UNIQUE KEY `health_profiles_pk` (`user_id`),
  CONSTRAINT `health_profiles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户的健康档案表，记录疾病史、过敏史及健康目标';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `health_profiles`
--

LOCK TABLES `health_profiles` WRITE;
/*!40000 ALTER TABLE `health_profiles` DISABLE KEYS */;
INSERT INTO `health_profiles` VALUES (10,58,'12  3','456','789','123213213','2025-01-05 09:07:16','2025-01-05 09:14:59'),(14,57,'疾病test','过敏test','运动习惯test','健康目标test','2025-01-18 06:02:56','2025-01-18 06:03:05');
/*!40000 ALTER TABLE `health_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password_hash` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密后的密码',
  `full_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户的全名',
  `gender` char(3) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户性别：男、女',
  `birth_date` date DEFAULT NULL COMMENT '用户出生日期',
  `height` decimal(5,2) DEFAULT NULL COMMENT '用户身高（单位：cm）',
  `weight` decimal(5,2) DEFAULT NULL COMMENT '用户体重（单位：kg）',
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户邮箱地址',
  `phone_number` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户电话号码',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '信息更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表，存储用户的个人基本资料及注册信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (0,'master','$2a$10$zGegJ.sq/2RT49f3bswwduVLH9S9QBrfcBkxNXwp7549HgGkXRlJa','徐铭泽','男','2004-07-20',175.00,75.00,'814637997@qq.com','13091638039','2025-01-06 08:20:32','2025-01-06 08:20:43'),(57,'admin','$2a$10$4VLmXx42ka2OH6pBExagjOE91vmzE1HUMEjpZrIN7tdePoTiNJlJm','zcm','女','2015-07-20',175.20,79.90,'123213@qq.com','17245628542','2025-01-05 06:26:15','2025-01-10 03:34:21'),(58,'root1','$2a$10$ApQCHQXTdJ5AnMPRooTYreRawScJlo4Iq82.avyY76DoDoUXiJqvm',NULL,NULL,NULL,NULL,NULL,'81463799@qq.com',NULL,'2025-01-05 06:38:43','2025-01-05 06:38:43'),(59,'admin1','$2a$10$enj9EJnhz3ODYQ15vgDKneXtQ3r2zKMxl4fuysqr/27pnZwfKjQaK',NULL,NULL,NULL,NULL,NULL,'8146397@qq.com',NULL,'2025-01-05 06:45:45','2025-01-05 06:45:45'),(60,'root2','$2a$10$EFVKGsTaXYFn/ULw/i1ILOtLyF6P2iqkq5hOqUG0.zWF8gE7BvAjK',NULL,NULL,NULL,NULL,NULL,'81497@qq.com',NULL,'2025-01-05 06:48:58','2025-01-05 06:48:58'),(62,'admin123','$2a$10$sqGeJ.wDpVhb./g.euo/puKxIU6PK8IDXhng4fkScuwDjPIKEecR6',NULL,NULL,NULL,NULL,NULL,'997@qq.com',NULL,'2025-01-05 07:03:50','2025-01-05 07:03:50'),(63,'qwer','$2a$10$uCwZvmyEkiJcvhBzeODvGOl1LgMPftNCg.cfSshXpIp.g9HLsnVWm','qwer','男','2024-12-29',102.10,85.21,'4651437@qq.com','14582647512','2025-01-05 09:54:50','2025-01-05 10:14:16'),(67,'test00','$2a$10$orLyWpDWyOHt7QYNFcI5kOkb5vb2lhBaFFoU/2spjIl3XkZhhwX/S',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-01-06 10:32:03','2025-01-06 10:32:03'),(68,'user_test','$2a$10$x.Jn6nxM2ayFmTB92t7IJOxioMCP7yj9sdUzHbH.VccY2pZuYztdq','zcm','女','2025-01-01',175.00,80.00,'7452@163.com','14725416987','2025-01-10 04:09:23','2025-01-10 04:10:37'),(69,'testtest','$2a$10$WvGEOqkns9A55f8KWAIQseKuXLESL574SiDzqDoJLRieWFaSSwQTW',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-01-17 10:27:38','2025-01-17 10:27:38'),(72,'teesst','$2a$10$epX5qLCf/tx0Q7iT5pih7.esQtFIsly5PGpGFQ3cwGCHGd1ev0bgW',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-01-17 13:47:13','2025-01-17 13:47:13'),(74,'testest','$2a$10$xjoBA6yGBIIFE7V4wayZQuPW0kdPzsk3Xvp3LtkyeEi6DaIl28JnW','tesset','女','2009-01-01',180.00,85.00,NULL,NULL,'2025-01-17 14:20:22','2025-01-17 14:20:22'),(78,'testestest','$2a$10$bQecS9u0aNZ0BkcwkNrlKur.FaXANRgaK3oHPmPNQp//n8yyJk0B2',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-01-17 14:27:08','2025-01-17 14:27:08'),(82,'111111','$2a$10$DcXbGXWPAEOj7V0qlhfaPOnp7LiMvFqWPOXObKHAo9Zf6KytCOEau',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-01-17 14:31:32','2025-01-17 14:31:32');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-06  9:31:07
