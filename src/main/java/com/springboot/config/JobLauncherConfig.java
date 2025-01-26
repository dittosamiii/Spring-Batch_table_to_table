package com.springboot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobLauncherConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobLauncherConfig.class);

	@Bean
	CommandLineRunner jobRunner(JobLauncher jobLauncher, Job job) {
		return _ -> {
			try {
				LOGGER.info("Job is Starting......");
				JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
						.toJobParameters();

				JobExecution run = jobLauncher.run(job, jobParameters);
				LOGGER.info("Job Execution Status " + run.getStatus());
				LOGGER.info("Job is completed successfully!");
			} catch (Exception e) {
				LOGGER.error("Job failed to execute" + e.getMessage());
			}

		};
	}
}
