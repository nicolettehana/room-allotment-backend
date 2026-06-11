package sad.sras.services.appdata;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sad.sras.models.appdata.HallBooking;
import sad.sras.models.appdata.Visitor;
import sad.sras.repo.appdata.HallBookingRepository;
import sad.sras.services.auth.AuthenticationService;

@Service
@RequiredArgsConstructor
public class HallBookingService {
	
	private final HallBookingRepository hallBookingRepo;
	private final AuthenticationService authService;
	
	public HallBooking createBooking(HallBooking request, String username) {

		request.setContactMobileNo(authService.decryptPassword(request.getContactMobileNo()));
        request.setAppStatus(1);
        request.setLevel(1);
        request.setAppliedBy(username);

        return hallBookingRepo.save(request);
    }
	
	public Page<HallBooking> getBookingsBetweenDates(
            LocalDate startDate,
            LocalDate endDate,
            String search,
            String username,
            Pageable pageable
    ) {
    	LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return hallBookingRepo.searchBookingsBetweenDates(
                startDateTime,
                endDateTime,
                search,
                username,
                pageable
        );
    }

}
