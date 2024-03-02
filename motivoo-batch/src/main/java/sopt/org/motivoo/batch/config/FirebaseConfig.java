package sopt.org.motivoo.batch.config;

import static sopt.org.motivoo.common.advice.CommonExceptionType.*;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.common.advice.BusinessException;

@Slf4j
@Configuration
public class FirebaseConfig {

	@Value("${firebase.key.path}")
	private String SERVICE_ACCOUNT_JSON;

	@Value("${firebase.database}")
	private String FIREBASE_DB;

	public static DatabaseReference ref;
	public static final String COLLECTION_NAME = "Users";

	@PostConstruct
	public void init() {
		log.info("Firebase 파일명: {}", SERVICE_ACCOUNT_JSON);
		try {
			ClassPathResource resource = new ClassPathResource(SERVICE_ACCOUNT_JSON);
			InputStream serviceAccount = resource.getInputStream();
			log.info("파일 가져오기 성공!");

			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.setDatabaseUrl("https://"+FIREBASE_DB+".firebaseio.com")
				.setDatabaseAuthVariableOverride(null)
				.build();

			FirebaseApp.initializeApp(options);
			log.info("파이어베이스 연결에 성공했습니다.");
			ref = FirebaseDatabase.getInstance().getReference(COLLECTION_NAME);

		} catch (IOException e) {
			log.error("파이어베이스 서버와의 연결에 실패했습니다.");
			throw new BusinessException(FIREBASE_CONNECTION_ERROR);
		}
	}
}
