package com.mariohit.batch.studentWithCategory;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
@AllArgsConstructor
public class StudentWithCategory {
    @Id
    @GeneratedValue
    private Long id;
    private String firstname;
    private String lastname;
    private int age;
    private String cat;

}
