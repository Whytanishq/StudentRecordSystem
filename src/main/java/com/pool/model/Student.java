package com.pool.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Student {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateOfBirth;
    private LocalDate enrollmentDate;
    private String course;
    private Double gpa;
}