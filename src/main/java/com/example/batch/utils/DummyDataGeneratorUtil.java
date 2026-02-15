package com.example.batch.utils;

import com.example.batch.entity.mysql.MySQLEmployee;
import com.example.batch.entity.postgres.PostgresEmployee;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DummyDataGeneratorUtil {

    private static final String[] FIRST_NAMES = {"John", "Alice", "Bob", "Maria", "George", "Eva", "Michael", "Sophia", "James", "Helen"};
    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor"};

    public static List<MySQLEmployee> generateMySQLEmployees(int count) {
        List<MySQLEmployee> employees = new ArrayList<>();
        Random random = new Random();
        for (long i = 1; i <= count; i++) {
            MySQLEmployee emp = new MySQLEmployee();
            emp.setFirstName(FIRST_NAMES[random.nextInt(FIRST_NAMES.length)]);
            emp.setLastName(LAST_NAMES[random.nextInt(LAST_NAMES.length)]);
            employees.add(emp);
        }
        return employees;
    }

    public static List<PostgresEmployee> generatePostgresEmployees(int count) {
        List<PostgresEmployee> employees = new ArrayList<>();
        Random random = new Random();
        for (long i = 1; i <= count; i++) {
            PostgresEmployee emp = new PostgresEmployee();
            emp.setFirstName(FIRST_NAMES[random.nextInt(FIRST_NAMES.length)]);
            emp.setLastName(LAST_NAMES[random.nextInt(LAST_NAMES.length)]);
            employees.add(emp);
        }
        return employees;
    }
}
