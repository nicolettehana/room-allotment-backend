package sad.sras.models.master;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "notifications", schema = "master")
@Data
public class Notification {
	
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
	private Long id;

	@Column(name="template_id")
	private String templateId;

	private String username;

	private String message;

	private String pin;

	private String signature;

	@Column(name="entity_id")
	private String entityId;

	@Column(name="message_id")
	private String messageId;

}
