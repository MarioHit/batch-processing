package com.mariohit.batch.config;

import com.mariohit.batch.studentWithCategory.StudentWithCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class StudentCategoryFilterProcessor implements ItemProcessor<StudentWithCategory, StudentWithCategory> {

    private static final Logger log = LoggerFactory.getLogger(StudentCategoryFilterProcessor.class);
    @Override
    public StudentWithCategory process(StudentWithCategory item) throws Exception {
        if ("Cinquantaine et plus".equalsIgnoreCase(item.getCat())) {
            log.debug("Processing student: {}", item.getId() + " - " + item.getAge()+ " - " + item.getCat());
            return item;
        }
        log.debug("Skipping student: {}", item.getId() + " - " + item.getAge()+ " - " + item.getCat());
        return null;
    }
}
