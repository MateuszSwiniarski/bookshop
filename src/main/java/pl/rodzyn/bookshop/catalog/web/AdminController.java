package pl.rodzyn.bookshop.catalog.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rodzyn.bookshop.catalog.application.port.CatalogInitializerUseCase;

import javax.transaction.Transactional;

@Slf4j
@RestController
@RequestMapping("/admin")
@AllArgsConstructor
class AdminController {

    private final CatalogInitializerUseCase initializer;

    @PostMapping("/initialization")
    @Transactional
    public void initialize() {
        initializer.initialize();
    }

}