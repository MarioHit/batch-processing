package com.mariohit.batch.config;

import com.mariohit.batch.studentWithCategory.StudentWithCategory;
import org.springframework.batch.item.ItemProcessor;

public class StudentCategoryFilterProcessor implements ItemProcessor<StudentWithCategory, StudentWithCategory> {
    @Override
    public StudentWithCategory process(StudentWithCategory item) throws Exception {
        if ("cinquantaine ou plus".equalsIgnoreCase(item.getCat())) {
            return item;
        }
        return null;
    }
}
