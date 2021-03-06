package com.cryptominer.indonesia.domain;


import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A Withdraw.
 */
@Entity
@Table(name = "withdraw")
public class Withdraw implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "amount", precision=65, scale=8)
    private BigDecimal amount;

    @Column(name = "status")
    private String status;

    @Column(name = "fee", precision=65, scale=8)
    private BigDecimal fee;

    @Column(name = "type")
    private String type;

    @OneToOne
    @JoinColumn(unique = true)
    private WalletUsdTransaction walletUsdTransaction;

    @OneToOne
    @JoinColumn(unique = true)
    private WalletBtcTransaction walletBtcTransaction;

    @JsonProperty
    @Transient
    private String gauth;
    
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public Withdraw username(String username) {
        this.username = username;
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Withdraw amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public Withdraw status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public Withdraw fee(BigDecimal fee) {
        this.fee = fee;
        return this;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public WalletUsdTransaction getWalletUsdTransaction() {
        return walletUsdTransaction;
    }

    public Withdraw walletUsdTransaction(WalletUsdTransaction id) {
        this.walletUsdTransaction = id;
        return this;
    }

    public void setWalletUsdTransaction(WalletUsdTransaction id) {
        this.walletUsdTransaction = id;
    }
    
    
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    public String getGauth() {
		return gauth;
	}

	public void setGauth(String gauth) {
		this.gauth = gauth;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public WalletBtcTransaction getWalletBtcTransaction() {
		return walletBtcTransaction;
	}

	public void setWalletBtcTransaction(WalletBtcTransaction walletBtcTransaction) {
		this.walletBtcTransaction = walletBtcTransaction;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Withdraw withdraw = (Withdraw) o;
        if (withdraw.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), withdraw.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Withdraw{" +
            "id=" + getId() +
            ", username='" + getUsername() + "'" +
            ", amount=" + getAmount() +
            ", status='" + getStatus() + "'" +
            ", fee=" + getFee() +
            "}";
    }
}
