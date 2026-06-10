package sad.sras.dto.master;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UnitRequestDTO {
	
	private LocalDate purchaseDate;
	
	private Long itemId;
	
	private Long subItemId;
	
	

}
