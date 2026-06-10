package sad.sras.repo.master;

import org.springframework.data.jpa.repository.JpaRepository;

import sad.sras.models.master.Room;

public interface RoomRepository extends JpaRepository<Room, Long>{

}
