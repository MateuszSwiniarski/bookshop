package pl.rodzyn.bookshop.catalog.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
@Getter
public class Book {

    private final long id;
    private final String title;
    private final String author;
    private final int year;
}
