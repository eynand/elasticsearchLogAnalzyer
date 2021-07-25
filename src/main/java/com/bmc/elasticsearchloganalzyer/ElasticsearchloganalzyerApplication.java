package com.bmc.elasticsearchloganalzyer;

import com.bmc.elasticsearchloganalzyer.elasticsearch.ElasticClient;
import com.bmc.elasticsearchloganalzyer.input.InputReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class ElasticsearchloganalzyerApplication {

	static LogAnalyzer logAnalyzer;
    static InputReader inputReader;
    static ElasticClient elasticClient;
	static public ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(ElasticsearchloganalzyerApplication.class, args);
		logAnalyzer = context.getBean(LogAnalyzer.class);
        inputReader = context.getBean(InputReader.class);
		elasticClient = context.getBean(ElasticClient.class);
        Scanner myObj = new Scanner(System.in);
        System.out.println("Welcome to HCU Analysis Tool :), Please Enter A Descriptive Name For The HCU:");
        String HCUName = myObj.nextLine();
		System.out.println("Welcome to HCU Analysis Tool :), Please Enter the full path to the HCU:");
		String hcuPath = myObj.nextLine();
		elasticClient.createIndices(HCUName);
        inputReader.createLogFilesList(hcuPath);
		logAnalyzer.analyze();
		SpringApplication.exit(context, () -> 0);
	}

}
