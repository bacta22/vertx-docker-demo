--
-- PostgreSQL database dump
--

-- Dumped from database version 13.7 (Ubuntu 13.7-1.pgdg20.04+1)
-- Dumped by pg_dump version 13.7 (Ubuntu 13.7-1.pgdg20.04+1)

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

--
-- Name: user_profile; Type: TABLE; Schema: public; Owner: deodd
--

CREATE TABLE public.user_profile (
    wallet character varying(64) NOT NULL,
    user_name character varying,
    avatar_id integer,
    public_address character varying,
    nonce integer,
    max_streak_length integer DEFAULT 0 NOT NULL,
    streak_amount numeric DEFAULT 0 NOT NULL,
    current_streak_length integer DEFAULT 0 NOT NULL,
    current_streak_amount numeric DEFAULT 0 NOT NULL,
    block_timestamp numeric
);
ALTER TABLE public.user_profile ALTER COLUMN nonce SET DEFAULT floor(random() * 10000)::int;

ALTER TABLE public.user_profile OWNER TO deodd;

--
-- Data for Name: user_profile; Type: TABLE DATA; Schema: public; Owner: deodd
--

INSERT INTO public.user_profile (wallet, user_name, avatar_id, max_streak_length, streak_amount, current_streak_length, current_streak_amount, block_timestamp) VALUES
('0x3C44CdDdB6a900fa2b585dd299e03d12FA4293BC', NULL, NULL,	4,	8000000000000000000,	0	,0	,1676026223),
('0x90F79bf6EB2c4f870365E785982E1f101E93b906', NULL, NULL,	3,	6000000000000000000,	1	,2000000000000000000	,1676027171),
('0x70997970C51812dc3A010C7d01b50e0d17dc79C8', NULL, NULL,	4,	8000000000000000000,	4	,8000000000000000000	,1676025999);



--
-- Name: user_profile user_profile_wallet_key; Type: CONSTRAINT; Schema: public; Owner: deodd
--

ALTER TABLE ONLY public.user_profile
    ADD CONSTRAINT user_profile_wallet_key UNIQUE (wallet);


--
-- PostgreSQL database dump complete
--

