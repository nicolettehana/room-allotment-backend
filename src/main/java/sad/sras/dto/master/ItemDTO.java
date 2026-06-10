package sad.sras.dto.master;

import java.util.List;

import lombok.Data;

@Data
public class ItemDTO {
	
	private String itemName;
	
    private String category;
    
    private Boolean hasSubItems;
    
    private List<String> subItems;

}
