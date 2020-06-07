package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import vn.candicode.models.enums.LanguageName;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"name"})
@Entity
@Table(name = "languages")
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class LanguageEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long languageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NaturalId
    private LanguageName text;
}
