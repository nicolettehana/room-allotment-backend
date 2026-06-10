package sad.sras.services.master;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sad.sras.models.master.Menu;
import sad.sras.repo.master.MenuRepository;

@Service
@RequiredArgsConstructor
public class MenuService {
	
	private final MenuRepository menuRepo;

	public List<Menu> getMenusByRole(String role) {
        return menuRepo.findByRole(role);
    }
}
