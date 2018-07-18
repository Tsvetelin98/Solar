CREATE TABLE solarnode.sn_instruction (
	id					BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	created				TIMESTAMP NOT NULL WITH DEFAULT CURRENT_TIMESTAMP,
	instruction_id 		VARCHAR(255) NOT NULL,
	instructor_id		VARCHAR(255) NOT NULL,
	topic				VARCHAR(255) NOT NULL,
	CONSTRAINT sn_instruction_pkey PRIMARY KEY (id),
	CONSTRAINT sn_instruction_unq UNIQUE (instruction_id, instructor_id)
);

CREATE INDEX instruction_created_idx ON solarnode.sn_instruction (created,topic);

CREATE TABLE solarnode.sn_instruction_param (
	instruction_id		BIGINT NOT NULL,
	pos					INTEGER NOT NULL,
	pname				VARCHAR(255) NOT NULL,
	pvalue				VARCHAR(2048) NOT NULL,
	CONSTRAINT sn_instruction_param_pkey PRIMARY KEY (instruction_id, pos),
	CONSTRAINT sn_instruction_param_instruction_fk 
		FOREIGN KEY (instruction_id) REFERENCES solarnode.sn_instruction ON DELETE CASCADE
);

CREATE TABLE solarnode.sn_instruction_status (
	instruction_id		BIGINT NOT NULL,
	modified			TIMESTAMP NOT NULL,
	state				VARCHAR(64) NOT NULL,
	jparams				VARCHAR(4096),
	ack_state			VARCHAR(64),
	CONSTRAINT sn_instruction_status_pkey PRIMARY KEY (instruction_id),
	CONSTRAINT sn_instruction_status_instruction_fk 
		FOREIGN KEY (instruction_id) REFERENCES solarnode.sn_instruction ON DELETE CASCADE
);

INSERT INTO solarnode.sn_settings (skey, svalue) 
VALUES ('solarnode.sn_instruction.version', '2');
