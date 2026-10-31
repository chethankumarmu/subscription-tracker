package com.subscriptiontracker.subscription;

import jakarta.persistence.*;

@Entity
@Table(name = "quotation_templates")
public class QuotationTemplate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    @Column(name = "validity_days", nullable = false) private int validityDays;
    @Column(name = "terms_text") private String termsText;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getValidityDays() { return validityDays; }
    public void setValidityDays(int validityDays) { this.validityDays = validityDays; }
    public String getTermsText() { return termsText; }
    public void setTermsText(String termsText) { this.termsText = termsText; }
}
