set search_path to candicode;

alter table categories
    add column parent_category_id bigint default null;

alter table categories
    add constraint parent_category_fkey foreign key (parent_category_id) references categories (category_id) on delete cascade;

alter table results
    rename to submission_results;

alter table submissions
    add column submitted_code text;

alter table categories
    add column count integer default 0;