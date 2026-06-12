package sad.sras.models.appdata;

import java.time.LocalDate;
import java.time.LocalTime;

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
@Table(name = "hall_allotment", schema = "appdata")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HallAllotment {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "office_code", nullable = false)
    private Long officeCode;

    @Column(name = "hall_id", nullable = false)
    private Long hallId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "hall_status")
    private String hallStatus;

    @Column(name = "booking_id")
    private String bookingId;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

}
