package com.example.batch.entity.postgres;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "employee_pg")
public class PostgresEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pg_emp_seq")
    @SequenceGenerator(
            name = "pg_emp_seq",
            sequenceName = "pg_emp_seq"
    )
    private Long id;
    private String firstName;
    private String lastName;
}