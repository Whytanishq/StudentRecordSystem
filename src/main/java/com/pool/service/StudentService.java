package com.pool.service;

import com.pool.dao.StudentDao;
import com.pool.dao.impl.StudentDaoImpl;
import com.pool.model.Student;

import java.util.List;

public class StudentService {
    private final StudentDao studentDao;

    public StudentService() {
        this.studentDao = new StudentDaoImpl();
    }

    public int addStudent(Student student) {
        return studentDao.save(student);
    }

    public int updateStudent(Student student){
        return studentDao.update(student);
    }

    public int deleteStudent(Integer id) {
        return studentDao.delete(id);
    }

    public Student getStudentById(Integer id) {
        return studentDao.findById(id).orElse(null);
    }

    public List<Student> getAllStudent() {
        return studentDao.findAll();
    }

    public List<Student> getStudentByCourse(String course) {
        return studentDao.findByCourse(course);
    }
}
