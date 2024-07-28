
# Étape 3 : Filtrage et Exportation CSV

## Objectif

Lire la nouvelle table, filtrer les enregistrements pour ne prendre que ceux dont la colonne `cat` est "cinquantaine ou plus", puis insérer ces données filtrées dans un fichier CSV.

## Code

### BatchConfig.java

```java
@Bean
public RepositoryItemReader<StudentWithCategory> studentWithCategoryReader() {
    RepositoryItemReader<StudentWithCategory> reader = new RepositoryItemReader<>();
    reader.setRepository(studentWithCategoryRepository);
    reader.setMethodName("findAll");
    reader.setPageSize(10);
    reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
    return reader;
}

@Bean
public StudentCategoryFilterProcessor studentCategoryFilterProcessor() {
    return new StudentCategoryFilterProcessor();
}

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
```



### StudentCategoryFilterProcessor.java

```java
package com.mariohit.batch.config;

import com.mariohit.batch.studentWithCategory.StudentWithCategory;
import org.springframework.batch.item.ItemProcessor;

public class StudentCategoryFilterProcessor implements ItemProcessor<StudentWithCategory, StudentWithCategory> {

    @Override
    public StudentWithCategory process(StudentWithCategory item) throws Exception {
        if ("Cinquantaine et plus".equals(item.getCategory())) {
            return item;
        }
        return null;
    }
}

```
