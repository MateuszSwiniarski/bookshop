package pl.rodzyn.bookshop.user.application;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.rodzyn.bookshop.user.db.UserEntityRepository;
import pl.rodzyn.bookshop.user.domain.UserEntity;
import pl.rodzyn.bookshop.user.application.port.UserRegistrationUseCase;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class UserService implements UserRegistrationUseCase {

    private final UserEntityRepository repository;
    private final PasswordEncoder encoder;

    @Transactional
    @Override
    public RegisterResponse register(String username, String password) {
        if(repository.findByUsernameIgnoreCase(username).isPresent()) {
            return RegisterResponse.failure("Account already exists");
        }
        UserEntity entity = new UserEntity(username, encoder.encode(password));
        return RegisterResponse.success(repository.save(entity));
    }
}
