# Étape 1 : Importation CSV

## Objectif

- Lire des données depuis un fichier CSV.
- Transformer les noms des étudiants en majuscules.
- Enregistrer les données transformées dans la table `student`.

## Code

### BatchConfig.java

```java
@Bean
public FlatFileItemReader<StudentRecord> itemReader() {
    FlatFileItemReader<StudentRecord> itemReader = new FlatFileItemReader<>();
    itemReader.setResource(new FileSystemResource(csvFilePath));
    itemReader.setName("csvReader");
    itemReader.setLinesToSkip(1);
    itemReader.setLineMapper(lineMapper());
    return itemReader;
}

@Bean
public StudentProcessor processor() {
    return new StudentProcessor();
}

@Bean
public RepositoryItemWriter<Student> writer() {
    RepositoryItemWriter<Student> writer = new RepositoryItemWriter<>();
    writer.setRepository(studentRepository);
    writer.setMethodName("save");
    return writer;
}

@Bean
public Step importStep() {
    return new StepBuilder("importCsv", jobRepository)
            .<StudentRecord, Student>chunk(10, platformTransactionManager)
            .reader(itemReader())
            .processor(processor())
            .writer(writer())
            .taskExecutor(taskExecutor())
            .build();
}
```

### StudentProcessor.java

```java
package com.mariohit.batch.config;

import com.mariohit.batch.student.Student;
import com.mariohit.batch.student.StudentRecord;
import org.springframework.batch.item.ItemProcessor;

public class StudentProcessor implements ItemProcessor<StudentRecord, Student> {

    @Override
    public Student process(StudentRecord studentRecord) throws Exception {
        if (studentRecord.age() == null) {
            throw new IllegalArgumentException("Age cannot be null");
        }
        return new Student(studentRecord.id(), studentRecord.firstname(), studentRecord.lastname().toUpperCase(), studentRecord.age());
    }
}
```

### StudentRecord.java

```java
package com.mariohit.batch.student;

public record StudentRecord(Long id, String firstname, String lastname, Integer age) {
}

```

### LineMapper Configuration

```java
private LineMapper<StudentRecord> lineMapper() {
    DefaultLineMapper<StudentRecord> lineMapper = new DefaultLineMapper<>();

    DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
    lineTokenizer.setDelimiter(",");
    lineTokenizer.setStrict(false);
    lineTokenizer.setNames("id", "firstname", "lastname", "age");

    BeanWrapperFieldSetMapper<StudentRecord> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(StudentRecord.class);

    lineMapper.setLineTokenizer(lineTokenizer);
    lineMapper.setFieldSetMapper(fieldSetMapper);

    return lineMapper;
}

```








