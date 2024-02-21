package sopt.org.motivoo.domain.parentchild.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.motivoo.domain.parentchild.entity.Parentchild;


public interface ParentchildRepository extends JpaRepository<Parentchild, Long> {
    Parentchild findByInviteCode(String inviteCode);
    void deleteById(Long id);

}
