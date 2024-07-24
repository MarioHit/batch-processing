package com.mariohit.batch.config;

import com.mariohit.batch.student.Student;
import com.mariohit.batch.student.StudentRecord;
import org.springframework.batch.item.ItemProcessor;

public class StudentProcessor implements ItemProcessor<StudentRecord, Student> {


    @Override
    public Student process(StudentRecord studentRecord) throws Exception {

        if ( studentRecord.age() == null ){
            throw new IllegalArgumentException("Age cannot be null");
        }
        return new Student(studentRecord.id(), studentRecord.firstname(), studentRecord.lastname().toUpperCase(), studentRecord.age());
    }
}
