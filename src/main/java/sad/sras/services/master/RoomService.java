package sad.sras.services.master;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sad.sras.models.master.Room;
import sad.sras.repo.master.RoomRepository;

@Service
@RequiredArgsConstructor
public class RoomService {
	
	private final RoomRepository roomRepo;
	
	public List<Room> getHalls(Long officeCode) {
        return roomRepo.findByRoomType_Office_OfficeCodeAndRoomType_HallTrueOrderBySortOrderAsc(officeCode);   
    }

}
