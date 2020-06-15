ALTER TABLE ONLY candicode.challenges
    ADD COLUMN tags varchar;

ALTER TABLE ONLY candicode.challenges
    ALTER COLUMN tags TYPE character varying(255);

alter table only candicode.challenges
    add column contest_challenge boolean default false;


create table candicode.tutorials
(
    tutorial_id bigint       not null,
    title       varchar(255) not null,
    banner      varchar(255),
    content     text         not null,
    description varchar(255) not null,
    created_at  timestamp without time zone,
    updated_at  timestamp without time zone
);

alter table candicode.tutorials
    owner to candicode;
alter table only candicode.tutorials
    add constraint tutorials_pkey primary key (tutorial_id);
create index tutorials_title_idx on candicode.tutorials (title);
alter table candicode.tutorials
    alter column description type character varying(255);
alter table candicode.tutorials
    add column tags character varying(255);
create index tutorials_tags_idx on candicode.tutorials (tags);



create table candicode.tutorial_comments
(
    tutorial_comment_id bigint                 not null,
    content             character varying(255) not null,
    likes               integer default 0,
    dislikes            integer default 0,
    author              character varying(255) not null,
    tutorial_id         bigint                 not null
);

alter table candicode.tutorial_comments
    owner to candicode;
alter table only candicode.tutorial_comments
    add constraint tutorial_comments_pkey primary key (tutorial_comment_id);
alter table only candicode.tutorial_comments
    add constraint tutorials_fkey foreign key (tutorial_id) references candicode.tutorials (tutorial_id) on delete cascade;
alter table only candicode.tutorial_comments
    add column created_at timestamp without time zone;
alter table only candicode.tutorial_comments
    add column updated_at timestamp without time zone;



create table candicode.tutorial_categories
(
    tutorial_category_id bigint not null,
    tutorial_id          bigint not null,
    category_id          bigint not null
);

alter table candicode.tutorial_categories
    owner to candicode;
alter table only candicode.tutorial_categories
    add constraint tutorial_categories_pkey primary key (tutorial_category_id);
alter table only candicode.tutorial_categories
    add constraint tutorial_categories_unique_idx unique (tutorial_id, category_id);
alter table only candicode.tutorial_categories
    add constraint tutorial_categories_tutorial_fkey foreign key (tutorial_id) references candicode.tutorials (tutorial_id) on delete cascade;
alter table only candicode.tutorial_categories
    add constraint tutorial_categories_category_fkey foreign key (category_id) references candicode.categories (category_id) on delete cascade;

alter table only candicode.tutorials
    add column likes    integer default 0,
    add column dislikes integer default 0;

alter table only candicode.tutorials
    add column author_id bigint not null;

alter table only candicode.tutorials
    add constraint tutorial_author_fkey foreign key (author_id) references candicode.users (user_id) on delete cascade;