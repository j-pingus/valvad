package com.valvad.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.valvad.domain.enumeration.Quality;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Ad.
 */
@Entity
@Table(name = "ad")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "ad")
public class Ad implements Serializable {

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
    @Enumerated(EnumType.STRING)
    @Column(name = "quality", nullable = false)
    private Quality quality;

    @NotNull
    @Column(name = "price", nullable = false)
    private Double price;

    @OneToMany(mappedBy = "ad")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "ad" }, allowSetters = true)
    private Set<Photo> photos = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private User publisher;

    @ManyToOne
    @JsonIgnoreProperties(value = { "compatibility", "brand", "ads" }, allowSetters = true)
    private Part part;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Ad id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Ad description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Quality getQuality() {
        return this.quality;
    }

    public Ad quality(Quality quality) {
        this.setQuality(quality);
        return this;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }

    public Double getPrice() {
        return this.price;
    }

    public Ad price(Double price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Set<Photo> getPhotos() {
        return this.photos;
    }

    public void setPhotos(Set<Photo> photos) {
        if (this.photos != null) {
            this.photos.forEach(i -> i.setAd(null));
        }
        if (photos != null) {
            photos.forEach(i -> i.setAd(this));
        }
        this.photos = photos;
    }

    public Ad photos(Set<Photo> photos) {
        this.setPhotos(photos);
        return this;
    }

    public Ad addPhoto(Photo photo) {
        this.photos.add(photo);
        photo.setAd(this);
        return this;
    }

    public Ad removePhoto(Photo photo) {
        this.photos.remove(photo);
        photo.setAd(null);
        return this;
    }

    public User getPublisher() {
        return this.publisher;
    }

    public void setPublisher(User user) {
        this.publisher = user;
    }

    public Ad publisher(User user) {
        this.setPublisher(user);
        return this;
    }

    public Part getPart() {
        return this.part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public Ad part(Part part) {
        this.setPart(part);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ad)) {
            return false;
        }
        return id != null && id.equals(((Ad) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ad{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", quality='" + getQuality() + "'" +
            ", price=" + getPrice() +
            "}";
    }
}
