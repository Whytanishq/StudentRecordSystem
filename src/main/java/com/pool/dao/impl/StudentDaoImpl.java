package com.pool.dao.impl;

import com.pool.dao.StudentDao;
import com.pool.config.DatabaseConfig;
import com.pool.model.Student;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDaoImpl implements StudentDao {
    private final HikariDataSource dataSource;

    public StudentDaoImpl() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    @Override
    public int save(Student student) {
        String sql = "INSERT INTO students (first_name, last_name, email, date_of_birth, enrollment_date, course, gpa) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getEmail());
            stmt.setDate(4, Date.valueOf(student.getDateOfBirth()));
            stmt.setDate(5, Date.valueOf(student.getEnrollmentDate()));
            stmt.setString(6, student.getCourse());
            stmt.setDouble(7, student.getGpa());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save student", e);
        }
    }

    @Override
    public int update(Student student) {
        String sql = "UPDATE students SET first_name=?, last_name=?, email=?, date_of_birth=?, enrollment_date=?, course=?, gpa=? WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setString(3, student.getEmail());
            pstmt.setDate(4, Date.valueOf(student.getDateOfBirth()));
            pstmt.setDate(5, Date.valueOf(student.getEnrollmentDate()));
            pstmt.setString(6, student.getCourse());
            pstmt.setDouble(7, student.getGpa());
            pstmt.setInt(8, student.getId());

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update student", e);
        }
    }

    @Override
    public int delete(Integer id) {
        String sql = "DELETE FROM students WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete student", e);
        }
    }

    @Override
    public Optional<Student> findById(Integer id) {
        String sql = "SELECT * FROM students WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Student student = mapResultSetToStudent(rs);
                    return Optional.of(student);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find student by id", e);
        }
    }

    @Override
    public List<Student> findAll() {
        String sql = "SELECT * FROM students";
        List<Student> students = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            return students;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all students", e);
        }
    }

    @Override
    public List<Student> findByCourse(String course) {
        String sql = "SELECT * FROM students WHERE course=?";
        List<Student> students = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, course);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
            return students;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find students by course", e);
        }
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setEmail(rs.getString("email"));
        student.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        student.setEnrollmentDate(rs.getDate("enrollment_date").toLocalDate());
        student.setCourse(rs.getString("course"));
        student.setGpa(rs.getDouble("gpa"));
        return student;
    }
}
