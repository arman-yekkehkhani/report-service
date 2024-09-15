package edu.personal.report.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    @Id
    UUID uuid;

    @Column(nullable = false)
    String title;

    @Column
    String description;

    @ManyToOne
    User reporter;
}
