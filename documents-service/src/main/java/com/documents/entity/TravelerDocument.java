import com.documents.entity.enums.DocumentStatus;
import com.documents.entity.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "traveler_document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelerDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_document")
    private Integer idDocument;

    @Column(name = "user_id")
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type")
    private DocumentType documentType;

    @Column(name = "file_url")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;
}