DROP TABLE IF EXISTS son_ems;
CREATE TABLE son_ems (
    ems_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ems_name VARCHAR(100) NOT NULL,
    region_code VARCHAR(16),
    pld_type VARCHAR(100) NOT NULL,

    id_insert VARCHAR(64) NULL,
    dt_insert DATETIME NULL,
    id_update VARCHAR(64) NULL,
    dt_update DATETIME NULL
);

DROP TABLE IF EXISTS son_ems_du;
CREATE TABLE son_ems_du (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ems_id INT NOT NULL,
    du_id VARCHAR(100) NOT NULL,
    collected_date DATETIME NOT NULL,
    collected_pld_list JSON
);

DROP TABLE IF EXISTS son_pld_group;
CREATE TABLE son_pld_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    region_code VARCHAR(16) NOT NULL,
    service_type VARCHAR(100) NOT NULL,
    group_name VARCHAR(100) NOT NULL,
    plds JSON,

    id_insert VARCHAR(64) NULL,
    dt_insert DATETIME NULL,
    id_update VARCHAR(64) NULL,
    dt_update DATETIME NULL
);

DROP TABLE IF EXISTS son_pld_base;
CREATE TABLE son_pld_base (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pld_name VARCHAR(100) NOT NULL,
    pld_paras JSON,

    id_insert VARCHAR(64) NULL,
    dt_insert DATETIME NULL,
    id_update VARCHAR(64) NULL,
    dt_update DATETIME NULL
);


DROP TABLE IF EXISTS son_btal_ho;
CREATE TABLE son_btal_ho (
    collected_date DATETIME NOT NULL,
    file_name VARCHAR(256) NULL,
    tal_id INT NOT NULL,
    att_tot_cnt BIGINT DEFAULT 0,
    intra_att_tot BIGINT DEFAULT 0,
    nb_tacs JSON,

    PRIMARY KEY (collected_date, file_name, tal_id)
);
