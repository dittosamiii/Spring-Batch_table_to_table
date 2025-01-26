package com.springboot.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class SpringBatchConfiguration {

	private JobRepository jobRepository;
	private DataSource dataSource;
	private PlatformTransactionManager platformTransactionManager;

	@Bean
	JdbcCursorItemReader<String> reader() {
		return new JdbcCursorItemReaderBuilder<String>().dataSource(dataSource).sql("SELECT name FROM input_names")
				.rowMapper((rs, _) -> rs.getString("name")).name("jdbcReader").build();
	}

	@Bean
	ItemProcessor<String, String> processor() {
		return name -> "hello " + name + " !";
	}

	@Bean
	JdbcBatchItemWriter<String> writer() {
		return new JdbcBatchItemWriterBuilder<String>().dataSource(dataSource)
				.sql("INSERT INTO output_greetings (greeting) VALUES (:greeting)")
				.itemSqlParameterSourceProvider(greeting -> {
					MapSqlParameterSource paramSource = new MapSqlParameterSource();
					paramSource.addValue("greeting", greeting);
					return paramSource;
				}).build();
	}

	@Bean
	Step step() {
		return new StepBuilder("step", jobRepository).<String, String>chunk(10, platformTransactionManager)
				.reader(reader()).processor(processor()).writer(writer()).build();
	}
	
	@Bean
	Job job() {
		return new JobBuilder("job", jobRepository).start(step()).build();
	}

}
