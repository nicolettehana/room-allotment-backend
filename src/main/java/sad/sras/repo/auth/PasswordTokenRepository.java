package sad.sras.repo.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sad.sras.models.auth.PasswordResetToken;

public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Integer>{
	
	Optional<PasswordResetToken> findByToken(String token);

}
