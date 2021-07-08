package jagodzinska.eventsupport.batch;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.batch.core.*;
import javax.validation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jagodzinska.eventsupport.entities.Event;

public class EventItemProcessor implements ItemProcessor<Event, Event> {
	@Autowired
	private Validator validator;
	private static final Logger log = LoggerFactory.getLogger(EventItemProcessor.class);
	private Map<String, Event> seenEvents = new HashMap<String, Event>();

	@Override
	public Event process(final Event event) throws ConstraintViolationException {

		Set<ConstraintViolation<Event>> constraintViolations = validator.validate(event);
		if (constraintViolations.isEmpty()) {

			Event value = seenEvents.get(event.getId());
			if (value != null) {
				Long duration = Math.abs(event.getTimestamp() - value.getTimestamp());
				event.setDuration(duration);
				if (duration > 4) {
					event.setAlert(true);
				}
				log.debug("Processing second part of event data " + event + " Calulating duration: "
						+ event.getTimestamp() + " - " + value.getTimestamp());
				return event;
			} else {
				log.debug("Processing first part of event data " + event);
				seenEvents.put(event.getId(), event);
				return null;
			}
		} else {
			throw new ConstraintViolationException(constraintViolations);
		}

	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public int getSeenEventsSize() {
		return seenEvents.size();
	}

}
