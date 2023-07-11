insert into address (country, city, postal_code, street)
values ('Poland', 'Dukla', '38-450', '18 Lotheville Point');

insert into medinet_user (email, password, active)
values ('doctor@example.com', '$2a$12$TwQsp1IusXTDl7LwZqL0qeu49Ypr6vRdEzRq2vAsgb.zvOtrnzm5G', true);

insert into doctor (user_id, name, surname, email, price_for_visit, specialization, address_id)
values (2,'Piotr', 'Piwowar', 'doctor@example.com', 291, 'Kardiolog', 2);

insert into medinet_user_role (user_id, role_id) values (2, 1);

insert into address (country, city, postal_code, street)
values ('Poland', 'Dukla2', '38-450', '18 Lotheville2 Point');

insert into medinet_user (email, password, active)
values ('doctor2@example.com', '$2a$12$TwQsp1IusXTDl7LwZqL0qeu49Ypr6vRdEzRq2vAsgb.zvOtrnzm5G', true);

insert into doctor (user_id, name, surname, email, price_for_visit, specialization, address_id)
values (3,'Piotr', 'Piwowar', 'doctor2@example.com', 291, 'Kardiolog', 3);

insert into medinet_user_role (user_id, role_id) values (3, 1);


insert into medinet_user ( email, password, active)
values ('test_user@test.pl', '$2a$12$TwQsp1IusXTDl7LwZqL0qeu49Ypr6vRdEzRq2vAsgb.zvOtrnzm5G', true);