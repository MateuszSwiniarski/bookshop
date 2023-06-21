package pl.rodzyn.bookshop.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.rodzyn.bookshop.catalog.domain.Book;
import java.util.List;
import java.util.Optional;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

    @Query("SELECT DISTINCT b FROM Book b JOIN FETCH b.authors")
    List<Book> findAllEager();

    List<Book> findByAuthors_nameContainsIgnoreCase(String name);

    List<Book> findByTitleStartsWithIgnoreCase(String title);

    @Query(
            " SELECT b FROM Book b JOIN b.authors a " +
                    " WHERE " +
                    " lower(a.name) LIKE lower(concat('%', :name,'%')) "
    )
    List<Book> findByAuthor(@Param("name") String name);


    List<Book> findByTitleContainsIgnoreCase(String title);

    Optional<Book> findDistinctFirstByTitleContainsIgnoreCase(String title);

    @Query(" SELECT b FROM Book b JOIN b.authors a " +
            " WHERE " +
            " lower(b.title) LIKE lower(concat('%', :title, '%' )) " +
            " AND " +
            " lower(a.name) LIKE lower(concat('%', :name, '%' )) ")
    List<Book> findByTitleAndAuthor(String title, String name);

    List<Book> findByTitleContainsIgnoreCaseAndAuthors_nameContainsIgnoreCase(String title, String name);



}
