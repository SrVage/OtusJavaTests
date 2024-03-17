package ru.otus.bank.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {

    private long id;
    private BigDecimal amount;

    private Integer type;

    private String number;

    private Long agreementId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(Long agreementId) {
        this.agreementId = agreementId;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", amount=" + amount +
                ", type=" + type +
                ", number='" + number + '\'' +
                ", agreementId=" + agreementId +
                "}\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id && Objects.equals(amount, account.amount) && Objects.equals(type, account.type) && Objects.equals(number, account.number) && Objects.equals(agreementId, account.agreementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, type, number, agreementId);
    }
}
