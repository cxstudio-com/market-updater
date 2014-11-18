CREATE TABLE `trade` (
  `trade_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `symbol_id` int(11) NOT NULL,
  `datetime` datetime NOT NULL,
  `open` float DEFAULT NULL,
  `close` float DEFAULT NULL,
  `high` float DEFAULT NULL,
  `low` float DEFAULT NULL,
  `percent_change` float DEFAULT NULL,
  `bid` float DEFAULT NULL,
  `ask` float DEFAULT NULL,
  `volume` int(11) DEFAULT NULL,
  PRIMARY KEY (`trade_id`),
  UNIQUE KEY `idx_trade_datetime_symbol_id` (`datetime`,`symbol_id`),
  KEY `idx_trade_datetime` (`datetime`),
  KEY `idx_symbol_id` (`symbol_id`)
) ENGINE=InnoDB AUTO_INCREMENT=939890 DEFAULT CHARSET=utf8;
