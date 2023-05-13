package pl.rodzyn.bookshop.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rodzyn.bookshop.catalog.domain.Book;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

}
