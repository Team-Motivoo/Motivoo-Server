package sopt.org.motivooServer.domain.parentchild.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.motivooServer.domain.parentchild.entity.Parentchild;

public interface ParentchildRepository extends JpaRepository<Parentchild, Long> {
}
