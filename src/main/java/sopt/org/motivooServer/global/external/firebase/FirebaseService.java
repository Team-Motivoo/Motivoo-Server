package sopt.org.motivooServer.global.external.firebase;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.remoteconfig.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseService {

	public static final String COLLECTION_NAME = "users";

	private final FirebaseConfig firebaseConfig;

	/*public void insertUser() throws Exception {
		Firestore db = FirestoreClient.getFirestore();
		User user = new User();
		user.setId("4444");
		user.setName("4444");
		ApiFuture<WriteResult> apiFuture = db.collection(COLLECTION_NAME).document("user_4").set(user);

		log.info(apiFuture.get().getUpdateTime().toString());
	}*/

	public void selectUser() throws Exception {

		Firestore db = FirestoreClient.getFirestore();
		User user = null;
		ApiFuture<DocumentSnapshot> apiFuture = db.collection(COLLECTION_NAME).document("user_4").get();
		DocumentSnapshot documentSnapshot = apiFuture.get();

		if (documentSnapshot.exists()) {
			user = documentSnapshot.toObject(User.class);
			log.info(user.toString());
		}
	}
}
