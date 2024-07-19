package com.mariohit.batch.config;

import com.mariohit.batch.student.Student;
import com.mariohit.batch.studentWithCategory.StudentWithCategory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StudentCategoryProcessor implements ItemProcessor<Student, StudentWithCategory> {

    private StepExecution stepExecution;

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public StudentWithCategory process(Student item) throws Exception {
        String category;
        int age = item.getAge();
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

        StudentWithCategory studentWithCategory = new StudentWithCategory(item.getId(), item.getFirstname(), item.getLastname(), item.getAge(), category);

        List<StudentWithCategory> processedStudents = (List<StudentWithCategory>) stepExecution.getJobExecution().getExecutionContext().get("processedStudents");
        if (processedStudents == null) {
            processedStudents = new CopyOnWriteArrayList<>();
            stepExecution.getJobExecution().getExecutionContext().put("processedStudents", processedStudents);
        }
        processedStudents.add(studentWithCategory);

        return studentWithCategory;
    }
}
