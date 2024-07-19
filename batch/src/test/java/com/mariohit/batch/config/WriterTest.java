package com.mariohit.batch.config;

import com.mariohit.batch.student.Student;
import com.mariohit.batch.student.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.Collections;

import static org.mockito.Mockito.verify;

@SpringBootTest
@SpringBatchTest
@Import(BatchConfig.class)
public class WriterTest {

    @Autowired
    private RepositoryItemWriter<Student> writer;

    @MockBean
    private StudentRepository studentRepository;

    @Test
    void testWriter() throws Exception {
        Student student = new Student();
        student.setFirstname("John");
        student.setLastname("Doe");
        student.setAge(25);

        Chunk<Student> chunk = new Chunk<>(Collections.singletonList(student));

        writer.write(chunk);
        verify(studentRepository).save(student);
    }
}
