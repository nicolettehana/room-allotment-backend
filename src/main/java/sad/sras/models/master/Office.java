package sad.sras.models.master;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "offices", schema = "master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Office {
	
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "office_code", nullable = false)
    private Integer officeCode;

    @Column(name = "office_name", nullable = false, length = 255)
    private String officeName;



}
