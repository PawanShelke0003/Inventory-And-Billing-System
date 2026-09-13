package com.example.demo.DTO;

import java.math.BigDecimal;

public class AnalyticsDTO {

    private String label;
    private BigDecimal value;


    public AnalyticsDTO() {}


    public AnalyticsDTO(String label, BigDecimal value) {
        this.label = label;
        this.value = value;
    }


    public AnalyticsDTO(String label, Long value) {
        this.label = label;
        this.value = value != null ? new BigDecimal(value) : BigDecimal.ZERO;
    }

    public String getLabel() { return label; }
    public BigDecimal getValue() { return value; }
    public void setLabel(String label) { this.label = label; }
    public void setValue(BigDecimal value) { this.value = value; }
}
