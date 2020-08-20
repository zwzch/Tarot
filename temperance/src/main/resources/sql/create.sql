use temperance;
DROP TABLE IF EXISTS `temperance`;
CREATE TABLE `temperance` (
  `tag` varchar(128)  NOT NULL DEFAULT '' COMMENT '业务标识',
  `max_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '业务最大id',
  `step` int(11) NOT NULL COMMENT '业务步长',
  `description` varchar(256)  DEFAULT NULL COMMENT '业务描述',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`tag`)
) ENGINE=InnoDB;
