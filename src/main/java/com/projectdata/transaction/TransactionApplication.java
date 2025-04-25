package com.projectdata.transaction;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TransactionApplication {
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("MONGO_URI", dotenv.get("MONGO_URI"));
        System.setProperty("MONGO_DB", dotenv.get("MONGO_DB"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));

        SpringApplication.run(TransactionApplication.class, args);

        System.out.println("...Running....");
    }
}
