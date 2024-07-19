package com.mariohit.batch.config;

import com.mariohit.batch.student.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StudentProcessorTest {
    private final StudentProcessor processor = new StudentProcessor();

    @Test
    void testProcessor() throws Exception {
        Student student = new Student();
        student.setFirstname("John");
        student.setLastname("Doe");
        student.setAge(25);

        Student processedStudent = processor.process(student);
        assertEquals("John", processedStudent.getFirstname());
        assertEquals("DOE", processedStudent.getLastname());
    }
}
