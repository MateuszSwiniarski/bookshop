package pl.rodzyn.bookshop.uploads.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@AllArgsConstructor
public class Upload {
    String id;
    byte[] file;
    String contentType;
    String filename;
    LocalDateTime createdAt;
}
