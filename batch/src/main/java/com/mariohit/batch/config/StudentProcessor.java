package com.mariohit.batch.config;

import com.mariohit.batch.student.Student;
import org.springframework.batch.item.ItemProcessor;

public class StudentProcessor implements ItemProcessor<Student, Student> {


    @Override
    public Student process(Student student) throws Exception {

        //student.setFirstname(student.getFirstname().toUpperCase());
        student.setId(null);
        student.setLastname(student.getLastname().toUpperCase());
        return student;
    }
}
