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

    @Override
    public void removeById(Long id) {
        storage.remove(id);
    }

    private long nextID() {
        return ID_NEXT_VALUE.getAndIncrement();
    }
}
