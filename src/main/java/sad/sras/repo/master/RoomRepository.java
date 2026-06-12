package sad.sras.repo.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sad.sras.models.master.Room;

public interface RoomRepository extends JpaRepository<Room, Long>{
	
	List<Room> findByRoomType_Office_OfficeCodeAndRoomType_HallTrueOrderBySortOrderAsc(Long officeCode);

}
