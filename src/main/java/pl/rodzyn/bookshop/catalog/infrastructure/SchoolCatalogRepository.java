package pl.rodzyn.bookshop.catalog.infrastructure;

import org.springframework.stereotype.Repository;
import pl.rodzyn.bookshop.catalog.domain.Book;
import pl.rodzyn.bookshop.catalog.domain.CatalogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
class SchoolCatalogRepository implements CatalogRepository {

    private Map<Long, Book> storage = new ConcurrentHashMap<>();

    public SchoolCatalogRepository() {
        storage.put(1L, new Book(1L, "Pan Tadeusz", "Adam Mickiewicz", 1983));
        storage.put(2L, new Book(2L, "Chłopi", "Własysław Reymon", 1899));
        storage.put(3L, new Book(3L, "Quo Vadis", "Heneryk Sienkiewicz", 1954));
        storage.put(4L, new Book(4L, "Pan Wołodyjowski", "Henryk Sienkiewicz", 1896));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }
}
