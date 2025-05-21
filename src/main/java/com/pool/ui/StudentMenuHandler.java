package com.pool.ui;

import com.pool.model.Student;
import com.pool.service.StudentService;
import com.pool.exception.StudentEmailException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class StudentMenuHandler {
    private final StudentService service = new StudentService();
    private final Scanner scanner;

    public StudentMenuHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    public void printMenu() {
        System.out.println("\n=== Student Record System ===");
        System.out.println("1. Add Student");
        System.out.println("2. Update Student");
        System.out.println("3. Delete Student");
        System.out.println("4. Get Student by ID");
        System.out.println("5. Get All Students");
        System.out.println("6. Get Students by Course");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");
    }

    public boolean handleChoice(int choice) {
        switch (choice) {
            case 1 : addStudent(); break;
            case 2 : updateStudent(); break;
            case 3 : deleteStudent(); break;
            case 4 : getStudentById(); break;
            case 5 : getAllStudents(); break;
            case 6 : getStudentsByCourse(); break;
            case 7 :  return false;
            default : System.out.println("Invalid choice! Please enter 1-7."); break;
        }
        return true;
    }

    private void addStudent() {
        System.out.println("\n--- Add New Student ---");
        Student student = new Student();
        try {
            System.out.print("First Name: ");
            student.setFirstName(scanner.nextLine());

            System.out.print("Last Name: ");
            student.setLastName(scanner.nextLine());

            System.out.print("Email: ");
            student.setEmail(scanner.nextLine().trim().toLowerCase());

            System.out.print("Date of Birth (YYYY-MM-DD): ");
            student.setDateOfBirth(LocalDate.parse(scanner.nextLine()));

            System.out.print("Enrollment Date (YYYY-MM-DD): ");
            student.setEnrollmentDate(LocalDate.parse(scanner.nextLine()));

            System.out.print("Course: ");
            student.setCourse(scanner.nextLine());

            System.out.print("GPA (0.0 - 10.0): ");
            double gpa = Double.parseDouble(scanner.nextLine());
            if (gpa < 0 || gpa > 10.0) throw new IllegalArgumentException("GPA must be between 0.0 and 10.0");
            student.setGpa(gpa);

            int id = service.addStudent(student);
            System.out.println("Student added successfully! ID: " + id);
        } catch (StudentEmailException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format! Use YYYY-MM-DD.");
        }
    }

    private void updateStudent() {
        System.out.println("\n--- Update Student ---");
        try {
            System.out.print("Enter Student ID to update: ");
            int id = Integer.parseInt(scanner.nextLine());

            Student student = service.getStudentById(id);
            if (student == null) {
                System.out.println("Student not found!");
                return;
            }

            System.out.println("Current Details: " + student);
            System.out.println("Enter new details (leave blank to keep current):");

            System.out.print("First Name (" + student.getFirstName() + "): ");
            String input = scanner.nextLine();
            if (!input.isEmpty()) student.setFirstName(input);

            System.out.print("Last Name (" + student.getLastName() + "): ");
            input = scanner.nextLine();
            if (!input.isEmpty()) student.setLastName(input);

            System.out.print("Email (" + student.getEmail() + "): ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) student.setEmail(input.toLowerCase());

            System.out.print("Date of Birth (" + student.getDateOfBirth() + "): ");
            input = scanner.nextLine();
            if (!input.isEmpty()) student.setDateOfBirth(LocalDate.parse(input));

            System.out.print("Course (" + student.getCourse() + "): ");
            input = scanner.nextLine();
            if (!input.isEmpty()) student.setCourse(input);

            System.out.print("GPA (" + student.getGpa() + "): ");
            input = scanner.nextLine();
            if (!input.isEmpty()) {
                double gpa = Double.parseDouble(input);
                if (gpa < 0 || gpa > 10.0) throw new IllegalArgumentException("GPA must be between 0.0 and 10.0");
                student.setGpa(gpa);
            }

            int result = service.updateStudent(student);
            System.out.println(result > 0 ? "Student updated successfully!" : "Update failed.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format! Use YYYY-MM-DD.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        System.out.println("\n--- Delete Student ---");
        try {
            System.out.print("Enter Student ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine());

            int result = service.deleteStudent(id);
            System.out.println(result > 0 ? "Student deleted successfully!" : "Student not found!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        }
    }

    private void getStudentById() {
        System.out.println("\n--- Get Student by ID ---");
        try {
            System.out.print("Enter Student ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            Student student = service.getStudentById(id);
            if (student != null) {
                System.out.println("Student Details:\n" + student);
            } else {
                System.out.println("Student not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        }
    }

    private void getAllStudents() {
        System.out.println("\n--- All Students ---");
        List<Student> students = service.getAllStudent();
        if (students.isEmpty()) {
            System.out.println("No students found!");
        } else {
            System.out.printf("%-5s %-15s %-15s %-25s %-12s %-15s %-20s %-4s\n",
                    "ID", "First Name", "Last Name", "Email", "DOB", "Enrollment", "Course", "GPA");
            students.forEach(s -> System.out.printf("%-5d %-15s %-15s %-25s %-12s %-15s %-20s %.2f\n",
                    s.getId(), s.getFirstName(), s.getLastName(), s.getEmail(),
                    s.getDateOfBirth(), s.getEnrollmentDate(), s.getCourse(), s.getGpa()));
        }
    }

    private void getStudentsByCourse() {
        System.out.println("\n--- Students by Course ---");
        System.out.print("Enter Course Name: ");
        String course = scanner.nextLine();

        List<Student> students = service.getStudentByCourse(course);
        if (students.isEmpty()) {
            System.out.println("No students found for course: " + course);
        } else {
            System.out.println("Students in " + course + ":");
            System.out.printf("%-5s %-15s %-15s %-25s %-12s %-15s %-4s\n",
                    "ID", "First Name", "Last Name", "Email", "DOB", "Enrollment", "GPA");
            students.forEach(s -> System.out.printf("%-5d %-15s %-15s %-25s %-12s %-15s %.2f\n",
                    s.getId(), s.getFirstName(), s.getLastName(), s.getEmail(),
                    s.getDateOfBirth(), s.getEnrollmentDate(), s.getGpa()));
        }
    }
}
