package com.projectdata.transaction.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "alert")
@Getter
@Setter
@NoArgsConstructor
public class Alert {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Column(name = "wallet_address", nullable = false)
    private String walletAddress;

    @Column(name = "chain", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private WalletChain chain;

    @Column(name = "alert_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AlertType alertType;

    @Column(name = "message", nullable = false)
    private String message;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
