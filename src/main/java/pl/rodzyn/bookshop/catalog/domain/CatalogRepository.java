package pl.rodzyn.bookshop.catalog.domain;

import java.util.List;

public interface CatalogRepository {

    List<Book> findAll();
}
