USE attendance_system;

-- Insert semesters
INSERT INTO Semester (academic_year, semester_number) VALUES ('2025/2026', 1);

-- Insert teachers
INSERT INTO Teacher (teacher_id, full_name, email, password, department) VALUES
('T001', 'Alice Johnson', 'alice@university.edu', '1234', 'Computer Science'),
('T002', 'Bob Smith', 'bob@university.edu', '1234', 'Information Systems');

-- Insert students ETS001 to ETS008
INSERT INTO Student (student_id, full_name, department, year_level, section) VALUES
('ETS001', 'Student One', 'Computer Science', 3, 'A'),
('ETS002', 'Student Two', 'Computer Science', 3, 'A'),
('ETS003', 'Student Three', 'Computer Science', 3, 'A'),
('ETS004', 'Student Four', 'Computer Science', 3, 'A'),
('ETS005', 'Student Five', 'Information Systems', 2, 'B'),
('ETS006', 'Student Six', 'Information Systems', 2, 'B'),
('ETS007', 'Student Seven', 'Information Systems', 2, 'B'),
('ETS008', 'Student Eight', 'Information Systems', 2, 'B');

-- Insert courses
INSERT INTO Course (course_code, course_name) VALUES
('CS401', 'Advanced Java'),
('CS402', 'Database Systems'),
('CS403', 'Software Engineering'),
('CS404', 'Web Development');

-- Insert class offerings (assign courses to teachers for semester_id=1)
INSERT INTO ClassOffering (course_id, teacher_id, semester_id, department, year_level, section) VALUES
(1, 'T001', 1, 'Computer Science', 3, 'A'),
(2, 'T001', 1, 'Computer Science', 3, 'A'),
(3, 'T002', 1, 'Information Systems', 2, 'B'),
(4, 'T002', 1, 'Information Systems', 2, 'B');

-- Enroll students
INSERT INTO Enrollment (class_id, student_id) VALUES
(1, 'ETS001'), (1, 'ETS002'), (1, 'ETS003'), (1, 'ETS004'),
(2, 'ETS001'), (2, 'ETS002'),
(3, 'ETS005'), (3, 'ETS006'), (3, 'ETS007'),
(4, 'ETS005'), (4, 'ETS006'), (4, 'ETS008');
