package com.mariohit.batch.config;

import com.mariohit.batch.student.Student;
import com.mariohit.batch.student.StudentRepository;
import com.mariohit.batch.studentWithCategory.StudentWithCategory;
import com.mariohit.batch.studentWithCategory.StudentWithCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final StudentRepository studentRepository;
    private final StudentWithCategoryRepository studentWithCategoryRepository;


    //create the itemreader
    @Bean
    public FlatFileItemReader<Student> itemReader() {
        FlatFileItemReader<Student> itemReader = new FlatFileItemReader<>();

        itemReader.setResource(new FileSystemResource("src/main/resources/students50k.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());

        return itemReader;
    }

    //create the item processor
    @Bean
    public StudentProcessor processor(){
        return new StudentProcessor();
    }


    //create the item writer
    @Bean
    public RepositoryItemWriter<Student> writer() {
        RepositoryItemWriter<Student> writer = new RepositoryItemWriter<>();
        writer.setRepository(studentRepository);
        writer.setMethodName("save");
        return writer;
    }

    // reader pour catégorie
    @Bean
    public RepositoryItemReader<Student> studentDatabaseItemReader() {
        RepositoryItemReader<Student> reader = new RepositoryItemReader<>();
        reader.setRepository(studentRepository);
        reader.setMethodName("findAll");
        reader.setPageSize(10);
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        return reader;
    }
    // processor pour categorie
    @Bean
    public StudentCategoryProcessor studentCategoryProcessor() {
        return new StudentCategoryProcessor();
    }

    // writer pour categorie
    @Bean
    public RepositoryItemWriter<StudentWithCategory> studentCategoryWriter() {
        RepositoryItemWriter<StudentWithCategory> writer = new RepositoryItemWriter<>();
        writer.setRepository(studentWithCategoryRepository);
        writer.setMethodName("save");
        return writer;
    }

    //création d'une étape
    @Bean
    public Step importStep() {
        return new StepBuilder("importCsv", jobRepository)
                .<Student, Student>chunk(10,platformTransactionManager)
                .reader(itemReader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    // catégoriser les student
    @Bean
    public Step categoryStep() {
        return new StepBuilder("categorizeStudents", jobRepository)
                .<Student, StudentWithCategory>chunk(10, platformTransactionManager)
                .reader(studentDatabaseItemReader())
                .processor(studentCategoryProcessor())
                .writer(studentCategoryWriter())
                .taskExecutor(taskExecutor())
                .build();
    }


    //création d'un job
    @Bean
    public Job runJob() {
        return new JobBuilder("importStudents",jobRepository)
                .start(importStep())
                .next(categoryStep())
                .build();
    }

    // Pour pouvoir lancer les job en parallele
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }



    //linemapper qui va associer une les lignes du fichier à la table student
    private LineMapper<Student> lineMapper() {
        DefaultLineMapper<Student> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id","firstname","lastname","age");

        BeanWrapperFieldSetMapper<Student> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Student.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;

    }
}
