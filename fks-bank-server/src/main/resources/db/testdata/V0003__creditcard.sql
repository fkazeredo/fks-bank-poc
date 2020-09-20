CREATE TABLE `credit_card` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `number` varchar(16) NOT NULL,
  `member_since` date NOT NULL,
  `valid_thru` date NOT NULL,
  `max_limit` numeric(10, 4) NOT NULL,
  `current_limit` numeric(10, 4) NOT NULL,
  `due_day` int(11) NOT NULL,
  `locked` bit(1) NOT NULL,
  `user_profile_id` bigint(20) NOT NULL,
  `user_profile_full_name` varchar(255) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `created_date` datetime NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `modified_date` datetime NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `version` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_credit_card_user_profile` FOREIGN KEY (`user_profile_id`) REFERENCES `user_profile` (`id`)
);

INSERT INTO `credit_card` VALUES
    (1, '2720991770739550', '2020-01-06', '2025-01-06', 5000.0, 2500.0, 5, false, 2, 'João Customer', true, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (2, '5241864752280864', '2020-01-06', '2025-01-06', 5000.0, 2500.0, 5, false, 6, 'Marcelo Customer', true, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0);

CREATE TABLE `invoice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `start_date` date NOT NULL,
  `closing_date` date NOT NULL,
  `due_date` date NOT NULL,
  `credit_card_id` bigint(20) NOT NULL,
  `last_invoice_id` bigint(20) NULL,
  `created_date` datetime NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `modified_date` datetime NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `version` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_invoice_credit_card` FOREIGN KEY (`credit_card_id`) REFERENCES `credit_card` (`id`),
  CONSTRAINT `FK_invoice_last_invoice` FOREIGN KEY (`last_invoice_id`) REFERENCES `invoice` (`id`)
);

INSERT INTO `invoice` VALUES
    (1, '2019-12-31', '2020-01-29', '2020-02-05', 1, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (2, '2020-01-30', '2020-02-27', '2020-03-05', 1, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (3, '2019-02-28', '2020-03-30', '2020-04-06', 1, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (4, '2020-03-31', '2020-04-29', '2020-05-05', 1, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (5, '2020-04-30', '2020-06-01', '2020-06-08', 1, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (6, '2020-06-02', '2020-06-29', '2020-07-06', 1, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (7, '2020-06-30', '2020-07-29', '2020-08-05', 1, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (8, '2020-07-30', '2020-08-29', '2020-09-05', 1, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (9, '2020-08-30', '2020-09-30', '2020-10-05', 1, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (10, '2019-12-31', '2020-01-29', '2020-02-05', 2, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (11, '2020-01-30', '2020-02-27', '2020-03-05', 2, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (12, '2019-02-28', '2020-03-30', '2020-04-06', 2, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (13, '2020-03-31', '2020-04-29', '2020-05-05', 2, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (14, '2020-04-30', '2020-06-01', '2020-06-08', 2, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (15, '2020-06-02', '2020-06-29', '2020-07-06', 2, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (16, '2020-06-30', '2020-07-29', '2020-08-05', 2, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (17, '2020-07-30', '2020-08-29', '2020-09-05', 2, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0),
    (18, '2020-08-30', '2020-09-30', '2020-10-05', 2, null, '2020-01-06 14:00', 'admin@admin.com', '2020-01-06 14:00', 'admin@admin.com', 0);

CREATE TABLE `invoice_transaction` (
  `invoice_id` bigint(20) NOT NULL,
  `category` varchar(255) NOT NULL,
  `date` date NOT NULL,
  `description` varchar(150) NOT NULL,
  `value` numeric(10, 4) NOT NULL,
  CONSTRAINT `FK_invoice_transaction_invoice` FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`id`)
);

INSERT INTO `invoice_transaction` VALUES
    (1, 'CLOTHING', '2020-01-06', '2 CAMISETAS BÁSICAS - CIA HERING', -20.25),
    (2, 'CLOTHING', '2020-02-06', '2 CAMISETAS BÁSICAS - CIA HERING', -20.25),
    (3, 'CLOTHING', '2020-03-06', '2 CAMISETAS BÁSICAS - CIA HERING', -20.25),
    (1, 'EDUCATION', '2020-01-15', 'CURSO SPRING BOOT - UDEMY', -256.39),
    (1, 'RESTAURANT', '2020-01-27', 'KIT SUSHI E SASHIMI - JAPA DELIVERY', -75.90),
    (2, 'RESTAURANT', '2020-02-27', 'KIT SUSHI E SASHIMI - JAPA DELIVERY', -75.90),
    (9, 'RESTAURANT', '2020-09-10', 'KIT MASTER SUSHI E SASHIMI - JAPA DELIVERY', -900.00);