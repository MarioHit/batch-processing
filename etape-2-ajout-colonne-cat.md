# Étape 2 : Ajout de la colonne "cat"

## Objectif

Ajouter une nouvelle étape pour écrire les données dans une nouvelle table avec une colonne supplémentaire `cat` qui indique la tranche d'âge (dizaine, vingtaine, etc.).

## Code

### BatchConfig.java

```java
@Bean
public RepositoryItemReader<Student> studentDatabaseItemReader() {
    RepositoryItemReader<Student> reader = new RepositoryItemReader<>();
    reader.setRepository(studentRepository);
    reader.setMethodName("findAll");
    reader.setPageSize(10);
    reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
    return reader;
}

@Bean
public StudentCategoryProcessor studentCategoryProcessor() {
    return new StudentCategoryProcessor();
}

@Bean
public RepositoryItemWriter<StudentWithCategory> studentCategoryWriter() {
    RepositoryItemWriter<StudentWithCategory> writer = new RepositoryItemWriter<>();
    writer.setRepository(studentWithCategoryRepository);
    writer.setMethodName("save");
    return writer;
}

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
```


### StudentCategoryProcessor.java

```java
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
        Integer age = item.getAge();
        switch (age / 10) {
            case 0:
            case 1:
                category = "Dizaine";
                break;
            case 2:
                category = "Vingtaine";
                break;
            case 3:
                category = "Trentaine";
                break;
            case 4:
                category = "Quarantaine";
                break;
            default:
                category = "Cinquantaine et plus";
                break;
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

```

### StudentWithCategory.java

```java
package com.mariohit.batch.studentWithCategory;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class StudentWithCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    private Integer age;
    private String category;

    public StudentWithCategory() {
    }

    public StudentWithCategory(Long id, String firstname, String lastname, Integer age, String category) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.category = category;
    }

    // getters and setters
}

```


## Navigation

- [Retour au README principal](README.md)
- [Étape 1 : Importation CSV](etape-1-importation-csv.md)
- [Étape 2 : Ajout de la colonne "cat"](etape-2-ajout-colonne-cat.md)
- [Étape 3 : Filtrage et Exportation CSV](etape-3-filtrage-exportation-csv.md)


