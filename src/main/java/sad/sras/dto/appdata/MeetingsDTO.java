package sad.sras.dto.appdata;

import java.time.LocalTime;

import lombok.Data;

@Data
public class MeetingsDTO {
	
	private Long hallId;
	
	private String department;
	
	private String purpose;
	
	private String start;
	
	private String end;
	
	private String color= "#3182CE";

}
