package com.mariohit.batch.config;

import com.mariohit.batch.studentWithCategory.StudentWithCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class JobCompletionNotificationListener extends StepExecutionListenerSupport {

    private List<StudentWithCategory> processedStudents = new ArrayList<>();

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        List<StudentWithCategory> students = (List<StudentWithCategory>) stepExecution.getJobExecution().getExecutionContext().get("processedStudents");
        if (students != null) {
            processedStudents.addAll(students);
        }
        return stepExecution.getExitStatus();
    }

    public List<StudentWithCategory> getProcessedStudents() {
        return processedStudents;
    }
}
