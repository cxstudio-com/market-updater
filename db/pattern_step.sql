CREATE TABLE `pattern_step` (
  `step_id` int(11) NOT NULL AUTO_INCREMENT,
  `pattern_id` int(11) NOT NULL,
  `change` float DEFAULT NULL,
  `index` smallint(6) DEFAULT NULL,
  `date_time` datetime DEFAULT NULL,
  PRIMARY KEY (`step_id`),
  UNIQUE KEY `step_id_UNIQUE` (`step_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1621 DEFAULT CHARSET=utf8;
