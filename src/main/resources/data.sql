set search_path to candicode;

insert into candicode.plans (plan_id, name)
values (1, 'Basic', -1),
       (2, 'Extended', 100),
       (3, 'Premium', 100);

insert into candicode.languages (language_id, text)
values (1, 'Java'),
       (2, 'Cpp'),
       (3, 'C'),
       (4, 'Python'),
       (5, 'JS'),
       (6, 'SQL');

insert into candicode.categories (category_id, text, parent_category_id, count)
values (1, 'Algorithm', null, 0),
       (2, 'Complexity', null, 0),
       (3, 'Database', null, 0),
       (4, 'DataStructure', null, 0);

insert into candicode.users (user_id, created_at, updated_at, email, enable, first_name, last_name, password)
values (1, '2020-06-06 16:35:44.866245', '2020-06-06 16:35:44.866245', 'admin@gmail.com', 't', 'Super', 'Admin',
        '$2a$10$4uC1E72bwmYZSRRqYVeazeyz/9qmLAn0agfrZNMG4cCnm6dHk9XkK');

insert into candicode.user_roles (user_id, role)
values (1, 'Admin'),
       (1, 'SuperAdmin');

select nextval('candicode.hibernate_sequence');

insert into candicode.admins (user_id)
values (1);