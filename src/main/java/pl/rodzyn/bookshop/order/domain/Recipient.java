package pl.rodzyn.bookshop.order.domain;

import lombok.*;

import javax.persistence.Embeddable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Recipient {
    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
    private String email;
}
