package sad.sras.services.appdata;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sad.sras.dto.appdata.PhotoData;
import sad.sras.exception.ObjectNotFoundException;
import sad.sras.exception.UnauthorizedException;
import sad.sras.models.appdata.HallAllotment;
import sad.sras.models.appdata.HallBooking;
import sad.sras.models.appdata.Visitor;
import sad.sras.models.auth.User;
import sad.sras.repo.appdata.HallAllotmentRepository;
import sad.sras.repo.appdata.HallBookingRepository;
import sad.sras.services.auth.AuthenticationService;

@Service
@RequiredArgsConstructor
public class HallBookingService {
	
	private final HallBookingRepository hallBookingRepo;
	private final AuthenticationService authService;
	private final CoreServices coreService;
	private final HallAllotmentRepository hallAllotmentRepo;
	
	@Transactional
	public HallBooking createBooking(HallBooking request, User user) {
		
		
		
		if(request.getBookingId()!=null) {
			boolean available = isHallAvailable(request.getHallOfficeCode(), request.getHallId(), request.getMeetingDate(), request.getStartTime(), request.getEndTime(),request.getBookingId());

			if (!available ) {
		        throw new UnauthorizedException("Hall is already allotted for the selected time slot.");
		    }
			Optional<HallBooking> booking = hallBookingRepo.findByBookingId(request.getBookingId());
			if(booking.isEmpty())
				throw new ObjectNotFoundException("Invalid booking ID");
			
			booking.get().setDepartment(request.getDepartment());
			booking.get().setPurpose(request.getPurpose());
			booking.get().setMeetingDate(request.getMeetingDate());
			booking.get().setStartTime(request.getStartTime());
			booking.get().setEndTime(request.getEndTime());
			booking.get().setHallOfficeCode(request.getHallOfficeCode());
			booking.get().setHallId(request.getHallId());
			booking.get().setNoOfAttendees(request.getNoOfAttendees());
			booking.get().setRemarks(request.getRemarks());
			booking.get().setContactName(request.getContactName());
			booking.get().setContactDesignation(request.getContactDesignation());
			
			if(request.getContactMobileNo()!=null)
				booking.get().setContactMobileNo(authService.decryptPassword(request.getContactMobileNo()));
			booking.get().setAppliedBy(user.getUsername());
			
			if(user.getRole().name().equals("DEPT"))
				booking.get().setAppStatus(1L);
			
			if(user.getRole().name().equals("ASAD"))
				booking.get().setAppStatus(2L);
			
			booking.get().setLevel(1);	
			
			if(user.getRole().name().equals("ASAD")) {
				HallAllotment allotment = hallAllotmentRepo.findByBookingId(booking.get().getBookingId()).get();
				allotment.setDate(request.getMeetingDate());
				allotment.setEndTime(request.getEndTime());
				allotment.setStartTime(request.getStartTime());
				allotment.setHallId(request.getHallId());
				allotment.setOfficeCode(request.getHallOfficeCode());
        		        		
        		hallAllotmentRepo.save(allotment);
        	}
			
			return hallBookingRepo.save(booking.get());
			
		}
		
		else {
			boolean available = isHallAvailable(request.getHallOfficeCode(), request.getHallId(), request.getMeetingDate(), request.getStartTime(), request.getEndTime());

			if (!available ) {
		        throw new UnauthorizedException("Hall is already allotted for the selected time slot.");
		    }
			
			if(request.getContactMobileNo()!=null)
				request.setContactMobileNo(authService.decryptPassword(request.getContactMobileNo()));
	        
			if(user.getRole().name().equals("DEPT"))
				request.setAppStatus(1L);
			
			if(user.getRole().name().equals("ASAD"))
				request.setAppStatus(2L);
	        request.setLevel(1);
	        request.setAppliedBy(user.getUsername());        
        	request.setBookingId(generateBookingID());
        	
        	HallBooking booking = hallBookingRepo.save(request);
        	
        	if(user.getRole().name().equals("ASAD")) {
        		HallAllotment hallAllotment = HallAllotment.builder()
                        .bookingId(booking.getBookingId())
                        .date(request.getMeetingDate())
                        .startTime(request.getStartTime())
                        .endTime(request.getEndTime())
                        .officeCode(request.getHallOfficeCode())
                        .hallStatus("Allotted")	                
                        .hallId(request.getHallId())
                        .build();
        		
        		hallAllotmentRepo.save(hallAllotment);
        	}

        	return booking;
        	
        	
		}
        

        
    }
	
	public Page<HallBooking> getBookingsBetweenDates(
            LocalDate startDate,
            LocalDate endDate,
            String search,
            User user,
            Integer status,
            Integer all,
            Pageable pageable
    ) {

		if(status==0)
			status=null;
		
		Page<HallBooking> page;
		
		//if(user.getRole().name().equals("ASAD"))
		if(all==1)
			page = hallBookingRepo.searchAllBookingsBetweenDates(
	                startDate,
	                endDate,
	                search,
	                status,
	                pageable
	        );
		else
        page = hallBookingRepo.searchBookingsBetweenDates(
                startDate,
                endDate,
                search,
                user.getUsername(),
                status,
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
	
	private boolean isHallAvailable(Long officeCode,
            Long hallId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime, String bookingId) {

				return hallAllotmentRepo.countOverlappingBookings(
				officeCode,
				hallId,
				date,
				startTime,
				endTime, bookingId) == 0;
			}
	
	public byte[] generateReport(LocalDate startDate, LocalDate endDate, User user, Integer status, Integer all) throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
		
		if(status==0)
			status=null;
		
		List<HallBooking> list;
		
		//if(user.getRole().name().equals("ASAD"))
		if(all==1)
			list = hallBookingRepo.getAllBookingsBetweenDates(
	                startDate,
	                endDate,
	                status
	        );
		else
        list = hallBookingRepo.getBookingsBetweenDates(
                startDate,
                endDate,
                user.getUsername(),
                status
        );
        
        list.forEach(booking -> {
            
            booking.setStatus(coreService.getStatus(booking.getAppStatus()));
            booking.setBuildingName(coreService.getOfficeName(booking.getHallOfficeCode()));
            booking.setHallName(coreService.getRoomName(booking.getHallId()));
            
        });
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Initialize PDF writer and document
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());
        document.setMargins(20, 20, 20, 20);
        
        DateTimeFormatter dateOnly = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        
        String headingg = "Hall Bookings by "+user.getUsername();
        if(all==1)
        	headingg ="All Hall Bookings";
        
        Paragraph heading = new Paragraph().setBold()
                .add(new Paragraph(headingg)
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(15))
                
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(0)
                .setMarginTop(0);
        document.add(heading);

        Paragraph dateRange = new Paragraph().setBold()
            .add(new Paragraph("Date: " + startDate.format(dateOnly)+" ")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(12))
            .add(new Paragraph(" to " + endDate.format(dateOnly))
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(12))
            .setTextAlignment(TextAlignment.LEFT)
            .setMarginBottom(0)
            .setMarginTop(0);
        
        document.add(dateRange);
        Paragraph statuss = new Paragraph().setBold()
                .add(new Paragraph("Status: " + (status==null? "All": coreService.getStatus(status.longValue())))
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(12))
      
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(0)
                .setMarginTop(0);
            
            document.add(statuss);
        float[] columnWidths = {1, 1}; // equal width columns
        Table table1 = new Table(UnitValue.createPercentArray(columnWidths))
                .useAllAvailableWidth();
        
        Cell leftCell = new Cell()
                .add(new Paragraph("No. of bookings: " + list.size())
                        .setBold()
                        .setFontSize(12))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT);
        
        
        Cell rightCell = new Cell()
                .add(new Paragraph("Generated on: " + LocalDateTime.now().format(formatter))
                        .setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);

        table1.addCell(leftCell);
        table1.addCell(rightCell);
        
        document.add(table1);
        Paragraph generatedBy = new Paragraph().setBold()
                .add(new Paragraph("Generated By: " + (user==null? "-": user.getUsername()))
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(12))
      
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(0)
                .setMarginTop(0);
            
            document.add(generatedBy);
        
        Table table;
        
        table = new Table(UnitValue.createPercentArray(
                new float[]{1, 3, 3, 3, 3, 3, 3, 3, 3, 3})).useAllAvailableWidth();
        		
        List<String> headers = new ArrayList<>();

        headers.addAll(List.of(
        		"Sl. No.",
                "Booking ID",
                "Department",
                "Purpose",
                "Date & Time",
                "Hall",
                "No. of attendees",
                "Status",
                "Remarks",
                "Contact Person Details"
        ));
        
        for (String h : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h))
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
        }
        
        // Table rows
        int serial = 1;
        for (HallBooking h : list) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(serial++))).setTextAlignment(TextAlignment.CENTER));

            String contactName = (h.getContactName()!=null?(h.getContactName()+" "):"");
            String contactDesignation = (h.getContactDesignation()!=null?(h.getContactDesignation()+" "):"");
            String contactMobileNo = (h.getContactMobileNo()!=null?(h.getContactMobileNo()+" "):"");
            table.addCell(new Cell().add(new Paragraph(h.getBookingId())));
            table.addCell(new Cell().add(new Paragraph(h.getDepartment())));
            table.addCell(new Cell().add(new Paragraph(h.getPurpose())));
            table.addCell(new Cell().add(new Paragraph(h.getMeetingDate()+"\n"+h.getStartTime()+"-"+h.getEndTime())));
            table.addCell(new Cell().add(new Paragraph(h.getHallName()+", "+h.getBuildingName())));
            table.addCell(new Cell().add(new Paragraph(h.getNoOfAttendees()!=null?h.getNoOfAttendees()+"":"")));
            table.addCell(new Cell().add(new Paragraph(h.getStatus())));
            table.addCell(new Cell().add(new Paragraph(h.getRemarks()!=null?h.getRemarks()+"":"")));
            table.addCell(new Cell().add(new Paragraph(contactName+"\n"+contactDesignation+"\n"+contactMobileNo)));
        }

        document.add(table);
                
        
     // Footer
        Paragraph footer = new Paragraph("This report is generated by e-Pass System on " + LocalDateTime.now().format(formatter))
                .setFontSize(8)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(10);
        document.add(footer);

        document.close();
        pdf.close();
        //writer.close(); 
        byte[] bytes = out.toByteArray();
        //return new ByteArrayInputStream(out.toByteArray());
        return bytes;
		
	}

}
