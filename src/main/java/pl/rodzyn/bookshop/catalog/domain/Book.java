package pl.rodzyn.bookshop.catalog.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@RequiredArgsConstructor
@Getter
@Setter
public class Book {

    private Long id;
    private String title;
    private String author;
    private Integer year;
    private BigDecimal price;

    public Book(String title, String author, int year, BigDecimal price) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.price = price;
    }
}
