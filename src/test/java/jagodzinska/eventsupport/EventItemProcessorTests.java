package jagodzinska.eventsupport;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jagodzinska.eventsupport.batch.BatchConfiguration;
import jagodzinska.eventsupport.batch.JobCompletionListener;
import jagodzinska.eventsupport.batch.EventItemProcessor;
import jagodzinska.eventsupport.entities.Event;
import jagodzinska.eventsupport.entities.State;
import jagodzinska.eventsupport.repositories.EventRepository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.sql.DataSource;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class EventItemProcessorTests {
	@Autowired
	private static Validator validator;

	private EventItemProcessor eventItemProcessor;

	@BeforeAll
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();

	}

	@BeforeEach
	public void setUpProcessor() {
		eventItemProcessor = new EventItemProcessor();
		eventItemProcessor.setValidator(validator);
	}

	@Test
	public void whenFirstSeen_thenReturnNull() throws Exception {

		Event event1 = new Event("id1", State.STARTED, 1000L, null, null, null, null);
		Event event2 = new Event("id2", State.FINISHED, 1001L, null, null, null, null);

		assertNull(eventItemProcessor.process(event1));
		assertNull(eventItemProcessor.process(event2));

	}

	@Test
	public void whenFirstSeen_thenIncreasedMapSize() throws Exception {

		Event event1 = new Event("id1", State.STARTED, 1000L, null, null, null, null);
		Event event2 = new Event("id2", State.FINISHED, 1001L, null, null, null, null);

		eventItemProcessor.process(event1);
		eventItemProcessor.process(event2);

		assertEquals(eventItemProcessor.getSeenEventsSize(), 2);

	}

	@Test
	public void whenSecondSeen_returnNotNullEvent() throws Exception {
		Event event1 = new Event("id1", State.STARTED, 1000L, null, null, null, null);
		Event event3 = new Event("id1", State.FINISHED, 1003L, null, null, null, null);

		eventItemProcessor.process(event1);
		Event resultEvent = eventItemProcessor.process(event3);

		assertNotNull(resultEvent);

	}

	@Test
	public void whenSecondSeen_returnEventWithDuration() throws Exception {
		Event event1 = new Event("id1", State.STARTED, 1000L, null, null, null, null);
		Event event3 = new Event("id1", State.FINISHED, 1003L, null, null, null, null);

		eventItemProcessor.process(event1);
		Event resultEvent = eventItemProcessor.process(event3);

		assertEquals(resultEvent.getDuration(), 3);

	}

	@Test
	public void whenIdEmpty_ThrowException() throws Exception {
		Event event5 = new Event("", State.STARTED, 1000L, null, null, null, null);

		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			eventItemProcessor.process(event5);
		});

		String expectedMessage = "Id";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));

	}

	@Test
	public void whenIdNull_ThrowException() throws Exception {
		Event event6 = new Event(null, State.FINISHED, 1001L, null, null, null, null);

		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			eventItemProcessor.process(event6);
		});

		String expectedMessage = "Id";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));

	}

	@Test
	public void whenTimestampNegative_ThrowException() throws Exception {
		Event event9 = new Event("id2", State.STARTED, -100L, null, null, null, null);

		Exception exception = assertThrows(ConstraintViolationException.class, () -> {
			eventItemProcessor.process(event9);
		});

		String expectedMessage = "timestamp";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));

	}

	@Test
	public void lala() throws Exception {

		Event event9 = new Event("id2", State.STARTED, -100L, null, null, null, null);

		validator.validate(event9);

	}
}
