package jagodzinska.eventsupport.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.file.FlatFileParseException;

import com.fasterxml.jackson.core.JsonProcessingException;

import jagodzinska.eventsupport.entities.Event;

public class EventSkipListener implements SkipListener<Event, Event> {

	private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);

	@Override
	public void onSkipInRead(Throwable t) {
		if (t instanceof FlatFileParseException) {
			FlatFileParseException ex = (FlatFileParseException) t;
			log.warn("Event was skipped due to: " + ex.getMessage() + " LINE:" + ex.getLineNumber());
		} else if (t instanceof JsonProcessingException) {
			JsonProcessingException ex = (JsonProcessingException) t;
			log.warn("Event was skipped due to: " + ex.getMessage() + " LOCATION: " + ex.getLocation());
		} else {
			log.warn("Event was skipped due to: " + t.getMessage());
		}

	}

	@Override
	public void onSkipInWrite(Event item, Throwable t) {
		log.warn("Event " + item + " was skipped due to: " + t.getMessage());

	}

	@Override
	public void onSkipInProcess(Event item, Throwable t) {
		log.warn("Event " + item + " was skipped due to: " + t.getMessage());

	}

}