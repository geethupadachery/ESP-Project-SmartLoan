package com.sjsu.esp.technocrats.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;



@SpringBootApplication
@ComponentScan({ "com.sjsu.esp.technocrats.*" })
public class SmartLoanApp 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        SpringApplication.run(SmartLoanApp.class, args);
    }
}
