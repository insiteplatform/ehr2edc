-------------------------------------------------------------------------------------------
-- create ENCOUNTER_MAPPING table with clustered PK on ENCOUNTER_IDE, ENCOUNTER_IDE_SOURCE 
-------------------------------------------------------------------------------------------

CREATE TABLE ENCOUNTER_MAPPING ( 
    ENCOUNTER_IDE       	VARCHAR(200)  NOT NULL,
    ENCOUNTER_IDE_SOURCE	VARCHAR(50)  NOT NULL,
    PROJECT_ID              VARCHAR(50) NOT NULL,
    ENCOUNTER_NUM			INT NOT NULL,
    PATIENT_IDE         	VARCHAR(200) NOT NULL,
    PATIENT_IDE_SOURCE  	VARCHAR(50) NOT NULL,
    ENCOUNTER_IDE_STATUS	VARCHAR(50) NULL,
    UPLOAD_DATE         	TIMESTAMP NULL,
    UPDATE_DATE             TIMESTAMP NULL,
    DOWNLOAD_DATE       	TIMESTAMP NULL,
    IMPORT_DATE             TIMESTAMP NULL,
    SOURCESYSTEM_CD         VARCHAR(50) NULL,
    UPLOAD_ID               INT NULL,
    CONSTRAINT ENCOUNTER_MAPPING_PK PRIMARY KEY(ENCOUNTER_IDE, ENCOUNTER_IDE_SOURCE, PROJECT_ID, PATIENT_IDE, PATIENT_IDE_SOURCE)
 )
WITH (
  OIDS=FALSE
);
ALTER TABLE ENCOUNTER_MAPPING
  OWNER TO i2b2demodata;


-------------------------------------------------------------------------------------
-- create PATIENT_MAPPING table with clustered PK on PATIENT_IDE, PATIENT_IDE_SOURCE
-------------------------------------------------------------------------------------

CREATE TABLE PATIENT_MAPPING ( 
    PATIENT_IDE         VARCHAR(200)  NOT NULL,
    PATIENT_IDE_SOURCE	VARCHAR(50)  NOT NULL,
    PATIENT_NUM       	INT NOT NULL,
    PATIENT_IDE_STATUS	VARCHAR(50) NULL,
    PROJECT_ID          VARCHAR(50) NOT NULL,
    UPLOAD_DATE       	TIMESTAMP NULL,
    UPDATE_DATE       	TIMESTAMP NULL,
    DOWNLOAD_DATE     	TIMESTAMP NULL,
    IMPORT_DATE         TIMESTAMP NULL,
    SOURCESYSTEM_CD   	VARCHAR(50) NULL,
    UPLOAD_ID         	INT NULL,
    CONSTRAINT PATIENT_MAPPING_PK PRIMARY KEY(PATIENT_IDE, PATIENT_IDE_SOURCE, PROJECT_ID)
 )
WITH (
  OIDS=FALSE
);
ALTER TABLE PATIENT_MAPPING
  OWNER TO i2b2demodata;


------------------------------------------------------------------------------
-- create CODE_LOOKUP table with clustered PK on TABLE_CD, COLUMN_CD, CODE_CD 
------------------------------------------------------------------------------

CREATE TABLE CODE_LOOKUP ( 
    TABLE_CD            VARCHAR(100) NOT NULL,
    COLUMN_CD           VARCHAR(100) NOT NULL,
    CODE_CD             VARCHAR(50) NOT NULL,
    NAME_CHAR           VARCHAR(650) NULL,
    LOOKUP_BLOB         TEXT NULL, 
    UPLOAD_DATE       	TIMESTAMP NULL,
    UPDATE_DATE         TIMESTAMP NULL,
    DOWNLOAD_DATE     	TIMESTAMP NULL,
    IMPORT_DATE         TIMESTAMP NULL,
    SOURCESYSTEM_CD   	VARCHAR(50) NULL,
    UPLOAD_ID         	INT NULL,
	CONSTRAINT CODE_LOOKUP_PK PRIMARY KEY(TABLE_CD, COLUMN_CD, CODE_CD)
	)
WITH (
  OIDS=FALSE
);
ALTER TABLE  CODE_LOOKUP
  OWNER TO i2b2demodata;



--------------------------------------------------------------------
-- create CONCEPT_DIMENSION table with clustered PK on CONCEPT_PATH 
--------------------------------------------------------------------

CREATE TABLE CONCEPT_DIMENSION ( 
	CONCEPT_PATH   		VARCHAR(700) NOT NULL,
	CONCEPT_CD     		VARCHAR(50) NULL,
	NAME_CHAR      		VARCHAR(2000) NULL,
	CONCEPT_BLOB   		TEXT NULL,
	UPDATE_DATE    		TIMESTAMP NULL,
	DOWNLOAD_DATE  		TIMESTAMP NULL,
	IMPORT_DATE    		TIMESTAMP NULL,
	SOURCESYSTEM_CD		VARCHAR(50) NULL,
    UPLOAD_ID			INT NULL,
    CONSTRAINT CONCEPT_DIMENSION_PK PRIMARY KEY(CONCEPT_PATH)
	)
WITH (
  OIDS=FALSE
);
ALTER TABLE CONCEPT_DIMENSION
  OWNER TO i2b2demodata;

---------------------------------------------------------------------------------------------------------------------------------------
-- create OBSERVATION_FACT table with NONclustered PK on ENCOUNTER_NUM, CONCEPT_CD, PROVIDER_ID, START_DATE, MODIFIER_CD, INSTANCE_NUM 
---------------------------------------------------------------------------------------------------------------------------------------

CREATE TABLE OBSERVATION_FACT ( 
	ENCOUNTER_NUM  		INT NOT NULL,
	PATIENT_NUM    		INT NOT NULL,
	CONCEPT_CD     		VARCHAR(50) NOT NULL,
	PROVIDER_ID    		VARCHAR(50) NOT NULL,
	START_DATE     		TIMESTAMP NOT NULL,
	MODIFIER_CD    		VARCHAR(100) default '@' NOT NULL,
	INSTANCE_NUM		INT default (1) NOT NULL,
	VALTYPE_CD     		VARCHAR(50) NULL,
	TVAL_CHAR      		VARCHAR(255) NULL,
	NVAL_NUM       		DECIMAL(18,5) NULL,
	VALUEFLAG_CD   		VARCHAR(50) NULL,
	QUANTITY_NUM   		DECIMAL(18,5) NULL,
	UNITS_CD       		VARCHAR(50) NULL,
	END_DATE       		TIMESTAMP NULL,
	LOCATION_CD    		VARCHAR(50) NULL,
	OBSERVATION_BLOB	TEXT NULL,
	CONFIDENCE_NUM 		DECIMAL(18,5) NULL,
	UPDATE_DATE    		TIMESTAMP NULL,
	DOWNLOAD_DATE  		TIMESTAMP NULL,
	IMPORT_DATE    		TIMESTAMP NULL,
	SOURCESYSTEM_CD		VARCHAR(50) NULL, 
    UPLOAD_ID         	INT NULL,
    TEXT_SEARCH_INDEX   SERIAL,
    CONSTRAINT OBSERVATION_FACT_PK PRIMARY KEY  (CONCEPT_CD,  MODIFIER_CD, PATIENT_NUM, ENCOUNTER_NUM, PROVIDER_ID, START_DATE, INSTANCE_NUM)
	)
WITH (
  OIDS=FALSE
);
ALTER TABLE OBSERVATION_FACT
  OWNER TO i2b2demodata;



-------------------------------------------------------------------
-- create PATIENT_DIMENSION table with clustered PK on PATIENT_NUM 
-------------------------------------------------------------------

CREATE TABLE PATIENT_DIMENSION ( 
	PATIENT_NUM      	INT NOT NULL,
	VITAL_STATUS_CD  	VARCHAR(50) NULL,
	BIRTH_DATE       	TIMESTAMP NULL,
	DEATH_DATE       	TIMESTAMP NULL,
	SEX_CD           	VARCHAR(50) NULL,
	AGE_IN_YEARS_NUM	INT NULL,
	LANGUAGE_CD      	VARCHAR(50) NULL,
	RACE_CD          	VARCHAR(50) NULL,
	MARITAL_STATUS_CD	VARCHAR(50) NULL,
	RELIGION_CD      	VARCHAR(50) NULL,
	ZIP_CD           	VARCHAR(10) NULL,
	STATECITYZIP_PATH	VARCHAR(700) NULL,
	INCOME_CD			VARCHAR(50) NULL,
	PATIENT_BLOB     	TEXT NULL,
	UPDATE_DATE      	TIMESTAMP NULL,
	DOWNLOAD_DATE    	TIMESTAMP NULL,
	IMPORT_DATE      	TIMESTAMP NULL,
	SOURCESYSTEM_CD  	VARCHAR(50) NULL,
    UPLOAD_ID         	INT NULL, 
    CONSTRAINT PATIENT_DIMENSION_PK PRIMARY KEY(PATIENT_NUM)
	)
WITH (
  OIDS=FALSE
);
ALTER TABLE PATIENT_DIMENSION
  OWNER TO i2b2demodata;


-----------------------------------------------------------------------------------
-- create PROVIDER_DIMENSION table with clustered PK on PROVIDER_PATH, PROVIDER_ID 
-----------------------------------------------------------------------------------

CREATE TABLE PROVIDER_DIMENSION ( 
	PROVIDER_ID    		VARCHAR(50) NOT NULL,
	PROVIDER_PATH  		VARCHAR(700) NOT NULL,
	NAME_CHAR      		VARCHAR(850) NULL,
	PROVIDER_BLOB  		TEXT NULL,
	UPDATE_DATE    		TIMESTAMP NULL,
	DOWNLOAD_DATE  		TIMESTAMP NULL,
	IMPORT_DATE    		TIMESTAMP NULL,
	SOURCESYSTEM_CD		VARCHAR(50) NULL ,
    UPLOAD_ID         	INT NULL,
    CONSTRAINT PROVIDER_DIMENSION_PK PRIMARY KEY(PROVIDER_PATH, PROVIDER_ID)
	)
WITH (
  OIDS=FALSE
);
ALTER TABLE PROVIDER_DIMENSION
  OWNER TO i2b2demodata;


-------------------------------------------------------------------
-- create VISIT_DIMENSION table with clustered PK on ENCOUNTER_NUM 
-------------------------------------------------------------------

CREATE TABLE VISIT_DIMENSION ( 
	ENCOUNTER_NUM  		INT NOT NULL,
	PATIENT_NUM    		INT NOT NULL,
	ACTIVE_STATUS_CD	VARCHAR(50) NULL,
	START_DATE     		TIMESTAMP NULL,
	END_DATE       		TIMESTAMP NULL,
	INOUT_CD       		VARCHAR(50) NULL,
	LOCATION_CD    		VARCHAR(50) NULL,
    LOCATION_PATH  		VARCHAR(900) NULL,
	LENGTH_OF_STAY		INT NULL,
	VISIT_BLOB     		TEXT NULL,
	UPDATE_DATE    		TIMESTAMP NULL,
	DOWNLOAD_DATE  		TIMESTAMP NULL,
	IMPORT_DATE    		TIMESTAMP NULL,
	SOURCESYSTEM_CD		VARCHAR(50) NULL ,
    UPLOAD_ID         	INT NULL, 
    CONSTRAINT VISIT_DIMENSION_PK PRIMARY KEY(ENCOUNTER_NUM, PATIENT_NUM)
	)
WITH (
  OIDS=FALSE
);
ALTER TABLE VISIT_DIMENSION
  OWNER TO i2b2demodata;


------------------------------------------------------------
-- create MODIFIER_DIMENSION table with PK on MODIFIER_PATH 
------------------------------------------------------------

CREATE TABLE MODIFIER_DIMENSION ( 
	MODIFIER_PATH   	VARCHAR(700) NOT NULL,
	MODIFIER_CD     	VARCHAR(50) NULL,
	NAME_CHAR      		VARCHAR(2000) NULL,
	MODIFIER_BLOB   	TEXT NULL,
	UPDATE_DATE    		TIMESTAMP NULL,
	DOWNLOAD_DATE  		TIMESTAMP NULL,
	IMPORT_DATE    		TIMESTAMP NULL,
	SOURCESYSTEM_CD		VARCHAR(50) NULL,
    UPLOAD_ID			INT NULL,
    CONSTRAINT MODIFIER_DIMENSION_PK PRIMARY KEY(modifier_path)
	)
WITH (
  OIDS=FALSE
);
ALTER TABLE MODIFIER_DIMENSION
  OWNER TO i2b2demodata;

  
 CREATE TABLE QT_QUERY_MASTER (
	QUERY_MASTER_ID		SERIAL PRIMARY KEY,
	NAME				VARCHAR(250) NOT NULL,
	USER_ID				VARCHAR(50) NOT NULL,
	GROUP_ID			VARCHAR(50) NOT NULL,
	MASTER_TYPE_CD		VARCHAR(2000),
	PLUGIN_ID			INT,
	CREATE_DATE			TIMESTAMP NOT NULL,
	DELETE_DATE			TIMESTAMP,
	DELETE_FLAG			VARCHAR(3),
	REQUEST_XML			TEXT,
	GENERATED_SQL		TEXT,
	I2B2_REQUEST_XML	TEXT,
	PM_XML				TEXT
)
WITH (
OIDS=FALSE
);
ALTER TABLE QT_QUERY_MASTER
  OWNER TO i2b2demodata;

/*============================================================================*/
/* Table: QT_QUERY_RESULT_TYPE										          */
/*============================================================================*/
CREATE TABLE QT_QUERY_RESULT_TYPE (
	RESULT_TYPE_ID				INT   PRIMARY KEY,
	NAME						VARCHAR(100),
	DESCRIPTION					VARCHAR(200),
	DISPLAY_TYPE_ID				VARCHAR(500),
	VISUAL_ATTRIBUTE_TYPE_ID	VARCHAR(3)
)
WITH (
OIDS=FALSE
);
ALTER TABLE QT_QUERY_RESULT_TYPE
  OWNER TO i2b2demodata;


/*============================================================================*/
/* Table: QT_QUERY_STATUS_TYPE										          */
/*============================================================================*/
CREATE TABLE QT_QUERY_STATUS_TYPE (
	STATUS_TYPE_ID	INT   PRIMARY KEY,
	NAME			VARCHAR(100),
	DESCRIPTION		VARCHAR(200)
)
WITH (
OIDS=FALSE
);
ALTER TABLE QT_QUERY_STATUS_TYPE
  OWNER TO i2b2demodata;


/*============================================================================*/
/* Table: QT_QUERY_INSTANCE 										          */
/*============================================================================*/
CREATE TABLE QT_QUERY_INSTANCE (
	QUERY_INSTANCE_ID	SERIAL PRIMARY KEY,
	QUERY_MASTER_ID		INT,
	USER_ID				VARCHAR(50) NOT NULL,
	GROUP_ID			VARCHAR(50) NOT NULL,
	BATCH_MODE			VARCHAR(50),
	START_DATE			TIMESTAMP NOT NULL,
	END_DATE			TIMESTAMP,
	DELETE_FLAG			VARCHAR(3),
	STATUS_TYPE_ID		INT,
	MESSAGE				TEXT,
	CONSTRAINT QT_FK_QI_MID FOREIGN KEY (QUERY_MASTER_ID)
		REFERENCES QT_QUERY_MASTER (QUERY_MASTER_ID),
	CONSTRAINT QT_FK_QI_STID FOREIGN KEY (STATUS_TYPE_ID)
		REFERENCES QT_QUERY_STATUS_TYPE (STATUS_TYPE_ID)
)
WITH (
OIDS=FALSE
);
ALTER TABLE QT_QUERY_INSTANCE
  OWNER TO i2b2demodata;

/*=============================================================================*/
/* Table: QT_QUERY_RESULT_INSTANCE   								          */
/*============================================================================*/
	CREATE TABLE QT_QUERY_RESULT_INSTANCE (
	RESULT_INSTANCE_ID	SERIAL PRIMARY KEY,
	QUERY_INSTANCE_ID	INT,
	RESULT_TYPE_ID		INT NOT NULL,
	SET_SIZE			INT,
	START_DATE			TIMESTAMP NOT NULL,
	END_DATE			TIMESTAMP,
	STATUS_TYPE_ID		INT NOT NULL,
	DELETE_FLAG			VARCHAR(3),
	MESSAGE				TEXT,
	DESCRIPTION			VARCHAR(200),
	REAL_SET_SIZE		INT,
	OBFUSC_METHOD		VARCHAR(500),
	CONSTRAINT QT_FK_QRI_RID FOREIGN KEY (QUERY_INSTANCE_ID)
		REFERENCES QT_QUERY_INSTANCE (QUERY_INSTANCE_ID),
	CONSTRAINT QT_FK_QRI_RTID FOREIGN KEY (RESULT_TYPE_ID)
		REFERENCES QT_QUERY_RESULT_TYPE (RESULT_TYPE_ID),
	CONSTRAINT QT_FK_QRI_STID FOREIGN KEY (STATUS_TYPE_ID)
		REFERENCES QT_QUERY_STATUS_TYPE (STATUS_TYPE_ID)
)
WITH (
OIDS=FALSE
);
ALTER TABLE QT_QUERY_RESULT_INSTANCE
  OWNER TO i2b2demodata;


/*============================================================================*/
/* Table: QT_PATIENT_SET_COLLECTION									          */
/*============================================================================*/
CREATE TABLE QT_PATIENT_SET_COLLECTION ( 
	PATIENT_SET_COLL_ID	BIGSERIAL PRIMARY KEY,
	RESULT_INSTANCE_ID	INT,
	SET_INDEX			INT,
	PATIENT_NUM			INT,
	CONSTRAINT QT_FK_PSC_RI FOREIGN KEY (RESULT_INSTANCE_ID )
		REFERENCES QT_QUERY_RESULT_INSTANCE (RESULT_INSTANCE_ID)
)
WITH (
OIDS=FALSE
);
ALTER TABLE QT_PATIENT_SET_COLLECTION 
  OWNER TO i2b2demodata;

/*============================================================================*/
/* Table: QT_PATIENT_ENC_COLLECTION									          */
/*============================================================================*/
CREATE TABLE QT_PATIENT_ENC_COLLECTION (
	PATIENT_ENC_COLL_ID	SERIAL PRIMARY KEY,
	RESULT_INSTANCE_ID	INT,
	SET_INDEX			INT,
	PATIENT_NUM			INT,
	ENCOUNTER_NUM		INT,
	CONSTRAINT QT_FK_PESC_RI FOREIGN KEY (RESULT_INSTANCE_ID)
		REFERENCES QT_QUERY_RESULT_INSTANCE(RESULT_INSTANCE_ID)
)
WITH (
OIDS=FALSE
);
ALTER TABLE QT_PATIENT_ENC_COLLECTION
  OWNER TO i2b2demodata;


/*============================================================================*/
/* Table: QT_XML_RESULT												          */
/*============================================================================*/
CREATE TABLE QT_XML_RESULT (
	XML_RESULT_ID		SERIAL PRIMARY KEY,
	RESULT_INSTANCE_ID	INT,
	XML_VALUE			TEXT,
	CONSTRAINT QT_FK_XMLR_RIID FOREIGN KEY (RESULT_INSTANCE_ID)
		REFERENCES QT_QUERY_RESULT_INSTANCE (RESULT_INSTANCE_ID)
)
WITH (
OIDS=FALSE
);
ALTER TABLE QT_XML_RESULT 
  OWNER TO i2b2demodata;


/*============================================================================*/
/* Table: QT_ANALYSIS_PLUGIN										          */
/*============================================================================*/
CREATE TABLE QT_ANALYSIS_PLUGIN (
	PLUGIN_ID			INT NOT NULL,
	PLUGIN_NAME			VARCHAR(2000),
	DESCRIPTION			VARCHAR(2000),
	VERSION_CD			VARCHAR(50),	--support for version
	PARAMETER_INFO		TEXT,			-- plugin parameter stored as xml
	PARAMETER_INFO_XSD	TEXT,
	COMMAND_LINE		TEXT,
	WORKING_FOLDER		TEXT,
	COMMANDOPTION_CD	TEXT,
	PLUGIN_ICON         TEXT,
	STATUS_CD			VARCHAR(50),	-- active,deleted,..
	USER_ID				VARCHAR(50),
	GROUP_ID			VARCHAR(50),
	CREATE_DATE			TIMESTAMP,
	UPDATE_DATE			TIMESTAMP,
	CONSTRAINT ANALYSIS_PLUGIN_PK PRIMARY KEY(PLUGIN_ID)
)
WITH (
OIDS=FALSE
);
ALTER TABLE QT_ANALYSIS_PLUGIN
  OWNER TO i2b2demodata;

/*============================================================================*/
/* Table: QT_ANALYSIS_PLUGIN_RESULT_TYPE							          */
/*============================================================================*/
CREATE TABLE QT_ANALYSIS_PLUGIN_RESULT_TYPE (
	PLUGIN_ID		INT,
	RESULT_TYPE_ID	INT,
	CONSTRAINT ANALYSIS_PLUGIN_RESULT_PK PRIMARY KEY(PLUGIN_ID,RESULT_TYPE_ID)
)
WITH (
OIDS=FALSE
);
ALTER TABLE QT_ANALYSIS_PLUGIN_RESULT_TYPE
  OWNER TO i2b2demodata;


/*============================================================================*/
/* Table: QT_PRIVILEGE												          */
/*============================================================================*/
CREATE TABLE QT_PRIVILEGE(
	PROTECTION_LABEL_CD		VARCHAR(1500),
	DATAPROT_CD				VARCHAR(1000),
	HIVEMGMT_CD				VARCHAR(1000),
	PLUGIN_ID				INT
)
WITH (
OIDS=FALSE
);
ALTER TABLE QT_PRIVILEGE
  OWNER TO i2b2demodata;


/*============================================================================*/
/* Table: QT_BREAKDOWN_PATH											          */
/*============================================================================*/
CREATE TABLE QT_BREAKDOWN_PATH (
	NAME			VARCHAR(100), 
	VALUE			VARCHAR(2000), 
	CREATE_DATE		TIMESTAMP,
	UPDATE_DATE		TIMESTAMP,
	USER_ID			VARCHAR(50)
)
WITH (
OIDS=FALSE
);
ALTER TABLE QT_BREAKDOWN_PATH
  OWNER TO i2b2demodata;


/*============================================================================*/
/* Table:QT_PDO_QUERY_MASTER 										          */
/*============================================================================*/
CREATE TABLE QT_PDO_QUERY_MASTER (
	QUERY_MASTER_ID		SERIAL PRIMARY KEY,
	USER_ID				VARCHAR(50) NOT NULL,
	GROUP_ID			VARCHAR(50) NOT NULL,
	CREATE_DATE			TIMESTAMP NOT NULL,
	REQUEST_XML			TEXT,
	I2B2_REQUEST_XML	TEXT
)
WITH (
OIDS=FALSE
);
ALTER TABLE QT_PDO_QUERY_MASTER
  OWNER TO i2b2demodata;


-- DX
CREATE GLOBAL TEMPORARY TABLE DX  (
	ENCOUNTER_NUM	INT,
	PATIENT_NUM		INT,
	INSTANCE_NUM	INT,
	CONCEPT_CD 		varchar(50), 
	START_DATE 		TIMESTAMP,
	PROVIDER_ID 	varchar(50), 
	temporal_start_date TIMESTAMP, 
	temporal_end_date TIMESTAMP	
 ) WITH (
OIDS=FALSE
) on COMMIT PRESERVE ROWS
;
ALTER TABLE DX
  OWNER TO i2b2demodata;

create  GLOBAL TEMPORARY TABLE TEMP_PDO_INPUTLIST    ( 
char_param1 varchar(100)
 ) WITH (
OIDS=FALSE
)ON COMMIT PRESERVE ROWS;
ALTER TABLE TEMP_PDO_INPUTLIST
  OWNER TO i2b2demodata;


-- QUERY_GLOBAL_TEMP
CREATE GLOBAL TEMPORARY TABLE QUERY_GLOBAL_TEMP   ( 
	ENCOUNTER_NUM	INT,
	PATIENT_NUM		INT,
	INSTANCE_NUM	INT ,
	CONCEPT_CD      VARCHAR(50),
	START_DATE	    DATE,
	PROVIDER_ID     VARCHAR(50),
	PANEL_COUNT		INT,
	FACT_COUNT		INT,
	FACT_PANELS		INT
 ) WITH (
OIDS=FALSE
)on COMMIT PRESERVE ROWS
;
ALTER TABLE QUERY_GLOBAL_TEMP
  OWNER TO i2b2demodata;

-- GLOBAL_TEMP_PARAM_TABLE
 CREATE GLOBAL TEMPORARY TABLE GLOBAL_TEMP_PARAM_TABLE   (
	SET_INDEX	INT,
	CHAR_PARAM1	VARCHAR(500),
	CHAR_PARAM2	VARCHAR(500),
	NUM_PARAM1	INT,
	NUM_PARAM2	INT
 ) WITH (
OIDS=FALSE
)ON COMMIT PRESERVE ROWS
;
ALTER TABLE GLOBAL_TEMP_PARAM_TABLE
  OWNER TO i2b2demodata;

-- GLOBAL_TEMP_FACT_PARAM_TABLE
CREATE GLOBAL TEMPORARY TABLE GLOBAL_TEMP_FACT_PARAM_TABLE   (
	SET_INDEX	INT,
	CHAR_PARAM1	VARCHAR(500),
	CHAR_PARAM2	VARCHAR(500),
	NUM_PARAM1	INT,
	NUM_PARAM2	INT
 ) WITH (
OIDS=FALSE
) ON COMMIT PRESERVE ROWS
;
ALTER TABLE GLOBAL_TEMP_FACT_PARAM_TABLE
  OWNER TO i2b2demodata;

-- MASTER_QUERY_GLOBAL_TEMP
CREATE GLOBAL TEMPORARY TABLE MASTER_QUERY_GLOBAL_TEMP    ( 
	ENCOUNTER_NUM	INT,
	PATIENT_NUM		INT,
	INSTANCE_NUM	INT ,
	CONCEPT_CD      VARCHAR(50),
	START_DATE	    DATE,
	PROVIDER_ID     VARCHAR(50),
	MASTER_ID		VARCHAR(50),
	LEVEL_NO		INT,
	TEMPORAL_START_DATE DATE,
	TEMPORAL_END_DATE DATE
 ) WITH (
OIDS=FALSE
) ON COMMIT PRESERVE ROWS
;
ALTER TABLE MASTER_QUERY_GLOBAL_TEMP
  OWNER TO i2b2demodata;

CREATE TABLE insite_query_id_mapping
(
  query_master_id integer,
  query_guid character varying(36) NOT NULL,
  study_id character varying(250) DEFAULT NULL::character varying,
  CONSTRAINT insite_query_id_mapping_pkey PRIMARY KEY (query_guid),
  CONSTRAINT qt_fk_qi_mid FOREIGN KEY (query_master_id)
      REFERENCES qt_query_master (query_master_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE insite_query_id_mapping
  OWNER TO i2b2demodata;
  
select * into ARCHIVE_OBSERVATION_FACT from OBSERVATION_FACT where 1=2 
;

ALTER TABLE ARCHIVE_OBSERVATION_FACT  ADD  ARCHIVE_UPLOAD_ID int
;

ALTER TABLE ARCHIVE_OBSERVATION_FACT
  OWNER TO i2b2demodata;


/*==============================================================*/
/* Table: DATAMART_REPORT			                    		*/
/*==============================================================*/
create table DATAMART_REPORT ( 
	TOTAL_PATIENT         int, 
	TOTAL_OBSERVATIONFACT int, 
	TOTAL_EVENT           int,
	REPORT_DATE           TIMESTAMP)
WITH (
OIDS=FALSE
);
ALTER TABLE DATAMART_REPORT
  OWNER TO i2b2demodata;




/*==============================================================*/
/* Table: UPLOAD_STATUS 					                    */
/*==============================================================*/
CREATE TABLE UPLOAD_STATUS (
	UPLOAD_ID 		    serial PRIMARY KEY, 	
    UPLOAD_LABEL 		VARCHAR(500) NOT NULL, 
    USER_ID      		VARCHAR(100) NOT NULL, 
    SOURCE_CD   		VARCHAR(50) NOT NULL,
    NO_OF_RECORD 		bigint,
    LOADED_RECORD 		bigint,
    DELETED_RECORD		bigint, 
    LOAD_DATE    		TIMESTAMP			  NOT NULL,
	END_DATE 	        TIMESTAMP , 
    LOAD_STATUS  		VARCHAR(100), 
    MESSAGE				TEXT,
    INPUT_FILE_NAME 	TEXT, 
    LOG_FILE_NAME 		TEXT, 
    TRANSFORM_NAME 		VARCHAR(500)
   
) 
WITH (
OIDS=FALSE
);
ALTER TABLE UPLOAD_STATUS
  OWNER TO i2b2demodata;

/*==============================================================*/
/* Table: SET_TYPE						                        */
/*==============================================================*/
CREATE TABLE SET_TYPE (
	ID 				INT, 
    NAME			VARCHAR(500),
    CREATE_DATE     TIMESTAMP,
    CONSTRAINT PK_ST_ID PRIMARY KEY (ID)
) 
WITH (
OIDS=FALSE
);
ALTER TABLE SET_TYPE
  OWNER TO i2b2demodata;



/*==============================================================*/
/* Table: SOURCE_MASTER					                        */
/*==============================================================*/
CREATE TABLE SOURCE_MASTER ( 
   SOURCE_CD 				VARCHAR(50) NOT NULL,
   DESCRIPTION  			VARCHAR(300),
   CREATE_DATE 				TIMESTAMP,
   CONSTRAINT PK_SOURCEMASTER_SOURCECD  PRIMARY KEY (SOURCE_CD)
)
WITH (
OIDS=FALSE
);
ALTER TABLE SOURCE_MASTER
  OWNER TO i2b2demodata;


/*==============================================================*/
/* Table: SET_UPLOAD_STATUS				                        */
/*==============================================================*/
CREATE TABLE SET_UPLOAD_STATUS  (
    UPLOAD_ID			INT,
    SET_TYPE_ID         INT,
    SOURCE_CD  		    VARCHAR(50) NOT NULL,
    NO_OF_RECORD 		BIGINT,
    LOADED_RECORD 		BIGINT,
    DELETED_RECORD		BIGINT, 
    LOAD_DATE    		TIMESTAMP NOT NULL,
    END_DATE            TIMESTAMP ,
    LOAD_STATUS  		VARCHAR(100), 
    MESSAGE			    TEXT,
    INPUT_FILE_NAME 	TEXT, 
    LOG_FILE_NAME 		TEXT, 
    TRANSFORM_NAME 		VARCHAR(500),
    CONSTRAINT PK_UP_UPSTATUS_IDSETTYPEID  PRIMARY KEY (UPLOAD_ID,SET_TYPE_ID),
    CONSTRAINT FK_UP_SET_TYPE_ID FOREIGN KEY (SET_TYPE_ID) REFERENCES SET_TYPE(ID)
) 
WITH (
OIDS=FALSE
);
ALTER TABLE SET_UPLOAD_STATUS 
  OWNER TO i2b2demodata;
  
-- Function: create_temp_concept_table(text)

-- DROP FUNCTION create_temp_concept_table(text);

CREATE OR REPLACE FUNCTION create_temp_concept_table(
    IN tempconcepttablename text,
    OUT errormsg text)
  RETURNS text AS
$BODY$
BEGIN 
    EXECUTE 'create table ' ||  tempConceptTableName || ' (
        CONCEPT_CD varchar(50) NOT NULL, 
        CONCEPT_PATH varchar(900) NOT NULL , 
        NAME_CHAR varchar(2000), 
        CONCEPT_BLOB text, 
        UPDATE_DATE timestamp, 
        DOWNLOAD_DATE timestamp, 
        IMPORT_DATE timestamp, 
        SOURCESYSTEM_CD varchar(50)
    ) WITH OIDS';
    EXECUTE 'CREATE INDEX idx_' || tempConceptTableName || '_pat_id ON ' || tempConceptTableName || '  (CONCEPT_PATH)';
    EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;      
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_temp_concept_table(text)
  OWNER TO i2b2demodata;

-- Function: create_temp_eid_table(text)

-- DROP FUNCTION create_temp_eid_table(text);

CREATE OR REPLACE FUNCTION create_temp_eid_table(
    IN temppatientmappingtablename text,
    OUT errormsg text)
  RETURNS text AS
$BODY$
BEGIN 
    EXECUTE 'create table ' ||  tempPatientMappingTableName || ' (
        ENCOUNTER_MAP_ID        varchar(200) NOT NULL,
        ENCOUNTER_MAP_ID_SOURCE     varchar(50) NOT NULL,
        PROJECT_ID              VARCHAR(50) NOT NULL,
        PATIENT_MAP_ID          varchar(200), 
        PATIENT_MAP_ID_SOURCE   varchar(50), 
        ENCOUNTER_ID            varchar(200) NOT NULL,
        ENCOUNTER_ID_SOURCE     varchar(50) ,
        ENCOUNTER_NUM           numeric, 
        ENCOUNTER_MAP_ID_STATUS    varchar(50),
        PROCESS_STATUS_FLAG     char(1),
        UPDATE_DATE timestamp, 
        DOWNLOAD_DATE timestamp, 
        IMPORT_DATE timestamp, 
        SOURCESYSTEM_CD varchar(50)
    ) WITH OIDS';
    EXECUTE 'CREATE INDEX idx_' || tempPatientMappingTableName || '_eid_id ON ' || tempPatientMappingTableName || '  (ENCOUNTER_ID, ENCOUNTER_ID_SOURCE, ENCOUNTER_MAP_ID, ENCOUNTER_MAP_ID_SOURCE, ENCOUNTER_NUM)';
    EXECUTE 'CREATE INDEX idx_' || tempPatientMappingTableName || '_stateid_eid_id ON ' || tempPatientMappingTableName || '  (PROCESS_STATUS_FLAG)';  
    EXCEPTION
    WHEN OTHERS THEN
        RAISE NOTICE '%%%', SQLSTATE || ' - ' || SQLERRM;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_temp_eid_table(text)
  OWNER TO i2b2demodata;

-- Function: create_temp_modifier_table(text)

-- DROP FUNCTION create_temp_modifier_table(text);

CREATE OR REPLACE FUNCTION create_temp_modifier_table(
    IN tempmodifiertablename text,
    OUT errormsg text)
  RETURNS text AS
$BODY$
BEGIN 
EXECUTE 'create table ' ||  tempModifierTableName || ' (
        MODIFIER_CD varchar(50) NOT NULL, 
        MODIFIER_PATH varchar(900) NOT NULL , 
        NAME_CHAR varchar(2000), 
        MODIFIER_BLOB text, 
        UPDATE_DATE timestamp, 
        DOWNLOAD_DATE timestamp, 
        IMPORT_DATE timestamp, 
        SOURCESYSTEM_CD varchar(50)
         ) WITH OIDS';
 EXECUTE 'CREATE INDEX idx_' || tempModifierTableName || '_pat_id ON ' || tempModifierTableName || '  (MODIFIER_PATH)';
EXCEPTION
        WHEN OTHERS THEN
        RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;      
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_temp_modifier_table(text)
  OWNER TO i2b2demodata;

-- Function: create_temp_patient_table(text)

-- DROP FUNCTION create_temp_patient_table(text);

CREATE OR REPLACE FUNCTION create_temp_patient_table(
    IN temppatientdimensiontablename text,
    OUT errormsg text)
  RETURNS text AS
$BODY$
BEGIN 
    -- Create temp table to store encounter/visit information
    EXECUTE 'create table ' ||  tempPatientDimensionTableName || ' (
        patient_id varchar(200), 
        patient_id_source varchar(50),
        patient_num numeric(38,0),
        vital_status_cd varchar(50), 
        birth_date timestamp, 
        death_date timestamp, 
        sex_cd char(50), 
        age_in_years_num numeric(5,0), 
        language_cd varchar(50), 
        race_cd varchar(50 ), 
        marital_status_cd varchar(50), 
        religion_cd varchar(50), 
        zip_cd varchar(50), 
        statecityzip_path varchar(700), 
        patient_blob text, 
        update_date timestamp, 
        download_date timestamp, 
        import_date timestamp, 
        sourcesystem_cd varchar(50)
    )';
    EXECUTE 'CREATE INDEX idx_' || tempPatientDimensionTableName || '_pat_id ON ' || tempPatientDimensionTableName || '  (patient_id, patient_id_source,patient_num)';
    EXCEPTION
    WHEN OTHERS THEN
        RAISE NOTICE '%%%', SQLSTATE || ' - ' || SQLERRM;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_temp_patient_table(text)
  OWNER TO i2b2demodata;

-- Function: create_temp_pid_table(text)

-- DROP FUNCTION create_temp_pid_table(text);

CREATE OR REPLACE FUNCTION create_temp_pid_table(
    IN temppatientmappingtablename text,
    OUT errormsg text)
  RETURNS text AS
$BODY$
BEGIN 
	EXECUTE 'create table ' ||  tempPatientMappingTableName || ' (
		PATIENT_MAP_ID varchar(200), 
		PATIENT_MAP_ID_SOURCE varchar(50), 
		PATIENT_ID_STATUS varchar(50), 
		PATIENT_ID  varchar(200),
		PATIENT_ID_SOURCE varchar(50),
		PROJECT_ID   VARCHAR(50) ,
		PATIENT_NUM numeric(38,0),
		PATIENT_MAP_ID_STATUS varchar(50), 
		PROCESS_STATUS_FLAG char(1), 
		UPDATE_DATE timestamp, 
		DOWNLOAD_DATE timestamp, 
		IMPORT_DATE timestamp, 
		SOURCESYSTEM_CD varchar(50)
	) WITH OIDS';
	EXECUTE 'CREATE INDEX idx_' || tempPatientMappingTableName || '_pid_id ON ' || tempPatientMappingTableName || '  ( PATIENT_ID, PATIENT_ID_SOURCE )';
	EXECUTE 'CREATE INDEX idx_' || tempPatientMappingTableName || 'map_pid_id ON ' || tempPatientMappingTableName || '  
	( PATIENT_ID, PATIENT_ID_SOURCE,PATIENT_MAP_ID, PATIENT_MAP_ID_SOURCE,  PATIENT_NUM )';
	EXECUTE 'CREATE INDEX idx_' || tempPatientMappingTableName || 'stat_pid_id ON ' || tempPatientMappingTableName || '  
	(PROCESS_STATUS_FLAG)';
	EXCEPTION
	WHEN OTHERS THEN
		RAISE NOTICE '%%%', SQLSTATE || ' - ' || SQLERRM;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_temp_pid_table(text)
  OWNER TO i2b2demodata;

-- Function: create_temp_provider_table(text)

-- DROP FUNCTION create_temp_provider_table(text);

CREATE OR REPLACE FUNCTION create_temp_provider_table(
    IN tempprovidertablename text,
    OUT errormsg text)
  RETURNS text AS
$BODY$
BEGIN 
    EXECUTE 'create table ' ||  tempProviderTableName || ' (
        PROVIDER_ID varchar(50) NOT NULL, 
        PROVIDER_PATH varchar(700) NOT NULL, 
        NAME_CHAR varchar(2000), 
        PROVIDER_BLOB text, 
        UPDATE_DATE timestamp, 
        DOWNLOAD_DATE timestamp, 
        IMPORT_DATE timestamp, 
        SOURCESYSTEM_CD varchar(50), 
        UPLOAD_ID numeric
    ) WITH OIDS';
    EXECUTE 'CREATE INDEX idx_' || tempProviderTableName || '_ppath_id ON ' || tempProviderTableName || '  (PROVIDER_PATH)';
    EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;      

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_temp_provider_table(text)
  OWNER TO i2b2demodata;

-- Function: create_temp_table(text)

-- DROP FUNCTION create_temp_table(text);

CREATE OR REPLACE FUNCTION create_temp_table(
    IN temptablename text,
    OUT errormsg text)
  RETURNS text AS
$BODY$
BEGIN 
    EXECUTE 'create table ' ||  tempTableName || '  (
        encounter_num  numeric(38,0),
        encounter_id varchar(200) not null, 
        encounter_id_source varchar(50) not null,
        concept_cd       varchar(50) not null, 
        patient_num numeric(38,0), 
        patient_id  varchar(200) not null,
        patient_id_source  varchar(50) not null,
        provider_id   varchar(50),
        start_date   timestamp, 
        modifier_cd varchar(100),
        instance_num numeric(18,0),
        valtype_cd varchar(50),
        tval_char varchar(255),
        nval_num numeric(18,5),
        valueflag_cd char(50),
        quantity_num numeric(18,5),
        confidence_num numeric(18,0),
        observation_blob text,
        units_cd varchar(50),
        end_date    timestamp,
        location_cd varchar(50),
        update_date  timestamp,
        download_date timestamp,
        import_date timestamp,
        sourcesystem_cd varchar(50) ,
        upload_id integer
    ) WITH OIDS';
    EXECUTE 'CREATE INDEX idx_' || tempTableName || '_pk ON ' || tempTableName || '  ( encounter_num,patient_num,concept_cd,provider_id,start_date,modifier_cd,instance_num)';
    EXECUTE 'CREATE INDEX idx_' || tempTableName || '_enc_pat_id ON ' || tempTableName || '  (encounter_id,encounter_id_source, patient_id,patient_id_source )';
    EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM; 
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_temp_table(text)
  OWNER TO i2b2demodata;

-- Function: create_temp_visit_table(text)

-- DROP FUNCTION create_temp_visit_table(text);

CREATE OR REPLACE FUNCTION create_temp_visit_table(
    IN temptablename text,
    OUT errormsg text)
  RETURNS text AS
$BODY$
BEGIN 
    -- Create temp table to store encounter/visit information
    EXECUTE 'create table ' ||  tempTableName || ' (
        encounter_id                    varchar(200) not null,
        encounter_id_source             varchar(50) not null, 
        project_id                      varchar(50) not null,
        patient_id                      varchar(200) not null,
        patient_id_source               varchar(50) not null,
        encounter_num                   numeric(38,0), 
        inout_cd                        varchar(50),
        location_cd                     varchar(50),
        location_path                   varchar(900),
        start_date                      timestamp, 
        end_date                        timestamp,
        visit_blob                      text,
        update_date                     timestamp,
        download_date                   timestamp,
        import_date                     timestamp,
        sourcesystem_cd                 varchar(50)
    ) WITH OIDS';
    EXECUTE 'CREATE INDEX idx_' || tempTableName || '_enc_id ON ' || tempTableName || '  ( encounter_id,encounter_id_source,patient_id,patient_id_source )';
    EXECUTE 'CREATE INDEX idx_' || tempTableName || '_patient_id ON ' || tempTableName || '  ( patient_id,patient_id_source )';
    EXCEPTION
    WHEN OTHERS THEN    
        RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;    
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_temp_visit_table(text)
  OWNER TO i2b2demodata;

-- Function: insert_concept_fromtemp(text, bigint)

-- DROP FUNCTION insert_concept_fromtemp(text, bigint);

CREATE OR REPLACE FUNCTION insert_concept_fromtemp(
    IN tempconcepttablename text,
    IN upload_id bigint,
    OUT errormsg text)
  RETURNS text AS
$BODY$
BEGIN 
    --Delete duplicate rows with same encounter and patient combination
    EXECUTE 'DELETE 
    FROM
    ' || tempConceptTableName || ' t1 
    WHERE
    oid > (SELECT  
        min(oid) 
        FROM 
        ' || tempConceptTableName || ' t2
        WHERE 
        t1.concept_cd = t2.concept_cd 
        AND t1.concept_path = t2.concept_path
    )';
    EXECUTE ' UPDATE concept_dimension  
    SET  
    concept_cd=temp.concept_cd
    ,name_char=temp.name_char
    ,concept_blob=temp.concept_blob
    ,update_date=temp.update_date
    ,download_date=temp.download_date
    ,import_date=Now()
    ,sourcesystem_cd=temp.sourcesystem_cd
    ,upload_id=' || UPLOAD_ID  || '
    FROM 
    ' || tempConceptTableName || '  temp   
    WHERE 
    temp.concept_path = concept_dimension.concept_path 
    AND temp.update_date >= concept_dimension.update_date 
    AND EXISTS (SELECT 1 
        FROM ' || tempConceptTableName || ' temp  
        WHERE temp.concept_path = concept_dimension.concept_path 
        AND temp.update_date >= concept_dimension.update_date
    )
    ';
    --Create new patient(patient_mapping) if temp table patient_ide does not exists 
    -- in patient_mapping table.
    EXECUTE 'INSERT INTO concept_dimension  (
        concept_cd
        ,concept_path
        ,name_char
        ,concept_blob
        ,update_date
        ,download_date
        ,import_date
        ,sourcesystem_cd
        ,upload_id
    )
    SELECT  
    concept_cd
    ,concept_path
    ,name_char
    ,concept_blob
    ,update_date
    ,download_date
    ,Now()
    ,sourcesystem_cd
    ,' || upload_id || '
    FROM ' || tempConceptTableName || '  temp
    WHERE NOT EXISTS (SELECT concept_cd 
        FROM concept_dimension cd 
        WHERE cd.concept_path = temp.concept_path)
    ';
    EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;      
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION insert_concept_fromtemp(text, bigint)
  OWNER TO i2b2demodata;

-- Function: insert_eid_map_fromtemp(text, bigint)

-- DROP FUNCTION insert_eid_map_fromtemp(text, bigint);

CREATE OR REPLACE FUNCTION insert_eid_map_fromtemp(
    IN tempeidtablename text,
    IN upload_id bigint,
    OUT errormsg text)
  RETURNS text AS
$BODY$
DECLARE

existingEncounterNum varchar(32);
maxEncounterNum bigint;
distinctEidCur REFCURSOR;
disEncounterId varchar(100); 
disEncounterIdSource varchar(100);

BEGIN
    EXECUTE ' delete  from ' || tempEidTableName ||  ' t1  where 
    oid > (select min(oid) from ' || tempEidTableName || ' t2 
        where t1.encounter_map_id = t2.encounter_map_id
        and t1.encounter_map_id_source = t2.encounter_map_id_source
        and t1.encounter_id = t2.encounter_id
        and t1.encounter_id_source = t2.encounter_id_source) ';
    LOCK TABLE  encounter_mapping IN EXCLUSIVE MODE NOWAIT;
    select max(encounter_num) into STRICT  maxEncounterNum from encounter_mapping ; 
    if coalesce(maxEncounterNum::text, '') = '' then 
        maxEncounterNum := 0;
    end if;
    open distinctEidCur for EXECUTE 'SELECT distinct encounter_id,encounter_id_source from ' || tempEidTableName ||' ' ;
    loop
        FETCH distinctEidCur INTO disEncounterId, disEncounterIdSource;
        IF NOT FOUND THEN EXIT; END IF; -- apply on distinctEidCur
            -- dbms_output.put_line(disEncounterId);
            if  disEncounterIdSource = 'HIVE'  THEN 
                begin
                    --check if hive number exist, if so assign that number to reset of map_id's within that pid
                    select encounter_num into existingEncounterNum from encounter_mapping where encounter_num = CAST(disEncounterId AS numeric) and encounter_ide_source = 'HIVE';
                    EXCEPTION  when NO_DATA_FOUND THEN
                        existingEncounterNum := null;
                end;
                if (existingEncounterNum IS NOT NULL AND existingEncounterNum::text <> '') then 
                    EXECUTE ' update ' || tempEidTableName ||' set encounter_num = CAST(encounter_id AS numeric), process_status_flag = ''P''
                    where encounter_id = $1 and not exists (select 1 from encounter_mapping em where em.encounter_ide = encounter_map_id
                        and em.encounter_ide_source = encounter_map_id_source)' using disEncounterId;
                else 
                    -- generate new patient_num i.e. take max(_num) + 1 
                    if maxEncounterNum < CAST(disEncounterId AS numeric) then 
                        maxEncounterNum := disEncounterId;
                    end if ;
                    EXECUTE ' update ' || tempEidTableName ||' set encounter_num = CAST(encounter_id AS numeric), process_status_flag = ''P'' where 
                    encounter_id =  $1 and encounter_id_source = ''HIVE'' and not exists (select 1 from encounter_mapping em where em.encounter_ide = encounter_map_id
                        and em.encounter_ide_source = encounter_map_id_source)' using disEncounterId;
                end if;    
                -- test if record fectched
                -- dbms_output.put_line(' HIVE ');
            else 
                begin
                    select encounter_num into STRICT  existingEncounterNum from encounter_mapping where encounter_ide = disEncounterId and 
                    encounter_ide_source = disEncounterIdSource ; 
                    -- test if record fetched. 
                    EXCEPTION
    WHEN NO_DATA_FOUND THEN
        existingEncounterNum := null;
                end;
                if existingEncounterNum is not  null then 
                    EXECUTE ' update ' || tempEidTableName ||' set encounter_num = CAST($1 AS numeric) , process_status_flag = ''P''
                    where encounter_id = $2 and not exists (select 1 from encounter_mapping em where em.encounter_ide = encounter_map_id
                        and em.encounter_ide_source = encounter_map_id_source)' using existingEncounterNum, disEncounterId;
                else 
                    maxEncounterNum := maxEncounterNum + 1 ;
                    --TODO : add update colunn
                    EXECUTE ' insert into ' || tempEidTableName ||' (encounter_map_id,encounter_map_id_source,encounter_id,encounter_id_source,encounter_num,process_status_flag
                        ,encounter_map_id_status,update_date,download_date,import_date,sourcesystem_cd,project_id) 
                    values($1,''HIVE'',$2,''HIVE'',$3,''P'',''A'',Now(),Now(),Now(),''edu.harvard.i2b2.crc'',''HIVE'')' using maxEncounterNum,maxEncounterNum,maxEncounterNum; 
                    EXECUTE ' update ' || tempEidTableName ||' set encounter_num =  $1 , process_status_flag = ''P'' 
                    where encounter_id = $2 and  not exists (select 1 from 
                        encounter_mapping em where em.encounter_ide = encounter_map_id
                        and em.encounter_ide_source = encounter_map_id_source)' using maxEncounterNum, disEncounterId;
                end if ;
                -- dbms_output.put_line(' NOT HIVE ');
            end if; 
    END LOOP;
    close distinctEidCur ;
    -- do the mapping update if the update date is old

EXECUTE 'UPDATE encounter_mapping
SET 
encounter_num = CAST(temp.encounter_id AS numeric)
,encounter_ide_status = temp.encounter_map_id_status
,update_date = temp.update_date
,download_date  = temp.download_date
,import_date = Now()
,sourcesystem_cd  = temp.sourcesystem_cd
,upload_id = ' || upload_id ||'
FROM '|| tempEidTableName || '  temp
WHERE 
temp.encounter_map_id = encounter_mapping.encounter_ide 
and temp.encounter_map_id_source = encounter_mapping.encounter_ide_source
and temp.encounter_id_source = ''HIVE''
and coalesce(temp.process_status_flag::text, '''') = ''''  
and coalesce(encounter_mapping.update_date,to_date(''01-JAN-1900'',''DD-MON-YYYY'')) <= coalesce(temp.update_date,to_date(''01-JAN-1900'',''DD-MON-YYYY''))
';

    -- insert new mapping records i.e flagged P
    EXECUTE ' insert into encounter_mapping (encounter_ide,encounter_ide_source,encounter_ide_status,encounter_num,patient_ide,patient_ide_source,update_date,download_date,import_date,sourcesystem_cd,upload_id,project_id) 
    SELECT encounter_map_id,encounter_map_id_source,encounter_map_id_status,encounter_num,patient_map_id,patient_map_id_source,update_date,download_date,Now(),sourcesystem_cd,' || upload_id || ' , project_id
    FROM ' || tempEidTableName || '  
    WHERE process_status_flag = ''P'' ' ; 
    EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;
    end;
    $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION insert_eid_map_fromtemp(text, bigint)
  OWNER TO i2b2demodata;

-- Function: insert_encountervisit_fromtemp(text, bigint)

-- DROP FUNCTION insert_encountervisit_fromtemp(text, bigint);

CREATE OR REPLACE FUNCTION insert_encountervisit_fromtemp(
    IN temptablename text,
    IN upload_id bigint,
    OUT errormsg text)
  RETURNS text AS
$BODY$
DECLARE

maxEncounterNum bigint; 

BEGIN 
    --Delete duplicate rows with same encounter and patient combination
    EXECUTE 'DELETE FROM ' || tempTableName || ' t1 WHERE oid > 
    (SELECT  min(oid) FROM ' || tempTableName || ' t2
        WHERE t1.encounter_id = t2.encounter_id 
        AND t1.encounter_id_source = t2.encounter_id_source
        AND coalesce(t1.patient_id,'''') = coalesce(t2.patient_id,'''')
        AND coalesce(t1.patient_id_source,'''') = coalesce(t2.patient_id_source,''''))';
    LOCK TABLE  encounter_mapping IN EXCLUSIVE MODE NOWAIT;
    -- select max(encounter_num) into maxEncounterNum from encounter_mapping ;
    --Create new patient(patient_mapping) if temp table patient_ide does not exists 
    -- in patient_mapping table.
    EXECUTE 'INSERT INTO encounter_mapping (
        encounter_ide
        , encounter_ide_source
        , encounter_num
        , patient_ide
        , patient_ide_source
        , encounter_ide_status
        , upload_id
        , project_id
    )
    (SELECT 
        distinctTemp.encounter_id
        , distinctTemp.encounter_id_source
        , CAST(distinctTemp.encounter_id AS numeric)
        , distinctTemp.patient_id
        , distinctTemp.patient_id_source
        , ''A''
        ,  '|| upload_id ||'
        , distinctTemp.project_id
        FROM 
        (SELECT 
            distinct encounter_id
            , encounter_id_source
            , patient_id
            , patient_id_source 
            , project_id
            FROM ' || tempTableName || '  temp
            WHERE 
            NOT EXISTS (SELECT encounter_ide 
                FROM encounter_mapping em 
                WHERE 
                em.encounter_ide = temp.encounter_id 
                AND em.encounter_ide_source = temp.encounter_id_source
            )
            AND encounter_id_source = ''HIVE'' 
    )   distinctTemp
) ' ;
    -- update patient_num for temp table
    EXECUTE ' UPDATE ' ||  tempTableName
    || ' SET encounter_num = (SELECT em.encounter_num
        FROM encounter_mapping em
        WHERE em.encounter_ide = '|| tempTableName ||'.encounter_id
        and em.encounter_ide_source = '|| tempTableName ||'.encounter_id_source 
        and coalesce(em.patient_ide_source,'''') = coalesce('|| tempTableName ||'.patient_id_source,'''')
        and coalesce(em.patient_ide,'''')= coalesce('|| tempTableName ||'.patient_id,'''')
    )
    WHERE EXISTS (SELECT em.encounter_num 
        FROM encounter_mapping em
        WHERE em.encounter_ide = '|| tempTableName ||'.encounter_id
        and em.encounter_ide_source = '||tempTableName||'.encounter_id_source
        and coalesce(em.patient_ide_source,'''') = coalesce('|| tempTableName ||'.patient_id_source,'''')
        and coalesce(em.patient_ide,'''')= coalesce('|| tempTableName ||'.patient_id,''''))';      

    EXECUTE ' UPDATE visit_dimension  SET  
    start_date =temp.start_date
    ,end_date=temp.end_date
    ,inout_cd=temp.inout_cd
    ,location_cd=temp.location_cd
    ,visit_blob=temp.visit_blob
    ,update_date=temp.update_date
    ,download_date=temp.download_date
    ,import_date=Now()
    ,sourcesystem_cd=temp.sourcesystem_cd
    , upload_id=' || UPLOAD_ID  || '
    FROM ' || tempTableName || '  temp       
    WHERE
    temp.encounter_num = visit_dimension.encounter_num 
    AND temp.update_date >= visit_dimension.update_date 
    AND exists (SELECT 1 
        FROM ' || tempTableName || ' temp 
        WHERE temp.encounter_num = visit_dimension.encounter_num 
        AND temp.update_date >= visit_dimension.update_date
    ) ';

    EXECUTE 'INSERT INTO visit_dimension  (encounter_num,patient_num,start_date,end_date,inout_cd,location_cd,visit_blob,update_date,download_date,import_date,sourcesystem_cd, upload_id)
    SELECT temp.encounter_num
    , pm.patient_num,
    temp.start_date,temp.end_date,temp.inout_cd,temp.location_cd,temp.visit_blob,
    temp.update_date,
    temp.download_date,
    Now(), 
    temp.sourcesystem_cd,
    '|| upload_id ||'
    FROM 
    ' || tempTableName || '  temp , patient_mapping pm 
    WHERE 
    (temp.encounter_num IS NOT NULL AND temp.encounter_num::text <> '''') and 
    NOT EXISTS (SELECT encounter_num 
        FROM visit_dimension vd 
        WHERE 
        vd.encounter_num = temp.encounter_num) 
    AND pm.patient_ide = temp.patient_id 
    AND pm.patient_ide_source = temp.patient_id_source
    ';
    EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;      
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION insert_encountervisit_fromtemp(text, bigint)
  OWNER TO i2b2demodata;

-- Function: insert_modifier_fromtemp(text, bigint)

-- DROP FUNCTION insert_modifier_fromtemp(text, bigint);

CREATE OR REPLACE FUNCTION insert_modifier_fromtemp(
    IN tempmodifiertablename text,
    IN upload_id bigint,
    OUT errormsg text)
  RETURNS text AS
$BODY$
BEGIN 
    --Delete duplicate rows 
    EXECUTE 'DELETE FROM ' || tempModifierTableName || ' t1 WHERE oid > 
    (SELECT  min(oid) FROM ' || tempModifierTableName || ' t2
        WHERE t1.modifier_cd = t2.modifier_cd 
        AND t1.modifier_path = t2.modifier_path
    )';
    EXECUTE ' UPDATE modifier_dimension  SET  
        modifier_cd=temp.modifier_cd
        ,name_char=temp.name_char
        ,modifier_blob=temp.modifier_blob
        ,update_date=temp.update_date
        ,download_date=temp.download_date
        ,import_date=Now()
        ,sourcesystem_cd=temp.SOURCESYSTEM_CD
        ,upload_id=' || UPLOAD_ID  || ' 
        FROM ' || tempModifierTableName || '  temp
        WHERE 
        temp.modifier_path = modifier_dimension.modifier_path 
        AND temp.update_date >= modifier_dimension.update_date
        AND EXISTS (SELECT 1 
            FROM ' || tempModifierTableName || ' temp  
            WHERE temp.modifier_path = modifier_dimension.modifier_path 
            AND temp.update_date >= modifier_dimension.update_date)
        ';
        --Create new modifier if temp table modifier_path does not exists 
        -- in modifier dimension table.
        EXECUTE 'INSERT INTO modifier_dimension  (
            modifier_cd
            ,modifier_path
            ,name_char
            ,modifier_blob
            ,update_date
            ,download_date
            ,import_date
            ,sourcesystem_cd
            ,upload_id
        )
        SELECT  
        modifier_cd
        ,modifier_path
        ,name_char
        ,modifier_blob
        ,update_date
        ,download_date
        ,Now()
        ,sourcesystem_cd
        ,' || upload_id || '  
        FROM
        ' || tempModifierTableName || '  temp
        WHERE NOT EXISTs (SELECT modifier_cd 
            FROM modifier_dimension cd
            WHERE cd.modifier_path = temp.modifier_path
        )
        ';
        EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;      
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION insert_modifier_fromtemp(text, bigint)
  OWNER TO i2b2demodata;

-- Function: insert_patient_fromtemp(text, bigint)

-- DROP FUNCTION insert_patient_fromtemp(text, bigint);

CREATE OR REPLACE FUNCTION insert_patient_fromtemp(
    IN temptablename text,
    IN upload_id bigint,
    OUT errormsg text)
  RETURNS text AS
$BODY$
DECLARE

maxPatientNum bigint; 

BEGIN 
    LOCK TABLE  patient_mapping IN EXCLUSIVE MODE NOWAIT;
    --select max(patient_num) into maxPatientNum from patient_mapping ;
    --Create new patient(patient_mapping) if temp table patient_ide does not exists 
    -- in patient_mapping table.
    EXECUTE ' INSERT INTO patient_mapping (patient_ide,patient_ide_source,patient_num,patient_ide_status, upload_id)
    (SELECT distinctTemp.patient_id, distinctTemp.patient_id_source, CAST(distinctTemp.patient_id AS numeric), ''A'',   '|| upload_id ||'
        FROM 
        (SELECT distinct patient_id, patient_id_source from ' || tempTableName || '  temp
            where  not exists (SELECT patient_ide from patient_mapping pm where pm.patient_ide = temp.patient_id and pm.patient_ide_source = temp.patient_id_source)
            and patient_id_source = ''HIVE'' )   distinctTemp) ';

    -- update patient_num for temp table
    EXECUTE ' UPDATE ' ||  tempTableName
    || ' SET patient_num = (SELECT pm.patient_num
        FROM patient_mapping pm
        WHERE pm.patient_ide = '|| tempTableName ||'.patient_id
        AND pm.patient_ide_source = '|| tempTableName ||'.patient_id_source
    )
    WHERE EXISTS (SELECT pm.patient_num 
        FROM patient_mapping pm
        WHERE pm.patient_ide = '|| tempTableName ||'.patient_id
        AND pm.patient_ide_source = '||tempTableName||'.patient_id_source)';       

    EXECUTE ' UPDATE patient_dimension  SET  
    vital_status_cd = temp.vital_status_cd
    , birth_date = temp.birth_date
    , death_date = temp.death_date
    , sex_cd = temp.sex_cd
    , age_in_years_num = temp.age_in_years_num
    , language_cd = temp.language_cd
    , race_cd = temp.race_cd
    , marital_status_cd = temp.marital_status_cd
    , religion_cd = temp.religion_cd
    , zip_cd = temp.zip_cd
    , statecityzip_path = temp.statecityzip_path
    , patient_blob = temp.patient_blob
    , update_date = temp.update_date
    , download_date = temp.download_date
    , import_date = Now()
    , sourcesystem_cd = temp.sourcesystem_cd 
    , upload_id =  ' || UPLOAD_ID  || '
    FROM  ' || tempTableName || '  temp
    WHERE 
    temp.patient_num = patient_dimension.patient_num 
    AND temp.update_date >= patient_dimension.update_date
    AND EXISTS (select 1 
        FROM ' || tempTableName || ' temp  
        WHERE 
        temp.patient_num = patient_dimension.patient_num 
        AND temp.update_date >= patient_dimension.update_date
    )    ';

    --Create new patient(patient_dimension) for above inserted patient's.
    --If patient_dimension table's patient num does match temp table,
    --then new patient_dimension information is inserted.
    EXECUTE 'INSERT INTO patient_dimension  (patient_num,vital_status_cd, birth_date, death_date,
        sex_cd, age_in_years_num,language_cd,race_cd,marital_status_cd, religion_cd,
        zip_cd,statecityzip_path,patient_blob,update_date,download_date,import_date,sourcesystem_cd,
        upload_id)
    SELECT temp.patient_num,
    temp.vital_status_cd, temp.birth_date, temp.death_date,
    temp.sex_cd, temp.age_in_years_num,temp.language_cd,temp.race_cd,temp.marital_status_cd, temp.religion_cd,
    temp.zip_cd,temp.statecityzip_path,temp.patient_blob,
    temp.update_date,
    temp.download_date,
    Now(),
    temp.sourcesystem_cd,
    '|| upload_id ||'
    FROM 
    ' || tempTableName || '  temp 
    WHERE 
    NOT EXISTS (SELECT patient_num 
        FROM patient_dimension pd 
        WHERE pd.patient_num = temp.patient_num) 
    AND 
    (patient_num IS NOT NULL AND patient_num::text <> '''')
    ';
    EXCEPTION WHEN OTHERS THEN
        RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION insert_patient_fromtemp(text, bigint)
  OWNER TO i2b2demodata;

-- Function: insert_patient_map_fromtemp(text, bigint)

-- DROP FUNCTION insert_patient_map_fromtemp(text, bigint);

CREATE OR REPLACE FUNCTION insert_patient_map_fromtemp(
    IN temppatienttablename text,
    IN upload_id bigint,
    OUT errormsg text)
  RETURNS text AS
$BODY$
BEGIN 
        --Create new patient mapping entry for HIVE patient's if they are not already mapped in mapping table
        EXECUTE 'insert into patient_mapping (
                PERFORM distinct temp.patient_id, temp.patient_id_source,''A'',temp.patient_id ,' || upload_id || '
                from ' || tempPatientTableName ||'  temp 
                where temp.patient_id_source = ''HIVE'' and 
                not exists (select patient_ide from patient_mapping pm where pm.patient_num = temp.patient_id and pm.patient_ide_source = temp.patient_id_source) 
                )'; 
    --Create new visit for above inserted encounter's
        --If Visit table's encounter and patient num does match temp table,
        --then new visit information is created.
        EXECUTE 'MERGE  INTO patient_dimension pd
                   USING ( select case when (ptemp.patient_id_source=''HIVE'') then  cast(ptemp.patient_id as int)
                                       else pmap.patient_num end patient_num,
                                  ptemp.VITAL_STATUS_CD, 
                                  ptemp.BIRTH_DATE,
                                  ptemp.DEATH_DATE, 
                                  ptemp.SEX_CD ,
                                  ptemp.AGE_IN_YEARS_NUM,
                                  ptemp.LANGUAGE_CD,
                                  ptemp.RACE_CD,
                                  ptemp.MARITAL_STATUS_CD,
                                  ptemp.RELIGION_CD,
                                  ptemp.ZIP_CD,
                                                                  ptemp.STATECITYZIP_PATH , 
                                                                  ptemp.PATIENT_BLOB, 
                                                                  ptemp.UPDATE_DATE, 
                                                                  ptemp.DOWNLOAD_DATE, 
                                                                  ptemp.IMPORT_DATE, 
                                                                  ptemp.SOURCESYSTEM_CD
                   from ' || tempPatientTableName || '  ptemp , patient_mapping pmap
                   where   ptemp.patient_id = pmap.patient_ide(+)
                   and ptemp.patient_id_source = pmap.patient_ide_source(+)
           ) temp
                   on (
                                pd.patient_num = temp.patient_num
                    )    
                        when matched then 
                                update  set 
                                        pd.VITAL_STATUS_CD= temp.VITAL_STATUS_CD,
                    pd.BIRTH_DATE= temp.BIRTH_DATE,
                    pd.DEATH_DATE= temp.DEATH_DATE,
                    pd.SEX_CD= temp.SEX_CD,
                    pd.AGE_IN_YEARS_NUM=temp.AGE_IN_YEARS_NUM,
                    pd.LANGUAGE_CD=temp.LANGUAGE_CD,
                    pd.RACE_CD=temp.RACE_CD,
                    pd.MARITAL_STATUS_CD=temp.MARITAL_STATUS_CD,
                    pd.RELIGION_CD=temp.RELIGION_CD,
                    pd.ZIP_CD=temp.ZIP_CD,
                                        pd.STATECITYZIP_PATH =temp.STATECITYZIP_PATH,
                                        pd.PATIENT_BLOB=temp.PATIENT_BLOB,
                                        pd.UPDATE_DATE=temp.UPDATE_DATE,
                                        pd.DOWNLOAD_DATE=temp.DOWNLOAD_DATE,
                                        pd.SOURCESYSTEM_CD=temp.SOURCESYSTEM_CD,
                                        pd.UPLOAD_ID = '||upload_id||'
                    where temp.update_date > pd.update_date
                         when not matched then 
                                insert (
                                        PATIENT_NUM,
                                        VITAL_STATUS_CD,
                    BIRTH_DATE,
                    DEATH_DATE,
                    SEX_CD,
                    AGE_IN_YEARS_NUM,
                    LANGUAGE_CD,
                    RACE_CD,
                    MARITAL_STATUS_CD,
                    RELIGION_CD,
                    ZIP_CD,
                                        STATECITYZIP_PATH,
                                        PATIENT_BLOB,
                                        UPDATE_DATE,
                                        DOWNLOAD_DATE,
                                        SOURCESYSTEM_CD,
                                        import_date,
                        upload_id
                                        ) 
                                values (
                                        temp.PATIENT_NUM,
                                        temp.VITAL_STATUS_CD,
                    temp.BIRTH_DATE,
                    temp.DEATH_DATE,
                    temp.SEX_CD,
                    temp.AGE_IN_YEARS_NUM,
                    temp.LANGUAGE_CD,
                    temp.RACE_CD,
                    temp.MARITAL_STATUS_CD,
                    temp.RELIGION_CD,
                    temp.ZIP_CD,
                                        temp.STATECITYZIP_PATH,
                                        temp.PATIENT_BLOB,
                                        temp.UPDATE_DATE,
                                        temp.DOWNLOAD_DATE,
                                        temp.SOURCESYSTEM_CD,
                                        LOCALTIMESTAMP,
                                '||upload_id||'
                                )';
EXCEPTION
        WHEN OTHERS THEN
                RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;      
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION insert_patient_map_fromtemp(text, bigint)
  OWNER TO i2b2demodata;

-- Function: insert_pid_map_fromtemp(text, bigint)

-- DROP FUNCTION insert_pid_map_fromtemp(text, bigint);

CREATE OR REPLACE FUNCTION insert_pid_map_fromtemp(
    IN temppidtablename text,
    IN upload_id bigint,
    OUT errormsg text)
  RETURNS text AS
$BODY$
DECLARE
existingPatientNum varchar(32);
maxPatientNum bigint;
distinctPidCur REFCURSOR;
disPatientId varchar(100); 
disPatientIdSource varchar(100);
BEGIN
	--delete the doublons
	EXECUTE ' delete  from ' || tempPidTableName ||  ' t1  where 
	oid > (select min(oid) from ' || tempPidTableName || ' t2 
		where t1.patient_map_id = t2.patient_map_id
		and t1.patient_map_id_source = t2.patient_map_id_source) ';
	LOCK TABLE  patient_mapping IN EXCLUSIVE MODE NOWAIT;
	select max(patient_num) into STRICT  maxPatientNum from patient_mapping ; 
	-- set max patient num to zero of the value is null
	if coalesce(maxPatientNum::text, '') = '' then 
		maxPatientNum := 0;
	end if;
	open distinctPidCur for EXECUTE 'SELECT distinct patient_id,patient_id_source from ' || tempPidTableName || '' ;
	loop
		FETCH distinctPidCur INTO disPatientId, disPatientIdSource;
		IF NOT FOUND THEN EXIT; 
	END IF; -- apply on distinctPidCur
	-- dbms_output.put_line(disPatientId);
	if  disPatientIdSource = 'HIVE'  THEN 
		begin
			--check if hive number exist, if so assign that number to reset of map_id's within that pid
			select patient_num into existingPatientNum from patient_mapping where patient_num = CAST(disPatientId AS numeric) and patient_ide_source = 'HIVE';
			EXCEPTION  when NO_DATA_FOUND THEN
				existingPatientNum := null;
		end;
		if (existingPatientNum IS NOT NULL AND existingPatientNum::text <> '') then 
			EXECUTE ' update ' || tempPidTableName ||' set patient_num = CAST(patient_id AS numeric), process_status_flag = ''P''
			where patient_id = $1 and not exists (select 1 from patient_mapping pm where pm.patient_ide = patient_map_id
				and pm.patient_ide_source = patient_map_id_source)' using disPatientId;
		else 
			-- generate new patient_num i.e. take max(patient_num) + 1 
			if maxPatientNum < CAST(disPatientId AS numeric) then 
				maxPatientNum := disPatientId;
			end if ;
			EXECUTE ' update ' || tempPidTableName ||' set patient_num = CAST(patient_id AS numeric), process_status_flag = ''P'' where 
			patient_id = $1 and patient_id_source = ''HIVE'' and not exists (select 1 from patient_mapping pm where pm.patient_ide = patient_map_id
				and pm.patient_ide_source = patient_map_id_source)' using disPatientId;
		end if;    
		-- test if record fectched
		-- dbms_output.put_line(' HIVE ');
	else 
		begin
			select patient_num into STRICT  existingPatientNum from patient_mapping where patient_ide = disPatientId and 
			patient_ide_source = disPatientIdSource ; 
			-- test if record fetched. 
			EXCEPTION
	WHEN NO_DATA_FOUND THEN
		existingPatientNum := null;
		end;
		if (existingPatientNum IS NOT NULL AND existingPatientNum::text <> '') then 
			EXECUTE ' update ' || tempPidTableName ||' set patient_num = CAST($1 AS numeric) , process_status_flag = ''P''
			where patient_id = $2 and not exists (select 1 from patient_mapping pm where pm.patient_ide = patient_map_id
				and pm.patient_ide_source = patient_map_id_source)' using  existingPatientNum,disPatientId;
		else 
			maxPatientNum := maxPatientNum + 1 ; 
			EXECUTE 'insert into ' || tempPidTableName ||' (
				patient_map_id
				,patient_map_id_source
				,patient_id
				,patient_id_source
				,patient_num
				,process_status_flag
				,patient_map_id_status
				,update_date
				,download_date
				,import_date
				,sourcesystem_cd
				,project_id) 
			values(
				$1
				,''HIVE''
				,$2
				,''HIVE''
				,$3
				,''P''
				,''A''
				,Now()
				,Now()
				,Now()
				,''edu.harvard.i2b2.crc''
			,''HIVE''
			)' using maxPatientNum,maxPatientNum,maxPatientNum; 
			EXECUTE 'update ' || tempPidTableName ||' set patient_num =  $1 , process_status_flag = ''P'' 
			where patient_id = $2 and  not exists (select 1 from 
				patient_mapping pm where pm.patient_ide = patient_map_id
				and pm.patient_ide_source = patient_map_id_source)' using maxPatientNum, disPatientId  ;
		end if ;
		-- dbms_output.put_line(' NOT HIVE ');
	end if; 
	END LOOP;
	close distinctPidCur ;
	-- do the mapping update if the update date is old
EXECUTE ' UPDATE patient_mapping
SET 
patient_num = CAST(temp.patient_id AS numeric)
,patient_ide_status = temp.patient_map_id_status
,update_date = temp.update_date
,download_date  = temp.download_date
,import_date = Now()
,sourcesystem_cd  = temp.sourcesystem_cd
,upload_id = ' || upload_id ||'
FROM '|| tempPidTableName || '  temp
WHERE 
temp.patient_map_id = patient_mapping.patient_ide 
and temp.patient_map_id_source = patient_mapping.patient_ide_source
and temp.patient_id_source = ''HIVE''
and coalesce(temp.process_status_flag::text, '''') = ''''  
and coalesce(patient_mapping.update_date,to_date(''01-JAN-1900'',''DD-MON-YYYY'')) <= coalesce(temp.update_date,to_date(''01-JAN-1900'',''DD-MON-YYYY''))
';
	-- insert new mapping records i.e flagged P
	EXECUTE ' insert into patient_mapping (patient_ide,patient_ide_source,patient_ide_status,patient_num,update_date,download_date,import_date,sourcesystem_cd,upload_id,project_id)
	SELECT patient_map_id,patient_map_id_source,patient_map_id_status,patient_num,update_date,download_date,Now(),sourcesystem_cd,' || upload_id ||', project_id from '|| tempPidTableName || ' 
	where process_status_flag = ''P'' ' ; 
	EXCEPTION WHEN OTHERS THEN
		RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;
	END;
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION insert_pid_map_fromtemp(text, bigint)
  OWNER TO i2b2demodata;
  


-- Function: insert_provider_fromtemp(text, bigint)

-- DROP FUNCTION insert_provider_fromtemp(text, bigint);

CREATE OR REPLACE FUNCTION insert_provider_fromtemp(
    IN tempprovidertablename text,
    IN upload_id bigint,
    OUT errormsg text)
  RETURNS text AS
$BODY$
BEGIN 
    --Delete duplicate rows with same encounter and patient combination
    EXECUTE 'DELETE FROM ' || tempProviderTableName || ' t1 WHERE oid > 
    (SELECT  min(oid) FROM ' || tempProviderTableName || ' t2
        WHERE t1.provider_id = t2.provider_id 
        AND t1.provider_path = t2.provider_path
    )';
    EXECUTE ' UPDATE provider_dimension  SET  
        provider_id =temp.provider_id
        , name_char = temp.name_char
        , provider_blob = temp.provider_blob
        , update_date=temp.update_date
        , download_date=temp.download_date
        , import_date=Now()
        , sourcesystem_cd=temp.sourcesystem_cd
        , upload_id = ' || upload_id || '
        FROM ' || tempProviderTableName || '  temp 
        WHERE 
        temp.provider_path = provider_dimension.provider_path and temp.update_date >= provider_dimension.update_date 
    AND EXISTS (select 1 from ' || tempProviderTableName || ' temp  where temp.provider_path = provider_dimension.provider_path 
        and temp.update_date >= provider_dimension.update_date) ';

    --Create new patient(patient_mapping) if temp table patient_ide does not exists 
    -- in patient_mapping table.
    EXECUTE 'insert into provider_dimension  (provider_id,provider_path,name_char,provider_blob,update_date,download_date,import_date,sourcesystem_cd,upload_id)
    SELECT  provider_id,provider_path, 
    name_char,provider_blob,
    update_date,download_date,
    Now(),sourcesystem_cd, ' || upload_id || '
    FROM ' || tempProviderTableName || '  temp
    WHERE NOT EXISTS (SELECT provider_id 
        FROM provider_dimension pd 
        WHERE pd.provider_path = temp.provider_path 
    )';
    EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;      
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION insert_provider_fromtemp(text, bigint)
  OWNER TO i2b2demodata;

-- Function: remove_temp_table(character varying)

-- DROP FUNCTION remove_temp_table(character varying);

CREATE OR REPLACE FUNCTION remove_temp_table(
    IN temptablename character varying,
    OUT errormsg text)
  RETURNS text AS
$BODY$

DECLARE

BEGIN
    EXECUTE 'DROP TABLE ' || tempTableName|| ' CASCADE ';

EXCEPTION 
WHEN OTHERS THEN
    RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;      
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION remove_temp_table(character varying)
  OWNER TO i2b2demodata;

-- Function: sync_clear_concept_table(text, text, bigint)

-- DROP FUNCTION sync_clear_concept_table(text, text, bigint);

CREATE OR REPLACE FUNCTION sync_clear_concept_table(
    IN tempconcepttablename text,
    IN backupconcepttablename text,
    IN uploadid bigint,
    OUT errormsg text)
  RETURNS text AS
$BODY$
DECLARE
 
interConceptTableName  varchar(400);

BEGIN 
        interConceptTableName := backupConceptTableName || '_inter';
                --Delete duplicate rows with same encounter and patient combination
        EXECUTE 'DELETE FROM ' || tempConceptTableName || ' t1 WHERE oid > 
                                           (SELECT  min(oid) FROM ' || tempConceptTableName || ' t2
                                             WHERE t1.concept_cd = t2.concept_cd 
                                            AND t1.concept_path = t2.concept_path
                                            )';
    EXECUTE 'create table ' ||  interConceptTableName || ' (
    CONCEPT_CD          varchar(50) NOT NULL,
        CONCEPT_PATH            varchar(700) NOT NULL,
        NAME_CHAR               varchar(2000) NULL,
        CONCEPT_BLOB        text NULL,
        UPDATE_DATE         timestamp NULL,
        DOWNLOAD_DATE       timestamp NULL,
        IMPORT_DATE         timestamp NULL,
        SOURCESYSTEM_CD     varchar(50) NULL,
        UPLOAD_ID               numeric(38,0) NULL,
    CONSTRAINT '|| interConceptTableName ||'_pk  PRIMARY KEY(CONCEPT_PATH)
         )';
    --Create new patient(patient_mapping) if temp table patient_ide does not exists 
        -- in patient_mapping table.
        EXECUTE 'insert into '|| interConceptTableName ||'  (concept_cd,concept_path,name_char,concept_blob,update_date,download_date,import_date,sourcesystem_cd,upload_id)
                            PERFORM  concept_cd, substring(concept_path from 1 for 700),
                        name_char,concept_blob,
                        update_date,download_date,
                        LOCALTIMESTAMP,sourcesystem_cd,
                         ' || uploadId || '  from ' || tempConceptTableName || '  temp ';
        --backup the concept_dimension table before creating a new one
        EXECUTE 'alter table concept_dimension rename to ' || backupConceptTableName  ||'' ;
        -- add index on upload_id 
    EXECUTE 'CREATE INDEX ' || interConceptTableName || '_uid_idx ON ' || interConceptTableName || '(UPLOAD_ID)';
    -- add index on upload_id 
    EXECUTE 'CREATE INDEX ' || interConceptTableName || '_cd_idx ON ' || interConceptTableName || '(concept_cd)';
    --backup the concept_dimension table before creating a new one
        EXECUTE 'alter table ' || interConceptTableName  || ' rename to concept_dimension' ;
EXCEPTION
        WHEN OTHERS THEN
                RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;      
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sync_clear_concept_table(text, text, bigint)
  OWNER TO i2b2demodata;

-- Function: sync_clear_modifier_table(text, text, bigint)

-- DROP FUNCTION sync_clear_modifier_table(text, text, bigint);

CREATE OR REPLACE FUNCTION sync_clear_modifier_table(
    IN tempmodifiertablename text,
    IN backupmodifiertablename text,
    IN uploadid bigint,
    OUT errormsg text)
  RETURNS text AS
$BODY$
DECLARE
 
interModifierTableName  varchar(400);

BEGIN 
        interModifierTableName := backupModifierTableName || '_inter';
        --Delete duplicate rows with same modifier_path and modifier cd
        EXECUTE 'DELETE FROM ' || tempModifierTableName || ' t1 WHERE oid > 
                                           (SELECT  min(oid) FROM ' || tempModifierTableName || ' t2
                                             WHERE t1.modifier_cd = t2.modifier_cd 
                                            AND t1.modifier_path = t2.modifier_path
                                            )';
    EXECUTE 'create table ' ||  interModifierTableName || ' (
        MODIFIER_CD          varchar(50) NOT NULL,
        MODIFIER_PATH           varchar(700) NOT NULL,
        NAME_CHAR               varchar(2000) NULL,
        MODIFIER_BLOB        text NULL,
        UPDATE_DATE         timestamp NULL,
        DOWNLOAD_DATE       timestamp NULL,
        IMPORT_DATE         timestamp NULL,
        SOURCESYSTEM_CD     varchar(50) NULL,
        UPLOAD_ID               numeric(38,0) NULL,
    CONSTRAINT '|| interModifierTableName ||'_pk  PRIMARY KEY(MODIFIER_PATH)
         )';
    --Create new patient(patient_mapping) if temp table patient_ide does not exists 
        -- in patient_mapping table.
        EXECUTE 'insert into '|| interModifierTableName ||'  (modifier_cd,modifier_path,name_char,modifier_blob,update_date,download_date,import_date,sourcesystem_cd,upload_id)
                            PERFORM  modifier_cd, substring(modifier_path from 1 for 700),
                        name_char,modifier_blob,
                        update_date,download_date,
                        LOCALTIMESTAMP,sourcesystem_cd,
                         ' || uploadId || '  from ' || tempModifierTableName || '  temp ';
        --backup the modifier_dimension table before creating a new one
        EXECUTE 'alter table modifier_dimension rename to ' || backupModifierTableName  ||'' ;
        -- add index on upload_id 
    EXECUTE 'CREATE INDEX ' || interModifierTableName || '_uid_idx ON ' || interModifierTableName || '(UPLOAD_ID)';
    -- add index on upload_id 
    EXECUTE 'CREATE INDEX ' || interModifierTableName || '_cd_idx ON ' || interModifierTableName || '(modifier_cd)';
       --backup the modifier_dimension table before creating a new one
        EXECUTE 'alter table ' || interModifierTableName  || ' rename to modifier_dimension' ;
EXCEPTION
        WHEN OTHERS THEN
                RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;      
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sync_clear_modifier_table(text, text, bigint)
  OWNER TO i2b2demodata;

-- Function: sync_clear_provider_table(text, text, bigint)

-- DROP FUNCTION sync_clear_provider_table(text, text, bigint);

CREATE OR REPLACE FUNCTION sync_clear_provider_table(
    IN tempprovidertablename text,
    IN backupprovidertablename text,
    IN uploadid bigint,
    OUT errormsg text)
  RETURNS text AS
$BODY$
DECLARE
 
interProviderTableName  varchar(400);

BEGIN 
        interProviderTableName := backupProviderTableName || '_inter';
                --Delete duplicate rows with same encounter and patient combination
        EXECUTE 'DELETE FROM ' || tempProviderTableName || ' t1 WHERE oid > 
                                           (SELECT  min(oid) FROM ' || tempProviderTableName || ' t2
                                             WHERE t1.provider_id = t2.provider_id 
                                            AND t1.provider_path = t2.provider_path
                                            )';
    EXECUTE 'create table ' ||  interProviderTableName || ' (
    PROVIDER_ID         varchar(50) NOT NULL,
        PROVIDER_PATH       varchar(700) NOT NULL,
        NAME_CHAR               varchar(850) NULL,
        PROVIDER_BLOB       text NULL,
        UPDATE_DATE             timestamp NULL,
        DOWNLOAD_DATE       timestamp NULL,
        IMPORT_DATE         timestamp NULL,
        SOURCESYSTEM_CD     varchar(50) NULL,
        UPLOAD_ID               numeric(38,0) NULL ,
    CONSTRAINT  ' || interProviderTableName || '_pk PRIMARY KEY(PROVIDER_PATH,provider_id)
         )';
    --Create new patient(patient_mapping) if temp table patient_ide does not exists 
        -- in patient_mapping table.
        EXECUTE 'insert into ' ||  interProviderTableName || ' (provider_id,provider_path,name_char,provider_blob,update_date,download_date,import_date,sourcesystem_cd,upload_id)
                            PERFORM  provider_id,provider_path, 
                        name_char,provider_blob,
                        update_date,download_date,
                        LOCALTIMESTAMP,sourcesystem_cd, ' || uploadId || '
                             from ' || tempProviderTableName || '  temp ';
        --backup the concept_dimension table before creating a new one
        EXECUTE 'alter table provider_dimension rename to ' || backupProviderTableName  ||'' ;
        -- add index on provider_id, name_char 
    EXECUTE 'CREATE INDEX ' || interProviderTableName || '_id_idx ON ' || interProviderTableName  || '(Provider_Id,name_char)';
    EXECUTE 'CREATE INDEX ' || interProviderTableName || '_uid_idx ON ' || interProviderTableName  || '(UPLOAD_ID)';
        --backup the concept_dimension table before creating a new one
        EXECUTE 'alter table ' || interProviderTableName  || ' rename to provider_dimension' ;
EXCEPTION
        WHEN OTHERS THEN
                RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;      
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sync_clear_provider_table(text, text, bigint)
  OWNER TO i2b2demodata;

-- Function: update_observation_fact(text, bigint, bigint)

-- DROP FUNCTION update_observation_fact(text, bigint, bigint);

CREATE OR REPLACE FUNCTION update_observation_fact(
    IN upload_temptable_name text,
    IN upload_id bigint,
    IN appendflag bigint,
    OUT errormsg text)
  RETURNS text AS
$BODY$
BEGIN
    -- appendFlag = 0 -> remove all and then insert
    -- appendFlag <> 0 -> do update, then insert what have not been updated    

    --Delete duplicate records(encounter_ide,patient_ide,concept_cd,start_date,modifier_cd,provider_id)
    EXECUTE 'DELETE FROM ' || upload_temptable_name ||'  t1 
    WHERE oid > (select min(oid) FROM ' || upload_temptable_name ||' t2 
        WHERE t1.encounter_id = t2.encounter_id  
        AND
        t1.encounter_id_source = t2.encounter_id_source
        AND
        t1.patient_id = t2.patient_id 
        AND 
        t1.patient_id_source = t2.patient_id_source
        AND 
        t1.concept_cd = t2.concept_cd
        AND 
        t1.start_date = t2.start_date
        AND 
        coalesce(t1.modifier_cd,''xyz'') = coalesce(t2.modifier_cd,''xyz'')
        AND 
        t1.instance_num = t2.instance_num
        AND 
        t1.provider_id = t2.provider_id)';
    --Delete records having null in start_date
    EXECUTE 'DELETE FROM ' || upload_temptable_name ||'  t1           
    WHERE coalesce(t1.start_date::text, '''') = '''' 
    ';
    --One time lookup on encounter_ide to get encounter_num 
    EXECUTE 'UPDATE ' ||  upload_temptable_name
    || ' SET encounter_num = (SELECT distinct em.encounter_num
        FROM encounter_mapping em
        WHERE em.encounter_ide = ' || upload_temptable_name||'.encounter_id
        AND em.encounter_ide_source = '|| upload_temptable_name||'.encounter_id_source
    )
    WHERE EXISTS (SELECT distinct em.encounter_num
        FROM encounter_mapping em
        WHERE em.encounter_ide = '|| upload_temptable_name||'.encounter_id
        AND em.encounter_ide_source = '||upload_temptable_name||'.encounter_id_source)';                
    --One time lookup on patient_ide to get patient_num 
    EXECUTE 'UPDATE ' ||  upload_temptable_name
    || ' SET patient_num = (SELECT distinct pm.patient_num
        FROM patient_mapping pm
        WHERE pm.patient_ide = '|| upload_temptable_name||'.patient_id
        AND pm.patient_ide_source = '|| upload_temptable_name||'.patient_id_source
    )
    WHERE EXISTS (SELECT distinct pm.patient_num 
        FROM patient_mapping pm
        WHERE pm.patient_ide = '|| upload_temptable_name||'.patient_id
        AND pm.patient_ide_source = '||upload_temptable_name||'.patient_id_source)';                    
    IF (appendFlag = 0) THEN
        --Archive records which are to be deleted in observation_fact table
        EXECUTE 'INSERT INTO  archive_observation_fact 
        SELECT obsfact.*, ' || upload_id ||'
        FROM observation_fact obsfact
        WHERE obsfact.encounter_num IN 
        (SELECT temp_obsfact.encounter_num
            FROM  ' ||upload_temptable_name ||' temp_obsfact
            GROUP BY temp_obsfact.encounter_num  
        )';
        --Delete above archived row FROM observation_fact
        EXECUTE 'DELETE  
        FROM observation_fact 
        WHERE EXISTS (
            SELECT archive.encounter_num
            FROM archive_observation_fact  archive
            WHERE archive.archive_upload_id = '||upload_id ||'
            AND archive.encounter_num=observation_fact.encounter_num
            AND archive.concept_cd = observation_fact.concept_cd
            AND archive.start_date = observation_fact.start_date
        )';
END IF;
-- if the append is true, then do the update else do insert all
IF (appendFlag <> 0) THEN -- update
    EXECUTE ' 
    UPDATE observation_fact f    
    SET valtype_cd = temp.valtype_cd ,
    tval_char=temp.tval_char, 
    nval_num = temp.nval_num,
    valueflag_cd=temp.valueflag_cd,
    quantity_num=temp.quantity_num,
    confidence_num=temp.confidence_num,
    observation_blob =temp.observation_blob,
    units_cd=temp.units_cd,
    end_date=temp.end_date,
    location_cd =temp.location_cd,
    update_date=temp.update_date ,
    download_date =temp.download_date,
    import_date=temp.import_date,
    sourcesystem_cd =temp.sourcesystem_cd,
    upload_id = temp.upload_id 
    FROM ' || upload_temptable_name ||' temp
    WHERE 
    temp.patient_num is not null 
    and temp.encounter_num is not null 
    and temp.encounter_num = f.encounter_num 
    and temp.patient_num = f.patient_num
    and temp.concept_cd = f.concept_cd
    and temp.start_date = f.start_date
    and temp.provider_id = f.provider_id
    and temp.modifier_cd = f.modifier_cd 
    and temp.instance_num = f.instance_num
    and coalesce(f.update_date,to_date(''01-JAN-1900'',''DD-MON-YYYY'')) <= coalesce(temp.update_date,to_date(''01-JAN-1900'',''DD-MON-YYYY''))';

    EXECUTE  'DELETE FROM ' || upload_temptable_name ||' temp WHERE EXISTS (SELECT 1 
        FROM observation_fact f 
        WHERE temp.patient_num is not null 
        and temp.encounter_num is not null 
        and temp.encounter_num = f.encounter_num 
        and temp.patient_num = f.patient_num
        and temp.concept_cd = f.concept_cd
        and temp.start_date = f.start_date
        and temp.provider_id = f.provider_id
        and temp.modifier_cd = f.modifier_cd 
        and temp.instance_num = f.instance_num
    )';

END IF;
--Transfer all rows FROM temp_obsfact to observation_fact
EXECUTE 'INSERT INTO observation_fact(
    encounter_num
    ,concept_cd
    , patient_num
    ,provider_id
    , start_date
    ,modifier_cd
    ,instance_num
    ,valtype_cd
    ,tval_char
    ,nval_num
    ,valueflag_cd
    ,quantity_num
    ,confidence_num
    ,observation_blob
    ,units_cd
    ,end_date
    ,location_cd
    , update_date
    ,download_date
    ,import_date
    ,sourcesystem_cd
    ,upload_id)
SELECT encounter_num
,concept_cd
, patient_num
,provider_id
, start_date
,modifier_cd
,instance_num
,valtype_cd
,tval_char
,nval_num
,valueflag_cd
,quantity_num
,confidence_num
,observation_blob
,units_cd
,end_date
,location_cd
, update_date
,download_date
,Now()
,sourcesystem_cd
,temp.upload_id 
FROM ' || upload_temptable_name ||' temp
WHERE (temp.patient_num IS NOT NULL AND temp.patient_num::text <> '''') AND  (temp.encounter_num IS NOT NULL AND temp.encounter_num::text <> '''')';


EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;      
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_observation_fact(text, bigint, bigint)
  OWNER TO i2b2demodata;

CREATE OR REPLACE FUNCTION instr(
    string character varying,
    string_to_search character varying,
    beg_index integer,
    occur_index integer)
  RETURNS integer AS
$BODY$
DECLARE
    pos integer NOT NULL DEFAULT 0;
    occur_number integer NOT NULL DEFAULT 0;
    temp_str varchar;
    beg integer;
    i integer;
    length integer;
    ss_length integer;
BEGIN
    IF beg_index > 0 THEN
        beg := beg_index;
        temp_str := substring(string FROM beg_index);

        FOR i IN 1..occur_index LOOP
            pos := position(string_to_search IN temp_str);

            IF i = 1 THEN
                beg := beg + pos - 1;
            ELSE
                beg := beg + pos;
            END IF;

            temp_str := substring(string FROM beg + 1);
        END LOOP;

        IF pos = 0 THEN
            RETURN 0;
        ELSE
            RETURN beg;
        END IF;
    ELSE
        ss_length := char_length(string_to_search);
        length := char_length(string);
        beg := length + beg_index - ss_length + 2;

        WHILE beg > 0 LOOP
            temp_str := substring(string FROM beg FOR ss_length);
            pos := position(string_to_search IN temp_str);

            IF pos > 0 THEN
                occur_number := occur_number + 1;

                IF occur_number = occur_index THEN
                    RETURN beg;
                END IF;
            END IF;

            beg := beg - 1;
        END LOOP;

        RETURN 0;
    END IF;
END;
$BODY$
  LANGUAGE plpgsql IMMUTABLE STRICT
  COST 100;
ALTER FUNCTION instr(character varying, character varying, integer, integer)
  OWNER TO i2b2demodata;

CREATE OR REPLACE FUNCTION instr(
    string character varying,
    string_to_search character varying,
    beg_index integer)
  RETURNS integer AS
$BODY$
DECLARE
    pos integer NOT NULL DEFAULT 0;
    temp_str varchar;
    beg integer;
    length integer;
    ss_length integer;
BEGIN
    IF beg_index > 0 THEN
        temp_str := substring(string FROM beg_index);
        pos := position(string_to_search IN temp_str);

        IF pos = 0 THEN
            RETURN 0;
        ELSE
            RETURN pos + beg_index - 1;
        END IF;
    ELSE
        ss_length := char_length(string_to_search);
        length := char_length(string);
        beg := length + beg_index - ss_length + 2;

        WHILE beg > 0 LOOP
            temp_str := substring(string FROM beg FOR ss_length);
            pos := position(string_to_search IN temp_str);

            IF pos > 0 THEN
                RETURN beg;
            END IF;

            beg := beg - 1;
        END LOOP;

        RETURN 0;
    END IF;
END;
$BODY$
  LANGUAGE plpgsql IMMUTABLE STRICT
  COST 100;
ALTER FUNCTION instr(character varying, character varying, integer)
  OWNER TO i2b2demodata;

CREATE OR REPLACE FUNCTION instr(
    character varying,
    character varying)
  RETURNS integer AS
$BODY$
DECLARE
    pos integer;
BEGIN
    pos:= instr($1, $2, 1);
    RETURN pos;
END;
$BODY$
  LANGUAGE plpgsql IMMUTABLE STRICT
  COST 100;
ALTER FUNCTION instr(character varying, character varying)
  OWNER TO i2b2demodata;

-- Function: calculate_upload_status(bigint)

-- DROP FUNCTION calculate_upload_status(bigint);

CREATE OR REPLACE FUNCTION calculate_upload_status(uploadid bigint)
  RETURNS void AS
$BODY$
BEGIN 
    -- update upload_status loaded record
    EXECUTE '       UPDATE upload_status 
    SET loaded_record = (
        SELECT count(1) 
        FROM observation_fact obsfact 
        WHERE obsfact.upload_id= ' || uploadId ||')
    WHERE upload_status.upload_id = '|| uploadId ||'';
    -- update upload_status no_of_record based on uploadid
    EXECUTE 'UPDATE upload_status 
    SET no_of_record = (
        SELECT count(1) 
        FROM temp_obsfact_'|| uploadId ||'
    )
    WHERE upload_status.upload_id = ' || uploadId ||'';
    -- update upload_status delete_record based on uploadid
    EXECUTE 'UPDATE upload_status 
    SET deleted_record = (
        SELECT count(1) 
        FROM archive_observation_fact archiveobsfact 
        WHERE archiveobsfact.archive_upload_id= ' || uploadId ||'
    )
    WHERE upload_status.upload_id = ' || uploadId ||'';
    EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;                
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION calculate_upload_status(bigint)
  OWNER TO i2b2demodata;

-- Function: delete_upload_data(bigint)

-- DROP FUNCTION delete_upload_data(bigint);

CREATE OR REPLACE FUNCTION delete_upload_data(uploadid bigint)
  RETURNS void AS
$BODY$
BEGIN 
        -- delete from observation_fact for the given upload_id
        EXECUTE '       DELETE 
    FROM observation_fact 
    WHERE upload_id = '|| uploadId ||'
    ';
        EXECUTE ' DELETE 
    FROM encounter_mapping 
    WHERE encounter_num IN (SELECT encounter_num 
        FROM visit_dimension 
        WHERE upload_id = '|| uploadId ||')
    ';
        EXECUTE ' DELETE 
    FROM visit_dimension 
    WHERE upload_id = '|| uploadId ||'
    ';
        EXECUTE ' UPDATE upload_status 
    SET load_status=''DELETED'' 
    WHERE upload_id = '|| uploadId ||'
    ';
EXCEPTION
        WHEN OTHERS THEN
      RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;                
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION delete_upload_data(bigint)
  OWNER TO i2b2demodata;

-- Function: istableexists(text)

-- DROP FUNCTION istableexists(text);

CREATE OR REPLACE FUNCTION istableexists(tablename text)
  RETURNS character varying AS
$BODY$
DECLARE

flag varchar(10);
countTableCur REFCURSOR;
countTable varchar(1); 

BEGIN 
	open countTableCur for EXECUTE 'SELECT count(1) FROM (select tablename from   pg_tables where  tableowner=(select current_user)) as p WHERE '''||tableName||''' LIKE p.tablename ' ;
	LOOP
		FETCH countTableCur INTO countTable;
		IF countTable = '0'
			THEN 
			flag := 'FALSE';
			EXIT;
		ELSE
			flag := 'TRUE';
			EXIT;
	END IF;
	
	END LOOP;
	close countTableCur ;
	return flag;

	EXCEPTION WHEN OTHERS THEN
	RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;                
	END;
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION istableexists(text)
  OWNER TO i2b2demodata;

-- Function: merge_temp_observation_fact(text)

-- DROP FUNCTION merge_temp_observation_fact(text);

CREATE OR REPLACE FUNCTION merge_temp_observation_fact(upload_temptable_name text)
  RETURNS void AS
$BODY$
BEGIN
        EXECUTE 'MERGE  INTO observation_fact obsfact
                   USING ( select emap.encounter_num,patmap.patient_num, 
                    utemp.concept_cd, 
                                        utemp.provider_id,
                                        utemp.start_date, 
                                        utemp.modifier_cd,
                                        utemp.valtype_cd,
                                        utemp.tval_char,
                                        utemp.nval_num,
                                        utemp.valueflag_cd,
                                        utemp.quantity_num,
                                        utemp.confidence_num,
                                        utemp.observation_blob,
                                        utemp.units_cd,
                                        utemp.end_date,
                                        utemp.location_cd,
                                        utemp.update_date,
                                        utemp.download_date,
                                        utemp.import_date,
                                        utemp.sourcesystem_cd,
                                        utemp.upload_id 
                   from ' || upload_temptable_name  || '  utemp , encounter_mapping emap, patient_mapping patmap 
                   where utemp.encounter_ide = emap.encounter_ide and  utemp.patient_ide = patmap.patient_ide
           ) temp
                   on (
                                temp.encounter_num = obsfact.encounter_num
                                and
                                temp.concept_cd = obsfact.concept_cd
                                and
                                temp.patient_num = obsfact.patient_num
            )    
                        when matched then 
                                update  set 
                                        obsfact.provider_id = temp.provider_id,
                                        obsfact.start_date = temp.start_date,
                                        obsfact.modifier_cd = temp.modifier_cd,
                                        obsfact.valtype_cd = temp.valtype_cd,
                                        obsfact.tval_char = temp.tval_char,
                                        obsfact.nval_num = temp.nval_num,
                                        obsfact.valueflag_cd = temp.valueflag_cd,
                                        obsfact.quantity_num = temp.quantity_num,
                                        obsfact.confidence_num = temp.confidence_num,
                                        obsfact.observation_blob = temp.observation_blob ,
                                        obsfact.units_cd = temp.units_cd,
                                        obsfact.end_date = temp.end_date,
                                        obsfact.location_cd = temp.location_cd,
                                        obsfact.update_date = temp.update_date,
                                        obsfact.download_date = temp.download_date,
                                        obsfact.import_date = temp.import_date,
                                        obsfact.sourcesystem_cd = temp.sourcesystem_cd,
                                        obsfact.upload_id = temp.upload_id
                    where temp.update_date > obsfact.update_date
                         when not matched then 
                                insert (encounter_num, 
                                        concept_cd, 
                                        patient_num,
                                        provider_id,
                                        start_date, 
                                        modifier_cd,
                                        valtype_cd,
                                        tval_char,
                                        nval_num,
                                        valueflag_cd,
                                        quantity_num,
                                        confidence_num,
                                        observation_blob,
                                        units_cd,
                                        end_date,
                                        location_cd,
                                        update_date,
                                        download_date,
                                        import_date,
                                        sourcesystem_cd,
                                        upload_id) 
                                values (
                                        temp.encounter_num, 
                                        temp.concept_cd, 
                                        temp.patient_num,
                                        temp.provider_id,
                                        temp.start_date, 
                                        temp.modifier_cd,
                                        temp.valtype_cd,
                                        temp.tval_char,
                                        temp.nval_num,
                                        temp.valueflag_cd,
                                        temp.quantity_num,
                                        temp.confidence_num,
                                        temp.observation_blob,
                                        temp.units_cd,
                                        temp.end_date,
                                        temp.location_cd,
                                        temp.update_date,
                                        temp.download_date,
                                        temp.import_date,
                                        temp.sourcesystem_cd,
                                        temp.upload_id
                                )';     
        EXCEPTION
        WHEN OTHERS THEN
                RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;      
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION merge_temp_observation_fact(text)
  OWNER TO i2b2demodata;

-- Function: report_missing_dimension(text)

-- DROP FUNCTION report_missing_dimension(text);

CREATE OR REPLACE FUNCTION report_missing_dimension(upload_temptable_name text)
  RETURNS void AS
$BODY$
BEGIN
--Missing Concept Code
EXECUTE 'INSERT ALL INTO missing_dimension_report 
PERFORM concept_cd dimension_value,count(*) total_count,''C'' dimension, upload_id FROM ' ||upload_temptable_name ||' temp
WHERE temp.concept_cd NOT IN (SELECT concept_cd FROM concept_dimension)
group by concept_cd,upload_id';
--Missing Encounter Ide
EXECUTE 'INSERT ALL INTO missing_dimension_report
SELECT encounter_ide dimension_value,count(*) total_count,''E'' dimension, upload_id FROM ' ||upload_temptable_name ||' temp
WHERE temp.encounter_ide NOT IN (SELECT encounter_ide FROM encounter_mapping)
group by encounter_ide,upload_id';
--Missing Patient Ide
EXECUTE 'INSERT ALL INTO missing_dimension_report 
PERFORM patient_ide dimension_value,count(*) total_count,''P'' dimension, upload_id FROM ' ||upload_temptable_name ||' temp
WHERE temp.patient_ide NOT IN (SELECT patient_ide FROM patient_mapping)
group by patient_ide,upload_id';
EXCEPTION
        WHEN OTHERS THEN
                RAISE EXCEPTION 'An error was encountered - % -ERROR- %',SQLSTATE,SQLERRM;      
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION report_missing_dimension(text)
  OWNER TO i2b2demodata;
  

CREATE INDEX EM_ENCNUM_IDX ON ENCOUNTER_MAPPING(ENCOUNTER_NUM);
CREATE INDEX CL_IDX_NAME_CHAR ON CODE_LOOKUP(NAME_CHAR);
CREATE INDEX CL_IDX_UPLOADID ON CODE_LOOKUP(UPLOAD_ID);
CREATE INDEX OF_IDX_START_DATE ON OBSERVATION_FACT(START_DATE, PATIENT_NUM);
CREATE INDEX OF_IDX_MODIFIER ON OBSERVATION_FACT(MODIFIER_CD);
CREATE INDEX OF_IDX_PATIENT_NUM ON OBSERVATION_FACT(PATIENT_NUM);
CREATE INDEX VD_IDX_DATES ON VISIT_DIMENSION(ENCOUNTER_NUM, START_DATE, END_DATE);
CREATE INDEX VD_IDX_INOUTCD ON VISIT_DIMENSION (INOUT_CD COLLATE PG_CATALOG.default);
CREATE INDEX PD_IDX_SEXCD ON PATIENT_DIMENSION(SEX_CD COLLATE PG_CATALOG.default);
CREATE INDEX MD_IDX_MODIFIERCD ON MODIFIER_DIMENSION(MODIFIER_CD COLLATE PG_CATALOG.default);
CREATE INDEX OF_IDX_MODIFIER_CONCEPT ON OBSERVATION_FACT(MODIFIER_CD COLLATE PG_CATALOG.default, CONCEPT_CD COLLATE PG_CATALOG.default);
CREATE INDEX CD_IDX_CONCEPT_CD ON CONCEPT_DIMENSION(CONCEPT_CD);
CREATE INDEX OF_IDX_ENCOUNTER ON OBSERVATION_FACT (ENCOUNTER_NUM);

CREATE INDEX QT_IDX_QM_UGID ON QT_QUERY_MASTER(USER_ID,GROUP_ID,MASTER_TYPE_CD);
CREATE INDEX QT_IDX_QI_UGID ON QT_QUERY_INSTANCE(USER_ID,GROUP_ID);
CREATE INDEX QT_IDX_QI_MSTARTID ON QT_QUERY_INSTANCE(QUERY_MASTER_ID,START_DATE);
CREATE INDEX QT_IDX_QPSC_RIID ON QT_PATIENT_SET_COLLECTION(RESULT_INSTANCE_ID);
CREATE INDEX QT_APNAMEVERGRP_IDX ON QT_ANALYSIS_PLUGIN(PLUGIN_NAME,VERSION_CD,GROUP_ID);
CREATE INDEX QT_IDX_PQM_UGID ON QT_PDO_QUERY_MASTER(USER_ID,GROUP_ID);

CREATE INDEX query_guid_idx
  ON insite_query_id_mapping
  (query_guid);
  
 CREATE INDEX PK_ARCHIVE_OBSFACT ON ARCHIVE_OBSERVATION_FACT
 		(ENCOUNTER_NUM , PATIENT_NUM , CONCEPT_CD , PROVIDER_ID , START_DATE , MODIFIER_CD , ARCHIVE_UPLOAD_ID) 
;

insert into QT_QUERY_STATUS_TYPE(STATUS_TYPE_ID,NAME,DESCRIPTION) values(1,'QUEUED',' WAITING IN QUEUE TO START PROCESS');
insert into QT_QUERY_STATUS_TYPE(STATUS_TYPE_ID,NAME,DESCRIPTION) values(2,'PROCESSING','PROCESSING');
insert into QT_QUERY_STATUS_TYPE(STATUS_TYPE_ID,NAME,DESCRIPTION) values(3,'FINISHED','FINISHED');
insert into QT_QUERY_STATUS_TYPE(STATUS_TYPE_ID,NAME,DESCRIPTION) values(4,'ERROR','ERROR');
insert into QT_QUERY_STATUS_TYPE(STATUS_TYPE_ID,NAME,DESCRIPTION) values(5,'INCOMPLETE','INCOMPLETE');
insert into QT_QUERY_STATUS_TYPE(STATUS_TYPE_ID,NAME,DESCRIPTION) values(6,'COMPLETED','COMPLETED');
insert into QT_QUERY_STATUS_TYPE(STATUS_TYPE_ID,NAME,DESCRIPTION) values(7,'MEDIUM_QUEUE','MEDIUM QUEUE');
insert into QT_QUERY_STATUS_TYPE(STATUS_TYPE_ID,NAME,DESCRIPTION) values(8,'LARGE_QUEUE','LARGE QUEUE');
insert into QT_QUERY_STATUS_TYPE(STATUS_TYPE_ID,NAME,DESCRIPTION) values(9,'CANCELLED','CANCELLED');
insert into QT_QUERY_STATUS_TYPE(STATUS_TYPE_ID,NAME,DESCRIPTION) values(10,'TIMEDOUT','TIMEDOUT');


insert into QT_QUERY_RESULT_TYPE(RESULT_TYPE_ID,NAME,DESCRIPTION,DISPLAY_TYPE_ID,VISUAL_ATTRIBUTE_TYPE_ID) values(1,'PATIENTSET','Patient set','LIST','LA');
insert into QT_QUERY_RESULT_TYPE(RESULT_TYPE_ID,NAME,DESCRIPTION,DISPLAY_TYPE_ID,VISUAL_ATTRIBUTE_TYPE_ID) values(2,'PATIENT_ENCOUNTER_SET','Encounter set','LIST','LA');
insert into QT_QUERY_RESULT_TYPE(RESULT_TYPE_ID,NAME,DESCRIPTION,DISPLAY_TYPE_ID,VISUAL_ATTRIBUTE_TYPE_ID) values(3,'XML','Generic query result','CATNUM','LH');
insert into QT_QUERY_RESULT_TYPE(RESULT_TYPE_ID,NAME,DESCRIPTION,DISPLAY_TYPE_ID,VISUAL_ATTRIBUTE_TYPE_ID) values(4,'PATIENT_COUNT_XML','Number of patients','CATNUM','LA');
insert into QT_QUERY_RESULT_TYPE(RESULT_TYPE_ID,NAME,DESCRIPTION,DISPLAY_TYPE_ID,VISUAL_ATTRIBUTE_TYPE_ID) values(5,'PATIENT_GENDER_COUNT_XML','Gender patient breakdown','CATNUM','LA');
insert into QT_QUERY_RESULT_TYPE(RESULT_TYPE_ID,NAME,DESCRIPTION,DISPLAY_TYPE_ID,VISUAL_ATTRIBUTE_TYPE_ID) values(6,'PATIENT_VITALSTATUS_COUNT_XML','Vital Status patient breakdown','CATNUM','LA');
insert into QT_QUERY_RESULT_TYPE(RESULT_TYPE_ID,NAME,DESCRIPTION,DISPLAY_TYPE_ID,VISUAL_ATTRIBUTE_TYPE_ID) values(7,'PATIENT_RACE_COUNT_XML','Race patient breakdown','CATNUM','LA');
insert into QT_QUERY_RESULT_TYPE(RESULT_TYPE_ID,NAME,DESCRIPTION,DISPLAY_TYPE_ID,VISUAL_ATTRIBUTE_TYPE_ID) values(8,'PATIENT_AGE_COUNT_XML','Age patient breakdown','CATNUM','LA');
insert into QT_QUERY_RESULT_TYPE(RESULT_TYPE_ID,NAME,DESCRIPTION,DISPLAY_TYPE_ID,VISUAL_ATTRIBUTE_TYPE_ID) values(9,'PATIENTSET','Timeline','LIST','LA');


insert into QT_PRIVILEGE(PROTECTION_LABEL_CD,DATAPROT_CD,HIVEMGMT_CD) values ('PDO_WITHOUT_BLOB','DATA_LDS','USER');
insert into QT_PRIVILEGE(PROTECTION_LABEL_CD,DATAPROT_CD,HIVEMGMT_CD) values ('PDO_WITH_BLOB','DATA_DEID','USER');
insert into QT_PRIVILEGE(PROTECTION_LABEL_CD,DATAPROT_CD,HIVEMGMT_CD) values ('SETFINDER_QRY_WITH_DATAOBFSC','DATA_OBFSC','USER');
insert into QT_PRIVILEGE(PROTECTION_LABEL_CD,DATAPROT_CD,HIVEMGMT_CD) values ('SETFINDER_QRY_WITHOUT_DATAOBFSC','DATA_AGG','USER');
insert into QT_PRIVILEGE(PROTECTION_LABEL_CD,DATAPROT_CD,HIVEMGMT_CD) values ('UPLOAD','DATA_OBFSC','MANAGER');
insert into QT_PRIVILEGE(PROTECTION_LABEL_CD,DATAPROT_CD,HIVEMGMT_CD) values ('SETFINDER_QRY_WITH_LGTEXT','DATA_DEID','USER'); 

INSERT INTO SET_TYPE(id,name,create_date) values (1,'event_set',now());
INSERT INTO SET_TYPE(id,name,create_date) values (2,'patient_set',now());
INSERT INTO SET_TYPE(id,name,create_date) values (3,'concept_set',now());
INSERT INTO SET_TYPE(id,name,create_date) values (4,'observer_set',now());
INSERT INTO SET_TYPE(id,name,create_date) values (5,'observation_set',now());
INSERT INTO SET_TYPE(id,name,create_date) values (6,'pid_set',now());
INSERT INTO SET_TYPE(id,name,create_date) values (7,'eid_set',now());
INSERT INTO SET_TYPE(id,name,create_date) values (8,'modifier_set',now());