package jagodzinska.eventsupport.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.SkipListenerSupport;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.Validator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jagodzinska.eventsupport.entities.Event;
import jagodzinska.eventsupport.repositories.EventRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public EventRepository eventRepository;

	@Autowired
	private ApplicationArguments applicationArguments;

	@Autowired
	public JobCompletionListener listener;

	private int skipLimit = 1;

	@Bean
	public Job createEventJob(Step processEvents) {
		return jobBuilderFactory.get("createEventJob").incrementer(new RunIdIncrementer()).listener(listener)
				.flow(processEvents).end().build();
	}

	@Bean
	public Step processEvents(RepositoryItemWriter<Event> writer) {
		return stepBuilderFactory.get("processEvents").<Event, Event>chunk(100).reader(reader()).processor(processor())
				.writer(writer).faultTolerant().skip(Exception.class).skipLimit(skipLimit).listener(eventSkipListener())
				.build();
	}

	@Bean
	public FlatFileItemReader<Event> reader() {
		FlatFileItemReader<Event> reader = new FlatFileItemReader<Event>();
		EventJsonLineMapper lineMapper = new EventJsonLineMapper();
		reader.setResource(sourceOfData());
		reader.setLineMapper(lineMapper);
		return reader;
	}

	@Bean
	public EventItemProcessor processor() {
		return new EventItemProcessor();
	}

	@Bean
	public RepositoryItemWriter<Event> writer(DataSource dataSource) {
		RepositoryItemWriter<Event> writer = new RepositoryItemWriter<Event>();
		writer.setRepository(eventRepository);
		writer.setMethodName("save");
		return writer;
	}

	@Bean
	public EventSkipListener eventSkipListener() {
		return new EventSkipListener();
	}

	@Bean
	public Resource sourceOfData() {
		String[] sourceArgs = applicationArguments.getSourceArgs();
		Resource file = new FileSystemResource(sourceArgs[0]);
		return file;
	}

	/*
	 * @Bean public LocalValidatorFactoryBean validator() { return new
	 * LocalValidatorFactoryBean(); }
	 */

}
