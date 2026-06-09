package sad.storereg.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import sad.storereg.models.auth.Role;

@Data
@Builder
public class RegisterRequestDTO {

	@NotBlank(message = "Name (name) is required")
	private String name;

	//@NotBlank(message = "Designation (designation) is required")
	private String designation;

	//@NotBlank(message = "Email (username) is required")
	private String username;

	@NotBlank(message = "Password (password) is required")
	@Size(min = 8, message = "Password should be at least 8 characters long.")
	private String password;

	@NotNull(message = "Role (role) is required")
	private Role role;

	private String housename;
	
	private String mobileNo;
	
	private String token;
	
	private String email;
}

