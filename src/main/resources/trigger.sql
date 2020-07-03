create or replace function get_default_student_plan()
    returns trigger as
$$
begin
    if new.user_type = 'student' then
        new.student_plan_id := 1;
    end if;
    return new;
end
$$ language 'plpgsql';

create trigger default_student_plan_trigger
    after insert on users
    for each row
execute procedure get_default_student_plan()
