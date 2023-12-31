package pl.rodzyn.bookshop.user.application.port;

import pl.rodzyn.bookshop.commons.Either;
import pl.rodzyn.bookshop.user.domain.UserEntity;

public interface UserRegisterUseCase {

    RegisterResponse register(String username, String password);

    class RegisterResponse extends Either<String, UserEntity> {

        public RegisterResponse(boolean success, String left, UserEntity right) {
            super(success, left, right);
        }

        public static RegisterResponse success(UserEntity right) {
            return new RegisterResponse(true, null, right);
        }

        public static RegisterResponse failure(String error) {
            return new RegisterResponse(false, error, null);
        }
    }
}

