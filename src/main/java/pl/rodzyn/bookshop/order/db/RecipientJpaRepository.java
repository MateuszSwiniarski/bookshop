package pl.rodzyn.bookshop.order.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rodzyn.bookshop.order.domain.Recipient;

import java.util.Optional;

public interface RecipientJpaRepository extends JpaRepository<Recipient, Long> {
    Optional<Recipient> findByEmailIgnoreCase(String email);
}
