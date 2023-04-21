package pl.rodzyn.bookshop;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    public CatalogService(){
        storage.put(1L, new Book(1L, "Pan Tadeusz", "Henryk Sienkiewicz", 1983));
        storage.put(2L, new Book(2L, "Chłopi", "Własysław Reymon", 1899));
        storage.put(3L, new Book(3L, "Quo Vadis", "Heneryk Sienkiewicz", 1954));
    }

    private Map<Long, Book> storage = new ConcurrentHashMap<>();

    List<Book> findByTitle(String title){
        return storage.values()
                .stream()
                .filter(book -> book.title.startsWith(title))
                .collect(Collectors.toList());
    }
}
