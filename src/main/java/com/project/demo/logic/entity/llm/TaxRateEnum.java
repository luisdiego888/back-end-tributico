package com.project.demo.logic.entity.llm;

public enum TaxRateEnum {
    TAX_RATE_GENERAL(13.0),
    TAX_RATE_REDUCED(10.0),
    TAX_RATE_SPECIAL(4.0),
    TAX_RATE_ADJUSTED(2.0),
    TAX_RATE_MINIMUM(1.0),
    TAX_RATE_ZERO(0.0);

    private final double rate;

    TaxRateEnum(double rate) {
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }
}
