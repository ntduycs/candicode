package vn.candicode.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {})
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public abstract class PartialBaseModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @CreatedDate
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss.SS")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss.SS")
    private LocalDateTime updatedAt;

    @JsonIgnore
    private LocalDateTime deletedAt;
}
