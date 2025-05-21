package com.pool.dao.impl;

import com.pool.dao.StudentDao;
import com.pool.config.DatabaseConfig;
import com.pool.exception.StudentEmailException;
import com.pool.model.Student;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
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
        // Check if email already exists (for new student email uniqueness)
        if (emailExists(student.getEmail(), 0)) {
            throw new StudentEmailException("Email already exists: " + student.getEmail());
        }

        String sql = "INSERT INTO students (first_name, last_name, email, date_of_birth, enrollment_date, course, gpa) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
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
                        return rs.getInt(1); // Return generated id
                    }
                }
            }
            return -1; // Insert failed without exception
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save student", e);
        }
    }

    @Override
    public int update(Student student) {
        // Check if email already exists for another student (exclude current student id)
        if (emailExists(student.getEmail(), student.getId())) {
            throw new StudentEmailException("Email already exists: " + student.getEmail());
        }

        String sql = "UPDATE students SET first_name=?, last_name=?, email=?, date_of_birth=?, enrollment_date=?, course=?, gpa=? WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getEmail());
            stmt.setDate(4, Date.valueOf(student.getDateOfBirth()));
            stmt.setDate(5, Date.valueOf(student.getEnrollmentDate()));
            stmt.setString(6, student.getCourse());
            stmt.setDouble(7, student.getGpa());
            stmt.setInt(8, student.getId());

            return stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update student", e);
        }
    }

    @Override
    public int delete(Integer id) {
        String sql = "DELETE FROM students WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete student", e);
        }
    }

    @Override
    public Optional<Student> findById(Integer id) {
        String sql = "SELECT * FROM students WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
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
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, course);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
            return students;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find students by course", e);
        }
    }

    /**
     * Check if email exists for a student other than the one with excludeId.
     * For new inserts, pass excludeId = 0.
     */
    private boolean emailExists(String email, int excludeId) {
        String sql = "SELECT id FROM students WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int foundId = rs.getInt("id");
                    if (foundId != excludeId) {
                        return true;
                    }
                }
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to check if email exists", e);
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
