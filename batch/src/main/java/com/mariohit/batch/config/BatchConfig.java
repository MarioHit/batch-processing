package com.mariohit.batch.config;

import com.mariohit.batch.error.BatchError;
import com.mariohit.batch.error.BatchErrorRepository;
import com.mariohit.batch.student.Student;
import com.mariohit.batch.student.StudentRecord;
import com.mariohit.batch.student.StudentRepository;
import com.mariohit.batch.studentWithCategory.StudentWithCategory;
import com.mariohit.batch.studentWithCategory.StudentWithCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfig.class);

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final StudentRepository studentRepository;
    private final StudentWithCategoryRepository studentWithCategoryRepository;
    private final JobCompletionNotificationListener listener;
    @Autowired
    private BatchErrorRepository batchErrorRepository;
    @Value("${CSV_FILE_PATH}")
    private String csvFilePath;



    /* Etape 1  lire le fichier csv et écrire en Bdd */

    //create the itemreader
    @Bean
    public FlatFileItemReader<StudentRecord> itemReader() {
        FlatFileItemReader<StudentRecord> itemReader = new FlatFileItemReader<>();

        itemReader.setResource(new FileSystemResource(csvFilePath));
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


    /* Etape 2  lire le fichier la bdd et écrire en Bdd dans une autre table */

    // reader pour catégorie
    @Bean
    public RepositoryItemReader<Student> studentDatabaseItemReader() {
        RepositoryItemReader<Student> reader = new RepositoryItemReader<>();
        reader.setRepository(studentRepository);
        reader.setMethodName("findAll");
        reader.setPageSize(10);
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        try {
            reader.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize RepositoryItemReader", e);
        }
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


    /* Etape 3  lire la bdd et écrire dans une fichier csv */

    // item reader
    @Bean
    public RepositoryItemReader<StudentWithCategory> studentWithCategoryReader() {
        RepositoryItemReader<StudentWithCategory> reader = new RepositoryItemReader<>();
        reader.setRepository(studentWithCategoryRepository);
        reader.setMethodName("findAll");
        reader.setPageSize(10);
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        try {
            reader.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize RepositoryItemReader", e);
        }
        return reader;
    }


    //item processor
    @Bean
    public StudentCategoryFilterProcessor studentCategoryFilterProcessor() {
        return new StudentCategoryFilterProcessor();
    }


    // item writer
    @Bean
    public FlatFileItemWriter<StudentWithCategory> csvWriter() {
        FlatFileItemWriter<StudentWithCategory> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("src/main/resources/output/students_cinquantaine.csv"));
        writer.setAppendAllowed(false);
        writer.setHeaderCallback(headerWriter -> headerWriter.write("id,firstname,lastname,age,cat"));
        writer.setLineAggregator(new DelimitedLineAggregator<StudentWithCategory>() {{
            setDelimiter(",");
            setFieldExtractor(new BeanWrapperFieldExtractor<StudentWithCategory>() {{
                setNames(new String[]{"id", "firstname", "lastname", "age", "cat"});
            }});
        }});
        return writer;
    }



    // gestion des erreurs :


    @Bean
    public SkipPolicy fileVerificationSkipper() {
        return new SkipPolicy() {
            @Override
            public boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException {
                if (t instanceof FlatFileParseException && skipCount <= 10) {
                    FlatFileParseException ffpe = (FlatFileParseException) t;
                    // Log or save the skip information, including the line number and input
                    LOGGER.info("Skipping line: " + ffpe.getLineNumber() + " Input: " + ffpe.getInput());
                    BatchError error = BatchError.builder()
                            .jobName("importStudents")
                            .stepName("importCsv")
                            .lineNumber(ffpe.getLineNumber())
                            .input(ffpe.getInput())
                            .errorMessage(t.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build();
                    batchErrorRepository.save(error);
                    return true;
                }
                return false;
            }
        };
    }




    //Création de l'étape 1
    @Bean
    public Step importStep() {
        return new StepBuilder("importCsv", jobRepository)
                .<StudentRecord, Student>chunk(10,platformTransactionManager)
                .reader(itemReader())
                .processor(processor())
                .writer(writer())
                .faultTolerant()
                .skipPolicy(fileVerificationSkipper())
                .taskExecutor(taskExecutor())
                .build();
    }

    // Création de l'étape 2  : catégoriser les student
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

    // Création de l'étape 3 : ecire dans Csv
    @Bean
    public Step filterAndExportStep() {
        return new StepBuilder("filterAndExport", jobRepository)
                .<StudentWithCategory, StudentWithCategory>chunk(10, platformTransactionManager)
                .reader(studentWithCategoryReader())
                .processor(studentCategoryFilterProcessor())
                .writer(csvWriter())
                .listener(listener)
                .taskExecutor(taskExecutor())
                .build();
    }


    //création d'un job
    @Bean
    public Job runJob() {
        return new JobBuilder("importStudents",jobRepository)
                .start(importStep())
                .next(categoryStep())
                .next(filterAndExportStep())
                .build();
    }

    // Pour pouvoir lancer les job en parallele
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }


    //lineMapper qui va associer une les lignes du fichier à la table student
    private LineMapper<StudentRecord> lineMapper() {
        DefaultLineMapper<StudentRecord> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstname", "lastname", "age");

        // Custom FieldSetMapper to handle null values
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSet -> {
            Long id = fieldSet.readLong("id");
            String firstname = fieldSet.readString("firstname");
            String lastname = fieldSet.readString("lastname");
            Integer age = fieldSet.readInt("age", 0); // Default age to 0 if null

            return new StudentRecord(id, firstname, lastname, age);
        });

        return lineMapper;
    }

}
