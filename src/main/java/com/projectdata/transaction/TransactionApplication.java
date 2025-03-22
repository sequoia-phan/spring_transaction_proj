package com.projectdata.transaction;

import com.projectdata.transaction.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TransactionApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TransactionApplication.class, args);
        UserRepository userRepository = context.getBean(UserRepository.class);

        System.out.println("...Running....");
    }
}
