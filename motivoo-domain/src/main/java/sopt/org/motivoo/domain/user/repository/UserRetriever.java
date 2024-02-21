package sopt.org.motivoo.domain.user.repository;

import static sopt.org.motivoo.domain.user.exception.UserExceptionType.*;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
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

	public String getRefreshTokenById(Long userId) {
		return userRepository.findRefreshTokenById(userId);
	}

	public String getAccessTokenById(Long userId) {
		return userRepository.findSocialAccessTokenById(userId);
	}

	public void saveUser(User newUser) {
		userRepository.save(newUser);
	}
}
