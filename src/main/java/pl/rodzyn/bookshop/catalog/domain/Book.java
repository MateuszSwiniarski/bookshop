package pl.rodzyn.bookshop.catalog.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@ToString
@RequiredArgsConstructor
@Getter
@Setter
@Entity
public class Book {
    @Id
    private Long id;
    private String title;
    private String author;
    private Integer year;
    private BigDecimal price;
    private String coverId;

    public Book(String title, String author, int year, BigDecimal price) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.price = price;
    }
}
