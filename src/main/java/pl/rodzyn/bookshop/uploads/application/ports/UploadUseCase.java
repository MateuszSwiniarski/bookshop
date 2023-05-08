package pl.rodzyn.bookshop.uploads.application.ports;

import lombok.Value;
import pl.rodzyn.bookshop.uploads.domain.Upload;

public interface UploadUseCase {

    Upload save(SaveUploadCommand command);

    @Value
    class SaveUploadCommand {
        String filename;
        byte[] file;
        String contentType;
    }
}
