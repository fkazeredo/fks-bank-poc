package com.fksoftwares.fksbank.creditcard

enum Category {

    PAYMENT("Pagamento"),
    HOME("Casa"),
    EDUCATION("Educação"),
    ELECTRONICS("Eletrônicos"),
    RECREATION("Lazer"),
    OTHERS("Outros"),
    RESTAURANT("Restaurante"),
    HEALTH("Saúde"),
    SERVICES("Serviços"),
    SUPERMARKET("Supermercado"),
    TRANSPORT("Transporte"),
    CLOTHING("Vestuário"),
    TRAVEL("Viagem"),
    INTEREST("Juros")

    private String description

    Category(String description) {
        this.description = description
    }

    String getDescription() {
        return description
    }
}