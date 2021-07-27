package com.bmc.elasticsearchloganalzyer;

import com.bmc.elasticsearchloganalzyer.elasticsearch.ElasticClient;
import com.bmc.elasticsearchloganalzyer.input.InputReader;
import com.bmc.elasticsearchloganalzyer.model.CsvLine;
import com.bmc.elasticsearchloganalzyer.model.Parameter;
import org.elasticsearch.ElasticsearchStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class ElasticsearchloganalzyerApplication {

	static LogAnalyzer logAnalyzer;
    static InputReader inputReader;
    static ElasticClient elasticClient;
	static public ConfigurableApplicationContext context;
	static private Class[] csvClasses = new Class[]{Parameter.class};

	public static void main(String[] args) {
		context = SpringApplication.run(ElasticsearchloganalzyerApplication.class, args);
		logAnalyzer = context.getBean(LogAnalyzer.class);
        inputReader = context.getBean(InputReader.class);
		elasticClient = context.getBean(ElasticClient.class);
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to HCU Analysis Tool :)\nPlease Enter A Descriptive Name For The HCU:");
        String HCUName = scan.nextLine();
		System.out.println("\nPlease Enter the full path to the HCU:");
		String hcuPath = scan.nextLine();
		try {
			elasticClient.createIndices(HCUName);
		}catch (ElasticsearchStatusException ex) {
			System.out.println("\nHCU Name already exist, Overwrite? (y/n):");
			String overwrite = scan.nextLine();
			if (overwrite.toLowerCase().equals("y"))
			{
				elasticClient.deleteIndices(HCUName);
				elasticClient.createIndices(HCUName);
			}else {
				SpringApplication.exit(context, () -> 0);
			}
		}
		try {
			inputReader.createLogFilesList(hcuPath);
			logAnalyzer.parseMetrics();
			logAnalyzer.analyze();
			for (Class c: csvClasses) {
				logAnalyzer.csvAnalyze(c);
			}

			SpringApplication.exit(context, () -> 0);
		}catch (Exception ex) {
			System.out.println("\nDirectory can't be analyzed:" + ex.getMessage());
			SpringApplication.exit(context, () -> 0);
		}
	}
}
