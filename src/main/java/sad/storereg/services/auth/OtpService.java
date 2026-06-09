package sad.storereg.services.auth;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import sad.storereg.config.JwtService;
import sad.storereg.dto.auth.AuthenticationResponse;
import sad.storereg.dto.auth.GetOtpRequestDTO;
import sad.storereg.dto.auth.GetOtpResponseDTO;
import sad.storereg.dto.auth.VerifyOtpRequestDTO;
import sad.storereg.exception.ObjectNotFoundException;
import sad.storereg.exception.UnauthorizedException;
import sad.storereg.models.auth.CurrentUsers;
import sad.storereg.models.auth.Otp;
import sad.storereg.models.auth.User;
import sad.storereg.repo.auth.CurrentUsersRepository;
import sad.storereg.repo.auth.OtpRepository;
import sad.storereg.repo.auth.UserRepository;
import sad.storereg.services.appdata.CoreServices;
import sad.storereg.services.appdata.ServiceNotification;

@Service
@RequiredArgsConstructor
public class OtpService {
	
	private final ServiceNotification serviceNotification;
	
	private final OtpRepository otpRepo;
	
	private final UserRepository userRepo;
	
	private final JwtService jwtService;

	private final CurrentUsersRepository currentUsersRepo;
	
	private final CoreServices coreServices;
	
	private final AuthenticationService authService;
	
	private static final Integer EXPIRE_MIN = 60;
	
	public GetOtpResponseDTO generateOtp(HttpServletRequest httpRequest, GetOtpRequestDTO request) {

		String otp = generateOTP();
		//otp = "123123";
		String[] params = { otp };
		CompletableFuture<Void> otpFuture = serviceNotification.sendOnlySms(httpRequest, "OTP", request.getMobileno(),
				params);
		// Simulate some delay
		try {
			Thread.sleep(3000); // Simulating sending OTP takes time
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//Optional<Otp> optOtp = otpRepo.findByUsername(request.getMobileno());
		Optional<Otp> optOtp = otpRepo.findByUsernameAndForgotPasswordIsNull(request.getMobileno());

		if (optOtp.isPresent()) {
			Duration duration = Duration.between(optOtp.get().getGeneratedAt().toInstant(),
					Timestamp.valueOf(LocalDateTime.now()).toInstant());
			if (duration.toMinutes() < 1)
				throw new UnauthorizedException("Resend OTP only after 1 minute");
			if (duration.toMinutes() < 2) {
				if (optOtp.get().getCount() > 10)
					throw new UnauthorizedException("OTP sent 10 times. Try again after some time");
				else
					optOtp.get().setCount((optOtp.get().getCount() + 1));
			} else
				optOtp.get().setCount(0);
			optOtp.get().setOtp(otp);
			optOtp.get().setGeneratedAt(Timestamp.valueOf(LocalDateTime.now()));
			otpRepo.save(optOtp.get());
		} else {
			Otp otpObj = Otp.builder().otp(otp).username(request.getMobileno())
					.generatedAt(Timestamp.valueOf(LocalDateTime.now())).count(0).build();
			otpRepo.save(otpObj);
		}
		// Here send the OTP to mobile. SMS Service
		return GetOtpResponseDTO.builder().otp(otp).expiry(EXPIRE_MIN.toString()).build();
	}
	
	private static String generateOTP() {
		int otpLength = 6;

		Random random = new Random();

		StringBuilder otp = new StringBuilder(otpLength);
		for (int i = 0; i < otpLength; i++) {
			int digit = random.nextInt(10); // Generate a random digit (0-9)
			otp.append(digit);
		}
		return otp.toString();
	}
	
	public boolean lastGeneratedOTP(String username, int isSignUp) {
		Optional<Otp> optOtp = otpRepo.findByUsername(username);
		if(isSignUp==1)
			optOtp = otpRepo.findByUsernameAndIsSignUpEquals(username, 1);
		else if(isSignUp==2)
			optOtp = otpRepo.findByUsernameAndForgotPasswordEquals(username, 1);
		if(optOtp.isEmpty()) {
			throw new UnauthorizedException("Not authorized");
		}
		Instant instant = optOtp.get().getGeneratedAt().toInstant();
        LocalDateTime timestamp = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        
		long differenceInMinutes = ChronoUnit.MINUTES.between(timestamp, LocalDateTime.now());

        return differenceInMinutes <= 60?true:false;
	}
	
	public AuthenticationResponse verifyOtp(VerifyOtpRequestDTO request, HttpServletRequest httpRequest) {
		if (verifyOTP(request.getMobileno(), request.getOtp(), 0)) {

			Optional<User> user = userRepo.findByUsername(request.getMobileno());
			if (user.isEmpty()) {
				throw new UnauthorizedException("User not found");
			}
			
			jwtService.invalidateUserWithDiffIP(request.getMobileno(), coreServices.getClientIp(httpRequest));
			
			//Invalidate other logins
			Optional<CurrentUsers> c = currentUsersRepo.findByUsername(user.get().getUsername());
			Integer session = 0;
			if(c.isPresent()) {
				//jwtService.invalidateToken(user.getUsername(), coreServices.getClientIp(httpRequest));
				session=c.get().getSession()+1;
				c.get().setSession(session);
				currentUsersRepo.save(c.get());
			}
			else {
				CurrentUsers currentUser = CurrentUsers.builder().username(user.get().getUsername()).ipAddress(coreServices.getClientIp(httpRequest)).entrydate(new Date()).session(session).build();
				currentUsersRepo.save(currentUser);
			}	
			
			Map<String, Object> extraClaims = new HashMap<>();
			extraClaims.put("session", session);
			var jwtToken = jwtService.generateToken(user.get());
			var refreshToken = jwtService.generateRefreshToken(extraClaims,user.get());
			// revokeAllUserTokens(user);
			// saveUserToken(user, jwtToken);
			// Here you need to clear the OTP
			MDC.put("username", request.getMobileno());
			//log.info("Login");
			MDC.remove("username");
			//serviceAuditTrail.saveTrail(request.getMobileno(), "/verifyotp", "Applicant Logged in", "Login",
			//		commonService.getClientIp(httpRequest), "Success");
			return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken)
					.role(user.get().getRole().toString()).build();
		} else
			//serviceAuditTrail.saveTrail(request.getMobileno(), "/verifyotp", "Applicant Log in attempted", "Login",
			//		commonService.getClientIp(httpRequest), "Failed");
		throw new UnauthorizedException("Invalid OTP");
	}
	
	private boolean verifyOTP(String username, String otp, int forgotPassword) {
		//Optional<Otp> optOtp = otpRepo.findByUsername(username);
		Optional<Otp> optOtp;
		if(forgotPassword==0)
			optOtp = otpRepo.findByUsernameAndForgotPasswordIsNull(username);
		else if(forgotPassword==2)//2 is for is SignUp
		{
			optOtp = otpRepo.findByUsernameAndIsSignUpEquals(username, 1);
		}
		else
			optOtp = otpRepo.findByUsernameAndForgotPasswordEquals(username, forgotPassword);
		if (optOtp.isPresent()) {
			if (optOtp.get().getOtp().equals(otp) && forgotPassword==0) {

				Duration duration = Duration.between(optOtp.get().getGeneratedAt().toInstant(),
						Timestamp.valueOf(LocalDateTime.now()).toInstant());
				otpRepo.deleteById(optOtp.get().getId());
				if (duration.toMinutes() > EXPIRE_MIN)
					throw new UnauthorizedException("OTP Expired");

				return true;
			}
			else if(optOtp.get().getOtp().equals(otp) && forgotPassword==1)
			{
				otpRepo.deleteById(optOtp.get().getId());
				return true;
			}
			else if(optOtp.get().getOtp().equals(otp) && forgotPassword==2)
			{
				otpRepo.deleteById(optOtp.get().getId());
				return true;
			}
			else
				return false;
		} else
			return false;
	}
	

}
