package sopt.org.motivooServer.domain.user.service;

import static sopt.org.motivooServer.domain.user.exception.UserExceptionType.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sopt.org.motivooServer.domain.user.dto.response.MyPageInfoResponse;
import sopt.org.motivooServer.domain.user.entity.User;
import sopt.org.motivooServer.domain.user.exception.UserException;
import sopt.org.motivooServer.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final HealthRepository healthRepository;

	public MyPageInfoResponse getMyPage(final Long userId) {
		User user = getUserById(userId);
		return MyPageInfoResponse.of(user.getUsername());
	}

	public MyPageInfoResponse getMyInfo(final Long userId) {
		User user = getUserById(userId);
		return MyPageInfoResponse.of(user.getUsername(), user.getAge());
	}

	public MyPageInfoResponse getMyHealthInfo(final Long userId) {

	}

	private User getUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(
			() -> new UserException(USER_NOT_FOUND)
		);
	}
}
