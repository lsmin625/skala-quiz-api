DROP TABLE IF EXISTS skala_score;
DROP TABLE IF EXISTS skala_applicant_quiz;
DROP TABLE IF EXISTS skala_applicant;
DROP TABLE IF EXISTS skala_quiz;
DROP TABLE IF EXISTS skala_subject;
DROP TABLE IF EXISTS skala_instructor;

CREATE TABLE skala_instructor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE skala_subject (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    instructor_id BIGINT,
    FOREIGN KEY (instructor_id) REFERENCES skala_instructor(id)
);

CREATE TABLE skala_quiz (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question TEXT NOT NULL,
    difficulty ENUM('HIGH', 'MEDIUM', 'LOW') NOT NULL,
    type ENUM('SHORT', 'MULTIPLE') NOT NULL,
    options TEXT,
    answer TEXT,
    subject_id BIGINT,
    FOREIGN KEY (subject_id) REFERENCES skala_subject(id)
);

CREATE TABLE skala_applicant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE skala_applicant_quiz (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    applicant_id BIGINT,
    quiz_id BIGINT,
    answer TEXT,
    is_final_submission BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (applicant_id) REFERENCES skala_applicant(id),
    FOREIGN KEY (quiz_id) REFERENCES skala_quiz(id)
);

CREATE TABLE skala_score (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    applicant_id BIGINT,
    subject_id BIGINT,
    score INT,
    FOREIGN KEY (applicant_id) REFERENCES skala_applicant(id),
    FOREIGN KEY (subject_id) REFERENCES skala_subject(id)
);
