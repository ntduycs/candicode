--
-- PostgreSQL database dump
--

-- Dumped from database version 12.3
-- Dumped by pg_dump version 12.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

CREATE SCHEMA IF NOT EXISTS candicode;

--
-- Name: admins; Type: TABLE; Schema: candicode; Owner: candicode
--

CREATE TABLE candicode.admins
(
    user_id bigint NOT NULL
);


ALTER TABLE candicode.admins
    OWNER TO candicode;

--
-- Name: categories; Type: TABLE; Schema: candicode; Owner: candicode
--

CREATE TABLE candicode.categories
(
    category_id bigint                 NOT NULL,
    text        character varying(255) NOT NULL
);


ALTER TABLE candicode.categories
    OWNER TO candicode;

--
-- Name: challenge_categories; Type: TABLE; Schema: candicode; Owner: candicode
--

CREATE TABLE candicode.challenge_categories
(
    challenge_category_id bigint NOT NULL,
    category_id           bigint NOT NULL,
    challenge_id          bigint NOT NULL
);


ALTER TABLE candicode.challenge_categories
    OWNER TO candicode;

--
-- Name: challenge_comments; Type: TABLE; Schema: candicode; Owner: candicode
--

CREATE TABLE candicode.challenge_comments
(
    challenge_comment_id bigint                 NOT NULL,
    created_at           timestamp without time zone,
    updated_at           timestamp without time zone,
    author               character varying(255) NOT NULL,
    content              character varying(255) NOT NULL,
    dislikes             integer DEFAULT 0,
    likes                integer DEFAULT 0,
    challenge_id         bigint                 NOT NULL
);


ALTER TABLE candicode.challenge_comments
    OWNER TO candicode;

--
-- Name: challenge_configs; Type: TABLE; Schema: candicode; Owner: candicode
--

CREATE TABLE candicode.challenge_configs
(
    challenge_config_id  bigint                 NOT NULL,
    created_at           timestamp without time zone,
    updated_at           timestamp without time zone,
    challenge_dir        character varying(255) NOT NULL,
    compatible           boolean DEFAULT FALSE,
    compile_path         character varying(255),
    implemented_path     character varying(255) NOT NULL,
    non_implemented_path character varying(255) NOT NULL,
    run_path             character varying(255) NOT NULL,
    challenge_id         bigint                 NOT NULL,
    language_id          bigint                 NOT NULL
);


ALTER TABLE candicode.challenge_configs
    OWNER TO candicode;

--
-- Name: challenges; Type: TABLE; Schema: candicode; Owner: candicode
--

CREATE TABLE candicode.challenges
(
    challenge_id  bigint                 NOT NULL,
    created_at    timestamp without time zone,
    updated_at    timestamp without time zone,
    banner        character varying(255),
    description   text                   NOT NULL,
    level         character varying(255) NOT NULL,
    point         integer,
    tc_in_format  character varying(255) NOT NULL,
    tc_out_format character varying(255) NOT NULL,
    title         character varying(255) NOT NULL,
    author_id     bigint                 NOT NULL
);


ALTER TABLE candicode.challenges
    OWNER TO candicode;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: candicode
--

CREATE SEQUENCE candicode.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE candicode.hibernate_sequence
    OWNER TO candicode;

--
-- Name: languages; Type: TABLE; Schema: candicode; Owner: candicode
--

CREATE TABLE candicode.languages
(
    language_id bigint                 NOT NULL,
    text        character varying(255) NOT NULL
);


ALTER TABLE candicode.languages
    OWNER TO candicode;

--
-- Name: partners; Type: TABLE; Schema: candicode; Owner: candicode
--

CREATE TABLE candicode.partners
(
    user_id bigint NOT NULL
);


ALTER TABLE candicode.partners
    OWNER TO candicode;

--
-- Name: plans; Type: TABLE; Schema: candicode; Owner: candicode
--

CREATE TABLE candicode.plans
(
    plan_id         bigint                 NOT NULL,
    text            character varying(255) NOT NULL,
    validity_period bigint DEFAULT -1
);


ALTER TABLE candicode.plans
    OWNER TO candicode;

--
-- Name: results; Type: TABLE; Schema: candicode; Owner: candicode
--

CREATE TABLE candicode.results
(
    result_id     bigint  NOT NULL,
    created_at    timestamp without time zone,
    updated_at    timestamp without time zone,
    pass          boolean NOT NULL,
    submission_id bigint  NOT NULL,
    testcase_id   bigint  NOT NULL
);


ALTER TABLE candicode.results
    OWNER TO candicode;

--
-- Name: students; Type: TABLE; Schema: candicode; Owner: candicode
--

CREATE TABLE candicode.students
(
    gained_point bigint DEFAULT 100,
    user_id      bigint NOT NULL,
    plan_id      bigint DEFAULT 1
);


ALTER TABLE candicode.students
    OWNER TO candicode;

--
-- Name: submissions; Type: TABLE; Schema: candicode; Owner: candicode
--

CREATE TABLE candicode.submissions
(
    submission_id   bigint  NOT NULL,
    created_at      timestamp without time zone,
    updated_at      timestamp without time zone,
    compile_success boolean NOT NULL,
    completion_time double precision,
    execution_time  double precision,
    used_memory     double precision,
    author_id       bigint  NOT NULL,
    challenge_id    bigint  NOT NULL
);


ALTER TABLE candicode.submissions
    OWNER TO candicode;

--
-- Name: testcases; Type: TABLE; Schema: candicode; Owner: candicode
--

CREATE TABLE candicode.testcases
(
    testcase_id  bigint                 NOT NULL,
    created_at   timestamp without time zone,
    updated_at   timestamp without time zone,
    output       character varying(255) NOT NULL,
    hidden       boolean,
    input        character varying(255) NOT NULL,
    challenge_id bigint                 NOT NULL
);


ALTER TABLE candicode.testcases
    OWNER TO candicode;

--
-- Name: user_roles; Type: TABLE; Schema: candicode; Owner: candicode
--

CREATE TABLE candicode.user_roles
(
    user_id bigint                 NOT NULL,
    role    character varying(255) NOT NULL
);


ALTER TABLE candicode.user_roles
    OWNER TO candicode;

--
-- Name: users; Type: TABLE; Schema: candicode; Owner: candicode
--

CREATE TABLE candicode.users
(
    user_id    bigint                 NOT NULL,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    email      character varying(255) NOT NULL,
    enable     boolean default TRUE,
    first_name character varying(255) NOT NULL,
    last_name  character varying(255) NOT NULL,
    password   character varying(255) NOT NULL
);


ALTER TABLE candicode.users
    OWNER TO candicode;

--
-- Name: admins admins_pkey; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.admins
    ADD CONSTRAINT admins_pkey PRIMARY KEY (user_id);


--
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (category_id);


--
-- Name: challenge_categories challenge_categories_pkey; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.challenge_categories
    ADD CONSTRAINT challenge_categories_pkey PRIMARY KEY (challenge_category_id);


--
-- Name: challenge_comments challenge_comments_pkey; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.challenge_comments
    ADD CONSTRAINT challenge_comments_pkey PRIMARY KEY (challenge_comment_id);


--
-- Name: challenge_configs challenge_configs_pkey; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.challenge_configs
    ADD CONSTRAINT challenge_configs_pkey PRIMARY KEY (challenge_config_id);


--
-- Name: challenges challenges_pkey; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.challenges
    ADD CONSTRAINT challenges_pkey PRIMARY KEY (challenge_id);


--
-- Name: languages languages_pkey; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.languages
    ADD CONSTRAINT languages_pkey PRIMARY KEY (language_id);


--
-- Name: partners partners_pkey; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.partners
    ADD CONSTRAINT partners_pkey PRIMARY KEY (user_id);


--
-- Name: plans plans_pkey; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.plans
    ADD CONSTRAINT plans_pkey PRIMARY KEY (plan_id);


--
-- Name: results results_pkey; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.results
    ADD CONSTRAINT results_pkey PRIMARY KEY (result_id);


--
-- Name: students students_pkey; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.students
    ADD CONSTRAINT students_pkey PRIMARY KEY (user_id);


--
-- Name: submissions submissions_pkey; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.submissions
    ADD CONSTRAINT submissions_pkey PRIMARY KEY (submission_id);


--
-- Name: testcases testcases_pkey; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.testcases
    ADD CONSTRAINT testcases_pkey PRIMARY KEY (testcase_id);


--
-- Name: challenge_configs uk5iliht9hhem1b2f54ti8382u; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.challenge_configs
    ADD CONSTRAINT uk5iliht9hhem1b2f54ti8382u UNIQUE (challenge_id, language_id);


--
-- Name: languages uk_5so8nskolp3h72pah4xixc9sc; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.languages
    ADD CONSTRAINT uk_5so8nskolp3h72pah4xixc9sc UNIQUE (text);


--
-- Name: categories uk_duwvonekljhgpk4pglnt56mfa; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.categories
    ADD CONSTRAINT uk_duwvonekljhgpk4pglnt56mfa UNIQUE (text);


--
-- Name: plans uk_rw75lfjnvlyh9hxyhjs1dcv1x; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.plans
    ADD CONSTRAINT uk_rw75lfjnvlyh9hxyhjs1dcv1x UNIQUE (text);


--
-- Name: users uk_sx468g52bpetvlad2j9y0lptc; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.users
    ADD CONSTRAINT uk_sx468g52bpetvlad2j9y0lptc UNIQUE (email);


--
-- Name: challenges uk_t5dasoln9te3aw9nijin7o0bw; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.challenges
    ADD CONSTRAINT uk_t5dasoln9te3aw9nijin7o0bw UNIQUE (title);


--
-- Name: challenge_categories ukk0bc95d51v90pmuxxidt037q4; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.challenge_categories
    ADD CONSTRAINT ukk0bc95d51v90pmuxxidt037q4 UNIQUE (challenge_id, category_id);


--
-- Name: results ukrears5g6ehrwreqc53p5o2q87; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.results
    ADD CONSTRAINT ukrears5g6ehrwreqc53p5o2q87 UNIQUE (submission_id, testcase_id);


--
-- Name: user_roles user_roles_pkey; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- Name: submissions fk1v4tcu89n68xe2ykuyv5r7959; Type: FK CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.submissions
    ADD CONSTRAINT fk1v4tcu89n68xe2ykuyv5r7959 FOREIGN KEY (author_id) REFERENCES candicode.students (user_id) ON DELETE CASCADE;


--
-- Name: challenge_configs fk3ux426hd6rtyu65e0jtkt9d0q; Type: FK CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.challenge_configs
    ADD CONSTRAINT fk3ux426hd6rtyu65e0jtkt9d0q FOREIGN KEY (language_id) REFERENCES candicode.languages (language_id) ON DELETE CASCADE;


--
-- Name: challenge_categories fk8j762l98145snqonyftalffty; Type: FK CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.challenge_categories
    ADD CONSTRAINT fk8j762l98145snqonyftalffty FOREIGN KEY (category_id) REFERENCES candicode.categories (category_id) ON DELETE CASCADE;


--
-- Name: testcases fkb2pnmopxuqd3bdnho22xrnig; Type: FK CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.testcases
    ADD CONSTRAINT fkb2pnmopxuqd3bdnho22xrnig FOREIGN KEY (challenge_id) REFERENCES candicode.challenges (challenge_id) ON DELETE CASCADE;


--
-- Name: submissions fkblbgb61gxgclqvjeumlnxu2g9; Type: FK CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.submissions
    ADD CONSTRAINT fkblbgb61gxgclqvjeumlnxu2g9 FOREIGN KEY (challenge_id) REFERENCES candicode.challenges (challenge_id) ON DELETE CASCADE;


--
-- Name: results fkblwh3w4ts9w5wgs122muuq281; Type: FK CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.results
    ADD CONSTRAINT fkblwh3w4ts9w5wgs122muuq281 FOREIGN KEY (submission_id) REFERENCES candicode.submissions (submission_id) ON DELETE CASCADE;


--
-- Name: students fkdt1cjx5ve5bdabmuuf3ibrwaq; Type: FK CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.students
    ADD CONSTRAINT fkdt1cjx5ve5bdabmuuf3ibrwaq FOREIGN KEY (user_id) REFERENCES candicode.users (user_id) ON DELETE CASCADE;


--
-- Name: admins fkgc8dtql9mkq268detxiox7fpm; Type: FK CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.admins
    ADD CONSTRAINT fkgc8dtql9mkq268detxiox7fpm FOREIGN KEY (user_id) REFERENCES candicode.users (user_id) ON DELETE CASCADE;


--
-- Name: results fkgdgguyyqpp4h0un3rj0ydyuv0; Type: FK CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.results
    ADD CONSTRAINT fkgdgguyyqpp4h0un3rj0ydyuv0 FOREIGN KEY (testcase_id) REFERENCES candicode.testcases (testcase_id) ON DELETE CASCADE;


--
-- Name: user_roles fkhfh9dx7w3ubf1co1vdev94g3f; Type: FK CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.user_roles
    ADD CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES candicode.users (user_id) ON DELETE CASCADE;


--
-- Name: challenges fklmvpo5d0d11lw0mrt1ghmohkq; Type: FK CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.challenges
    ADD CONSTRAINT fklmvpo5d0d11lw0mrt1ghmohkq FOREIGN KEY (author_id) REFERENCES candicode.users (user_id) ON DELETE CASCADE;


--
-- Name: challenge_comments fklq1s5eb5mfha0ufdd3wd7tqgh; Type: FK CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.challenge_comments
    ADD CONSTRAINT fklq1s5eb5mfha0ufdd3wd7tqgh FOREIGN KEY (challenge_id) REFERENCES candicode.challenges (challenge_id) ON DELETE CASCADE;


--
-- Name: students fknp8abbl9bsv4l4mqiqtetu2g0; Type: FK CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.students
    ADD CONSTRAINT fknp8abbl9bsv4l4mqiqtetu2g0 FOREIGN KEY (plan_id) REFERENCES candicode.plans (plan_id) ON DELETE SET DEFAULT;


--
-- Name: challenge_categories fkqpg5b03xauwsu1e2jd3lrufjd; Type: FK CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.challenge_categories
    ADD CONSTRAINT fkqpg5b03xauwsu1e2jd3lrufjd FOREIGN KEY (challenge_id) REFERENCES candicode.challenges (challenge_id) ON DELETE CASCADE;


--
-- Name: partners fkrd9a2kyvi3j1dbsrkc74ghtw3; Type: FK CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.partners
    ADD CONSTRAINT fkrd9a2kyvi3j1dbsrkc74ghtw3 FOREIGN KEY (user_id) REFERENCES candicode.users (user_id) ON DELETE CASCADE;


--
-- Name: challenge_configs fkt8kjlom0hq4anj6ava62mfqer; Type: FK CONSTRAINT; Schema: candicode; Owner: candicode
--

ALTER TABLE ONLY candicode.challenge_configs
    ADD CONSTRAINT fkt8kjlom0hq4anj6ava62mfqer FOREIGN KEY (challenge_id) REFERENCES candicode.challenges (challenge_id) ON DELETE CASCADE;

ALTER TABLE ONLY candicode.challenges
    ADD COLUMN tags varchar;

--
-- PostgreSQL database dump complete
--

-- Then, login to psql with user = candicode and execute the command: \i path_to_data.sql

