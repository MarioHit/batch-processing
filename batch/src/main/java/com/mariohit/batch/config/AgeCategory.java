package com.mariohit.batch.config;

public enum AgeCategory {
    DIZAINE("Dizaine"),
    VINGTAINE("Vingtaine"),
    TRENTAINE("Trentaine"),
    QUARANTAINE("Quarantaine"),
    CINQUANTAINE_ET_PLUS("Cinquantaine et plus"),
    AGE_INCONNU("Age Inconnu");

    private final String category;

    AgeCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}
