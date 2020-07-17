insert into roles
values (1, 'student'),
       (2, 'challenge creator'),
       (3, 'tutorial creator'),
       (4, 'contest creator'),
       (5, 'admin'),
       (6, 'super admin');

insert into categories
values (1, 'data structure'),
       (2, 'algorithm'),
       (3, 'database'),
       (4, 'multithreading'),
       (5, 'networking'),
       (6, 'oop');

insert into languages
values (1, 'java'),
       (2, 'cpp'),
       (3, 'c'),
       (4, 'js'),
       (5, 'python');

insert into student_plans ('student_plan_id', 'duration', 'name', 'price')
values (1, -1, 'basic', 0),
       (2, 30, 'standard', 80000),
       (3, 30, 'premium', 100000);