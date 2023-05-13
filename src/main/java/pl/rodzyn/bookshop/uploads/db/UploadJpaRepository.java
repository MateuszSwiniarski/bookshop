package pl.rodzyn.bookshop.uploads.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rodzyn.bookshop.uploads.domain.Upload;

public interface UploadJpaRepository extends JpaRepository<Upload, Long> {
}
