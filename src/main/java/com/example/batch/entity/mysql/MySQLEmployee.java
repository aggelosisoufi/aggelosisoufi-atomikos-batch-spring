package com.example.batch.entity.mysql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "employee_mysql")
public class MySQLEmployee {
    @Id
    @TableGenerator(
            name = "emp_gen",
            table = "id_gen",
            pkColumnName = "gen_name",
            valueColumnName = "gen_val",
            pkColumnValue = "emp_id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "emp_gen")
    private Long id;
    private String firstName;
    private String lastName;
}