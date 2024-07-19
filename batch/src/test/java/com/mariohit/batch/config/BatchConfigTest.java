package com.mariohit.batch.config;

import com.mariohit.batch.student.Student;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@SpringBatchTest
@Import(BatchConfig.class)
public class BatchConfigTest {

    @Autowired
    private FlatFileItemReader<Student> itemReader;

    @Test
    void testItemReader() throws Exception {
        itemReader.open(MetaDataInstanceFactory.createStepExecution().getExecutionContext());
        Student student = itemReader.read();
        assertNotNull(student);
        assertEquals("Jean", student.getFirstname());
    }
}