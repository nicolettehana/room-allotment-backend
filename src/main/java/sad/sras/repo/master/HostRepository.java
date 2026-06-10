package sad.sras.repo.master;

import org.springframework.data.jpa.repository.JpaRepository;

import sad.sras.models.master.Host;

public interface HostRepository  extends JpaRepository<Host, Long>{

}
