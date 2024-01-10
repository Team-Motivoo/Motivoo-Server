package sopt.org.motivooServer.domain.parentchild.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.motivooServer.domain.parentchild.entity.Parentchild;

import java.util.Optional;

public interface ParentChildRepository extends JpaRepository<Parentchild, Long> {
    Parentchild findByInviteCode(String inviteCode);

}
