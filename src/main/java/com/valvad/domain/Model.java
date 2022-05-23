package com.valvad.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Model.
 */
@Entity
@Table(name = "model")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "model")
public class Model implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "years")
    private String years;

    @OneToMany(mappedBy = "model")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "part", "model" }, allowSetters = true)
    private Set<Compatibility> compatibilities = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Model id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Model name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYears() {
        return this.years;
    }

    public Model years(String years) {
        this.setYears(years);
        return this;
    }

    public void setYears(String years) {
        this.years = years;
    }

    public Set<Compatibility> getCompatibilities() {
        return this.compatibilities;
    }

    public void setCompatibilities(Set<Compatibility> compatibilities) {
        if (this.compatibilities != null) {
            this.compatibilities.forEach(i -> i.setModel(null));
        }
        if (compatibilities != null) {
            compatibilities.forEach(i -> i.setModel(this));
        }
        this.compatibilities = compatibilities;
    }

    public Model compatibilities(Set<Compatibility> compatibilities) {
        this.setCompatibilities(compatibilities);
        return this;
    }

    public Model addCompatibility(Compatibility compatibility) {
        this.compatibilities.add(compatibility);
        compatibility.setModel(this);
        return this;
    }

    public Model removeCompatibility(Compatibility compatibility) {
        this.compatibilities.remove(compatibility);
        compatibility.setModel(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Model)) {
            return false;
        }
        return id != null && id.equals(((Model) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Model{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", years='" + getYears() + "'" +
            "}";
    }
}
