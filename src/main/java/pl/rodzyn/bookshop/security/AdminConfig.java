package pl.rodzyn.bookshop.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.stream.*;

@Data
@ConfigurationProperties("app.security.admin")
public class AdminConfig {
    private String username;
    private String password;
    private Set<String> roles;

    User adminUser() {
        return new User(
                username,
                password,
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet())
        );
    }
}
