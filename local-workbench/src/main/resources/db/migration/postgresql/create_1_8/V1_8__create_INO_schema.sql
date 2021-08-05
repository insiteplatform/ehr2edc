SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

SET default_with_oids = false;

--
-- TOC entry 176 (class 1259 OID 18191)
-- Name: analysis; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE analysis (
    id bigint NOT NULL,
    creationtime timestamp without time zone,
    errormessage character varying(255),
    status integer,
    timewindowenddate timestamp without time zone,
    timewindowstartdate timestamp without time zone,
    description character varying(255),
    implementingclass character varying(255),
    name character varying(255),
    creator_id bigint,
    targetid bigint
);


--
-- TOC entry 177 (class 1259 OID 18199)
-- Name: analysis_age_distribution; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE analysis_age_distribution (
    binsize smallint NOT NULL,
    totalbins smallint NOT NULL,
    id bigint NOT NULL
);


--
-- TOC entry 178 (class 1259 OID 18204)
-- Name: analysis_cohort_comparison; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE analysis_cohort_comparison (
    id bigint NOT NULL,
    comparedid bigint
);


--
-- TOC entry 179 (class 1259 OID 18209)
-- Name: analysis_gender_distribution; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE analysis_gender_distribution (
    id bigint NOT NULL
);


--
-- TOC entry 206 (class 1259 OID 46505)
-- Name: analysis_measurement_distribution; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE analysis_measurement_distribution (
    id bigint NOT NULL,
    chosenunitcode character varying(255),
    referencedate timestamp without time zone,
    conceptcategory character varying(255),
    conceptid character varying(255),
    conceptcoding character varying(255),
    conceptoid character varying(255),
    conceptlabel character varying(255),
    conceptqualifier character varying(255),
    concepturn character varying(255),
    conceptunit character varying(255),
    conceptoperator character varying(255),
    conceptvalue character varying(255),
    conceptdefaultunit character varying(255),
    conceptruletype character varying(255),
    conceptunitlabel character varying(255),
    conceptunitid character varying(255),
    conceptunitcoding character varying(255),
    conceptunitoid character varying(255),
    consideredfactscount integer
);


--
-- TOC entry 180 (class 1259 OID 18214)
-- Name: analysis_prevalence; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE analysis_prevalence (
    code character varying(255),
    codingsystemname character varying(255),
    codingsystemoid character varying(255),
    text character varying(255),
    hierarchylevelofreporting integer NOT NULL,
    id bigint NOT NULL,
    clinicalfactsstartdate timestamp without time zone,
    clinicalfactsenddate timestamp without time zone
);


--
-- TOC entry 181 (class 1259 OID 18222)
-- Name: analysis_result_binning; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE analysis_result_binning (
    id bigint NOT NULL,
    executionendtime timestamp without time zone,
    executionstarttime timestamp without time zone,
    analysisid bigint
);


--
-- TOC entry 182 (class 1259 OID 18227)
-- Name: analysis_result_binning_bins; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE analysis_result_binning_bins (
    analysis_binning_result_id bigint NOT NULL,
    count double precision NOT NULL,
    variablelabel character varying(255),
    variablename character varying(255)
);


--
-- TOC entry 175 (class 1259 OID 18183)
-- Name: app_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE app_user (
    id bigint NOT NULL,
    version bigint NOT NULL,
    email character varying(80),
    enabled boolean NOT NULL,
    expirationdate timestamp without time zone,
    first_name character varying(80),
    deleted boolean,
    last_name character varying(80),
    password character varying(80) NOT NULL,
    temppassword character varying(255),
    username character varying(80) NOT NULL,
    securityanswer character varying(255) DEFAULT NULL::character varying,
    securityquestionid character varying(255) DEFAULT NULL::character varying
);


--
-- TOC entry 183 (class 1259 OID 18233)
-- Name: authority; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE authority (
    id bigint NOT NULL,
    version bigint NOT NULL,
    name character varying(80) NOT NULL
);


--
-- TOC entry 218 (class 1259 OID 378585)
-- Name: clinical_patient_exclusion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE clinical_patient_exclusion (
    id bigint NOT NULL,
    dateexcluded timestamp without time zone,
    exclusionreason character varying,
    originalcohorttype character varying,
    patientid character varying,
    excludedby_id bigint,
    screeningfilter_id bigint
);


--
-- TOC entry 207 (class 1259 OID 67164)
-- Name: clinical_study; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE clinical_study (
    id bigint NOT NULL,
    archived boolean,
    deadline timestamp without time zone,
    description text,
    goal bigint,
    name character varying(255) DEFAULT NULL::character varying,
    state character varying(255) DEFAULT NULL::character varying,
    sponsor_id bigint,
    currentversion_id bigint,
    lastupdated timestamp without time zone,
    reached bigint,
    creationdate timestamp without time zone,
    currentscreeningfilters_id bigint,
    externalid character varying
);


--
-- TOC entry 208 (class 1259 OID 67174)
-- Name: clinical_study_acceptance; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE clinical_study_acceptance (
    id bigint NOT NULL,
    accepteddate timestamp without time zone,
    coordinator_id bigint,
    study_id bigint
);


--
-- TOC entry 217 (class 1259 OID 250475)
-- Name: clinical_study_cohort; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE clinical_study_cohort (
    id bigint NOT NULL,
    cohorttype character varying,
    patientcount bigint,
    repokey character varying,
    screeningresult_id bigint,
    iscompleted boolean,
    message character varying,
    study_id bigint
);


--
-- TOC entry 209 (class 1259 OID 67179)
-- Name: clinical_study_investigators; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE clinical_study_investigators (
    clinical_study_id bigint NOT NULL,
    investigator_id bigint NOT NULL
);


--
-- TOC entry 215 (class 1259 OID 96272)
-- Name: clinical_study_patient_assignment; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE clinical_study_patient_assignment (
    id bigint NOT NULL,
    assignmenttype character varying(255) DEFAULT NULL::character varying,
    patientid bigint,
    protocolversion_id bigint,
    user_id bigint
);


--
-- TOC entry 216 (class 1259 OID 250462)
-- Name: clinical_study_protocol_screening_result; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE clinical_study_protocol_screening_result (
    id bigint NOT NULL,
    repokey character varying,
    protocolscreeningfilter_id bigint,
    dateexecuted timestamp without time zone
);


--
-- TOC entry 210 (class 1259 OID 67184)
-- Name: clinical_study_protocol_version; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE clinical_study_protocol_version (
    id bigint NOT NULL,
    version character varying(255) DEFAULT NULL::character varying,
    study_id bigint
);


--
-- TOC entry 211 (class 1259 OID 67190)
-- Name: clinical_study_protocol_version_document; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE clinical_study_protocol_version_document (
    id bigint NOT NULL,
    filename character varying(255) DEFAULT NULL::character varying,
    filesize bigint,
    filetype character varying(255) DEFAULT NULL::character varying,
    protocolversion_id bigint,
    file oid
);


--
-- TOC entry 212 (class 1259 OID 67200)
-- Name: clinical_study_protocol_version_filter; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE clinical_study_protocol_version_filter (
    id bigint NOT NULL,
    archived boolean,
    fromsponsor boolean,
    jsoncontent text,
    version character varying(255) DEFAULT NULL::character varying,
    protocolversion_id bigint,
    creationdate timestamp without time zone,
    creator_id bigint,
    sponsor_id bigint
);


--
-- TOC entry 185 (class 1259 OID 18240)
-- Name: cohort; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE cohort (
    id bigint NOT NULL,
    creationdate timestamp without time zone,
    description text,
    name character varying(255),
    patientcount bigint,
    referencedate timestamp without time zone,
    repokey character varying(255),
    author_id bigint,
    cstudyid bigint,
    source_id bigint
);


--
-- TOC entry 187 (class 1259 OID 18251)
-- Name: cohort_export; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE cohort_export (
    id bigint NOT NULL,
    date timestamp without time zone,
    filename character varying(255),
    filepath character varying(255),
    size character varying(255),
    status integer,
    resultformattype integer,
    cohortid bigint,
    user_id bigint,
    factsstartdate timestamp without time zone,
    factsenddate timestamp without time zone,
    errormessage character varying(255) DEFAULT NULL::character varying,
    resultcontenttype integer
);


--
-- TOC entry 186 (class 1259 OID 18249)
-- Name: cohort_export_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE cohort_export_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3152 (class 0 OID 0)
-- Dependencies: 186
-- Name: cohort_export_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE cohort_export_id_seq OWNED BY cohort_export.id;


--
-- TOC entry 189 (class 1259 OID 18262)
-- Name: cohort_filterblock; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE cohort_filterblock (
    id bigint NOT NULL,
    creationdate timestamp without time zone,
    description text,
    favorite boolean,
    jsonquery text,
    name character varying(255),
    author_id bigint,
    cohort_id bigint
);


--
-- TOC entry 188 (class 1259 OID 18260)
-- Name: cohort_filterblock_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE cohort_filterblock_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3153 (class 0 OID 0)
-- Dependencies: 188
-- Name: cohort_filterblock_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE cohort_filterblock_id_seq OWNED BY cohort_filterblock.id;


--
-- TOC entry 184 (class 1259 OID 18238)
-- Name: cohort_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE cohort_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3154 (class 0 OID 0)
-- Dependencies: 184
-- Name: cohort_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE cohort_id_seq OWNED BY cohort.id;


--
-- TOC entry 191 (class 1259 OID 18273)
-- Name: cohort_patient; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE cohort_patient (
    id bigint NOT NULL,
    dateadded timestamp without time zone,
    cohortid bigint,
    patientid bigint,
    userid bigint
);


--
-- TOC entry 190 (class 1259 OID 18271)
-- Name: cohort_patient_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE cohort_patient_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3155 (class 0 OID 0)
-- Dependencies: 190
-- Name: cohort_patient_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE cohort_patient_id_seq OWNED BY cohort_patient.id;


--
-- TOC entry 193 (class 1259 OID 18281)
-- Name: cohort_study; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE cohort_study (
    id bigint NOT NULL,
    creationdate timestamp without time zone,
    description text,
    name character varying(255),
    author_id bigint,
    repostudyid character varying(255) DEFAULT NULL::character varying
);


--
-- TOC entry 192 (class 1259 OID 18279)
-- Name: cohort_study_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE cohort_study_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3156 (class 0 OID 0)
-- Dependencies: 192
-- Name: cohort_study_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE cohort_study_id_seq OWNED BY cohort_study.id;


--
-- TOC entry 195 (class 1259 OID 18292)
-- Name: coverage_item; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE coverage_item (
    coverageid bigint NOT NULL,
    count bigint,
    date timestamp without time zone,
    error character varying(255),
    code character varying(255),
    base character varying(255),
    state integer,
    task_id bigint
);


--
-- TOC entry 197 (class 1259 OID 18303)
-- Name: coverage_task; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE coverage_task (
    id bigint NOT NULL,
    completed boolean,
    completedconcepts bigint,
    maxlevel bigint,
    startdate timestamp without time zone,
    totalconcepts bigint
);


--
-- TOC entry 194 (class 1259 OID 18290)
-- Name: coverageitem_coverageid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE coverageitem_coverageid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3157 (class 0 OID 0)
-- Dependencies: 194
-- Name: coverageitem_coverageid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE coverageitem_coverageid_seq OWNED BY coverage_item.coverageid;


--
-- TOC entry 196 (class 1259 OID 18301)
-- Name: coveragetask_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE coveragetask_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3158 (class 0 OID 0)
-- Dependencies: 196
-- Name: coveragetask_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE coveragetask_id_seq OWNED BY coverage_task.id;


--
-- TOC entry 213 (class 1259 OID 67223)
-- Name: external_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE external_user (
    id bigint NOT NULL,
    companyname character varying(255) DEFAULT NULL::character varying,
    email character varying(255) DEFAULT NULL::character varying,
    name character varying(255) DEFAULT NULL::character varying
);


--
-- TOC entry 174 (class 1259 OID 18180)
-- Name: hibernate_sequences; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE hibernate_sequences (
    sequence_name character varying(255),
    sequence_next_hi_value integer
);


--
-- TOC entry 198 (class 1259 OID 18309)
-- Name: patient; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE patient (
    patientid bigint NOT NULL,
    age smallint,
    gender character varying(255),
    tpid bigint
);


--
-- TOC entry 200 (class 1259 OID 18316)
-- Name: physicians; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE physicians (
    id bigint NOT NULL,
    isdefault boolean,
    providerid character varying(255),
    userid bigint
);


--
-- TOC entry 199 (class 1259 OID 18314)
-- Name: physicians_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE physicians_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3159 (class 0 OID 0)
-- Dependencies: 199
-- Name: physicians_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE physicians_id_seq OWNED BY physicians.id;


--
-- TOC entry 214 (class 1259 OID 67234)
-- Name: principal_investigator; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE principal_investigator (
    id bigint NOT NULL,
    occupied boolean,
    userid bigint
);


--
-- TOC entry 201 (class 1259 OID 18322)
-- Name: user_authority; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE user_authority (
    user_id bigint NOT NULL,
    authority_id bigint NOT NULL
);


--
-- TOC entry 202 (class 1259 OID 18327)
-- Name: usergroup; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE usergroup (
    id bigint NOT NULL,
    version bigint NOT NULL,
    name character varying(80) NOT NULL
);


--
-- TOC entry 203 (class 1259 OID 18332)
-- Name: usergroup_authority; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE usergroup_authority (
    group_id bigint NOT NULL,
    authority_id bigint NOT NULL
);


--
-- TOC entry 204 (class 1259 OID 18337)
-- Name: usergroup_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE usergroup_user (
    group_id bigint NOT NULL,
    user_id bigint NOT NULL
);


--
-- TOC entry 2890 (class 2604 OID 18243)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort ALTER COLUMN id SET DEFAULT nextval('cohort_id_seq'::regclass);


--
-- TOC entry 2891 (class 2604 OID 18254)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_export ALTER COLUMN id SET DEFAULT nextval('cohort_export_id_seq'::regclass);


--
-- TOC entry 2893 (class 2604 OID 18265)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_filterblock ALTER COLUMN id SET DEFAULT nextval('cohort_filterblock_id_seq'::regclass);


--
-- TOC entry 2894 (class 2604 OID 18276)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_patient ALTER COLUMN id SET DEFAULT nextval('cohort_patient_id_seq'::regclass);


--
-- TOC entry 2895 (class 2604 OID 18284)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_study ALTER COLUMN id SET DEFAULT nextval('cohort_study_id_seq'::regclass);


--
-- TOC entry 2897 (class 2604 OID 18295)
-- Name: coverageid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY coverage_item ALTER COLUMN coverageid SET DEFAULT nextval('coverageitem_coverageid_seq'::regclass);


--
-- TOC entry 2898 (class 2604 OID 18306)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY coverage_task ALTER COLUMN id SET DEFAULT nextval('coveragetask_id_seq'::regclass);


--
-- TOC entry 2899 (class 2604 OID 18319)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY physicians ALTER COLUMN id SET DEFAULT nextval('physicians_id_seq'::regclass);


--
-- TOC entry 2917 (class 2606 OID 18203)
-- Name: analysis_age_distribution_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis_age_distribution
    ADD CONSTRAINT analysis_age_distribution_pkey PRIMARY KEY (id);


--
-- TOC entry 2919 (class 2606 OID 18208)
-- Name: analysis_cohort_comparison_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis_cohort_comparison
    ADD CONSTRAINT analysis_cohort_comparison_pkey PRIMARY KEY (id);


--
-- TOC entry 2921 (class 2606 OID 18213)
-- Name: analysis_gender_distribution_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis_gender_distribution
    ADD CONSTRAINT analysis_gender_distribution_pkey PRIMARY KEY (id);


--
-- TOC entry 2962 (class 2606 OID 46512)
-- Name: analysis_measurement_distr_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis_measurement_distribution
    ADD CONSTRAINT analysis_measurement_distr_pkey PRIMARY KEY (id);


--
-- TOC entry 2915 (class 2606 OID 18198)
-- Name: analysis_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis
    ADD CONSTRAINT analysis_pkey PRIMARY KEY (id);


--
-- TOC entry 2923 (class 2606 OID 18221)
-- Name: analysis_prevalence_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis_prevalence
    ADD CONSTRAINT analysis_prevalence_pkey PRIMARY KEY (id);


--
-- TOC entry 2925 (class 2606 OID 18226)
-- Name: analysis_result_binning_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis_result_binning
    ADD CONSTRAINT analysis_result_binning_pkey PRIMARY KEY (id);


--
-- TOC entry 2911 (class 2606 OID 18190)
-- Name: appuser_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY app_user
    ADD CONSTRAINT appuser_pkey PRIMARY KEY (id);


--
-- TOC entry 2927 (class 2606 OID 18237)
-- Name: authority_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authority
    ADD CONSTRAINT authority_pkey PRIMARY KEY (id);


--
-- TOC entry 2980 (class 2606 OID 96277)
-- Name: clinical_study_patient_assignment_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_patient_assignment
    ADD CONSTRAINT clinical_study_patient_assignment_pkey PRIMARY KEY (id);


--
-- TOC entry 2933 (class 2606 OID 18259)
-- Name: cohort_export_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_export
    ADD CONSTRAINT cohort_export_pkey PRIMARY KEY (id);


--
-- TOC entry 2935 (class 2606 OID 18270)
-- Name: cohort_filterblock_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_filterblock
    ADD CONSTRAINT cohort_filterblock_pkey PRIMARY KEY (id);


--
-- TOC entry 2937 (class 2606 OID 18278)
-- Name: cohort_patient_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_patient
    ADD CONSTRAINT cohort_patient_pkey PRIMARY KEY (id);


--
-- TOC entry 2931 (class 2606 OID 18248)
-- Name: cohort_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort
    ADD CONSTRAINT cohort_pkey PRIMARY KEY (id);


--
-- TOC entry 2941 (class 2606 OID 18289)
-- Name: cohort_study_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_study
    ADD CONSTRAINT cohort_study_pkey PRIMARY KEY (id);


--
-- TOC entry 2943 (class 2606 OID 18300)
-- Name: coverageitem_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY coverage_item
    ADD CONSTRAINT coverageitem_pkey PRIMARY KEY (coverageid);


--
-- TOC entry 2946 (class 2606 OID 18308)
-- Name: coveragetask_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY coverage_task
    ADD CONSTRAINT coveragetask_pkey PRIMARY KEY (id);


--
-- TOC entry 2948 (class 2606 OID 18313)
-- Name: patient_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY patient
    ADD CONSTRAINT patient_pkey PRIMARY KEY (patientid);


--
-- TOC entry 2950 (class 2606 OID 18321)
-- Name: physicians_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY physicians
    ADD CONSTRAINT physicians_pkey PRIMARY KEY (id);


--
-- TOC entry 2988 (class 2606 OID 378592)
-- Name: pk_clinical_patient_exclusion; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_patient_exclusion
    ADD CONSTRAINT pk_clinical_patient_exclusion PRIMARY KEY (id);


--
-- TOC entry 2964 (class 2606 OID 67173)
-- Name: pk_clinical_study; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study
    ADD CONSTRAINT pk_clinical_study PRIMARY KEY (id);


--
-- TOC entry 2966 (class 2606 OID 67178)
-- Name: pk_clinical_study_acceptance; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_acceptance
    ADD CONSTRAINT pk_clinical_study_acceptance PRIMARY KEY (id);


--
-- TOC entry 2984 (class 2606 OID 250482)
-- Name: pk_clinical_study_cohort; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_cohort
    ADD CONSTRAINT pk_clinical_study_cohort PRIMARY KEY (id);


--
-- TOC entry 2968 (class 2606 OID 67183)
-- Name: pk_clinical_study_investigators; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_investigators
    ADD CONSTRAINT pk_clinical_study_investigators PRIMARY KEY (clinical_study_id, investigator_id);


--
-- TOC entry 2982 (class 2606 OID 250469)
-- Name: pk_clinical_study_protocol_screening_result; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_protocol_screening_result
    ADD CONSTRAINT pk_clinical_study_protocol_screening_result PRIMARY KEY (id);


--
-- TOC entry 2970 (class 2606 OID 67189)
-- Name: pk_clinical_study_protocol_version; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_protocol_version
    ADD CONSTRAINT pk_clinical_study_protocol_version PRIMARY KEY (id);


--
-- TOC entry 2972 (class 2606 OID 67199)
-- Name: pk_clinical_study_protocol_version_document; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_protocol_version_document
    ADD CONSTRAINT pk_clinical_study_protocol_version_document PRIMARY KEY (id);


--
-- TOC entry 2974 (class 2606 OID 67208)
-- Name: pk_clinical_study_protocol_version_filter; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_protocol_version_filter
    ADD CONSTRAINT pk_clinical_study_protocol_version_filter PRIMARY KEY (id);


--
-- TOC entry 2976 (class 2606 OID 67233)
-- Name: pk_external_user; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY external_user
    ADD CONSTRAINT pk_external_user PRIMARY KEY (id);


--
-- TOC entry 2978 (class 2606 OID 67238)
-- Name: pk_principal_investigator; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY principal_investigator
    ADD CONSTRAINT pk_principal_investigator PRIMARY KEY (id);


--
-- TOC entry 2954 (class 2606 OID 18350)
-- Name: uk_5y5j21fk5xr30i9xybr7hkf1r; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY usergroup
    ADD CONSTRAINT uk_5y5j21fk5xr30i9xybr7hkf1r UNIQUE (name);


--
-- TOC entry 2913 (class 2606 OID 18343)
-- Name: uk_atqgqm46rh7b0lrgl80ryd5tp; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY app_user
    ADD CONSTRAINT uk_atqgqm46rh7b0lrgl80ryd5tp UNIQUE (username);


--
-- TOC entry 2986 (class 2606 OID 250484)
-- Name: uk_clinical_cohort_result; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_cohort
    ADD CONSTRAINT uk_clinical_cohort_result UNIQUE (screeningresult_id, cohorttype);


--
-- TOC entry 2929 (class 2606 OID 18345)
-- Name: uk_jdeu5vgpb8k5ptsqhrvamuad2; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authority
    ADD CONSTRAINT uk_jdeu5vgpb8k5ptsqhrvamuad2 UNIQUE (name);


--
-- TOC entry 2939 (class 2606 OID 18347)
-- Name: uk_ph94d5nmxkcf51jepql4ti6c3; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_patient
    ADD CONSTRAINT uk_ph94d5nmxkcf51jepql4ti6c3 UNIQUE (cohortid, patientid);


--
-- TOC entry 2952 (class 2606 OID 18326)
-- Name: user_authority_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_authority
    ADD CONSTRAINT user_authority_pkey PRIMARY KEY (user_id, authority_id);


--
-- TOC entry 2958 (class 2606 OID 18336)
-- Name: usergroup_authority_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY usergroup_authority
    ADD CONSTRAINT usergroup_authority_pkey PRIMARY KEY (group_id, authority_id);


--
-- TOC entry 2956 (class 2606 OID 18331)
-- Name: usergroup_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY usergroup
    ADD CONSTRAINT usergroup_pkey PRIMARY KEY (id);


--
-- TOC entry 2960 (class 2606 OID 18341)
-- Name: usergroup_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY usergroup_user
    ADD CONSTRAINT usergroup_user_pkey PRIMARY KEY (group_id, user_id);


--
-- TOC entry 2944 (class 1259 OID 18348)
-- Name: index_oid_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX index_oid_id ON coverage_item USING btree (base, code);


--
-- TOC entry 3010 (class 2606 OID 18456)
-- Name: fk_18upjvct1ad5yg964lc4kmpr5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY patient
    ADD CONSTRAINT fk_18upjvct1ad5yg964lc4kmpr5 FOREIGN KEY (tpid) REFERENCES physicians(id);


--
-- TOC entry 3007 (class 2606 OID 18431)
-- Name: fk_24fgnq9feognbdpmhlk27arc6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_patient
    ADD CONSTRAINT fk_24fgnq9feognbdpmhlk27arc6 FOREIGN KEY (cohortid) REFERENCES cohort(id);


--
-- TOC entry 3015 (class 2606 OID 18476)
-- Name: fk_42dircqidlfk4ojfn9009mlty; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY usergroup_authority
    ADD CONSTRAINT fk_42dircqidlfk4ojfn9009mlty FOREIGN KEY (authority_id) REFERENCES authority(id);


--
-- TOC entry 2992 (class 2606 OID 18371)
-- Name: fk_832up5yg946i1qicixc050ryj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis_cohort_comparison
    ADD CONSTRAINT fk_832up5yg946i1qicixc050ryj FOREIGN KEY (id) REFERENCES analysis(id);


--
-- TOC entry 3006 (class 2606 OID 18436)
-- Name: fk_8c85ixr1959qlbiu8spue05x0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_patient
    ADD CONSTRAINT fk_8c85ixr1959qlbiu8spue05x0 FOREIGN KEY (patientid) REFERENCES patient(patientid);


--
-- TOC entry 2989 (class 2606 OID 50611)
-- Name: fk_analysis_appuser_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis
    ADD CONSTRAINT fk_analysis_appuser_id FOREIGN KEY (creator_id) REFERENCES app_user(id);


--
-- TOC entry 3033 (class 2606 OID 96278)
-- Name: fk_assignment_protocol_version; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_patient_assignment
    ADD CONSTRAINT fk_assignment_protocol_version FOREIGN KEY (protocolversion_id) REFERENCES clinical_study_protocol_version(id);


--
-- TOC entry 3032 (class 2606 OID 96283)
-- Name: fk_assignment_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_patient_assignment
    ADD CONSTRAINT fk_assignment_user FOREIGN KEY (user_id) REFERENCES app_user(id);


--
-- TOC entry 2997 (class 2606 OID 18391)
-- Name: fk_cgnqutxbvq0vibywh4707j28q; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis_result_binning_bins
    ADD CONSTRAINT fk_cgnqutxbvq0vibywh4707j28q FOREIGN KEY (analysis_binning_result_id) REFERENCES analysis_result_binning(id);


--
-- TOC entry 3036 (class 2606 OID 250485)
-- Name: fk_clinical_cohort_result; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_cohort
    ADD CONSTRAINT fk_clinical_cohort_result FOREIGN KEY (screeningresult_id) REFERENCES clinical_study_protocol_screening_result(id);


--
-- TOC entry 3022 (class 2606 OID 67254)
-- Name: fk_clinical_study_acceptance_study; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_acceptance
    ADD CONSTRAINT fk_clinical_study_acceptance_study FOREIGN KEY (study_id) REFERENCES clinical_study(id);


--
-- TOC entry 3023 (class 2606 OID 67249)
-- Name: fk_clinical_study_acceptance_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_acceptance
    ADD CONSTRAINT fk_clinical_study_acceptance_user FOREIGN KEY (coordinator_id) REFERENCES app_user(id);


--
-- TOC entry 3035 (class 2606 OID 378580)
-- Name: fk_clinical_study_cohort; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_cohort
    ADD CONSTRAINT fk_clinical_study_cohort FOREIGN KEY (study_id) REFERENCES clinical_study(id);


--
-- TOC entry 3019 (class 2606 OID 250490)
-- Name: fk_clinical_study_current_filter; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study
    ADD CONSTRAINT fk_clinical_study_current_filter FOREIGN KEY (currentscreeningfilters_id) REFERENCES clinical_study_protocol_version_filter(id);


--
-- TOC entry 3021 (class 2606 OID 67239)
-- Name: fk_clinical_study_current_version; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study
    ADD CONSTRAINT fk_clinical_study_current_version FOREIGN KEY (currentversion_id) REFERENCES clinical_study_protocol_version(id);


--
-- TOC entry 3024 (class 2606 OID 67264)
-- Name: fk_clinical_study_investigators_investigator; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_investigators
    ADD CONSTRAINT fk_clinical_study_investigators_investigator FOREIGN KEY (investigator_id) REFERENCES principal_investigator(id);


--
-- TOC entry 3025 (class 2606 OID 67259)
-- Name: fk_clinical_study_investigators_study; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_investigators
    ADD CONSTRAINT fk_clinical_study_investigators_study FOREIGN KEY (clinical_study_id) REFERENCES clinical_study(id);


--
-- TOC entry 3026 (class 2606 OID 67269)
-- Name: fk_clinical_study_protocol_version; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_protocol_version
    ADD CONSTRAINT fk_clinical_study_protocol_version FOREIGN KEY (study_id) REFERENCES clinical_study(id);


--
-- TOC entry 3027 (class 2606 OID 67274)
-- Name: fk_clinical_study_protocol_version_document; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_protocol_version_document
    ADD CONSTRAINT fk_clinical_study_protocol_version_document FOREIGN KEY (protocolversion_id) REFERENCES clinical_study_protocol_version(id);


--
-- TOC entry 3028 (class 2606 OID 67289)
-- Name: fk_clinical_study_protocol_version_filter_creator; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_protocol_version_filter
    ADD CONSTRAINT fk_clinical_study_protocol_version_filter_creator FOREIGN KEY (creator_id) REFERENCES app_user(id);


--
-- TOC entry 3029 (class 2606 OID 67284)
-- Name: fk_clinical_study_protocol_version_filter_sponsor; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_protocol_version_filter
    ADD CONSTRAINT fk_clinical_study_protocol_version_filter_sponsor FOREIGN KEY (sponsor_id) REFERENCES external_user(id);


--
-- TOC entry 3030 (class 2606 OID 67279)
-- Name: fk_clinical_study_protocol_version_filter_version; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_protocol_version_filter
    ADD CONSTRAINT fk_clinical_study_protocol_version_filter_version FOREIGN KEY (protocolversion_id) REFERENCES clinical_study_protocol_version(id);


--
-- TOC entry 3020 (class 2606 OID 67244)
-- Name: fk_clinical_study_sponsor; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study
    ADD CONSTRAINT fk_clinical_study_sponsor FOREIGN KEY (sponsor_id) REFERENCES external_user(id);


--
-- TOC entry 2998 (class 2606 OID 50616)
-- Name: fk_cohort_appuser_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort
    ADD CONSTRAINT fk_cohort_appuser_id FOREIGN KEY (author_id) REFERENCES app_user(id);


--
-- TOC entry 3001 (class 2606 OID 50621)
-- Name: fk_cohortexport_appuser_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_export
    ADD CONSTRAINT fk_cohortexport_appuser_id FOREIGN KEY (user_id) REFERENCES app_user(id);


--
-- TOC entry 3005 (class 2606 OID 50631)
-- Name: fk_cohortpatient_appuser_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_patient
    ADD CONSTRAINT fk_cohortpatient_appuser_id FOREIGN KEY (userid) REFERENCES app_user(id);


--
-- TOC entry 3008 (class 2606 OID 50636)
-- Name: fk_cohortstudy_appuser_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_study
    ADD CONSTRAINT fk_cohortstudy_appuser_id FOREIGN KEY (author_id) REFERENCES app_user(id);


--
-- TOC entry 3009 (class 2606 OID 50656)
-- Name: fk_coverageitem_coveragetask_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY coverage_item
    ADD CONSTRAINT fk_coverageitem_coveragetask_id FOREIGN KEY (task_id) REFERENCES coverage_task(id);


--
-- TOC entry 3004 (class 2606 OID 18426)
-- Name: fk_ehwn72jip0apo99aethb2spnp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_filterblock
    ADD CONSTRAINT fk_ehwn72jip0apo99aethb2spnp FOREIGN KEY (cohort_id) REFERENCES cohort(id);


--
-- TOC entry 3003 (class 2606 OID 50626)
-- Name: fk_filterblock_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_filterblock
    ADD CONSTRAINT fk_filterblock_user FOREIGN KEY (author_id) REFERENCES app_user(id);


--
-- TOC entry 3002 (class 2606 OID 18411)
-- Name: fk_hmfvjskpswo4nmdt6dghmm9uh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort_export
    ADD CONSTRAINT fk_hmfvjskpswo4nmdt6dghmm9uh FOREIGN KEY (cohortid) REFERENCES cohort(id);


--
-- TOC entry 3017 (class 2606 OID 18491)
-- Name: fk_hup1frhuvvr5iq8rygosyipjt; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY usergroup_user
    ADD CONSTRAINT fk_hup1frhuvvr5iq8rygosyipjt FOREIGN KEY (group_id) REFERENCES usergroup(id);


--
-- TOC entry 2993 (class 2606 OID 18366)
-- Name: fk_ilhf8alo7m4yl6srj56gfyv5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis_cohort_comparison
    ADD CONSTRAINT fk_ilhf8alo7m4yl6srj56gfyv5 FOREIGN KEY (comparedid) REFERENCES cohort(id);


--
-- TOC entry 2994 (class 2606 OID 18376)
-- Name: fk_kt9dpk7ulbkxkfrfsxx6dhp6w; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis_gender_distribution
    ADD CONSTRAINT fk_kt9dpk7ulbkxkfrfsxx6dhp6w FOREIGN KEY (id) REFERENCES analysis(id);


--
-- TOC entry 3018 (class 2606 OID 46513)
-- Name: fk_measurement_distr_analysisid; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis_measurement_distribution
    ADD CONSTRAINT fk_measurement_distr_analysisid FOREIGN KEY (id) REFERENCES analysis(id);


--
-- TOC entry 3014 (class 2606 OID 18481)
-- Name: fk_nbas572la8r2to890d67og9yh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY usergroup_authority
    ADD CONSTRAINT fk_nbas572la8r2to890d67og9yh FOREIGN KEY (group_id) REFERENCES usergroup(id);


--
-- TOC entry 2995 (class 2606 OID 18381)
-- Name: fk_ou1hrexicbp0dpfrj53n0gkcu; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis_prevalence
    ADD CONSTRAINT fk_ou1hrexicbp0dpfrj53n0gkcu FOREIGN KEY (id) REFERENCES analysis(id);


--
-- TOC entry 3037 (class 2606 OID 378598)
-- Name: fk_patient_exclusion_filter; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_patient_exclusion
    ADD CONSTRAINT fk_patient_exclusion_filter FOREIGN KEY (screeningfilter_id) REFERENCES clinical_study_protocol_version_filter(id);


--
-- TOC entry 3038 (class 2606 OID 378593)
-- Name: fk_patient_exclusion_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_patient_exclusion
    ADD CONSTRAINT fk_patient_exclusion_user FOREIGN KEY (excludedby_id) REFERENCES app_user(id);


--
-- TOC entry 3011 (class 2606 OID 50641)
-- Name: fk_physicians_appuser_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY physicians
    ADD CONSTRAINT fk_physicians_appuser_id FOREIGN KEY (userid) REFERENCES app_user(id);


--
-- TOC entry 3031 (class 2606 OID 67299)
-- Name: fk_principal_investigator_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY principal_investigator
    ADD CONSTRAINT fk_principal_investigator_user FOREIGN KEY (userid) REFERENCES app_user(id);


--
-- TOC entry 3013 (class 2606 OID 18466)
-- Name: fk_r26d2qfcm6jm4jykhho6kn3u6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_authority
    ADD CONSTRAINT fk_r26d2qfcm6jm4jykhho6kn3u6 FOREIGN KEY (authority_id) REFERENCES authority(id);


--
-- TOC entry 2999 (class 2606 OID 18406)
-- Name: fk_r98jv6340klc55upyk4y7huey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort
    ADD CONSTRAINT fk_r98jv6340klc55upyk4y7huey FOREIGN KEY (source_id) REFERENCES cohort(id);


--
-- TOC entry 2991 (class 2606 OID 18361)
-- Name: fk_rguipftusfk8eo6oqu6u0o9hp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis_age_distribution
    ADD CONSTRAINT fk_rguipftusfk8eo6oqu6u0o9hp FOREIGN KEY (id) REFERENCES analysis(id);


--
-- TOC entry 3034 (class 2606 OID 250470)
-- Name: fk_screening_filter_result; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY clinical_study_protocol_screening_result
    ADD CONSTRAINT fk_screening_filter_result FOREIGN KEY (protocolscreeningfilter_id) REFERENCES clinical_study_protocol_version_filter(id);


--
-- TOC entry 2990 (class 2606 OID 18356)
-- Name: fk_telp3omeivwe5ob8jg2ufyoob; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis
    ADD CONSTRAINT fk_telp3omeivwe5ob8jg2ufyoob FOREIGN KEY (targetid) REFERENCES cohort(id);


--
-- TOC entry 3000 (class 2606 OID 18401)
-- Name: fk_tj8l3t0m56d7iypiafisgi3ea; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY cohort
    ADD CONSTRAINT fk_tj8l3t0m56d7iypiafisgi3ea FOREIGN KEY (cstudyid) REFERENCES cohort_study(id);


--
-- TOC entry 2996 (class 2606 OID 18386)
-- Name: fk_tkehlbslfudvuv1pfjutq4ao4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY analysis_result_binning
    ADD CONSTRAINT fk_tkehlbslfudvuv1pfjutq4ao4 FOREIGN KEY (analysisid) REFERENCES analysis(id);


--
-- TOC entry 3012 (class 2606 OID 50646)
-- Name: fk_userauthority_appuser_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_authority
    ADD CONSTRAINT fk_userauthority_appuser_id FOREIGN KEY (user_id) REFERENCES app_user(id);


--
-- TOC entry 3016 (class 2606 OID 50651)
-- Name: fk_usergroupuser_appuser_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY usergroup_user
    ADD CONSTRAINT fk_usergroupuser_appuser_id FOREIGN KEY (user_id) REFERENCES app_user(id);
	
CREATE SEQUENCE hibernate_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 31
  CACHE 1;
ALTER TABLE hibernate_sequence
  OWNER TO postgres;

-- Add users and authorities

INSERT INTO usergroup (id, version, name)
VALUES 
(1, 1, 'Administrators'),(2, 1, 'Treating Physicians'),(3, 1, 'Database Relational Managers'),(4, 1, 'Users');

INSERT INTO authority (id, version, name)
VALUES
(1, 1, 'ManageAccount_View'),
(2, 1, 'ManageAccount_Edit'),
(3, 1, 'ManageUsers_View'),
(4, 1, 'ManageUsers_Edit'),
(5, 1, 'Studies_View'),
(6, 1, 'Studies_ViewAsDRM'),
(7, 1, 'Studies_ViewAsPI'),
(8, 1, 'Studies_ViewAsTP'),
(9, 1, 'Study_Metadata_View'),
(10, 1, 'Study_Query_View'),
(11, 1, 'Study_Protocole_View'),
(12, 1, 'Study_Results_View'),
(13, 1, 'Study_Patients_View'),
(14, 1, 'Study_Patients_Import'),
(15, 1, 'Study_Patients_Update'),
(16, 1, 'Study_PI_View'),
(17, 1, 'Study_PI_Edit'),
(18, 1, 'Study_TP_View'),
(19, 1, 'Study_TP_Edit'),
(20, 1, 'Study_AcceptDecline'),
(21, 1, 'Study_Patients_ViewAsPI'),
(22, 1, 'Study_Patients_ViewAsTP'),
(23, 1, 'Study_Patient_View'),
(24, 1, 'Study_Patient_ViewAsPI'),
(25, 1, 'Study_Patient_ViewAsTP'),
(26, 1, 'Feasibility');

INSERT INTO usergroup_authority (group_id, authority_id)
VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
(1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17), (1, 18), (1, 19),
(1, 20), (1, 21), (1, 22), (1, 23), (1, 24), (1, 25), (1, 26),
(3, 1), (3, 2), (3, 5), (3, 9), (3, 10), (3, 11), (3, 6), (3, 12), (3, 16), (3, 17), (3, 20),
(2, 1), (2, 2), (2, 5), (2, 9), (2, 10), (2, 11), (2, 8), (2, 13), (2, 22), (2, 23), (2, 25),
(4, 1), (4, 2), (4, 5), (4, 9), (4, 10), (4, 11), (4, 8), (4, 13), (4, 22), (4, 23), (4, 25);