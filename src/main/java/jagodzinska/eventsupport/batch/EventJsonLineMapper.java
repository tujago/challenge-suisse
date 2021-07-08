package jagodzinska.eventsupport.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jagodzinska.eventsupport.entities.Event;

import org.springframework.batch.item.file.LineMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

public class EventJsonLineMapper implements LineMapper<Event> {

	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * Interpret the line as a Json object and create a Event Entity from it.
	 * 
	 * @throws JsonProcessingException
	 */
	@Override
	public Event mapLine(String line, int lineNumber) throws JsonProcessingException {
		return mapper.readValue(line, Event.class);
	}

}
