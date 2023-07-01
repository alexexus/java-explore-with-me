package ru.practicum.yandex.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.practicum.yandex.stats.client", "ru.practicum.yandex.main"})
public class MainServer {

    public static void main(String[] args) {
        SpringApplication.run(MainServer.class, args);
    }
}
