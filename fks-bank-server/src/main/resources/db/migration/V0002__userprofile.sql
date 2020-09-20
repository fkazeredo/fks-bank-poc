CREATE TABLE `user_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cpf` varchar(11) NOT NULL,
  `username` varchar(150) NOT NULL,
  `first_name` varchar(150) NOT NULL,
  `last_name` varchar(150) NOT NULL,
  `mail` varchar(150) NOT NULL,
  `phone` varchar(50) NULL,
  `picture_url` varchar(255) NULL,
  `zip_code` varchar(50) NULL,
  `street` varchar(255) NULL,
  `number` varchar(50) NULL,
  `complement` varchar(150) NULL,
  `neighborhood` varchar(150) NULL,
  `city` varchar(150) NULL,
  `password` varchar(255) NULL,
  `password_recovery_token_value` varchar(512) NULL,
  `password_recovery_token_expiration_date` datetime NULL,
  `password_recovery_token_created_date` datetime NULL,
  `permission` varchar(255) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `status` varchar(255) NOT NULL,
  `created_date` datetime NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `modified_date` datetime NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `version` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
);

INSERT INTO `user_profile` VALUES
    (1, '59738139082', 'manager@fksoftwares.com', 'Roberto', 'Manager', 'manager@fksoftwares.com', '2125698899', null, '20250000', 'Rua Maia de Lacerda', '105', 'Bloco 10, apt 20', 'Estácio', 'Rio de Janeiro', '$2a$10$2NRzCbyrjPWlP1q9haDlouF0VDDk6RQRjCfjU7f1X7Ynjw9gBdZWe', null, null, null, 'MANAGER', true, 'APPROVED', '2020-08-31 00:00', 'manager@fksoftwares.com', '2020-08-31 00:00', 'manager@fksoftwares.com', 0);

    
