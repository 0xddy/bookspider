/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50726
Source Host           : localhost:3306
Source Database       : bookstore

Target Server Type    : MYSQL
Target Server Version : 50726
File Encoding         : 65001

Date: 2020-10-09 15:13:16
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for bookurl
-- ----------------------------
DROP TABLE IF EXISTS `bookurl`;
CREATE TABLE `bookurl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '小说名称',
  `author` varchar(20) DEFAULT NULL,
  `url` varchar(255) NOT NULL,
  `project_id` varchar(20) NOT NULL,
  `status` int(11) DEFAULT NULL,
  `category` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `name` (`name`) USING HASH,
  KEY `author` (`author`)
) ENGINE=InnoDB AUTO_INCREMENT=455354 DEFAULT CHARSET=utf8mb4;
