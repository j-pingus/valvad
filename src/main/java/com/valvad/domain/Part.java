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
 * A Part.
 */
@Entity
@Table(name = "part")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "part")
public class Part implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "number", nullable = false)
    private String number;

    @ManyToOne
    @JsonIgnoreProperties(value = { "parts", "models" }, allowSetters = true)
    private Compatibility compatibility;

    @ManyToOne
    @JsonIgnoreProperties(value = { "parts" }, allowSetters = true)
    private Brand brand;

    @OneToMany(mappedBy = "part")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "photos", "publisher", "part" }, allowSetters = true)
    private Set<Ad> ads = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Part id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Part description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumber() {
        return this.number;
    }

    public Part number(String number) {
        this.setNumber(number);
        return this;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Compatibility getCompatibility() {
        return this.compatibility;
    }

    public void setCompatibility(Compatibility compatibility) {
        this.compatibility = compatibility;
    }

    public Part compatibility(Compatibility compatibility) {
        this.setCompatibility(compatibility);
        return this;
    }

    public Brand getBrand() {
        return this.brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Part brand(Brand brand) {
        this.setBrand(brand);
        return this;
    }

    public Set<Ad> getAds() {
        return this.ads;
    }

    public void setAds(Set<Ad> ads) {
        if (this.ads != null) {
            this.ads.forEach(i -> i.setPart(null));
        }
        if (ads != null) {
            ads.forEach(i -> i.setPart(this));
        }
        this.ads = ads;
    }

    public Part ads(Set<Ad> ads) {
        this.setAds(ads);
        return this;
    }

    public Part addAd(Ad ad) {
        this.ads.add(ad);
        ad.setPart(this);
        return this;
    }

    public Part removeAd(Ad ad) {
        this.ads.remove(ad);
        ad.setPart(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Part)) {
            return false;
        }
        return id != null && id.equals(((Part) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Part{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", number='" + getNumber() + "'" +
            "}";
    }
}
