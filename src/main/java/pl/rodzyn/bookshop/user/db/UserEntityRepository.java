package pl.rodzyn.bookshop.user.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rodzyn.bookshop.user.domain.UserEntity;

import java.util.*;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameIgnoreCase(String username);
}
