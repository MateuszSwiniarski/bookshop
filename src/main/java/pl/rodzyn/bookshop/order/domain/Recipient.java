package pl.rodzyn.bookshop.order.domain;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipient {
    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
    private String email;
}
