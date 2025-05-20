package com.pool.dao;

import com.pool.model.Student;
import java.util.List;
import java.util.Optional;

public interface StudentDao {
    int save(Student student);
    int update(Student student);
    int delete(Integer id);
    Optional<Student> findById(Integer id);
    List<Student> findAll();
    List<Student> findByCourse(String course);
}
