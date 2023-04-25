package pl.rodzyn.bookshop.catalog.infrastructure;

import org.springframework.stereotype.Repository;
import pl.rodzyn.bookshop.catalog.domain.Book;
import pl.rodzyn.bookshop.catalog.domain.CatalogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
class MemoryCatalogRepository implements CatalogRepository {

    private Map<Long, Book> storage = new ConcurrentHashMap<>();
    private AtomicLong ID_NEXT_VALUE = new AtomicLong(0L);

    public MemoryCatalogRepository() {
//        storage.put(1L, new Book(1L, "Pan Tadeusz", "Adam Mickiewicz", 1983));
//        storage.put(2L, new Book(2L, "Chłopi", "Własysław Reymon", 1899));
//        storage.put(3L, new Book(3L, "Quo Vadis", "Henryk Sienkiewicz", 1954));
//        storage.put(4L, new Book(4L, "Pan Wołodyjowski", "Henryk Sienkiewicz", 1896));

//        storage.put(1L, new Book(1L, "Harry Potter i komnata tajemnic", "JK Rowlink", 1999));
//        storage.put(2L, new Book(2L, "Władca pierścieni i dwie wieże", "JRR Tolkien", 1987));
//        storage.put(3L, new Book(3L, "Mężczyżni, którzy nienawidzą kobiet", "Stieg Larsson", 1981));
//        storage.put(4L, new Book(4L, "Sezon Burz", "Andrzej Sapkowski", 2014));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void save(Book book) {
        if(book.getId() != null){
            storage.put(book.getId(), book);
        }else {
            long nextID = nextID();
            book.setId(nextID);
            storage.put(nextID, book);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    private long nextID() {
        return ID_NEXT_VALUE.getAndIncrement();
    }
}
