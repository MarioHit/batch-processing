package com.mariohit.batch.studentWithCategory;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class StudentWithCategory {
    @Id
    @GeneratedValue
    private Long id;
    private String firstname;
    private String lastname;
    private int age;
    private String cat;
}
