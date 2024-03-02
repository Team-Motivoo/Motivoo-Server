package sopt.org.motivoo.domain.parentchild.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.motivoo.domain.parentchild.entity.Parentchild;


public interface ParentchildRepository extends JpaRepository<Parentchild, Long> {
    Optional<Parentchild> findByInviteCode(String inviteCode);
}
