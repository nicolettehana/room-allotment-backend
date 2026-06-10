package sad.sras.controller.master;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import sad.sras.annotations.Auditable;
import sad.sras.dto.master.OfficeRequest;
import sad.sras.exception.InternalServerError;
import sad.sras.exception.UnauthorizedException;
import sad.sras.models.auth.User;
import sad.sras.models.master.Category;
import sad.sras.models.master.Menu;
import sad.sras.models.master.Office;
import sad.sras.services.master.OfficesService;

@RestController
@RequestMapping("/offices")
@RequiredArgsConstructor
public class OfficesController {
	
	private final OfficesService officeService;
	
	@GetMapping
	public List<Office> getMenu(HttpServletRequest request, HttpServletResponse response , @AuthenticationPrincipal User user) throws IOException {
		try {
			return officeService.getOffices();
		} catch (UnauthorizedException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalServerError("Unable to fetch menu", ex);
		}
	}
	
	@Auditable
	@PostMapping
    public ResponseEntity<?> createOffice(@RequestBody OfficeRequest request) {
		try {
			System.out.println("Request: "+request);
			return ResponseEntity.ok(officeService.createOffice(request));
			//return ResponseEntity.ok("ok");
		} catch (UnauthorizedException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalServerError("Unable to add office", ex);
		}        
    }

}
