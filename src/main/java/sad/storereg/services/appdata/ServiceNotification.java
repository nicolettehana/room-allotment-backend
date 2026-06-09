package sad.storereg.services.appdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import sad.storereg.models.master.Notification;
import sad.storereg.repo.appdata.VisitorRepository;
import sad.storereg.repo.master.NotificationRepository;

@Service
@RequiredArgsConstructor
public class ServiceNotification {
	
	private final NotificationRepository notificationRepo;
	
	private final CoreServices coreService;
	
	private String messageId;
	
	private Notification getNotification(String messageId) {

		this.messageId = messageId;
		Optional<Notification> optNotification = notificationRepo.findByMessageIdEquals(messageId);
		if (optNotification.isPresent())
			return optNotification.get();
		else {
			// Here keep audit trail to log this information
			return null;
		}
		// throw new InternalServerError("Message ID doesn't exist");
	}
	
	@Transactional
	@Async
	public CompletableFuture<Void> sendOnlySms(HttpServletRequest request, String messageId, String recipientMobileNo,
			String[] mobileParams) {
		String mobileMessage = createMessage(getNotification(messageId).getMessage(), Arrays.asList(mobileParams));
		if (mobileMessage == null) {
			//serviceAuditTrail.saveTrail(recipientMobileNo, null, "Message ID not found", messageId,
			//		coreService.getClientIp(request), "Failed");
			return CompletableFuture.completedFuture(null);
		}
		try {
			//System.out.println("Sending SMS...");
			//System.out.println("messageId: "+messageId+" mobileNo: "+recipientMobileNo+" message: "+mobileMessage);
			sendSms(request, messageId, recipientMobileNo, mobileMessage);
			//serviceAuditTrail.saveTrail(recipientMobileNo, null, "SMS sent to mobile no ", messageId,
			//		coreService.getClientIp(request), "Success");
			return CompletableFuture.completedFuture(null);
		} catch (Exception e) {
			// Here audit trail
			//serviceAuditTrail.saveTrail(recipientMobileNo, null, "SMS not sent", messageId,
			//		coreService.getClientIp(request), "Failed");
			// throw e;
			// LOG.log(Level.SEVERE, e.getLocalizedMessage());
		}
		return CompletableFuture.completedFuture(null);
	}
	
	private String createMessage(String message, List<String> params) {
		String msg = message;
		for (int i = 0; i < params.size(); i++) {
			msg = msg.replace("{" + i + "}", params.get(i));
		}
		return msg;
	}

	private void sendSms(HttpServletRequest request, String messageId, String recipientMobileNo, String message2)
			throws Exception {
		String responseString = "";
		try {
			
			SSLSocketFactory sf = null;
			SSLContext context = null;

			// Create a trust manager that trusts all certificates
			TrustManager[] trustAllCertificates = new TrustManager[] { new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };

			context = SSLContext.getInstance("TLSv1.2");
			context.init(null, trustAllCertificates, new SecureRandom());
			sf = context.getSocketFactory();

			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true; // Allow all hostnames
				}
			});

			// Open connection
			URL url = new URL("https://hydgw.sms.gov.in/failsafe/MLink");
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setRequestProperty("Accept", "application/json");
			http.setRequestProperty("Content-Type", "text/plain");

			// String data =
			// "username=mlagda.sms&pin=fdIDaxVs&mnumber=919774124758&message=123123 is your
			// OTP to Login to Online Portal for Reservation of Meghalaya House
			// Accommodation. Do not share this OTP with anyone for security
			// reasons.Regards,
			// GAD(A)&signature=MLGADA&dlt_entity_id=1401504830000041324&dlt_template_id=1407170108988285837";
			String data = "username=" + getNotification(messageId).getUsername() + "&pin="
					+ getNotification(messageId).getPin() + "&mnumber=91" + recipientMobileNo + "&message=" + message2
					+ "&signature=" + getNotification(messageId).getSignature() + "&dlt_entity_id="
					+ getNotification(messageId).getEntityId() + "&dlt_template_id="
					+ getNotification(messageId).getTemplateId();
			byte[] out = data.getBytes(StandardCharsets.UTF_8);
			try (OutputStream stream = http.getOutputStream()) {
				stream.write(out);
			}

			// Get response code
			int responseCode = http.getResponseCode();

			// Read response
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()))) {
				String line;
				StringBuilder response = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				responseString = response.toString();
				//serviceAuditTrail.saveTrail(recipientMobileNo, null, "SMS response:"+responseString, messageId,
				//		coreService.getClientIp(request), "N/A");
				 
				 
			}
			// Close connection
			http.disconnect();
		} catch (IOException e) {
			//serviceAuditTrail.saveTrail(recipientMobileNo, null, "SMS response:"+responseString, messageId,
			//		coreService.getClientIp(request), "Failed");
			throw e;
		} catch (Exception e) {
			//serviceAuditTrail.saveTrail(recipientMobileNo, null, "SMS response:"+responseString, messageId,
			//		coreService.getClientIp(request), "Failed");
			throw e;
		}
	}

}
