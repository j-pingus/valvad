package com.valvad.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Photo.
 */
@Entity
@Table(name = "photo")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "photo")
public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "jhi_binary", nullable = false)
    private byte[] binary;

    @NotNull
    @Column(name = "jhi_binary_content_type", nullable = false)
    private String binaryContentType;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    @JsonIgnoreProperties(value = { "photos", "publisher", "part" }, allowSetters = true)
    private Ad ad;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Photo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getBinary() {
        return this.binary;
    }

    public Photo binary(byte[] binary) {
        this.setBinary(binary);
        return this;
    }

    public void setBinary(byte[] binary) {
        this.binary = binary;
    }

    public String getBinaryContentType() {
        return this.binaryContentType;
    }

    public Photo binaryContentType(String binaryContentType) {
        this.binaryContentType = binaryContentType;
        return this;
    }

    public void setBinaryContentType(String binaryContentType) {
        this.binaryContentType = binaryContentType;
    }

    public String getDescription() {
        return this.description;
    }

    public Photo description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Ad getAd() {
        return this.ad;
    }

    public void setAd(Ad ad) {
        this.ad = ad;
    }

    public Photo ad(Ad ad) {
        this.setAd(ad);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Photo)) {
            return false;
        }
        return id != null && id.equals(((Photo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Photo{" +
            "id=" + getId() +
            ", binary='" + getBinary() + "'" +
            ", binaryContentType='" + getBinaryContentType() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
