package sopt.org.motivooServer.domain.parentchild.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.motivooServer.domain.parentchild.entity.Parentchild;

import java.util.Optional;


public interface ParentchildRepository extends JpaRepository<Parentchild, Long> {
    Optional<Parentchild> findByInviteCode(String inviteCode);

    void deleteById(Long id);

}
