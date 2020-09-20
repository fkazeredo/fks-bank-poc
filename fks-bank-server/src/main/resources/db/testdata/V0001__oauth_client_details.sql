CREATE TABLE oauth_client_details (
    client_id VARCHAR(255),
    resource_ids VARCHAR(255),
    client_secret VARCHAR(255),
    scope VARCHAR(255),
    authorized_grant_types VARCHAR(255),
    web_server_redirect_uri VARCHAR(255),
    authorities VARCHAR(255),
    access_token_validity INT(11),
    refresh_token_validity INT(11),
    additional_information VARCHAR(4096),
    autoapprove VARCHAR(255),
    PRIMARY KEY (client_id)
);

INSERT INTO oauth_client_details
    (client_id, client_secret, scope, authorized_grant_types,
    web_server_redirect_uri, authorities, access_token_validity,
    refresh_token_validity, additional_information, autoapprove)
VALUES
    ('fkbank_ui', '$2a$10$2NRzCbyrjPWlP1q9haDlouF0VDDk6RQRjCfjU7f1X7Ynjw9gBdZWe', 'read,write','password,refresh_token', null, null, 60, 36000, null, null);