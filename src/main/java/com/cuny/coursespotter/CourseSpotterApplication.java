package com.cuny.coursespotter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class CourseSpotterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseSpotterApplication.class, args);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable r = () -> {
            try {
                System.out.println("[DBWorker] Executing at " + Instant.now());
                DBWorker.checkData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };


        executor.scheduleAtFixedRate(r, 0L, 60L, TimeUnit.SECONDS);

    }

}
