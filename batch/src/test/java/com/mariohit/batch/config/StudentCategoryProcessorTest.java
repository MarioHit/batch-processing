package com.mariohit.batch.config;

import com.mariohit.batch.student.Student;
import com.mariohit.batch.studentWithCategory.StudentWithCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StudentCategoryProcessorTest {
    private final StudentCategoryProcessor processor = new StudentCategoryProcessor();

    @Test
    void testProcessStudentInTeens() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setFirstname("John");
        student.setLastname("Doe");
        student.setAge(15);

        StudentWithCategory processedStudent = processor.process(student);

        assertNotNull(processedStudent);
        assertEquals("John", processedStudent.getFirstname());
        assertEquals("Doe", processedStudent.getLastname());
        assertEquals(15, processedStudent.getAge());
        assertEquals("Dizaine", processedStudent.getCat());
    }
    @Test
    void testProcessStudentInTwenties() throws Exception {
        Student student = new Student();
        student.setId(2L);
        student.setFirstname("Jane");
        student.setLastname("Smith");
        student.setAge(25);

        StudentWithCategory processedStudent = processor.process(student);

        assertNotNull(processedStudent);
        assertEquals("Jane", processedStudent.getFirstname());
        assertEquals("Smith", processedStudent.getLastname());
        assertEquals(25, processedStudent.getAge());
        assertEquals("Vingtaine", processedStudent.getCat());
    }

    @Test
    void testProcessStudentInThirties() throws Exception {
        Student student = new Student();
        student.setId(3L);
        student.setFirstname("Emily");
        student.setLastname("Brown");
        student.setAge(35);

        StudentWithCategory processedStudent = processor.process(student);

        assertNotNull(processedStudent);
        assertEquals("Emily", processedStudent.getFirstname());
        assertEquals("Brown", processedStudent.getLastname());
        assertEquals(35, processedStudent.getAge());
        assertEquals("Trentaine", processedStudent.getCat());
    }

    @Test
    void testProcessStudentInForties() throws Exception {
        Student student = new Student();
        student.setId(4L);
        student.setFirstname("Michael");
        student.setLastname("Johnson");
        student.setAge(45);

        StudentWithCategory processedStudent = processor.process(student);

        assertNotNull(processedStudent);
        assertEquals("Michael", processedStudent.getFirstname());
        assertEquals("Johnson", processedStudent.getLastname());
        assertEquals(45, processedStudent.getAge());
        assertEquals("Quarantaine", processedStudent.getCat());
    }

    @Test
    void testProcessStudentInFiftiesOrMore() throws Exception {
        Student student = new Student();
        student.setId(5L);
        student.setFirstname("Sarah");
        student.setLastname("Williams");
        student.setAge(55);

        StudentWithCategory processedStudent = processor.process(student);

        assertNotNull(processedStudent);
        assertEquals("Sarah", processedStudent.getFirstname());
        assertEquals("Williams", processedStudent.getLastname());
        assertEquals(55, processedStudent.getAge());
        assertEquals("Cinquantaine et plus", processedStudent.getCat());
    }
}
