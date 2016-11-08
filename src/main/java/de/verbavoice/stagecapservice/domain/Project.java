package de.verbavoice.stagecapservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Project.
 */
@Entity
@Table(name = "project")
@Document(indexName = "project")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "image")
    private byte[] image;

    @Column(name = "image_content_type")
    private String imageContentType;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "playing")
    private Boolean playing;

    @Column(name = "free")
    private Boolean free;

    @Column(name = "seeker")
    private Long seeker;

    @CreatedBy
    @Column(name = "user")
    private String user;

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    private Set<Subtitle> subtitles = new HashSet<>();

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    private Set<AudioDescription> audioDescriptions = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean isPlaying() {
        return playing;
    }

    public void setPlaying(Boolean playing) {
        this.playing = playing;
    }

    public Boolean isFree() {
        return free;
    }

    public void setFree(Boolean free) {
        this.free = free;
    }

    public Long getSeeker() {
        return seeker;
    }

    public void setSeeker(Long seeker) {
        this.seeker = seeker;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Set<Subtitle> getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(Set<Subtitle> subtitles) {
        this.subtitles = subtitles;
    }

    public Set<AudioDescription> getAudioDescriptions() {
        return audioDescriptions;
    }

    public void setAudioDescriptions(Set<AudioDescription> audioDescriptions) {
        this.audioDescriptions = audioDescriptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Project project = (Project) o;
        if(project.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Project{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", description='" + description + "'" +
            ", image='" + image + "'" +
            ", imageContentType='" + imageContentType + "'" +
            ", active='" + active + "'" +
            ", playing='" + playing + "'" +
            ", free='" + free + "'" +
            ", seeker='" + seeker + "'" +
            ", user='" + user + "'" +
            '}';
    }
}
