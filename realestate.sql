--
-- PostgreSQL database dump
--

\restrict hJkNhXEuL4Z2K4FGnyRlX26jeIayq3IDKa6uc1XVHE5HpPw1l8jbqhmPBBfmIyd

-- Dumped from database version 16.13 (Debian 16.13-1.pgdg13+1)
-- Dumped by pg_dump version 16.13 (Debian 16.13-1.pgdg13+1)

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
-- Name: admin; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admin (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    is_deleted boolean NOT NULL,
    is_super_admin boolean,
    user_id uuid NOT NULL
);


ALTER TABLE public.admin OWNER TO postgres;

--
-- Name: allocated_employee_project; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.allocated_employee_project (
    employee_id uuid NOT NULL,
    project_id uuid NOT NULL
);


ALTER TABLE public.allocated_employee_project OWNER TO postgres;

--
-- Name: amenity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.amenity (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    amenity_name character varying(255) NOT NULL,
    project_id uuid NOT NULL
);


ALTER TABLE public.amenity OWNER TO postgres;

--
-- Name: bank_project_info; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bank_project_info (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    bank_name character varying(255) NOT NULL,
    branch_name character varying(255) NOT NULL,
    contact_number character varying(15) NOT NULL,
    contact_person character varying(255) NOT NULL,
    project_id uuid NOT NULL
);


ALTER TABLE public.bank_project_info OWNER TO postgres;

--
-- Name: booking; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.booking (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    agreement_amount character varying(20) NOT NULL,
    booking_amount character varying(20) NOT NULL,
    booking_date date NOT NULL,
    cheque_date date NOT NULL,
    cheque_no character varying(20) NOT NULL,
    gst_amount character varying(20) NOT NULL,
    gst_percentage real NOT NULL,
    infra_amount character varying(20) NOT NULL,
    is_cancelled boolean NOT NULL,
    is_registered boolean NOT NULL,
    rate character varying(20) NOT NULL,
    reg_date date,
    reg_no character varying(20),
    remark character varying(255),
    total_amount character varying(20) NOT NULL,
    client_id uuid NOT NULL,
    enquiry_id uuid,
    property_id uuid NOT NULL
);


ALTER TABLE public.booking OWNER TO postgres;

--
-- Name: client_user_info; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.client_user_info (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    aadhar_no character varying(20),
    address character varying(500),
    city character varying(255),
    client_name character varying(255) NOT NULL,
    company character varying(255),
    dob date,
    email character varying(255),
    landline_number character varying(15),
    mobile_number character varying(15),
    occupation character varying(255),
    pan_no character varying(20)
);


ALTER TABLE public.client_user_info OWNER TO postgres;

--
-- Name: disbursement; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.disbursement (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    description character varying(255),
    disbursement_title character varying(255) NOT NULL,
    percentage real NOT NULL,
    project_id uuid NOT NULL
);


ALTER TABLE public.disbursement OWNER TO postgres;

--
-- Name: documents; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documents (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    document_title character varying(255) NOT NULL,
    document_type smallint NOT NULL,
    document_url character varying(255) NOT NULL,
    project_id uuid NOT NULL,
    CONSTRAINT documents_document_type_check CHECK (((document_type >= 0) AND (document_type <= 2)))
);


ALTER TABLE public.documents OWNER TO postgres;

--
-- Name: employee; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employee (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    user_id uuid NOT NULL
);


ALTER TABLE public.employee OWNER TO postgres;

--
-- Name: enquiries; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.enquiries (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    area double precision,
    budget character varying(255) NOT NULL,
    lead_address character varying(500) NOT NULL,
    lead_city character varying(255) NOT NULL,
    lead_company character varying(255) NOT NULL,
    lead_email character varying(255) NOT NULL,
    lead_landline_number character varying(15),
    lead_mobile_number character varying(15) NOT NULL,
    lead_name character varying(255) NOT NULL,
    lead_occupation character varying(255) NOT NULL,
    property character varying(20),
    property_type smallint,
    reference character varying(255) NOT NULL,
    reference_name character varying(255) NOT NULL,
    remark character varying(255),
    status character varying(255) NOT NULL,
    project_id uuid NOT NULL,
    CONSTRAINT enquiries_property_type_check CHECK (((property_type >= 0) AND (property_type <= 1))),
    CONSTRAINT enquiries_status_check CHECK (((status)::text = ANY ((ARRAY['ONGOING'::character varying, 'CANCELLED'::character varying, 'BOOKED'::character varying, 'COLD_LEAD'::character varying, 'WARM_LEAD'::character varying, 'HOT_LEAD'::character varying])::text[])))
);


ALTER TABLE public.enquiries OWNER TO postgres;

--
-- Name: flat; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.flat (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    area double precision NOT NULL,
    property_number character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    floor_id uuid NOT NULL,
    project_id uuid NOT NULL,
    wing_id uuid NOT NULL,
    CONSTRAINT flat_status_check CHECK (((status)::text = ANY ((ARRAY['Vacant'::character varying, 'Booked'::character varying, 'Registered'::character varying])::text[])))
);


ALTER TABLE public.flat OWNER TO postgres;

--
-- Name: floor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.floor (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    area double precision NOT NULL,
    floor_name character varying(255) NOT NULL,
    floor_no smallint NOT NULL,
    property character varying(20) NOT NULL,
    property_type character varying(255) NOT NULL,
    quantity integer NOT NULL,
    project_id uuid NOT NULL,
    wing_id uuid NOT NULL,
    CONSTRAINT floor_property_type_check CHECK (((property_type)::text = ANY ((ARRAY['Residential'::character varying, 'Commercial'::character varying])::text[])))
);


ALTER TABLE public.floor OWNER TO postgres;

--
-- Name: follow_up; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.follow_up (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    description character varying(500),
    follow_up_next_date date NOT NULL,
    enquiry_id uuid NOT NULL
);


ALTER TABLE public.follow_up OWNER TO postgres;

--
-- Name: follow_up_node; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.follow_up_node (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    body character varying(255) NOT NULL,
    follow_up_date_time timestamp(6) without time zone NOT NULL,
    tag character varying(255),
    follow_up_id uuid NOT NULL,
    user_id uuid NOT NULL
);


ALTER TABLE public.follow_up_node OWNER TO postgres;

--
-- Name: locked_resources; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.locked_resources (
    id uuid NOT NULL,
    lock_reason character varying(255),
    locked_at timestamp(6) with time zone NOT NULL,
    resource_id character varying(255) NOT NULL,
    resource_type character varying(255) NOT NULL,
    organization_id uuid NOT NULL
);


ALTER TABLE public.locked_resources OWNER TO postgres;

--
-- Name: notification_logs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.notification_logs (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    channel character varying(255),
    last_error character varying(255),
    last_sent_at timestamp(6) with time zone,
    recipient character varying(255),
    retry_count integer,
    status character varying(255),
    template_key character varying(255),
    organization_id uuid NOT NULL,
    user_id uuid,
    CONSTRAINT notification_logs_channel_check CHECK (((channel)::text = ANY ((ARRAY['EMAIL'::character varying, 'SMS'::character varying, 'WHATSAPP'::character varying])::text[]))),
    CONSTRAINT notification_logs_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'SENT'::character varying, 'FAILED'::character varying, 'RETRYING'::character varying])::text[])))
);


ALTER TABLE public.notification_logs OWNER TO postgres;

--
-- Name: org_usage_counters; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.org_usage_counters (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    period_start date NOT NULL,
    usage_count integer NOT NULL,
    organization_id uuid NOT NULL,
    plan_entitlement_id uuid NOT NULL
);


ALTER TABLE public.org_usage_counters OWNER TO postgres;

--
-- Name: organization; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.organization (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    logo_url character varying(255),
    org_email character varying(255) NOT NULL,
    org_name character varying(255) NOT NULL
);


ALTER TABLE public.organization OWNER TO postgres;

--
-- Name: organization_subscriptions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.organization_subscriptions (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    end_date date NOT NULL,
    start_date date NOT NULL,
    status character varying(255),
    organization_id uuid NOT NULL,
    subscription_plan_id uuid NOT NULL,
    CONSTRAINT organization_subscriptions_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'EXPIRED'::character varying, 'CANCELLED'::character varying])::text[])))
);


ALTER TABLE public.organization_subscriptions OWNER TO postgres;

--
-- Name: payment_orders; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.payment_orders (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    amount_paise bigint NOT NULL,
    currency character varying(255) NOT NULL,
    failure_code character varying(255),
    failure_description character varying(255),
    paid_at timestamp(6) with time zone,
    payment_attempts integer NOT NULL,
    razorpay_order_id character varying(255) NOT NULL,
    razorpay_payment_id character varying(255),
    razorpay_signature character varying(255),
    status character varying(255) NOT NULL,
    version bigint,
    created_by_id uuid NOT NULL,
    organization_id uuid NOT NULL,
    subscription_plan_id uuid NOT NULL,
    CONSTRAINT payment_orders_status_check CHECK (((status)::text = ANY ((ARRAY['CREATED'::character varying, 'PENDING'::character varying, 'SUCCESS'::character varying, 'FAILED'::character varying, 'REFUNDED'::character varying, 'CANCELLED'::character varying])::text[])))
);


ALTER TABLE public.payment_orders OWNER TO postgres;

--
-- Name: plan_entitlements; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.plan_entitlements (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    description character varying(255),
    entitlement_code character varying(255) NOT NULL,
    limit_type character varying(255) NOT NULL,
    limit_value character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    subscription_plan_id uuid NOT NULL
);


ALTER TABLE public.plan_entitlements OWNER TO postgres;

--
-- Name: project_detail_pdf_policies; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.project_detail_pdf_policies (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    include_amenities boolean NOT NULL,
    include_bank_project_info boolean NOT NULL,
    include_disbursements boolean NOT NULL,
    include_documents boolean NOT NULL,
    include_flats boolean NOT NULL,
    include_floors boolean NOT NULL,
    include_project_overview boolean NOT NULL,
    include_wings boolean NOT NULL,
    policy_name character varying(255) NOT NULL,
    project_id uuid NOT NULL
);


ALTER TABLE public.project_detail_pdf_policies OWNER TO postgres;

--
-- Name: project_detail_pdf_policy_documents; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.project_detail_pdf_policy_documents (
    project_detail_pdf_policy_id uuid NOT NULL,
    document_id uuid NOT NULL,
    "position" integer NOT NULL
);


ALTER TABLE public.project_detail_pdf_policy_documents OWNER TO postgres;

--
-- Name: projects; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.projects (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    completion_date date NOT NULL,
    letterhead_url character varying(255) NOT NULL,
    lock_reason character varying(255),
    locked boolean NOT NULL,
    maharera_no character varying(255) NOT NULL,
    pincode character varying(255),
    progress smallint NOT NULL,
    project_address character varying(255) NOT NULL,
    project_name character varying(255) NOT NULL,
    start_date date NOT NULL,
    status character varying(255) NOT NULL,
    org_id uuid NOT NULL,
    CONSTRAINT projects_status_check CHECK (((status)::text = ANY ((ARRAY['UPCOMING'::character varying, 'IN_PROGRESS'::character varying, 'COMPLETED'::character varying])::text[])))
);


ALTER TABLE public.projects OWNER TO postgres;

--
-- Name: subscription_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.subscription_history (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    changed_at timestamp(6) with time zone NOT NULL,
    changed_by uuid,
    from_status character varying(255) NOT NULL,
    reason character varying(255) NOT NULL,
    to_status character varying(255) NOT NULL,
    from_plan_id uuid,
    organization_id uuid NOT NULL,
    to_plan_id uuid NOT NULL,
    CONSTRAINT subscription_history_from_status_check CHECK (((from_status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'EXPIRED'::character varying, 'CANCELLED'::character varying])::text[]))),
    CONSTRAINT subscription_history_reason_check CHECK (((reason)::text = ANY ((ARRAY['NEW_SUBSCRIPTION'::character varying, 'RENEWAL'::character varying, 'UPGRADE'::character varying, 'DOWNGRADE'::character varying, 'CANCELLATION'::character varying, 'EXPIRED'::character varying])::text[]))),
    CONSTRAINT subscription_history_to_status_check CHECK (((to_status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'EXPIRED'::character varying, 'CANCELLED'::character varying])::text[])))
);


ALTER TABLE public.subscription_history OWNER TO postgres;

--
-- Name: subscription_plans; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.subscription_plans (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    active boolean NOT NULL,
    duration_months integer NOT NULL,
    plan_code character varying(255) NOT NULL,
    plan_description character varying(255),
    plan_name character varying(255) NOT NULL,
    price double precision NOT NULL
);


ALTER TABLE public.subscription_plans OWNER TO postgres;

--
-- Name: task; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.task (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    follow_up_id uuid NOT NULL
);


ALTER TABLE public.task OWNER TO postgres;

--
-- Name: user_permissions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_permissions (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    permission character varying(255) NOT NULL,
    granted_by uuid NOT NULL,
    user_id uuid NOT NULL,
    CONSTRAINT user_permissions_permission_check CHECK (((permission)::text = ANY ((ARRAY['VIEW_PROJECT'::character varying, 'CREATE_PROJECT'::character varying, 'UPDATE_PROJECT'::character varying, 'DELETE_PROJECT'::character varying, 'CREATE_WING'::character varying, 'UPDATE_WING'::character varying, 'DELETE_WING'::character varying, 'CREATE_FLOOR'::character varying, 'UPDATE_FLOOR'::character varying, 'DELETE_FLOOR'::character varying, 'VIEW_ENQUIRY'::character varying, 'CREATE_ENQUIRY'::character varying, 'UPDATE_ENQUIRY'::character varying, 'CANCEL_ENQUIRY'::character varying, 'CHANGE_ENQUIRY_STATUS'::character varying, 'VIEW_BOOKING'::character varying, 'CREATE_BOOKING'::character varying, 'VIEW_CLIENT'::character varying, 'UPDATE_CLIENT'::character varying, 'VIEW_USER'::character varying, 'CREATE_USER'::character varying, 'UPDATE_USER'::character varying, 'DELETE_USER'::character varying, 'VIEW_FOLLOWUP'::character varying, 'CREATE_FOLLOWUP'::character varying, 'UPDATE_FOLLOWUP'::character varying, 'VIEW_DOCUMENT'::character varying, 'UPLOAD_DOCUMENT'::character varying, 'DELETE_DOCUMENT'::character varying, 'EXPORT_PDF'::character varying, 'EXPORT_LEADS'::character varying, 'VIEW_DASHBOARD'::character varying, 'VIEW_DISBURSEMENT'::character varying, 'CREATE_DISBURSEMENT'::character varying, 'UPDATE_DISBURSEMENT'::character varying])::text[])))
);


ALTER TABLE public.user_permissions OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    email character varying(255) NOT NULL,
    email_verified boolean NOT NULL,
    enabled boolean,
    full_name character varying(255) NOT NULL,
    mobile_number character varying(15),
    mobile_verified boolean NOT NULL,
    password_hash character varying(255) NOT NULL,
    role character varying(255) NOT NULL,
    username character varying(255) NOT NULL,
    org_id uuid NOT NULL,
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['DEVELOPER'::character varying, 'ADMIN'::character varying, 'EMPLOYEE'::character varying])::text[])))
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: wing; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.wing (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    deleted boolean NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    no_of_floors integer NOT NULL,
    no_of_properties integer NOT NULL,
    wing_name character varying(255) NOT NULL,
    project_id uuid NOT NULL
);


ALTER TABLE public.wing OWNER TO postgres;

--
-- Data for Name: admin; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.admin (id, created_at, deleted, updated_at, is_deleted, is_super_admin, user_id) FROM stdin;
\.


--
-- Data for Name: allocated_employee_project; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.allocated_employee_project (employee_id, project_id) FROM stdin;
\.


--
-- Data for Name: amenity; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.amenity (id, created_at, deleted, updated_at, amenity_name, project_id) FROM stdin;
\.


--
-- Data for Name: bank_project_info; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.bank_project_info (id, created_at, deleted, updated_at, bank_name, branch_name, contact_number, contact_person, project_id) FROM stdin;
\.


--
-- Data for Name: booking; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.booking (id, created_at, deleted, updated_at, agreement_amount, booking_amount, booking_date, cheque_date, cheque_no, gst_amount, gst_percentage, infra_amount, is_cancelled, is_registered, rate, reg_date, reg_no, remark, total_amount, client_id, enquiry_id, property_id) FROM stdin;
\.


--
-- Data for Name: client_user_info; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.client_user_info (id, created_at, deleted, updated_at, aadhar_no, address, city, client_name, company, dob, email, landline_number, mobile_number, occupation, pan_no) FROM stdin;
\.


--
-- Data for Name: disbursement; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.disbursement (id, created_at, deleted, updated_at, description, disbursement_title, percentage, project_id) FROM stdin;
\.


--
-- Data for Name: documents; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.documents (id, created_at, deleted, updated_at, document_title, document_type, document_url, project_id) FROM stdin;
\.


--
-- Data for Name: employee; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.employee (id, created_at, deleted, updated_at, user_id) FROM stdin;
\.


--
-- Data for Name: enquiries; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.enquiries (id, created_at, deleted, updated_at, area, budget, lead_address, lead_city, lead_company, lead_email, lead_landline_number, lead_mobile_number, lead_name, lead_occupation, property, property_type, reference, reference_name, remark, status, project_id) FROM stdin;
\.


--
-- Data for Name: flat; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.flat (id, created_at, deleted, updated_at, area, property_number, status, floor_id, project_id, wing_id) FROM stdin;
\.


--
-- Data for Name: floor; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.floor (id, created_at, deleted, updated_at, area, floor_name, floor_no, property, property_type, quantity, project_id, wing_id) FROM stdin;
\.


--
-- Data for Name: follow_up; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.follow_up (id, created_at, deleted, updated_at, description, follow_up_next_date, enquiry_id) FROM stdin;
\.


--
-- Data for Name: follow_up_node; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.follow_up_node (id, created_at, deleted, updated_at, body, follow_up_date_time, tag, follow_up_id, user_id) FROM stdin;
\.


--
-- Data for Name: locked_resources; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.locked_resources (id, lock_reason, locked_at, resource_id, resource_type, organization_id) FROM stdin;
\.


--
-- Data for Name: notification_logs; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.notification_logs (id, created_at, deleted, updated_at, channel, last_error, last_sent_at, recipient, retry_count, status, template_key, organization_id, user_id) FROM stdin;
\.


--
-- Data for Name: org_usage_counters; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.org_usage_counters (id, created_at, deleted, updated_at, period_start, usage_count, organization_id, plan_entitlement_id) FROM stdin;
\.


--
-- Data for Name: organization; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.organization (id, created_at, deleted, updated_at, logo_url, org_email, org_name) FROM stdin;
\.


--
-- Data for Name: organization_subscriptions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.organization_subscriptions (id, created_at, deleted, updated_at, end_date, start_date, status, organization_id, subscription_plan_id) FROM stdin;
\.


--
-- Data for Name: payment_orders; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.payment_orders (id, created_at, deleted, updated_at, amount_paise, currency, failure_code, failure_description, paid_at, payment_attempts, razorpay_order_id, razorpay_payment_id, razorpay_signature, status, version, created_by_id, organization_id, subscription_plan_id) FROM stdin;
\.


--
-- Data for Name: plan_entitlements; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.plan_entitlements (id, created_at, deleted, updated_at, description, entitlement_code, limit_type, limit_value, name, subscription_plan_id) FROM stdin;
\.


--
-- Data for Name: project_detail_pdf_policies; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.project_detail_pdf_policies (id, created_at, deleted, updated_at, include_amenities, include_bank_project_info, include_disbursements, include_documents, include_flats, include_floors, include_project_overview, include_wings, policy_name, project_id) FROM stdin;
\.


--
-- Data for Name: project_detail_pdf_policy_documents; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.project_detail_pdf_policy_documents (project_detail_pdf_policy_id, document_id, "position") FROM stdin;
\.


--
-- Data for Name: projects; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.projects (id, created_at, deleted, updated_at, completion_date, letterhead_url, lock_reason, locked, maharera_no, pincode, progress, project_address, project_name, start_date, status, org_id) FROM stdin;
\.


--
-- Data for Name: subscription_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.subscription_history (id, created_at, deleted, updated_at, changed_at, changed_by, from_status, reason, to_status, from_plan_id, organization_id, to_plan_id) FROM stdin;
\.


--
-- Data for Name: subscription_plans; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.subscription_plans (id, created_at, deleted, updated_at, active, duration_months, plan_code, plan_description, plan_name, price) FROM stdin;
\.


--
-- Data for Name: task; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.task (id, created_at, deleted, updated_at, follow_up_id) FROM stdin;
\.


--
-- Data for Name: user_permissions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_permissions (id, created_at, deleted, updated_at, permission, granted_by, user_id) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, created_at, deleted, updated_at, email, email_verified, enabled, full_name, mobile_number, mobile_verified, password_hash, role, username, org_id) FROM stdin;
\.


--
-- Data for Name: wing; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.wing (id, created_at, deleted, updated_at, no_of_floors, no_of_properties, wing_name, project_id) FROM stdin;
\.


--
-- Name: admin admin_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin
    ADD CONSTRAINT admin_pkey PRIMARY KEY (id);


--
-- Name: allocated_employee_project allocated_employee_project_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allocated_employee_project
    ADD CONSTRAINT allocated_employee_project_pkey PRIMARY KEY (employee_id, project_id);


--
-- Name: amenity amenity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.amenity
    ADD CONSTRAINT amenity_pkey PRIMARY KEY (id);


--
-- Name: bank_project_info bank_project_info_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bank_project_info
    ADD CONSTRAINT bank_project_info_pkey PRIMARY KEY (id);


--
-- Name: booking booking_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.booking
    ADD CONSTRAINT booking_pkey PRIMARY KEY (id);


--
-- Name: client_user_info client_user_info_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.client_user_info
    ADD CONSTRAINT client_user_info_pkey PRIMARY KEY (id);


--
-- Name: disbursement disbursement_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.disbursement
    ADD CONSTRAINT disbursement_pkey PRIMARY KEY (id);


--
-- Name: documents documents_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documents
    ADD CONSTRAINT documents_pkey PRIMARY KEY (id);


--
-- Name: employee employee_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employee
    ADD CONSTRAINT employee_pkey PRIMARY KEY (id);


--
-- Name: enquiries enquiries_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.enquiries
    ADD CONSTRAINT enquiries_pkey PRIMARY KEY (id);


--
-- Name: flat flat_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flat
    ADD CONSTRAINT flat_pkey PRIMARY KEY (id);


--
-- Name: floor floor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.floor
    ADD CONSTRAINT floor_pkey PRIMARY KEY (id);


--
-- Name: follow_up_node follow_up_node_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.follow_up_node
    ADD CONSTRAINT follow_up_node_pkey PRIMARY KEY (id);


--
-- Name: follow_up follow_up_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.follow_up
    ADD CONSTRAINT follow_up_pkey PRIMARY KEY (id);


--
-- Name: locked_resources locked_resources_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.locked_resources
    ADD CONSTRAINT locked_resources_pkey PRIMARY KEY (id);


--
-- Name: notification_logs notification_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notification_logs
    ADD CONSTRAINT notification_logs_pkey PRIMARY KEY (id);


--
-- Name: org_usage_counters org_usage_counters_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.org_usage_counters
    ADD CONSTRAINT org_usage_counters_pkey PRIMARY KEY (id);


--
-- Name: organization organization_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY (id);


--
-- Name: organization_subscriptions organization_subscriptions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization_subscriptions
    ADD CONSTRAINT organization_subscriptions_pkey PRIMARY KEY (id);


--
-- Name: payment_orders payment_orders_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment_orders
    ADD CONSTRAINT payment_orders_pkey PRIMARY KEY (id);


--
-- Name: plan_entitlements plan_entitlements_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.plan_entitlements
    ADD CONSTRAINT plan_entitlements_pkey PRIMARY KEY (id);


--
-- Name: project_detail_pdf_policies project_detail_pdf_policies_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.project_detail_pdf_policies
    ADD CONSTRAINT project_detail_pdf_policies_pkey PRIMARY KEY (id);


--
-- Name: project_detail_pdf_policy_documents project_detail_pdf_policy_documents_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.project_detail_pdf_policy_documents
    ADD CONSTRAINT project_detail_pdf_policy_documents_pkey PRIMARY KEY (project_detail_pdf_policy_id, "position");


--
-- Name: projects projects_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_pkey PRIMARY KEY (id);


--
-- Name: subscription_history subscription_history_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.subscription_history
    ADD CONSTRAINT subscription_history_pkey PRIMARY KEY (id);


--
-- Name: subscription_plans subscription_plans_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.subscription_plans
    ADD CONSTRAINT subscription_plans_pkey PRIMARY KEY (id);


--
-- Name: task task_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task
    ADD CONSTRAINT task_pkey PRIMARY KEY (id);


--
-- Name: payment_orders uk3crv9wke92o3hwlgsulh5xses; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment_orders
    ADD CONSTRAINT uk3crv9wke92o3hwlgsulh5xses UNIQUE (razorpay_order_id);


--
-- Name: projects uk9q1d2eyxs0mitqyprtlfbavxb; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT uk9q1d2eyxs0mitqyprtlfbavxb UNIQUE (maharera_no);


--
-- Name: projects uka7kh7hhtu5ohhc4l8swr229o1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT uka7kh7hhtu5ohhc4l8swr229o1 UNIQUE (org_id, project_name);


--
-- Name: booking ukbkv0h9k91pss959k8osg3ogxf; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.booking
    ADD CONSTRAINT ukbkv0h9k91pss959k8osg3ogxf UNIQUE (client_id);


--
-- Name: locked_resources ukdlupob2dsw42gdghh0jwiiobg; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.locked_resources
    ADD CONSTRAINT ukdlupob2dsw42gdghh0jwiiobg UNIQUE (organization_id, resource_type, resource_id);


--
-- Name: users uke8mq90mspr745wbmr2p2b0kfu; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uke8mq90mspr745wbmr2p2b0kfu UNIQUE (org_id, email);


--
-- Name: task ukeliwwf1l9t1u7ajrp9gqgystr; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task
    ADD CONSTRAINT ukeliwwf1l9t1u7ajrp9gqgystr UNIQUE (follow_up_id);


--
-- Name: follow_up ukftqfe03i7vbxfeki5r6g7ronw; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.follow_up
    ADD CONSTRAINT ukftqfe03i7vbxfeki5r6g7ronw UNIQUE (enquiry_id);


--
-- Name: booking ukgltwih8kqqk8hpa5it6vcets0; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.booking
    ADD CONSTRAINT ukgltwih8kqqk8hpa5it6vcets0 UNIQUE (property_id);


--
-- Name: organization_subscriptions ukh4lcews5jri4aw7upla04b17y; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization_subscriptions
    ADD CONSTRAINT ukh4lcews5jri4aw7upla04b17y UNIQUE (organization_id);


--
-- Name: admin ukhawikyhwwfvbnog5byokutpff; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin
    ADD CONSTRAINT ukhawikyhwwfvbnog5byokutpff UNIQUE (user_id);


--
-- Name: organization uki21y6di2u0g0o52qou7s5q2v9; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization
    ADD CONSTRAINT uki21y6di2u0g0o52qou7s5q2v9 UNIQUE (org_email);


--
-- Name: employee ukmpps3d3r9pdvyjx3iqixi96fi; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employee
    ADD CONSTRAINT ukmpps3d3r9pdvyjx3iqixi96fi UNIQUE (user_id);


--
-- Name: project_detail_pdf_policies ukmtyqktcf54ejhj4nx8qibh0b3; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.project_detail_pdf_policies
    ADD CONSTRAINT ukmtyqktcf54ejhj4nx8qibh0b3 UNIQUE (project_id, policy_name);


--
-- Name: org_usage_counters uknjiioiue82hcex2dghtgf0sy2; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.org_usage_counters
    ADD CONSTRAINT uknjiioiue82hcex2dghtgf0sy2 UNIQUE (organization_id, plan_entitlement_id);


--
-- Name: booking ukokaaak6x800rxcbb99k0uiu3o; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.booking
    ADD CONSTRAINT ukokaaak6x800rxcbb99k0uiu3o UNIQUE (enquiry_id);


--
-- Name: plan_entitlements ukqdcv3jlr8i2xu4qt0x6p7t2ed; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.plan_entitlements
    ADD CONSTRAINT ukqdcv3jlr8i2xu4qt0x6p7t2ed UNIQUE (subscription_plan_id, entitlement_code);


--
-- Name: users ukr43af9ap4edm43mmtq01oddj6; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT ukr43af9ap4edm43mmtq01oddj6 UNIQUE (username);


--
-- Name: subscription_plans ukrsiqsy651mlytqpby2qlh7orb; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.subscription_plans
    ADD CONSTRAINT ukrsiqsy651mlytqpby2qlh7orb UNIQUE (plan_code);


--
-- Name: user_permissions user_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_permissions
    ADD CONSTRAINT user_permissions_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: wing wing_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.wing
    ADD CONSTRAINT wing_pkey PRIMARY KEY (id);


--
-- Name: users fk2h5ste2cf69o51tcfvwx503ge; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk2h5ste2cf69o51tcfvwx503ge FOREIGN KEY (org_id) REFERENCES public.organization(id);


--
-- Name: locked_resources fk3bha8uouo8ln0j1r8w687f653; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.locked_resources
    ADD CONSTRAINT fk3bha8uouo8ln0j1r8w687f653 FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: bank_project_info fk3dsn76n8mb8nirgcm5t7lfbuy; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bank_project_info
    ADD CONSTRAINT fk3dsn76n8mb8nirgcm5t7lfbuy FOREIGN KEY (project_id) REFERENCES public.projects(id);


--
-- Name: org_usage_counters fk3lays1wamifdbbnf7tpvv3rf9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.org_usage_counters
    ADD CONSTRAINT fk3lays1wamifdbbnf7tpvv3rf9 FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: flat fk4g92ox83s1amvquqomybogmpj; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flat
    ADD CONSTRAINT fk4g92ox83s1amvquqomybogmpj FOREIGN KEY (wing_id) REFERENCES public.wing(id);


--
-- Name: subscription_history fk4mdf767e5i2bvh0bl7edwdeuf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.subscription_history
    ADD CONSTRAINT fk4mdf767e5i2bvh0bl7edwdeuf FOREIGN KEY (from_plan_id) REFERENCES public.subscription_plans(id);


--
-- Name: enquiries fk4wbly0a65xwjp17e1eipfua51; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.enquiries
    ADD CONSTRAINT fk4wbly0a65xwjp17e1eipfua51 FOREIGN KEY (project_id) REFERENCES public.projects(id);


--
-- Name: allocated_employee_project fk6avoi15pcaqumbvvmuehlo8qf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allocated_employee_project
    ADD CONSTRAINT fk6avoi15pcaqumbvvmuehlo8qf FOREIGN KEY (project_id) REFERENCES public.projects(id);


--
-- Name: floor fk6qieey41o91ffqqgfd7xcksw2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.floor
    ADD CONSTRAINT fk6qieey41o91ffqqgfd7xcksw2 FOREIGN KEY (project_id) REFERENCES public.projects(id);


--
-- Name: subscription_history fk76cs26gvh6t1hmhi5j2xwn50v; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.subscription_history
    ADD CONSTRAINT fk76cs26gvh6t1hmhi5j2xwn50v FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: booking fk8c1n7i5aqs5pvyxguffcyt60l; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.booking
    ADD CONSTRAINT fk8c1n7i5aqs5pvyxguffcyt60l FOREIGN KEY (property_id) REFERENCES public.flat(id);


--
-- Name: projects fk8eo48r000akn6mhrato49soip; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT fk8eo48r000akn6mhrato49soip FOREIGN KEY (org_id) REFERENCES public.organization(id);


--
-- Name: flat fk9drhxh03ceutns6y2uclfjh1l; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flat
    ADD CONSTRAINT fk9drhxh03ceutns6y2uclfjh1l FOREIGN KEY (project_id) REFERENCES public.projects(id);


--
-- Name: booking fka0siakatc8odogaborlqghtt; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.booking
    ADD CONSTRAINT fka0siakatc8odogaborlqghtt FOREIGN KEY (enquiry_id) REFERENCES public.enquiries(id);


--
-- Name: task fka5ae51x0ac8dgcqwig2p30fhp; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task
    ADD CONSTRAINT fka5ae51x0ac8dgcqwig2p30fhp FOREIGN KEY (follow_up_id) REFERENCES public.follow_up(id);


--
-- Name: allocated_employee_project fkavnn7fhd9hxqvoce7q2p799of; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allocated_employee_project
    ADD CONSTRAINT fkavnn7fhd9hxqvoce7q2p799of FOREIGN KEY (employee_id) REFERENCES public.employee(id);


--
-- Name: plan_entitlements fkc0mtb9frj01tiu7shr111cchd; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.plan_entitlements
    ADD CONSTRAINT fkc0mtb9frj01tiu7shr111cchd FOREIGN KEY (subscription_plan_id) REFERENCES public.subscription_plans(id);


--
-- Name: booking fkcq9f8xfn63m372nytsewrqg5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.booking
    ADD CONSTRAINT fkcq9f8xfn63m372nytsewrqg5 FOREIGN KEY (client_id) REFERENCES public.client_user_info(id);


--
-- Name: subscription_history fkd0ulkr2h6xfy92px8f67g64fw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.subscription_history
    ADD CONSTRAINT fkd0ulkr2h6xfy92px8f67g64fw FOREIGN KEY (to_plan_id) REFERENCES public.subscription_plans(id);


--
-- Name: wing fkdayvk6599lr0e375u0836n1p9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.wing
    ADD CONSTRAINT fkdayvk6599lr0e375u0836n1p9 FOREIGN KEY (project_id) REFERENCES public.projects(id);


--
-- Name: floor fkeqpvb2ek863mss3qqnkmjlp5s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.floor
    ADD CONSTRAINT fkeqpvb2ek863mss3qqnkmjlp5s FOREIGN KEY (wing_id) REFERENCES public.wing(id);


--
-- Name: payment_orders fkfia7bk0t7972v5tuiv2mgw4ho; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment_orders
    ADD CONSTRAINT fkfia7bk0t7972v5tuiv2mgw4ho FOREIGN KEY (subscription_plan_id) REFERENCES public.subscription_plans(id);


--
-- Name: follow_up_node fkfppa24qpkuc8oj89kgqyg91fh; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.follow_up_node
    ADD CONSTRAINT fkfppa24qpkuc8oj89kgqyg91fh FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: organization_subscriptions fkglybjnxk3lx9w8rl49bf4nl8r; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization_subscriptions
    ADD CONSTRAINT fkglybjnxk3lx9w8rl49bf4nl8r FOREIGN KEY (subscription_plan_id) REFERENCES public.subscription_plans(id);


--
-- Name: org_usage_counters fkh1wse48xcybro1dkb6qm4mrsc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.org_usage_counters
    ADD CONSTRAINT fkh1wse48xcybro1dkb6qm4mrsc FOREIGN KEY (plan_entitlement_id) REFERENCES public.plan_entitlements(id);


--
-- Name: employee fkhal2duyxxjtadykhxos7wd3wg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employee
    ADD CONSTRAINT fkhal2duyxxjtadykhxos7wd3wg FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: user_permissions fkhfrplgy2fli1hd1saw6qyjuda; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_permissions
    ADD CONSTRAINT fkhfrplgy2fli1hd1saw6qyjuda FOREIGN KEY (granted_by) REFERENCES public.users(id);


--
-- Name: amenity fkht2ughe0tu3nvhmls6j3wf6bt; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.amenity
    ADD CONSTRAINT fkht2ughe0tu3nvhmls6j3wf6bt FOREIGN KEY (project_id) REFERENCES public.projects(id);


--
-- Name: organization_subscriptions fkifm162rt7181cysaywodemy4j; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization_subscriptions
    ADD CONSTRAINT fkifm162rt7181cysaywodemy4j FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: follow_up_node fkis01f89fhuh9s2rky9887irfh; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.follow_up_node
    ADD CONSTRAINT fkis01f89fhuh9s2rky9887irfh FOREIGN KEY (follow_up_id) REFERENCES public.follow_up(id);


--
-- Name: payment_orders fkkkokvqjfoq9hq7wsgebu4oi1y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment_orders
    ADD CONSTRAINT fkkkokvqjfoq9hq7wsgebu4oi1y FOREIGN KEY (created_by_id) REFERENCES public.users(id);


--
-- Name: user_permissions fkkowxl8b2bngrxd1gafh13005u; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_permissions
    ADD CONSTRAINT fkkowxl8b2bngrxd1gafh13005u FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: disbursement fklcjk6g16lo6k1r5ak6ji6ut0l; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.disbursement
    ADD CONSTRAINT fklcjk6g16lo6k1r5ak6ji6ut0l FOREIGN KEY (project_id) REFERENCES public.projects(id);


--
-- Name: notification_logs fklo3vn31nndb01tud6231f2pjp; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notification_logs
    ADD CONSTRAINT fklo3vn31nndb01tud6231f2pjp FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: flat fklx49opgoxshpplsrpbis3cefu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flat
    ADD CONSTRAINT fklx49opgoxshpplsrpbis3cefu FOREIGN KEY (floor_id) REFERENCES public.floor(id);


--
-- Name: documents fkn3yorpjknmi88ikdmdrrkrp0g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documents
    ADD CONSTRAINT fkn3yorpjknmi88ikdmdrrkrp0g FOREIGN KEY (project_id) REFERENCES public.projects(id);


--
-- Name: payment_orders fkn6o86jkkuwi51gcl40jxe3rep; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment_orders
    ADD CONSTRAINT fkn6o86jkkuwi51gcl40jxe3rep FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- Name: follow_up fkprtu7ya5tf39g1mm7bw48hpr3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.follow_up
    ADD CONSTRAINT fkprtu7ya5tf39g1mm7bw48hpr3 FOREIGN KEY (enquiry_id) REFERENCES public.enquiries(id);


--
-- Name: admin fkq7pdkck9je126wpd9ijw3uwml; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin
    ADD CONSTRAINT fkq7pdkck9je126wpd9ijw3uwml FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: project_detail_pdf_policy_documents fkquq2rvqjo8a447fv3ttf0xkwf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.project_detail_pdf_policy_documents
    ADD CONSTRAINT fkquq2rvqjo8a447fv3ttf0xkwf FOREIGN KEY (project_detail_pdf_policy_id) REFERENCES public.project_detail_pdf_policies(id);


--
-- Name: project_detail_pdf_policies fkr2lfc4kcg4ujyt473hf1iwdi2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.project_detail_pdf_policies
    ADD CONSTRAINT fkr2lfc4kcg4ujyt473hf1iwdi2 FOREIGN KEY (project_id) REFERENCES public.projects(id);


--
-- Name: notification_logs fksbx1lf2w8tr7siwwibcj9k3fg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notification_logs
    ADD CONSTRAINT fksbx1lf2w8tr7siwwibcj9k3fg FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--

\unrestrict hJkNhXEuL4Z2K4FGnyRlX26jeIayq3IDKa6uc1XVHE5HpPw1l8jbqhmPBBfmIyd

