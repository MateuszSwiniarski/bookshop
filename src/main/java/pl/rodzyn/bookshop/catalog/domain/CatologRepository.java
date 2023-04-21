package pl.rodzyn.bookshop.catalog.domain;

import java.util.List;

public interface CatologRepository {

    List<Book> findAll();
}
