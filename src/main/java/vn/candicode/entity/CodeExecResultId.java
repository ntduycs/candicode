package vn.candicode.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Embeddable
@AllArgsConstructor
public class CodeExecResultId implements Serializable {
    @ManyToOne
    @JoinColumn(name = "challenge_config_id", nullable = false, foreignKey = @ForeignKey(name = "challenge_config_fk"))
    private ChallengeConfigurationEntity challenge;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "user_fk"))
    private UserEntity user;

    @Column(nullable = false, updatable = false)
    private String submitAt;

    public CodeExecResultId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CodeExecResultId that = (CodeExecResultId) o;

        if (!challenge.getChallengeLanguageId().equals(that.challenge.getChallengeLanguageId())) return false;
        if (!user.getUserId().equals(that.user.getUserId())) return false;
        return submitAt.equals(that.submitAt);
    }

    @Override
    public int hashCode() {
        int result = challenge.getChallengeLanguageId().hashCode();
        result = 31 * result + user.getUserId().hashCode();
        result = 31 * result + submitAt.hashCode();
        return result;
    }
}
