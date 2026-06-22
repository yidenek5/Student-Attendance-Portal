-- Database: attendance_system

CREATE DATABASE IF NOT EXISTS attendance_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE attendance_system;

CREATE TABLE IF NOT EXISTS Teacher (
  teacher_id VARCHAR(20) PRIMARY KEY,
  full_name VARCHAR(100),
  email VARCHAR(100),
  password VARCHAR(255),
  department VARCHAR(100)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Student (
  student_id VARCHAR(20) PRIMARY KEY,
  full_name VARCHAR(100),
  department VARCHAR(100),
  year_level INT,
  section VARCHAR(10)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Semester (
  semester_id INT AUTO_INCREMENT PRIMARY KEY,
  academic_year VARCHAR(20),
  semester_number INT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Course (
  course_id INT AUTO_INCREMENT PRIMARY KEY,
  course_code VARCHAR(20),
  course_name VARCHAR(100)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS ClassOffering (
  class_id INT AUTO_INCREMENT PRIMARY KEY,
  course_id INT,
  teacher_id VARCHAR(20),
  semester_id INT,
  department VARCHAR(100),
  year_level INT,
  section VARCHAR(10),
  FOREIGN KEY (course_id) REFERENCES Course(course_id),
  FOREIGN KEY (teacher_id) REFERENCES Teacher(teacher_id),
  FOREIGN KEY (semester_id) REFERENCES Semester(semester_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Enrollment (
  enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
  class_id INT,
  student_id VARCHAR(20),
  UNIQUE KEY unique_enrollment (class_id, student_id),
  FOREIGN KEY (class_id) REFERENCES ClassOffering(class_id),
  FOREIGN KEY (student_id) REFERENCES Student(student_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS AttendanceSession (
  session_id INT AUTO_INCREMENT PRIMARY KEY,
  class_id INT,
  session_code VARCHAR(20),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  duration_minutes INT,
  active BOOLEAN DEFAULT TRUE,
  FOREIGN KEY (class_id) REFERENCES ClassOffering(class_id),
  UNIQUE KEY unique_session_code (session_code)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS AttendanceRecord (
  record_id INT AUTO_INCREMENT PRIMARY KEY,
  session_id INT,
  student_id VARCHAR(20),
  status ENUM('PRESENT','ABSENT'),
  marked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_record (session_id, student_id),
  FOREIGN KEY (session_id) REFERENCES AttendanceSession(session_id),
  FOREIGN KEY (student_id) REFERENCES Student(student_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS AttendanceDevice (
  session_id INT,
  device_id VARCHAR(100),
  student_id VARCHAR(20),
  registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (session_id, device_id),
  UNIQUE KEY unique_session_student (session_id, student_id),
  FOREIGN KEY (session_id) REFERENCES AttendanceSession(session_id),
  FOREIGN KEY (student_id) REFERENCES Student(student_id)
) ENGINE=InnoDB;
