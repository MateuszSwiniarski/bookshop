package pl.rodzyn.bookshop.order.domain;

import lombok.*;
import pl.rodzyn.bookshop.jpa.BaseEntity;

import javax.persistence.Entity;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Recipient extends BaseEntity {
    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
    private String email;

}
