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
    quiz_question TEXT NOT NULL,
    quiz_difficulty INT NOT NULL,
    quiz_type INT NOT NULL,
    quiz_options TEXT,
    quiz_answer TEXT,
    subject_id BIGINT,
    FOREIGN KEY (subject_id) REFERENCES skala_subject(id)
);

CREATE TABLE skala_applicant_quiz (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    applicant_id VARCHAR(255) NOT NULL,
    applicant_name VARCHAR(255) NOT NULL,
    quiz_id BIGINT,
    applicant_answer TEXT,
    is_final_submission BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (quiz_id) REFERENCES skala_quiz(id)
);

CREATE TABLE skala_score (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    applicant_id VARCHAR(255) NOT NULL,
    applicant_name VARCHAR(255) NOT NULL,
    subject_id BIGINT,
    score INT,
    FOREIGN KEY (subject_id) REFERENCES skala_subject(id)
);
