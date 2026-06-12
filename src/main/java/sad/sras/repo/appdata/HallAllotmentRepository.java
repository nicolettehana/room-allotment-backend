package sad.sras.repo.appdata;

import org.springframework.data.jpa.repository.JpaRepository;

import sad.sras.models.appdata.HallAllotment;

public interface HallAllotmentRepository extends JpaRepository<HallAllotment, Long>{

}
