import com.documental.entity.TravelerDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelerDocumentRepository
        extends JpaRepository<TravelerDocument, Integer> {

    List<TravelerDocument> findByUserId(Integer userId);
}