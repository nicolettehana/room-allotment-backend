package sad.sras.repo.appdata;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sad.sras.models.appdata.HallAllotment;

public interface HallAllotmentRepository extends JpaRepository<HallAllotment, Long>{
	
	Optional<HallAllotment> findByBookingId(String bookingId);
	
	List<HallAllotment> findAllByDate(LocalDate date);
	
	List<HallAllotment> findAllByDateAndOfficeCode(LocalDate date, Long officeCode);
	
	@Query("""
	        SELECT COUNT(h)
	        FROM HallAllotment h
	        WHERE h.officeCode = :officeCode
	          AND h.hallId = :hallId
	          AND h.date = :date
	          AND h.startTime < :endTime
	          AND h.endTime > :startTime
	    """)
	    long countOverlappingBookings(
	            @Param("officeCode") Long officeCode,
	            @Param("hallId") Long hallId,
	            @Param("date") LocalDate date,
	            @Param("startTime") LocalTime startTime,
	            @Param("endTime") LocalTime endTime);

}
