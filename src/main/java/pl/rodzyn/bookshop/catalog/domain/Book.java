package pl.rodzyn.bookshop.catalog.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@RequiredArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Book {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private Integer year;
    private BigDecimal price;
    private Long coverId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable
    private Set<Author> authors;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedat;

    public Book(String title, int year, BigDecimal price) {
        this.title = title;
        this.year = year;
        this.price = price;
    }
}
