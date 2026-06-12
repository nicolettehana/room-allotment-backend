package sad.sras.models.appdata;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hall_booking", schema = "appdata")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HallBooking {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "department", nullable = false)
    private String department;

    @Column(name = "purpose", nullable = false)
    private String purpose;

    @Column(name = "meeting_date", nullable = false)
    private LocalDate meetingDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "no_of_attendees")
    private Integer noOfAttendees;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "contact_designation")
    private String contactDesignation;

    @Column(name = "contact_mobile_no")
    private String contactMobileNo;
    
    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    @Column(name = "app_status")
    private Long appStatus;
    
    @Column(name = "level")
    private Integer level;
    
    @Column(name = "hall_office_code")
    private Long hallOfficeCode;
    
    @Column(name = "hall_id")
    private Long hallId;
    
    @Column(name = "applied_by")
    private String appliedBy;
    
    @Column(name = "booking_id")
    private String bookingId;
    
    @Transient
    private String buildingName;
    
    @Transient
    private String hallName;
    
    @Transient
    private String status;

}
