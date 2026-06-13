package sad.sras.services.appdata;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sad.sras.dto.appdata.TakeActionDTO;
import sad.sras.exception.UnauthorizedException;
import sad.sras.models.appdata.HallAllotment;
import sad.sras.models.appdata.HallBooking;
import sad.sras.models.appdata.Remark;
import sad.sras.models.appdata.Visitor;
import sad.sras.models.auth.User;
import sad.sras.repo.appdata.HallAllotmentRepository;
import sad.sras.repo.appdata.HallBookingRepository;
import sad.sras.repo.appdata.RemarkRepository;

@Service
@RequiredArgsConstructor
public class HallAllotmentService {
	
	private final HallAllotmentRepository hallAllotmentRepo;
	private final HallBookingService hallBookingService;
	private final RemarkRepository remarkRepo;
	
	public boolean takeAction(TakeActionDTO request, User user) {
		
		HallBooking hallBooking = hallBookingService.getBooking(request.getBookingId());
		
		if(request.getAction().equals("A")) {
			
			boolean available = isHallAvailable(
		            hallBooking.getHallOfficeCode(),
		            hallBooking.getHallId(),
		            hallBooking.getMeetingDate(),
		            hallBooking.getStartTime(),
		            hallBooking.getEndTime());

		    if (!available) {
		        throw new UnauthorizedException("Hall is already allotted for the selected time slot.");
		    }
		    
			hallBookingService.setStatus(hallBooking, 2L, null);
			
			HallAllotment hallAllotment = HallAllotment.builder()
	                .bookingId(request.getBookingId())
	                .date(hallBooking.getMeetingDate())
	                .startTime(hallBooking.getStartTime())
	                .endTime(hallBooking.getEndTime())
	                .officeCode(hallBooking.getHallOfficeCode())
	                .hallStatus("Allotted")	                
	                .hallId(hallBooking.getHallId())
	                .build();
			
			hallAllotmentRepo.save(hallAllotment);
		}
		else if(request.getAction().equals("R")) {
			
			Remark remark = Remark.builder()
					.actionCode("R")
					.bookingId(hallBooking.getBookingId())
					.remark(request.getRemark())
					.user(user.getUsername())
					.build();
			Remark r = remarkRepo.save(remark);
			hallBookingService.setStatus(hallBooking, 4L, r.getId());
		}
		else if(request.getAction().equals("S")) {
			
			Remark remark = Remark.builder()
			.actionCode("S")
			.bookingId(hallBooking.getBookingId())
			.remark(request.getRemark())
			.user(user.getUsername())
			.build();
			Remark r = remarkRepo.save(remark);
			hallBookingService.setStatus(hallBooking, 3L, r.getId());
		}
		else if(request.getAction().equals("C")) {
			//If hall is allotted, un-allot first
			if(hallBooking.getAppStatus()==2) {
				Optional<HallAllotment> allotment = hallAllotmentRepo.findByBookingId(hallBooking.getBookingId());
				if(allotment.isPresent()) {
					hallAllotmentRepo.delete(allotment.get());
				}
			}
			Remark remark = Remark.builder()
					.actionCode("C")
					.bookingId(hallBooking.getBookingId())
					.remark(request.getRemark())
					.user(user.getUsername())
					.build();
					Remark r = remarkRepo.save(remark);
			hallBookingService.setStatus(hallBooking, 5L, r.getId());
		}
		
		return true;	
    }
	
	private boolean isHallAvailable(Long officeCode,
            Long hallId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {

				return hallAllotmentRepo.countOverlappingBookings(
				officeCode,
				hallId,
				date,
				startTime,
				endTime) == 0;
	}
	
	public String getRemark(TakeActionDTO request) {
		
		HallBooking hallBooking = hallBookingService.getBooking(request.getBookingId());
		
		Optional<Remark> remark = remarkRepo.findById(hallBooking.getNazirRemark());
		
		String nazirRemark = "";
		
		if(remark.isPresent())
			nazirRemark = remark.get().getRemark();
		
		return nazirRemark;	
    }

}
