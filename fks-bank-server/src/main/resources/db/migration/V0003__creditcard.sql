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

CREATE TABLE `invoice_transaction` (
  `invoice_id` bigint(20) NOT NULL,
  `category` varchar(255) NOT NULL,
  `date` date NOT NULL,
  `description` varchar(150) NOT NULL,
  `value` numeric(10, 4) NOT NULL,
  CONSTRAINT `FK_invoice_transaction_invoice` FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`id`)
);