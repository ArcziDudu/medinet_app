insert  into address(country, city, postal_code, street)
values('Polska','Admin', '33-314', 'adminowa 32');

insert into medinet_user (email, password, active)
values ('admin@admin.pl', '$2a$12$TwQsp1IusXTDl7LwZqL0qeu49Ypr6vRdEzRq2vAsgb.zvOtrnzm5G', true);

insert into patient(user_id, name, surname, email, phone, address_id)
values (1, 'Admin', 'Admin', 'admin@admin.pl', '+48 000 000 000', 1);

insert into medinet_role (role_id, role) values (1, 'DOCTOR'), (2, 'PATIENT'),(3, 'ADMIN'), (4, 'REST_API');

insert into medinet_user_role (user_id, role_id) values (1, 3);
