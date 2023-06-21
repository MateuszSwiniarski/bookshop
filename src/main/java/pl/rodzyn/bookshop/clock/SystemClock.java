package pl.rodzyn.bookshop.clock;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
class SystemClock implements Clock {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
