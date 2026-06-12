package sad.sras.repo.appdata;

import org.springframework.data.jpa.repository.JpaRepository;

import sad.sras.models.appdata.Remark;

public interface RemarkRepository extends JpaRepository<Remark, Long>{

}
