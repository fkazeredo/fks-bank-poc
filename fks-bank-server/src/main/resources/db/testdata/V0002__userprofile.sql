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
    (1, '87417926029', 'admin@admin.com', 'Ricardo', 'Admin', 'admin@admin.com', '2125698899', null, '20250000', 'Rua Maia de Lacerda', '105', 'Bloco 10, apt 20', 'Estácio', 'Rio de Janeiro', '$2a$10$2NRzCbyrjPWlP1q9haDlouF0VDDk6RQRjCfjU7f1X7Ynjw9gBdZWe', null, null, null, 'MANAGER', true, 'APPROVED', '2020-08-31 00:00', 'admin@admin.com', '2020-08-31 00:00', 'admin@admin.com', 0),
    (2, '90489052061', 'customer@customer.com', 'João', 'Customer', 'customer@customer.com', '2125465669', null, '20521000', 'Rua dos Araújos', '500', 'Apt 502', 'Tijuca', 'Rio de Janeiro', '$2a$10$2NRzCbyrjPWlP1q9haDlouF0VDDk6RQRjCfjU7f1X7Ynjw9gBdZWe', null, null, null, 'CUSTOMER', true, 'APPROVED','2020-08-31 00:00', 'admin@admin.com', '2020-08-31 00:00', 'admin@admin.com', 0),
    (3, '42417974070', 'disabled@admin.com', 'Maria', 'Disabled', 'disabled@admin.com', '2122568963', null, '23520750', 'Rua Pedro Costa', '22', null, 'Santa Cruz', 'Rio de Janeiro', '$2a$10$2NRzCbyrjPWlP1q9haDlouF0VDDk6RQRjCfjU7f1X7Ynjw9gBdZWe', null, null, null,'MANAGER', false, 'APPROVED','2020-08-31 00:00', 'admin@admin.com', '2020-08-31 00:00', 'admin@admin.com', 0),
    (4, '36532576034', 'expired@admin.com', 'Joana', 'Expired', 'expired@admin.com', '2122736985', null, '57073469', 'Travessa Francisco Holanda', '35', null, 'Cidade Universitária', 'Maceió', '$2a$10$2NRzCbyrjPWlP1q9haDlouF0VDDk6RQRjCfjU7f1X7Ynjw9gBdZWe', 'cca1b723-bcfd-4ead-92b4-ac8b2340c8da', '2020-01-01 06:00', '2020-01-01 00:00', 'MANAGER', true, 'APPROVED', '2020-08-31 00:00', 'admin@admin.com', '2020-08-31 00:00', 'admin@admin.com', 0),
    (5, '99511159003', 'notexpired@admin.com', 'Marcia', 'Not Expired', 'notexpired@admin.com', '2125698899', null, '57073469', 'Travessa Francisco Holanda', '52', null, 'Cidade Universitária', 'Maceió', '$2a$10$2NRzCbyrjPWlP1q9haDlouF0VDDk6RQRjCfjU7f1X7Ynjw9gBdZWe', 'c85ed632-1d2b-43ef-ab4e-5ea49e2b0b7c', (NOW() + INTERVAL 6 HOUR), NOW(), 'MANAGER', true, 'APPROVED', '2020-08-31 00:00', 'admin@admin.com', '2020-08-31 00:00', 'admin@admin.com', 0),
    (6, '57299315068', 'another.customer@customer.com', 'Marcelo', 'Customer', 'another.customer@customer.com', '2125465669', null, '20521000', 'Rua dos Araújos', '500', 'Apt 502', 'Tijuca', 'Rio de Janeiro', '$2a$10$2NRzCbyrjPWlP1q9haDlouF0VDDk6RQRjCfjU7f1X7Ynjw9gBdZWe', null, null, null, 'CUSTOMER', true, 'APPROVED', '2020-08-31 00:00', 'admin@admin.com', '2020-08-31 00:00', 'admin@admin.com', 0),
    (7, '26876158039', 'pending@customer.com', 'João', 'Pending', 'pending@customer.com', '1125698899', null, '20521000', 'Rua dos Araújos', '500', 'Apt 502', 'Tijuca', 'Rio de Janeiro', '$2a$10$2NRzCbyrjPWlP1q9haDlouF0VDDk6RQRjCfjU7f1X7Ynjw9gBdZWe', '85de81ea-45f9-4844-a1e1-dda601b581b2', (NOW() + INTERVAL 6 HOUR), NOW(), 'CUSTOMER', true, 'PENDING', '2020-08-31 00:00', 'admin@admin.com', '2020-08-31 00:00', 'admin@admin.com', 0),
    (8, '38215218059', 'rejected@customer.com', 'Lana', 'Rejected', 'rejected@customer.com', '1125029866', null, '20521000', 'Rua dos Araújos', '500', 'Apt 502', 'Tijuca', 'Rio de Janeiro', '$2a$10$2NRzCbyrjPWlP1q9haDlouF0VDDk6RQRjCfjU7f1X7Ynjw9gBdZWe', '68b851a2-9558-4b26-a6ed-41b092def15a', (NOW() + INTERVAL 6 HOUR), NOW(), 'CUSTOMER', true, 'REJECTED', '2020-08-31 00:00', 'admin@admin.com', '2020-08-31 00:00', 'admin@admin.com', 0);
