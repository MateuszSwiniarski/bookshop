package pl.rodzyn.bookshop.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.rodzyn.bookshop.jpa.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = "authors")
@RequiredArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Book extends BaseEntity {
    @Column(unique = true)
    private String title;
    private Integer year;
    private BigDecimal price;
    private Long coverId;
    private Long available;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable
    @JsonIgnoreProperties("books")
    private Set<Author> authors = new HashSet<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedat;

    public Book(String title, int year, BigDecimal price, Long available) {
        this.title = title;
        this.year = year;
        this.price = price;
        this.available = available;
    }
    
    public void addAuthor(Author author){
        authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author author){
        authors.remove(author);
        author.getBooks().remove(this);
    }

    public void removeAuthors(){
        Book self = this;
        authors.forEach(author -> author.getBooks().remove(self));
        authors.clear();
    }
}
