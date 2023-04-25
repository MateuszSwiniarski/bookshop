package pl.rodzyn.bookshop.catalog.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
@Getter
@Setter
public class Book {

    private Long id;
    private String title;
    private String author;
    private Integer year;

    public Book(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }
}
