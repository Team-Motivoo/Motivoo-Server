package sopt.org.motivoo.domain.user.service;

import static sopt.org.motivoo.domain.user.exception.UserExceptionType.*;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import sopt.org.motivoo.domain.user.entity.User;
import sopt.org.motivoo.domain.user.exception.UserException;

@Slf4j
@Component
@Transactional(readOnly = true)
public class UserManager {

	@Transactional
	public void deleteKakaoAccount(List<User> users){

		users.stream()
			.filter(u -> !u.isDeleted())
			.peek(u -> {
				u.updateDeleted();
				u.updateDeleteAt();
				u.deleteSocialInfo();

				log.info("유저 정보=" + u.isDeleted() + " " + u.getDeletedAt());
				u.updateRefreshToken(null);
			})
			.findFirst().orElseThrow(
				() -> new UserException(ALREADY_WITHDRAW_USER));

		//sendRevokeRequest(null, KAKAO, "FHdk_lLY2GObLpez2qGCdmqDlMBwwDk7FXgKPXTZAAABjRVYY2X6Fwx8Dt1GgQ"); //카카오 연결 해제
	}
}
