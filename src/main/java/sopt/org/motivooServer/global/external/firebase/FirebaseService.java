package sopt.org.motivooServer.global.external.firebase;

import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.*;
import static sopt.org.motivooServer.global.advice.CommonExceptionType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.motivooServer.domain.user.entity.User;
import sopt.org.motivooServer.domain.user.exception.UserException;
import sopt.org.motivooServer.domain.user.repository.UserRepository;
import sopt.org.motivooServer.global.advice.BusinessException;

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

	public void insertUserStepById(Long id) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(COLLECTION_NAME);

		Map<String, Integer> userSteps = new HashMap<>();
		userSteps.put(id.toString(), 0);

		ref.setValueAsync(userSteps);

		log.info("새로 가입한 유저의 데이터 insert 성공!");
	}

	public void selectAllUserStep() {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(COLLECTION_NAME);

		readAllData(ref);
	}

	public Map<String, Integer> selectUserStep(List<Long> ids) {
		try {
			final FirebaseDatabase database = FirebaseDatabase.getInstance();
			DatabaseReference ref = database.getReference(COLLECTION_NAME);

			return readDataByIds(ids.stream().map(Object::toString)
				.collect(Collectors.toList()), ref);
		} catch (InterruptedException e) {
			throw new BusinessException(FIREBASE_DB_READ_ERROR);
		}
	}

	public static Map<String, Integer> readDataByIds(List<String> ids, DatabaseReference ref) throws InterruptedException {
		Map<String, Integer> result = new HashMap<>();
		CountDownLatch latch = new CountDownLatch(ids.size());

		for (String id : ids) {
			ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					String key = dataSnapshot.getKey();
					Object value = dataSnapshot.getValue();
					log.info(key + " : " + value.toString());
					result.put(key, Integer.parseInt(value.toString()));
					latch.countDown();
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					System.out.println("Failed to read value." + databaseError.toException());
					latch.countDown();
				}
			});
		}

		latch.await();  // 모든 데이터를 받아올 때까지 기다립니다.
		log.info("FB에서 가져온 Map 사이즈: {}", result.size());
		return result;
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
