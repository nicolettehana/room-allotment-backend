package sad.sras.repo.master;

import org.springframework.data.jpa.repository.JpaRepository;

import sad.sras.models.master.AppStatus;

public interface AppStatusRepository extends JpaRepository<AppStatus, Long>{

}
