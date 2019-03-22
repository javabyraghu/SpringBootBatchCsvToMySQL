package com.app.raghu.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.app.raghu.model.Product;
import com.app.raghu.processor.ProductProcessor;

@EnableBatchProcessing
@Configuration
public class BatchConfig {

	@Autowired
	private JobBuilderFactory jf;
	
	@Bean
	public Job jobA() {
		return jf.get("jobA").incrementer(new RunIdIncrementer()).start(stepA()).build();
	}
	
	@Autowired
	private StepBuilderFactory sf;
	
	@Bean
	public Step stepA() {
		return sf.get("stepA").<Product,Product>chunk(3).reader(reader()).processor(processor()).writer(writer()).build();
	}
	
	@Bean
	public FlatFileItemReader<Product> reader(){
		FlatFileItemReader<Product> reader=new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource("product.csv"));
		reader.setLineMapper(new DefaultLineMapper<Product>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames("prodId","prodCode","prodCost");
			}});
			setFieldSetMapper(new BeanWrapperFieldSetMapper<Product>() {{
				setTargetType(Product.class);

			}});
		}});
		return reader;
	}
	@Bean
	public ItemProcessor<Product, Product> processor(){
		return new ProductProcessor();
	}
	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource ds=new DriverManagerDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://localhost:3306/test");
		ds.setUsername("root");
		ds.setPassword("root");
		return ds;
	}
	
	
	
	@Bean
	public JdbcBatchItemWriter<Product> writer(){
		JdbcBatchItemWriter<Product> writer=new JdbcBatchItemWriter<>();
		writer.setDataSource(dataSource());
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Product>());
		writer.setSql("INSERT INTO PRODSTAB (PID,PNAME,PCOST,GST,DISC) VALUES (:prodId,:prodCode,:prodCost,:gst,:disc)");
		return writer;
	}
	
	
	
}
