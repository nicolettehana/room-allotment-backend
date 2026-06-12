package sad.sras.repo.master;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sad.sras.models.master.Office;

public interface OfficeRepository extends JpaRepository<Office, Integer>{
	
	Optional<Office> findByOfficeCode(Long officeCode);
	
	Optional<Office> findByOfficeName(String officeName);

}
