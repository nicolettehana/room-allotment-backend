package sad.sras.repo.master;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sad.sras.models.master.RoomType;

public interface RoomTypeRepository  extends JpaRepository<RoomType, Long>{
	
	Optional<RoomType> findByOffice_OfficeCodeAndHallTrue(Long officeCode);

}
