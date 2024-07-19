package com.mariohit.batch.config;

import com.mariohit.batch.student.Student;
import com.mariohit.batch.student.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@SpringBatchTest
@Import(BatchConfig.class)
public class StudentDatabaseItemReaderTest {

    @Autowired
    private RepositoryItemReader<Student> reader;

    @MockBean
    private StudentRepository studentRepository;

    @Test
    void testDatabaseItemReader1() throws Exception {
        Student student1 = new Student(1L, "John", "Doe", 20);
        Student student2 = new Student(2L, "Jane", "Doe", 25);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Student> page = new PageImpl<>(List.of(student1, student2), pageable, 2);
        when(studentRepository.findAll(pageable)).thenReturn(page);

        reader.setRepository(studentRepository);
        reader.setMethodName("findAll");
        reader.setPageSize(10);
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        reader.afterPropertiesSet(); // pour initialiser le reader

        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext); // Open reader avec ExecutionContext valide

        Student student = reader.read();
        assertNotNull(student);
        assertEquals("John", student.getFirstname());

        student = reader.read();
        assertNotNull(student);
        assertEquals("Jane", student.getFirstname());

        reader.close(); // Close le reader
    }

    @Test
    void testDatabaseItemReaderNoData() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Student> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(studentRepository.findAll(pageable)).thenReturn(emptyPage);

        reader.setRepository(studentRepository);
        reader.setMethodName("findAll");
        reader.setPageSize(10);
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        reader.afterPropertiesSet();

        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);

        Student student = reader.read();
        assertNull(student);

        reader.close();
    }

    @Test
    void testDatabaseItemReaderException() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        when(studentRepository.findAll(pageable)).thenThrow(new RuntimeException("Database is down"));

        reader.setRepository(studentRepository);
        reader.setMethodName("findAll");
        reader.setPageSize(10);
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        reader.afterPropertiesSet();

        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reader.read();
        });

        String expectedMessage = "Database is down";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        reader.close();
    }
}
