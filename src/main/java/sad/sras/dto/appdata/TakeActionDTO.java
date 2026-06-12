package sad.sras.dto.appdata;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

import lombok.Data;

@Data
public class TakeActionDTO {
	
	@Required
	public String action;
	
	@Required
	public String bookingId;
	
	public String remark;

}
