package account.repositories;

import account.models.LogEvent;
import org.springframework.data.repository.CrudRepository;

public interface EventsRepository extends CrudRepository<LogEvent, Long> {
}
