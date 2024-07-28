package com.mariohit.batch.config;

import com.mariohit.batch.student.Student;
import com.mariohit.batch.student.StudentRecord;
import org.springframework.batch.item.ItemProcessor;

public class StudentProcessor implements ItemProcessor<StudentRecord, Student> {


    @Override
    public Student process(StudentRecord studentRecord) throws Exception {

        int age = 0;
        if ( studentRecord.age() == null ){
            age = 0;
            //throw new IllegalArgumentException("Age cannot be null");
        }else {
            age = studentRecord.age();
        }

        String firstname = (studentRecord.firstname() == null || studentRecord.firstname().isEmpty()) ? "UNKNOWN" : studentRecord.firstname();
        String lastname = (studentRecord.lastname() == null || studentRecord.lastname().isEmpty()) ? "UNKNOWN" : studentRecord.lastname().toUpperCase();
        return new Student(studentRecord.id().longValue(), firstname, lastname, age);
    }
}
