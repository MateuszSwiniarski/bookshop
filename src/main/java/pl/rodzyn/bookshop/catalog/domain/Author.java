package pl.rodzyn.bookshop.catalog.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class Author {
    @Id
    @GeneratedValue
    private Long id;

    private String firstName;

    private String lastName;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "authors")
    private Set<Book> books;

    @CreatedDate
    private LocalDateTime createdAt;
}
