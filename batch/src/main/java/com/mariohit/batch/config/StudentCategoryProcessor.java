package com.mariohit.batch.config;

import com.mariohit.batch.student.Student;
import com.mariohit.batch.studentWithCategory.StudentWithCategory;
import org.springframework.batch.item.ItemProcessor;

public class StudentCategoryProcessor implements ItemProcessor<Student, StudentWithCategory> {
    @Override
    public StudentWithCategory process(Student student) throws Exception {
        String category;
        int age = student.getAge();

        if (age < 20) {
            category = "Dizaine";
        } else if (age < 30) {
            category = "Vingtaine";
        } else if (age < 40) {
            category = "Trentaine";
        } else if (age < 50) {
            category = "Quarantaine";
        } else {
            category = "Cinquantaine et plus";
        }

        return new StudentWithCategory(
                student.getId(),
                student.getFirstname(),
                student.getLastname(),
                student.getAge(),
                category);
    }
}
