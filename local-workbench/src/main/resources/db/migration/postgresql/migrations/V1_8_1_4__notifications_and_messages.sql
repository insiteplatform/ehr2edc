CREATE TABLE notification
(
  id bigint NOT NULL,
  sender_id bigint NOT NULL,
  recipient_id bigint NOT NULL,
  sending_date timestamp without time zone NOT NULL,
  reading_date timestamp without time zone NULL,
  expiry_date timestamp without time zone NULL,
  priority character varying(10) NOT NULL,
  type character varying(50) NOT NULL,
  is_read boolean NOT NULL,

  CONSTRAINT notification_pkey PRIMARY KEY (id),
  CONSTRAINT fk_notification_sender FOREIGN KEY (sender_id)
	REFERENCES app_user (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT fk_notification_receiver FOREIGN KEY (recipient_id)
	REFERENCES app_user (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION
	)
WITH (
  OIDS=FALSE
);
ALTER TABLE cohort_export
  OWNER TO postgres;

CREATE TABLE message (
  id bigint NOT NULL,
  subject character varying(255) NULL,
  body TEXT NULL,
  attachment TEXT NULL,
  notification_id bigint NOT NULL,
  CONSTRAINT message_pkey PRIMARY KEY (id),
  CONSTRAINT fk_message_notification FOREIGN KEY (notification_id)
	REFERENCES notification (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION);