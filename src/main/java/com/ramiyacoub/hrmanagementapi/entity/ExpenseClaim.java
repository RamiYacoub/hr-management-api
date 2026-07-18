package com.ramiyacoub.hrmanagementapi.entity;

import com.ramiyacoub.hrmanagementapi.enums.ExpenseClaimStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expense_claims")
@Getter
@Setter
@NoArgsConstructor
public class ExpenseClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false,  length = 1000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseClaimStatus status = ExpenseClaimStatus.DRAFT;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @OneToMany(
            mappedBy = "expenseClaim",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ExpenseClaimEntry> entries = new ArrayList<>();

    public void addEntry(ExpenseClaimEntry entry) {
        entries.add(entry);
        entry.setExpenseClaim(this);
    }

    public void removeEntry(ExpenseClaimEntry entry) {
        entries.remove(entry);
        entry.setExpenseClaim(null);
    }

}
