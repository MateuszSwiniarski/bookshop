package pl.rodzyn.bookshop.catalog.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase;
import pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase.UpdateBookCoverCommand;
import pl.rodzyn.bookshop.catalog.domain.Book;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static pl.rodzyn.bookshop.catalog.application.port.CatalogUseCase.CreateBookCommand;

@RequestMapping("/catalog")
@RestController
@AllArgsConstructor
public class CatalogController {
    private final CatalogUseCase catalog;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Book> getAll(
            @RequestParam Optional<String> title,
            @RequestParam Optional<String> author) {
        if(title.isPresent() && author.isPresent()){
            return catalog.findByTitleAndAuthor(title.get(), author.get());
        }else if(title.isPresent()){
            return catalog.findByTitle(title.get());
        }else if(author.isPresent()){
            return catalog.findByAuthor(author.get());
        }
        return catalog.findAll();
    }

    @GetMapping(params = {"title"})
    @ResponseStatus(HttpStatus.OK)
    public List<Book> getAllFiltered(@RequestParam String title){
        return catalog.findByTitle(title);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable Long id){
        return catalog
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateBook(@PathVariable Long id, @RequestBody RestBookCommand command){
        CatalogUseCase.UpdateBookResponse response = catalog.updateBook(command.toUpdateCommand(id));
        if(!response.isSuccess()){
            String message = String.join(", ", response.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @PutMapping("/{id}/cover")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addBookCover(@PathVariable Long id, @RequestParam("file")MultipartFile file) throws IOException {
        System.out.println("Got file: " + file.getOriginalFilename());
        catalog.updateBookCover(new UpdateBookCoverCommand(
                id,
                file.getBytes(),
                file.getContentType(),
                file.getOriginalFilename()
        ));
    }

    @DeleteMapping("/{id}/cover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookCover(@PathVariable Long id) {
        catalog.removeBookCover(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addBook(@Valid @RequestBody RestBookCommand command){
        Book book = catalog.addBook(command.toCreateCommand());
        return ResponseEntity.created(createBookUri(book)).build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id){
        catalog.removeById(id);
    }

    private URI createBookUri(Book book) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/" + book.getId().toString()).build().toUri();
    }

    @Data
    private static class RestBookCommand {
        @NotBlank(message = "Please provide a title")
        private String title;

        @NotBlank(message = "Please provide an author")
        private String author;

        @NotNull
        private Integer year;

        @NotNull
        @DecimalMin("0.00")
        private BigDecimal price;

        CreateBookCommand toCreateCommand(){
            return new CreateBookCommand(
                    title, author, year, price
            );
        }

        UpdateBookCommand toUpdateCommand(Long id){
            return new UpdateBookCommand(id, title, author, year, price);
        }
    }
}
