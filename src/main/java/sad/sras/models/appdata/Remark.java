package sad.sras.models.appdata;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "remark", schema = "appdata")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Remark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private String bookingId;

    @Column(name = "remark", nullable = false)
    private String remark;

    @Column(name = "acition_code", nullable = false)
    private String actionCode;

    @Column(name = "by_user", nullable = false)
    private String user;
}
