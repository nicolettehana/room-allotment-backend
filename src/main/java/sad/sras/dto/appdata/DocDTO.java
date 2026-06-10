package sad.sras.dto.appdata;

import java.util.UUID;

import lombok.Data;

@Data
public class DocDTO {

	private String documentType;
	
    private UUID documentCode;
}
