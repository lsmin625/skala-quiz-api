DROP TABLE IF EXISTS skala_score;
DROP TABLE IF EXISTS skala_applicant_quiz;
DROP TABLE IF EXISTS skala_quiz;
DROP TABLE IF EXISTS skala_subject;
DROP TABLE IF EXISTS skala_instructor;

CREATE TABLE skala_instructor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    instructor_email VARCHAR(255) NOT NULL,
    instructor_password VARCHAR(255) NOT NULL,
    instructor_name VARCHAR(255) NOT NULL
);

CREATE TABLE skala_subject (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_name VARCHAR(255) NOT NULL,
    instructor_id BIGINT,
    FOREIGN KEY (instructor_id) REFERENCES skala_instructor(id)
);

CREATE TABLE skala_quiz (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT,
    quiz_question TEXT NOT NULL,
    quiz_difficulty INT NOT NULL,
    quiz_type INT NOT NULL,
    quiz_options TEXT,
    quiz_answer TEXT,
    quiz_score FLOAT,
    FOREIGN KEY (subject_id) REFERENCES skala_subject(id)
);

CREATE TABLE skala_applicant_quiz (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    applicant_id VARCHAR(255) NOT NULL,
    applicant_name VARCHAR(255) NOT NULL,
    start_time DATETIME NULL,
    finish_time DATETIME NULL,
    quiz_answers TEXT,
    applicant_score FLOAT,
    finished BOOLEAN DEFAULT FALSE
);
