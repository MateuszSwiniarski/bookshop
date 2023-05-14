package pl.rodzyn.bookshop.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.rodzyn.bookshop.catalog.domain.Book;
import java.util.List;
import java.util.Optional;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

    List<Book> findByAuthors_firstNameContainsIgnoreCaseOrAuthors_lastNameContainsIgnoreCase(String firstName, String lastName);

    List<Book> findByTitleContainsIgnoreCase(String title);

    Optional<Book> findDistinctFirstByTitleContainsIgnoreCase(String title);

    @Query(" SELECT b FROM Book b JOIN b.authors a " +
            " WHERE " +
            " lower(a.firstName) LIKE lower(concat('%', :name, '%')) " +
            " OR lower(a.lastName) LIKE lower(concat('%', :name, '%')) ")
    List<Book> findByAuthor(@Param("name") String name);

    @Query(" SELECT b FROM Book b JOIN b.authors a " +
            " WHERE " +
            " lower(b.title) LIKE lower(concat('%', :title, '%' )) " +
            " AND " +
            " (lower(a.firstName) LIKE lower(concat('%', :author, '%' )) " +
            " OR lower(a.lastName) LIKE lower(concat('%', :author, '%' ))) ")
    List<Book> findByTitleAndAuthor(String title, String author);

    List<Book> findByTitleContainsIgnoreCaseAndAuthors_firstNameContainsIgnoreCase(String title, String author);


}
