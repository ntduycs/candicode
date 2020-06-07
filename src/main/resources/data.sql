insert into candicode.plans
values (1, 'Basic', -1),
       (2, 'Extended', 100),
       (3, 'Premium', 100);

insert into candicode.languages
values (1, 'Java'),
       (2, 'Cpp'),
       (3, 'C'),
       (4, 'Python'),
       (5, 'JS'),
       (6, 'SQL');

insert into candicode.categories
values (1, 'Algorithm'),
       (2, 'Complexity'),
       (3, 'Database'),
       (4, 'DataStructure');

select nextval('candicode.hibernate_sequence');

insert into candicode.users
values (1, '2020-06-06 16:35:44.866245', '2020-06-06 16:35:44.866245', 'admin@gmail.com', 't', 'Super', 'Admin',
        '$2a$10$4uC1E72bwmYZSRRqYVeazeyz/9qmLAn0agfrZNMG4cCnm6dHk9XkK');

insert into candicode.user_roles
values (1, 'Admin'),
       (1, 'SuperAdmin');

insert into candicode.admins
values (1);