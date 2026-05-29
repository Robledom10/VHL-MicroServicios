import com.documental.entity.TravelerDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TravelerDocumentService {

    TravelerDocument uploadDocument(
            Integer userId,
            String documentType,
            MultipartFile file
    ) throws IOException;

    List<TravelerDocument> getUserDocuments(
            Integer userId
    );
}