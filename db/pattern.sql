CREATE TABLE `pattern` (
  `pattern_id` int(11) NOT NULL AUTO_INCREMENT,
  `base_trade_id` int(11) DEFAULT NULL,
  `config` varchar(25) NOT NULL,
  `confidence` float DEFAULT NULL,
  `performance` float DEFAULT NULL,
  `trend` float DEFAULT NULL,
  `voting_pattern_ids` varchar(2048) DEFAULT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`pattern_id`),
  UNIQUE KEY `pattern_id_UNIQUE` (`pattern_id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8;
