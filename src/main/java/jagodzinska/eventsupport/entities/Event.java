package jagodzinska.eventsupport.entities;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "events")
public class Event {

	@Id
	@NotEmpty(message = "Id must be not null/empty")
	private String id;

	@Transient
	private State state;

	@Transient
	@Positive
	private Long timestamp;

	private Long duration;

	private String type;

	private String host;

	private Boolean alert;

	@JsonCreator
	public Event(@JsonProperty(value = "id", required = true) String id,
			@JsonProperty(value = "state", required = true) State state,
			@JsonProperty(value = "timestamp", required = true) Long timestamp,
			@JsonProperty(value = "type") String type, @JsonProperty(value = "host") String host) {
		super();
		this.id = id;
		this.state = state;
		this.timestamp = timestamp;
		this.type = type;
		this.host = host;
	}

}