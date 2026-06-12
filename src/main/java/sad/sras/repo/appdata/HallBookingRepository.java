package sad.sras.repo.appdata;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sad.sras.models.appdata.HallBooking;

public interface HallBookingRepository extends JpaRepository<HallBooking, Long>{
	
	@Query("""
		    SELECT h
		    FROM HallBooking h
		    WHERE h.meetingDate BETWEEN :startDate AND :endDate
		      AND h.appliedBy = :username
		      AND (
		            :search IS NULL
		            OR TRIM(:search) = ''
		            OR LOWER(h.department) LIKE LOWER(CONCAT('%', :search, '%'))
		            OR LOWER(h.purpose) LIKE LOWER(CONCAT('%', :search, '%'))
		            OR LOWER(h.contactName) LIKE LOWER(CONCAT('%', :search, '%'))
		            OR LOWER(h.contactDesignation) LIKE LOWER(CONCAT('%', :search, '%'))
		          )
		    ORDER BY h.createdDate DESC
		""")
		Page<HallBooking> searchBookingsBetweenDates(
		        @Param("startDate") LocalDate startDate,
		        @Param("endDate") LocalDate endDate,
		        @Param("search") String search,
		        @Param("username") String username,
		        Pageable pageable
		);
	
	@Query("""
		    SELECT h
		    FROM HallBooking h
		    WHERE h.meetingDate BETWEEN :startDate AND :endDate
		      AND (
		            :search IS NULL
		            OR TRIM(:search) = ''
		            OR LOWER(h.department) LIKE LOWER(CONCAT('%', :search, '%'))
		            OR LOWER(h.purpose) LIKE LOWER(CONCAT('%', :search, '%'))
		            OR LOWER(h.contactName) LIKE LOWER(CONCAT('%', :search, '%'))
		            OR LOWER(h.contactDesignation) LIKE LOWER(CONCAT('%', :search, '%'))
		          )
		    ORDER BY h.createdDate DESC
		""")
		Page<HallBooking> searchAllBookingsBetweenDates(
		        @Param("startDate") LocalDate startDate,
		        @Param("endDate") LocalDate endDate,
		        @Param("search") String search,
		        Pageable pageable
		);
	
	Page<HallBooking> findAllByAppStatus(Integer appStatus, Pageable pageable);
	
	Optional<HallBooking> findFirstByOrderByIdDesc();
	
	@Query("SELECT COUNT(a) > 0 FROM HallBooking a WHERE a.bookingId = :bookingID")
	boolean applicationNoExists(@Param("bookingID") String bookingID);
	
	Optional<HallBooking> findByBookingId(String bookingId);

}
