package sopt.org.motivooServer.global.external.firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.user.entity.User;
import sopt.org.motivooServer.domain.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseService {

	public static final String COLLECTION_NAME = "Users";
	private final UserRepository userRepository;

	public void insertUserStep() {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(COLLECTION_NAME);

		Map<String, Integer> userSteps = new HashMap<>();
		userRepository.findAllByDeleted(false)
				.forEach(user -> userSteps.put(user.getId().toString(), 0));

		ref.setValueAsync(userSteps);

		log.info("모든 활성 유저의 데이터 insert 성공!");
	}

	public void selectUserStep() throws Exception {

		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(COLLECTION_NAME);

		readAllData(ref);
	/*	List<String> ids = userRepository.findAllByDeleted(false).stream()
			.map(user -> user.getId().toString())
			.toList();

		readDataByIds(ids, ref);*/
	}

	private static void readDataByIds(List<String> ids, DatabaseReference ref) {
		for (String id : ids) {
			ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					String key = dataSnapshot.getKey();
					Object value = dataSnapshot.getValue();
					System.out.println(key + " : " + value.toString());
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					System.out.println("Failed to read value." + databaseError.toException());
				}
			});
		}
	}

	private static void readAllData(DatabaseReference ref) {
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
					String key = childSnapshot.getKey();
					Object value = childSnapshot.getValue();
					System.out.println(key + " : " + value.toString());
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				System.out.println("Failed to read value." + databaseError.toException());
			}
		});
	}
}
