package sad.sras.services.appdata;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sad.sras.exception.ObjectNotFoundException;
import sad.sras.models.appdata.HallBooking;
import sad.sras.models.appdata.Visitor;
import sad.sras.models.auth.User;
import sad.sras.repo.appdata.HallBookingRepository;
import sad.sras.services.auth.AuthenticationService;

@Service
@RequiredArgsConstructor
public class HallBookingService {
	
	private final HallBookingRepository hallBookingRepo;
	private final AuthenticationService authService;
	private final CoreServices coreService;
	
	public HallBooking createBooking(HallBooking request, String username) {

		request.setContactMobileNo(authService.decryptPassword(request.getContactMobileNo()));
        request.setAppStatus(1L);
        request.setLevel(1);
        request.setAppliedBy(username);
        request.setBookingId(generateBookingID());

        return hallBookingRepo.save(request);
    }
	
	public Page<HallBooking> getBookingsBetweenDates(
            LocalDate startDate,
            LocalDate endDate,
            String search,
            User user,
            Pageable pageable
    ) {
    	//LocalDateTime startDateTime = startDate.atStartOfDay();
        //LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

		Page<HallBooking> page;
		
		if(user.getRole().name().equals("ASAD"))
			page = hallBookingRepo.searchAllBookingsBetweenDates(
	                startDate,
	                endDate,
	                search,
	                pageable
	        );
		else
        page = hallBookingRepo.searchBookingsBetweenDates(
                startDate,
                endDate,
                search,
                user.getUsername(),
                pageable
        );
        
        page.getContent().forEach(booking -> {
            
            booking.setStatus(coreService.getStatus(booking.getAppStatus()));
            booking.setBuildingName(coreService.getOfficeName(booking.getHallOfficeCode()));
            booking.setHallName(coreService.getRoomName(booking.getHallId()));
            
        });
        
        return page;
    }
	
	public Page<HallBooking> getPendingBookings(
            Pageable pageable
    ) {

        Page<HallBooking> page = hallBookingRepo.findAllByAppStatus(
                1,
                pageable
        );
        
        page.getContent().forEach(booking -> {
            
            booking.setStatus(coreService.getStatus(booking.getAppStatus()));
            booking.setBuildingName(coreService.getOfficeName(booking.getHallId()));
            booking.setHallName(coreService.getRoomName(booking.getHallId()));
            
        });
        
        return page;
    }
	
	public String generateBookingID() {
		Optional<HallBooking> optHallBooking = hallBookingRepo.findFirstByOrderByIdDesc();

		if (optHallBooking.isPresent()) {
			String lastBookingID = optHallBooking.get().getBookingId();
			
			int year = Year.now().getValue() % 100; // Last 2 digits of year
		    //String officeCode = new DecimalFormat("00").format(commonService.getOfficeCode(houseId));
			int serial = Integer.valueOf(lastBookingID.substring(lastBookingID.lastIndexOf('/')+1));
			String prefix = year+"";
			String newBookingID;
		    do {
		        serial += 1;
		        String serialFormatted = new DecimalFormat("000000").format(serial);
		        newBookingID = prefix +"/"+ serial;
		    } while (hallBookingRepo.applicationNoExists(newBookingID)); 

		    return newBookingID;

		} else {
			String newBookingID="";
			int serial = 0;
			String prefix = Integer.toString(Year.now().getValue() % 100) + "";
			do {
		        serial += 1;
		        String serialFormatted = new DecimalFormat("000000").format(serial);
		        newBookingID = prefix + "/"+serial;
		    } while (hallBookingRepo.applicationNoExists(newBookingID)); 
			return (newBookingID);
		}
	}
	
	public HallBooking getBooking(String bookingId) {

		Optional<HallBooking> optHallBooking = hallBookingRepo.findByBookingId(bookingId);
		if(optHallBooking.isEmpty())
			throw new ObjectNotFoundException("Invalid booking ID");

        return hallBookingRepo.save(optHallBooking.get());
    }
	
	public HallBooking setStatus(HallBooking booking,Long statusId, Long remarkId) {

		booking.setAppStatus(statusId);
		if(remarkId!=null)
			booking.setNazirRemark(remarkId);

        return hallBookingRepo.save(booking);
    }

}
