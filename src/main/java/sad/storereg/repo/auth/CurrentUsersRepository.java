package sad.storereg.repo.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sad.storereg.models.auth.CurrentUsers;

public interface CurrentUsersRepository extends JpaRepository<CurrentUsers, Integer>{
	
	Optional<CurrentUsers> findByUsername(String username);

}
