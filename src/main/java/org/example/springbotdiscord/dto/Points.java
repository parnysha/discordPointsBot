package org.example.springbotdiscord.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Points {
    @Id
    @Column(nullable = false)
    private long userId;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private long pointsBalance;
    @Column
    private long pointsStart;
}
