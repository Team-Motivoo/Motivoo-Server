package sopt.org.motivooServer.global.external.firebase;

import static sopt.org.motivooServer.global.advice.CommonExceptionType.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.global.advice.BusinessException;

@Slf4j
@Configuration
public class FirebaseConfig {

	@Value("${firebase.key.path}")
	private String SERVICE_ACCOUNT_JSON;

	@Value("${firebase.database}")
	private String FIREBASE_DB;

	@PostConstruct
	public void init() {
		try {
			ClassPathResource resource = new ClassPathResource(SERVICE_ACCOUNT_JSON);
			InputStream serviceAccount = resource.getInputStream();

			Map<String, Object> auth = new HashMap<String, Object>();
			auth.put("uid", "motivoo-user");

			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.setDatabaseUrl("https://"+FIREBASE_DB+".firebaseio.com")
				.setDatabaseAuthVariableOverride(null)
				.build();

			FirebaseApp.initializeApp(options);

			// getOpponentStepCount();

			log.info("파이어베이스 연결에 성공했습니다.");


		} catch (IOException e) {
			log.error("파이어베이스 서버와의 연결에 실패했습니다.");
			throw new BusinessException(FIREBASE_CONNECTION_ERROR);
		}
	}

	public void getOpponentStepCount(Long opponentUid, Long opponentStepCount) {
		DatabaseReference ref = FirebaseDatabase.getInstance()
			.getReference("/Users");

		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				DataSnapshot res = dataSnapshot.child(opponentUid.toString());
				log.info("Firebase DB에서 받아온 데이터: {}", res.getValue());
			}

			@Override
			public void onCancelled(DatabaseError error) {

			}
		});
	}
}