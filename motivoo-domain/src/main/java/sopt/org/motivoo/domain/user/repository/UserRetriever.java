package sopt.org.motivoo.domain.user.repository;

import static sopt.org.motivoo.domain.parentchild.exception.ParentchildExceptionType.*;
import static sopt.org.motivoo.domain.user.exception.UserExceptionType.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.domain.parentchild.entity.Parentchild;
import sopt.org.motivoo.domain.parentchild.exception.ParentchildException;
import sopt.org.motivoo.domain.user.entity.User;
import sopt.org.motivoo.domain.user.exception.UserException;

@Component
@RequiredArgsConstructor
public class UserRetriever {

	private final UserRepository userRepository;

	public User getUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(
			() -> new UserException(USER_NOT_FOUND)
		);
	}

	public List<User> getUsersBySocialId(String socialId) {
		return userRepository.findBySocialId(socialId);
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public String getRefreshTokenById(Long userId) {
		return userRepository.findRefreshTokenById(userId);
	}

	public String getAccessTokenById(Long userId) {
		return userRepository.findSocialAccessTokenById(userId);
	}

	public void saveUser(User newUser) {
		userRepository.save(newUser);
	}


	// 자신과 매칭된 부모자녀 유저 조회
	public User getMatchedUserWith(User user) {
		return userRepository.findByIdAndParentchild(user.getId(), user.getParentchild()).orElseThrow(
			() -> new ParentchildException(NOT_EXIST_PARENTCHILD_USER));
	}

	public List<User> getUsersByParentchildId(Long parentchildId) {
		return userRepository.findUsersByParentchildId(parentchildId);
	}

	public List<User> getUserByParentchild(Parentchild parentchild) {
		return userRepository.findUserByParentchild(parentchild);
	}

	public List<User> getAllByParentchild(Parentchild parentchild) {
		return userRepository.findByParentchild(parentchild);
	}

	public List<User> getUsersByIds(Long myUserId, Long opponentUserId) {
		return userRepository.findAllByIds(Arrays.asList(myUserId, opponentUserId));
	}

	public int getMatchedCnt(Parentchild parentchild) {
		return userRepository.countByParentchild(parentchild);
	}

	public Long getOpponentUserId(Parentchild parentchild, Long userId) {
		return userRepository.findOpponentId(parentchild, userId);
	}

	public void deleteById(Long userId) {
		userRepository.deleteById(userId);
	}

	public List<User> deleteExpiredUsers() {
		return userRepository.deleteExpiredUser();
	}

	public Optional<User> getByIdAndParentchild(Long userId, Parentchild parentchild) {
		return userRepository.findByIdAndParentchild(userId, parentchild);
	}
}
