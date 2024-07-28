package com.mariohit.batch.config;

import com.mariohit.batch.student.Student;
import com.mariohit.batch.student.StudentRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StudentProcessorTest {
    private final StudentProcessor processor = new StudentProcessor();

    @Test
    void testProcessor() throws Exception {
        StudentRecord studentRecord = new StudentRecord(1, "John", "Doe", 25);

        Student processedStudent = processor.process(studentRecord);
        assertEquals("John", processedStudent.getFirstname());
        assertEquals("DOE", processedStudent.getLastname());
    }


    @Test
    void testProcessorWithNullAge() {
        StudentRecord studentRecord = new StudentRecord(1, "John", "Doe", null);
        assertThrows(IllegalArgumentException.class, () -> {
            processor.process(studentRecord);
        });
    }

}
