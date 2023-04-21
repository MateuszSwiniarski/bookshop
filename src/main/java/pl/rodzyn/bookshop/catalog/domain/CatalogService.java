package pl.rodzyn.bookshop.catalog.domain;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    CatologRepository repository;

    public CatalogService(CatologRepository repository){
        this.repository = repository;
    }

    public List<Book> findByTitle(String title){
        return repository.findAll()
                .stream()
                .filter(book -> book.title.startsWith(title))
                .collect(Collectors.toList());
    }
}
