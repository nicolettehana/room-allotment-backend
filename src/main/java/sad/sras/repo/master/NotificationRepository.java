package sad.sras.repo.master;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sad.sras.models.master.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>{
	
	Optional<Notification> findByMessageIdEquals(String messageId);

	List<Notification> findAll();

}
