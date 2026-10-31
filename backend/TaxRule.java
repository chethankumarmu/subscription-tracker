package com.subscriptiontracker.tax;

import jakarta.persistence.*;

@Entity
@Table(name = "tax_rules")
public class TaxRule {

    public enum Type { GST, VAT, SALES }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    @Column(name = "rate_basis_points", nullable = false) private int rateBasisPoints; // 1800 = 18%
    @Enumerated(EnumType.STRING) @Column(name = "tax_type", nullable = false) private Type taxType;
    @Column(name = "is_default", nullable = false) private boolean defaultRule;
    @Column(nullable = false) private boolean active = true;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getRateBasisPoints() { return rateBasisPoints; }
    public void setRateBasisPoints(int rateBasisPoints) { this.rateBasisPoints = rateBasisPoints; }
    public Type getTaxType() { return taxType; }
    public void setTaxType(Type taxType) { this.taxType = taxType; }
    public boolean isDefaultRule() { return defaultRule; }
    public void setDefaultRule(boolean defaultRule) { this.defaultRule = defaultRule; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
