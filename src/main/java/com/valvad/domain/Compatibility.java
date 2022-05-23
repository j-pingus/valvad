package com.valvad.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Compatibility.
 */
@Entity
@Table(name = "compatibility")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "compatibility")
public class Compatibility implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "compatibility")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "compatibility", "brand", "ads" }, allowSetters = true)
    private Set<Part> parts = new HashSet<>();

    @OneToMany(mappedBy = "compatibility")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "compatibility" }, allowSetters = true)
    private Set<Model> models = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Compatibility id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Part> getParts() {
        return this.parts;
    }

    public void setParts(Set<Part> parts) {
        if (this.parts != null) {
            this.parts.forEach(i -> i.setCompatibility(null));
        }
        if (parts != null) {
            parts.forEach(i -> i.setCompatibility(this));
        }
        this.parts = parts;
    }

    public Compatibility parts(Set<Part> parts) {
        this.setParts(parts);
        return this;
    }

    public Compatibility addPart(Part part) {
        this.parts.add(part);
        part.setCompatibility(this);
        return this;
    }

    public Compatibility removePart(Part part) {
        this.parts.remove(part);
        part.setCompatibility(null);
        return this;
    }

    public Set<Model> getModels() {
        return this.models;
    }

    public void setModels(Set<Model> models) {
        if (this.models != null) {
            this.models.forEach(i -> i.setCompatibility(null));
        }
        if (models != null) {
            models.forEach(i -> i.setCompatibility(this));
        }
        this.models = models;
    }

    public Compatibility models(Set<Model> models) {
        this.setModels(models);
        return this;
    }

    public Compatibility addModel(Model model) {
        this.models.add(model);
        model.setCompatibility(this);
        return this;
    }

    public Compatibility removeModel(Model model) {
        this.models.remove(model);
        model.setCompatibility(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Compatibility)) {
            return false;
        }
        return id != null && id.equals(((Compatibility) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Compatibility{" +
            "id=" + getId() +
            "}";
    }
}
