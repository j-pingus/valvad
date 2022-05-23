package com.valvad.domain;

import com.valvad.domain.enumeration.Right;
import com.valvad.domain.enumeration.Subject;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Permit.
 */
@Entity
@Table(name = "permit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "permit")
public class Permit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * identifier of the permit object\ni.e. Part-1 or Model-9999
     */
    @Schema(description = "identifier of the permit object\ni.e. Part-1 or Model-9999", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "subject", nullable = false)
    private Subject subject;

    @NotNull
    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "jhi_right", nullable = false)
    private Right right;

    @ManyToOne
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Permit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public Permit subject(Subject subject) {
        this.setSubject(subject);
        return this;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Long getSubjectId() {
        return this.subjectId;
    }

    public Permit subjectId(Long subjectId) {
        this.setSubjectId(subjectId);
        return this;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Right getRight() {
        return this.right;
    }

    public Permit right(Right right) {
        this.setRight(right);
        return this;
    }

    public void setRight(Right right) {
        this.right = right;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Permit user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Permit)) {
            return false;
        }
        return id != null && id.equals(((Permit) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Permit{" +
            "id=" + getId() +
            ", subject='" + getSubject() + "'" +
            ", subjectId=" + getSubjectId() +
            ", right='" + getRight() + "'" +
            "}";
    }
}
