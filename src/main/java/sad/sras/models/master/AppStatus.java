package sad.sras.models.master;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "application_status", schema = "master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppStatus {
	
	@Id
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String status;

}
