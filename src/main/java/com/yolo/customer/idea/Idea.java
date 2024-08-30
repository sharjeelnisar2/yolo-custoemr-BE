package com.yolo.customer.idea;

import com.yolo.customer.idea.ideaStatus.IdeaStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Idea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", length = 64, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "code", length = 8, unique = true, nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_status_id", nullable = false)
    private IdeaStatus ideaStatus;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Defensive copy of mutable internal state
    public IdeaStatus getIdeaStatus() {
        return ideaStatus != null ? new IdeaStatus(ideaStatus) : null;
    }

    public void setIdeaStatus(IdeaStatus ideaStatus) {
        this.ideaStatus = ideaStatus != null ? new IdeaStatus(ideaStatus) : null;
    }

    // Copy constructor
    public Idea(Idea other) {
        if (other != null) {
            this.id = other.id;
            this.title = other.title;
            this.description = other.description;
            this.code = other.code;
            this.ideaStatus = other.ideaStatus != null ? new IdeaStatus(other.ideaStatus) : null;
            this.userId = other.userId;
            this.createdAt = other.createdAt;
            this.updatedAt = other.updatedAt;
        }
    }

    // Default constructor
    public Idea() {}
}
