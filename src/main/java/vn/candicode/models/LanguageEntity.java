package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import vn.candicode.models.enums.LanguageName;

import javax.persistence.*;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"name"})
public class LanguageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long languageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LanguageName name;
}
