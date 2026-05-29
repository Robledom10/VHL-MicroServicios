import com.documents.entity.enums.ValidationResult;
import com.documents.entity.enums.ValidationSource;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "document_validation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentValidation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_validation")
    private Integer idValidation;

    @ManyToOne
    @JoinColumn(name = "fk_id_document")
    private TravelerDocument travelerDocument;

    @Enumerated(EnumType.STRING)
    private ValidationResult result;

    @Enumerated(EnumType.STRING)
    private ValidationSource source;

    private String observations;
}