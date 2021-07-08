package jagodzinska.eventsupport.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import jagodzinska.eventsupport.entities.Event;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
}
