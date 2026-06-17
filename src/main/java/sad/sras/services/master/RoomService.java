package sad.sras.services.master;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sad.sras.dto.appdata.OfficeRooms;
import sad.sras.dto.appdata.RoomsDTO;
import sad.sras.dto.master.OfficeRequest;
import sad.sras.models.master.Office;
import sad.sras.models.master.Room;
import sad.sras.models.master.RoomType;
import sad.sras.repo.master.RoomRepository;
import sad.sras.repo.master.RoomTypeRepository;

@Service
@RequiredArgsConstructor
public class RoomService {
	
	private final RoomRepository roomRepo;
	private final RoomTypeRepository roomTypeRepo;
	private final OfficesService officesService;
	
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
	
	public List<OfficeRooms> getHallsOfficeWise() {
		List<Office> offices = officesService.getOffices();
		
		return offices.stream()
        .map(office-> {
        	OfficeRooms dto = new OfficeRooms();
    		dto.setOfficeName(office.getOfficeName());
    		dto.setRooms(getHalls(office.getOfficeCode()));
            return dto;
        })
        .collect(Collectors.toList());
		
	}
	
	public String createHall(OfficeRequest request) {
		try {
			Office office = officesService.findOffice(request.getOfficeCode().longValue());
			 
			// Create hall type
		    RoomType hallType = RoomType.builder()
		            .name(request.getHallName())
		            .office(office)
		            .hall(true)
		            .sortOrder(5)
		            .build();

		    hallType = roomTypeRepo.save(hallType);

		    // Create room entry for the hall
		    Room room = Room.builder()
		            .name(request.getHallName())
		            .roomType(hallType)
		            .sortOrder(5)
		            .build();

		    roomRepo.save(room);

		    return "Hall created successfully";
		}catch(Exception ex) {
			throw ex;
		}
    }

}
