package sopt.org.motivooServer.domain.parentchild.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.motivooServer.domain.parentchild.entity.Parentchild;


public interface ParentchildRepository extends JpaRepository<Parentchild, Long> {
    Parentchild findByInviteCode(String inviteCode);
    void deleteById(Long id);

}
