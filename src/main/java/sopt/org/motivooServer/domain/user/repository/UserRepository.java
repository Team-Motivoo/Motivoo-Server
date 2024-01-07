package sopt.org.motivooServer.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.motivooServer.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
