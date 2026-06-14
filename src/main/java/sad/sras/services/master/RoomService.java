package sad.sras.services.master;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sad.sras.dto.appdata.RoomsDTO;
import sad.sras.models.master.Room;
import sad.sras.repo.master.RoomRepository;

@Service
@RequiredArgsConstructor
public class RoomService {
	
	private final RoomRepository roomRepo;
	
	public List<RoomsDTO> getHalls(Long officeCode) {
		List<Room> rooms = null;
		if(officeCode==-1)
			rooms = roomRepo.findByRoomType_HallTrueOrderBySortOrderAsc();
		else
			rooms = roomRepo.findByRoomType_Office_OfficeCodeAndRoomType_HallTrueOrderBySortOrderAsc(officeCode); 
        
        return rooms.stream()
	            .map(room-> {
	                RoomsDTO dto = new RoomsDTO();

	                dto.setId(room.getId());
	                dto.setName(room.getName());
	                dto.setOffice(room.getRoomType().getOffice().getOfficeName());	     
	                return dto;
	            })
	            .collect(Collectors.toList());
    }

}
