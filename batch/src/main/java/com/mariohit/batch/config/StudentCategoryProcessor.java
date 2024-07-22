package com.mariohit.batch.config;

import com.mariohit.batch.student.Student;
import com.mariohit.batch.studentWithCategory.StudentWithCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StudentCategoryProcessor implements ItemProcessor<Student, StudentWithCategory> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentCategoryProcessor.class);
    private static final String PROCESSED_STUDENTS_KEY = "processedStudents";

    private StepExecution stepExecution;

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public StudentWithCategory process(Student item) throws Exception {
        validateStudent(item);

        String category = determineCategory(item.getAge());
        StudentWithCategory studentWithCategory = new StudentWithCategory(
                item.getId(), item.getFirstname(), item.getLastname(), item.getAge(), category
        );

        addProcessedStudent(studentWithCategory);

        LOGGER.info("Processed student: {} with category: {}", item.getId(), category);

        return studentWithCategory;
    }

    private void validateStudent(Student student) {
        if (student.getId() == null || student.getFirstname() == null || student.getLastname() == null) {
            throw new IllegalArgumentException("Student ID, firstname, and lastname cannot be null");
        }
    }

    private String determineCategory(Integer age) {
        if (age == null) {
            return AgeCategory.AGE_INCONNU.getCategory();
        } else if (age < 20) {
            return AgeCategory.DIZAINE.getCategory();
        } else if (age < 30) {
            return AgeCategory.VINGTAINE.getCategory();
        } else if (age < 40) {
            return AgeCategory.TRENTAINE.getCategory();
        } else if (age < 50) {
            return AgeCategory.QUARANTAINE.getCategory();
        } else {
            return AgeCategory.CINQUANTAINE_ET_PLUS.getCategory();
        }
    }

    private void addProcessedStudent(StudentWithCategory studentWithCategory) {
        List<StudentWithCategory> processedStudents = (List<StudentWithCategory>) stepExecution
                .getJobExecution()
                .getExecutionContext()
                .get(PROCESSED_STUDENTS_KEY);

        if (processedStudents == null) {
            processedStudents = new CopyOnWriteArrayList<>();
            stepExecution.getJobExecution().getExecutionContext().put(PROCESSED_STUDENTS_KEY, processedStudents);
        }

        processedStudents.add(studentWithCategory);
    }
}
