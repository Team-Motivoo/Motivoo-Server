package sopt.org.motivoo.domain.parentchild.repository;

import static sopt.org.motivoo.domain.parentchild.exception.ParentchildExceptionType.*;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sopt.org.motivoo.domain.parentchild.entity.Parentchild;
import sopt.org.motivoo.domain.parentchild.exception.ParentchildException;

@Component
@RequiredArgsConstructor
public class ParentchildRetriever {

	private final ParentchildRepository parentchildRepository;

	public void saveParentchild(Parentchild parentchild) {
		parentchildRepository.save(parentchild);
	}

	public Parentchild getByInviteCode(String inviteCode) {
		return parentchildRepository.findByInviteCode(inviteCode).orElseThrow(
			() -> new ParentchildException(INVITE_CODE_NOT_FOUND));
	}

	public void deleteById(Long parentchildId) {
		parentchildRepository.deleteById(parentchildId);
	}
}
