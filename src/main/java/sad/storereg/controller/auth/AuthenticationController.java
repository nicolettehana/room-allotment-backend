package sad.storereg.controller.auth;

import static sad.storereg.models.auth.Role.USER;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sad.storereg.annotations.Auditable;
import sad.storereg.dto.auth.AuthenticationRequest;
import sad.storereg.dto.auth.AuthenticationResponse;
import sad.storereg.dto.auth.GetOtpRequestDTO;
import sad.storereg.dto.auth.RegisterRequest;
import sad.storereg.dto.auth.RegisterRequestDTO;
import sad.storereg.dto.auth.VerifyOtpRequestDTO;
import sad.storereg.exception.InternalServerError;
import sad.storereg.exception.UnauthorizedException;
import sad.storereg.models.auth.CaptchaSettings;
import sad.storereg.models.auth.User;
import sad.storereg.repo.auth.UserRepository;
import sad.storereg.services.auth.AuthenticationService;
import sad.storereg.services.auth.CaptchaService;
import sad.storereg.services.auth.OtpService;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
	
	private final AuthenticationService authService;
	private final CaptchaService captchaService;
	private final UserRepository userRepo;
	private final PasswordEncoder passwordEncoder;
	private final OtpService otpService;
	
	@Auditable
	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request,
			HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws BadCredentialsException, UsernameNotFoundException, IOException {
//		CaptchaEntry captcha = captchaStore.get(request.getUuid().toString());
//	        
//	        System.out.println(captcha != null && captcha.getCaptcha().equals(request.getCaptcha()));
//	        System.out.println("User captcha is: "+request.getCaptcha()+" and the captcha store captcha is: "+captcha);
//	        System.out.println("The captcha Store is:"+captchaStore);
//	        if (captcha != null && captcha.getCaptcha().equals(request.getCaptcha())) {
//	            captchaStore.remove(request.getUuid().toString());
//	        }
//	        System.out.println("The captcha Store is:"+captchaStore);
		if (captchaService.validateCaptcha(request.getCaptchaToken().toString(), request.getCaptcha())) {
			return ResponseEntity.ok(authService.authenticate2(request, httpRequest, httpResponse));
		
		} else
			throw new UnauthorizedException("Invalid Captcha");
	}
	
	@Auditable
	@PostMapping("/authenticate-1")
	public ResponseEntity<?> authenticateStep1(@Valid @RequestBody AuthenticationRequest request,
			HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws BadCredentialsException, UsernameNotFoundException, IOException {
//		CaptchaEntry captcha = captchaStore.get(request.getUuid().toString());
//	        
//	        System.out.println(captcha != null && captcha.getCaptcha().equals(request.getCaptcha()));
//	        System.out.println("User captcha is: "+request.getCaptcha()+" and the captcha store captcha is: "+captcha);
//	        System.out.println("The captcha Store is:"+captchaStore);
//	        if (captcha != null && captcha.getCaptcha().equals(request.getCaptcha())) {
//	            captchaStore.remove(request.getUuid().toString());
//	        }
//	        System.out.println("The captcha Store is:"+captchaStore);
		if (captchaService.validateCaptcha(request.getCaptchaToken().toString(), request.getCaptcha())) {
			return ResponseEntity.ok(authService.authStep1(request, httpRequest));
		
		} else
			throw new UnauthorizedException("Invalid Captcha");
	}

	@GetMapping("/refresh-token")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			authService.refreshToken(request, response);
		} catch (UnauthorizedException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalServerError("Unable to refresh token.", ex);
		}
	}

	@GetMapping("/refresh-captcha")
	public ResponseEntity<Map<String, Object>> refreshCaptcha() {
		try {			
			return ResponseEntity.ok(captchaService.generateCaptcha());
		} catch (Exception ex) {
			throw new InternalServerError("Unable to refresh Captcha", ex);
		}
	}

	@GetMapping("/get-public-key")
	public ResponseEntity<Map<String, Object>> getPublicKey() {
		Map<String, Object> map = new HashMap<>();
		try {
			map.put("publicKey", authService.getPublicKey());
			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (Exception ex) {
			throw new InternalServerError("Unable to get Public Key", ex);
		}
	}
	
	@Auditable
	@Transactional
	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request,
			HttpServletRequest httpRequest) {
		try {
			Map<String, String> map = new HashMap<>();
			
			request.setRole(USER);
			
			if (captchaService.validateCaptcha(request.getCaptchaToken().toString(), request.getCaptcha())) {
				authService.register(request);
			} else
				throw new UnauthorizedException("Inavlid Captcha");
			
			map.put("detail", "User Registered.");

			return new ResponseEntity<>(map, HttpStatus.OK);

		} catch (UnauthorizedException|InternalServerError ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalServerError("Unable to register user", ex);
		}
	}
	
	@PostMapping("/get-otp")
	public ResponseEntity<Map<String, Object>> getotp(@Valid @RequestBody GetOtpRequestDTO request,
			HttpServletRequest httpRequest) {
		Map<String, Object> map = new HashMap<>();
		try {
			if(request.getCaptcha().length()==0 || request.getCaptcha()==null) {
				request.setMobileno(authService.decryptPassword(request.getMobileno()));
				Optional<User> user = userRepo.findByUsername(request.getMobileno());

				if(!otpService.lastGeneratedOTP(user.get().getUsername(), 0)) 
					throw new UnauthorizedException("Not authorized");
				otpService.generateOtp(httpRequest, request);
				Map<String, Object> c = captchaService.generateCaptcha();
				map.put("message", "Successfully sent OTP");
				
				return new ResponseEntity<>(map, HttpStatus.OK);
			}
			else if (captchaService.validateCaptcha(request.getUuid().toString(), request.getCaptcha())) {
				String encryptedMobile=request.getMobileno();
				request.setMobileno(authService.decryptPassword(request.getMobileno()));
				Optional<User> user = userRepo.findByUsername(request.getMobileno());

				//if(request.getIsSignUp()!=null && request.getIsSignUp()==1 && user.get().getName()!=null && user.get().getName().length()>1)
				//{
				//	throw new UnauthorizedException("User is already registered");
				//}
				if (user.isEmpty()) {
					//Here check if name is there or not and do differently
					var regis = RegisterRequest.builder().username(request.getMobileno()).mobileNo(encryptedMobile).role(USER).build();
					authService.register(regis);
				}
				otpService.generateOtp(httpRequest, request);
				Map<String, Object> c = captchaService.generateCaptcha();
				map.put("message", "Successfully sent OTP");
				
				return new ResponseEntity<>(map, HttpStatus.OK);
			} else
				throw new UnauthorizedException("Inavlid Captcha");

		} catch (UnauthorizedException|InternalServerError ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalServerError("Unable to fetch OTP", ex);
		}
	}
	
	@PostMapping("/verify-otp")
	public ResponseEntity<AuthenticationResponse> verifyotp(@Valid @RequestBody VerifyOtpRequestDTO request,
			HttpServletRequest httpRequest) {
		request.setMobileno(authService.decryptPassword(request.getMobileno()));
		//if (service.verifyCaptcha(request.getUuid(), request.getCaptcha()))
			return ResponseEntity.ok(otpService.verifyOtp(request, httpRequest));
		//else
		//	throw new UnauthorizedException("Inavlid Captcha");
	}
	
}
