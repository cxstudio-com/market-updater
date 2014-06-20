CREATE TABLE `symbol` (
  `ticker` varchar(24) NOT NULL,
  `exchange` varchar(12) NOT NULL,
  `symbol_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `last_sale` float DEFAULT NULL,
  `market_cap` double DEFAULT NULL,
  `ipo_year` varchar(50) DEFAULT NULL,
  `sector` varchar(128) DEFAULT NULL,
  `industry` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `last_update` datetime DEFAULT CURRENT_TIMESTAMP,
  `collectable` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`symbol_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7448 DEFAULT CHARSET=utf8;
