package sad.storereg.repo.master;

import org.springframework.data.jpa.repository.JpaRepository;

import sad.storereg.models.master.Host;

public interface HostRepository  extends JpaRepository<Host, Long>{

}
