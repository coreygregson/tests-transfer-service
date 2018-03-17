package com.ingenico.transferservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@SpringBootApplication (exclude = {DataSourceAutoConfiguration.class})  //DONT DELETE unless we are using a database type datastore.
/**
 * 
 * @author cgregson
 * Spring Boot wrapper for service.
 */
public class App 
{
    public static void main( String[] args ) {
    	SpringApplication.run(App.class, args);
    }
}
