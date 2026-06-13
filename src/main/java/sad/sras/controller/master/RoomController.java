package sad.sras.controller.master;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sad.sras.dto.appdata.RoomsDTO;
import sad.sras.exception.InternalServerError;
import sad.sras.exception.UnauthorizedException;
import sad.sras.models.master.Room;
import sad.sras.services.master.RoomService;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;
	
	@GetMapping("/hall")
	public List<RoomsDTO> getHalls(@RequestParam Long officeCode) throws IOException {
		try {
			return roomService.getHalls(officeCode);
		} catch (UnauthorizedException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalServerError("Unable to fetch menu", ex);
		}
	}
	
	
}
