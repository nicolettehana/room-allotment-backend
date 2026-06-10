package sad.sras.models.auth;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sad.sras.models.appdata.Visitor;

@Builder
@Data
@Table(name = "current_users", schema = "auth")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUsers {
	
	@JsonProperty(access = Access.WRITE_ONLY)
	@Id
	@GeneratedValue
	public Integer id;
	
	@Column(name="username")
	public String username;
	
	@Column(name="token")
	public String token;
	
	@Column(name="ip_address")
	public String ipAddress;
	
	@Column(name="entrydate")
	public Date entrydate;
	
	public Integer session;

}
