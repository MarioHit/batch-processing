package com.mariohit.batch.config;

import com.mariohit.batch.student.Student;
import com.mariohit.batch.student.StudentRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StudentProcessorTest {
    private final StudentProcessor processor = new StudentProcessor();

    @Test
    void testProcessor() throws Exception {
        StudentRecord studentRecord = new StudentRecord(1L, "John", "Doe", 25);

        Student processedStudent = processor.process(studentRecord);
        assertEquals("John", processedStudent.getFirstname());
        assertEquals("DOE", processedStudent.getLastname());
    }
}
