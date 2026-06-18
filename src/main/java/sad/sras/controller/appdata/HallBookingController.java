package sad.sras.controller.appdata;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sad.sras.annotations.Auditable;
import sad.sras.dto.appdata.MeetingsDTO;
import sad.sras.dto.appdata.TakeActionDTO;
import sad.sras.models.appdata.HallAllotment;
import sad.sras.models.appdata.HallBooking;
import sad.sras.models.auth.User;
import sad.sras.services.appdata.HallAllotmentService;
import sad.sras.services.appdata.HallBookingService;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class HallBookingController {
	
	private final HallBookingService hallBookingService;
	private final HallAllotmentService hallAllotmentService;
	
	@Auditable
	@PostMapping
	public ResponseEntity<?> createBooking(
	        @RequestBody HallBooking request, @AuthenticationPrincipal User user) {
//		if(request.getBookingId()!=null)
//		{
//			System.out.println(request);
//			return ResponseEntity.ok(null);
//		}

		Map<String, Object> data = new HashMap<>();
		data.put("detail", "Request Submitted");
		hallBookingService.createBooking(request, user);
	    return ResponseEntity.ok(data);
	}
	
	@GetMapping
    public Page<HallBooking> getBookingsByDateRange(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
 	        @RequestParam(defaultValue = "10") int size,
 	        @RequestParam(defaultValue = "") String search,
 	       @RequestParam(defaultValue = "0") Integer status,
 	      @RequestParam(defaultValue = "0") Integer all,
 	     @RequestParam(required = false, defaultValue = "ASC") String order,
 	       @AuthenticationPrincipal User user
    ) {

        Pageable pageable = null;
        
        if(order.equals("DESC"))
        	pageable =PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "meetingDate"));
        else
        	pageable =PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "meetingDate"));

        return hallBookingService.getBookingsBetweenDates(startDate,endDate,search, user, status, all, pageable);
    }
	
	@GetMapping("/pending")
    public Page<HallBooking> getPendingBookings(
            @RequestParam(defaultValue = "0") int page,
 	        @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "meetingDate"));

        return hallBookingService.getPendingBookings(pageable);
    }
	
	@Auditable
	@PostMapping("/action")
	public ResponseEntity<?> action(
	        @RequestBody TakeActionDTO request, @AuthenticationPrincipal User user) {

		Map<String, Object> data = new HashMap<>();
		
		hallAllotmentService.takeAction(request, user);
		
		data.put("detail", "Success");
		
	    return ResponseEntity.ok(data);
	}
	
	@PostMapping("/get-remark")
	public ResponseEntity<?> getRemark(
	        @RequestBody TakeActionDTO request) {

		Map<String, Object> data = new HashMap<>();
		
		
		
		data.put("detail", "Success");
		data.put("remark", hallAllotmentService.getRemark(request));
		
	    return ResponseEntity.ok(data);
	}
	
	@GetMapping("/hall-allotments")
    public List<MeetingsDTO> getHallAllotments(
            @RequestParam LocalDate date,@RequestParam Long officeCode
    ) {

        return hallAllotmentService.getHallAllotments(date, officeCode);
    }
	
	@GetMapping("/export")
    public ResponseEntity<byte[]> generateReport(
    		@RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
 	       @RequestParam(defaultValue = "0") Integer status,
 	      @RequestParam(defaultValue = "0") Integer all,
 	       @AuthenticationPrincipal User user) throws Exception {

        byte[] fileBytes;
        String fileName;
        MediaType mediaType;
        
        fileBytes = hallBookingService.generateReport(startDate, endDate, user, status, all);
        fileName = "visitor_report.pdf";
        mediaType = MediaType.APPLICATION_PDF;


        if (fileBytes == null || fileBytes.length < 100) {
            throw new RuntimeException("Generated file is empty");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentType(mediaType);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileBytes);
    }

}
