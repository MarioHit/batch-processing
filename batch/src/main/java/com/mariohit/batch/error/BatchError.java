package com.mariohit.batch.error;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Entity
@Table(name = "batch_errors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchError {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobName;
    private String stepName;
    private Integer lineNumber;
    private String input;
    private String errorMessage;
    private LocalDateTime timestamp;
}
