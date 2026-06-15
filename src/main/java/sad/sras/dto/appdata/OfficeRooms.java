package sad.sras.dto.appdata;

import java.util.List;

import lombok.Data;

@Data
public class OfficeRooms {
	
	private String officeName;
	
	private List<RoomsDTO> rooms; 

}
