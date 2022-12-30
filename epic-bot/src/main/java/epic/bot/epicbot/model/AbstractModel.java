package epic.bot.epicbot.model;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.dao.DataIntegrityViolationException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(unique = true)
    private String mnemonic;

    public boolean isPersisted() {
        return this.id != 0;
    }

    public boolean allowDelete() {
        return this.mnemonic == null;
    }

    @PreRemove
    private void preDestroy() throws DataIntegrityViolationException {
        if (!allowDelete()) {
            throw new DataIntegrityViolationException(
                    "Não é possível excluir essa informação por ser uma informação de domínio.");
        }
    }

    @PrePersist
    private void prePersist() {
    }

    @PreUpdate
    private void preUpdate() {
    }

}
