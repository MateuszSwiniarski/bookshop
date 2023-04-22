package pl.rodzyn.bookshop.catalog.infrastructure;

import org.springframework.stereotype.Repository;
import pl.rodzyn.bookshop.catalog.domain.Book;
import pl.rodzyn.bookshop.catalog.domain.CatalogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
class BestsellerCatalogRepository implements CatalogRepository {
    private Map<Long, Book> storage = new ConcurrentHashMap<>();

    public BestsellerCatalogRepository() {
        storage.put(1L, new Book(1L, "Harry Potter i komnata tajemnic", "JK Rowlink", 1999));
        storage.put(2L, new Book(2L, "Władca pierścieni i dwie wieże", "JRR Tolkien", 1987));
        storage.put(3L, new Book(3L, "Mężczyżni, którzy nienawidzą kobiet", "Stieg Larsson", 1981));
        storage.put(4L, new Book(4L, "Sezon Burz", "Andrzej Sapkowski", 2014));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }
}
