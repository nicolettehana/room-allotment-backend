package sad.sras.controller.master;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import sad.sras.exception.InternalServerError;
import sad.sras.exception.UnauthorizedException;
import sad.sras.models.auth.User;
import sad.sras.models.master.Menu;
import sad.sras.services.master.MenuService;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {
	
	private final MenuService menuService;

	@GetMapping
	public List<Menu> getMenu(HttpServletRequest request, HttpServletResponse response , @AuthenticationPrincipal User user) throws IOException {
		try {
			return menuService.getMenusByRole(user.getRole().toString());
		} catch (UnauthorizedException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalServerError("Unable to fetch menu", ex);
		}
	}
}
