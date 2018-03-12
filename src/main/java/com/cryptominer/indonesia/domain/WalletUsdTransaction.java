package com.cryptominer.indonesia.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import com.cryptominer.indonesia.domain.enumeration.TransactionType;

/**
 * A WalletUsdTransaction.
 */
@Entity
@Table(name = "wallet_usd_transaction")
public class WalletUsdTransaction extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "amount", precision=10, scale=2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "jhi_type")
    private TransactionType type;

    @Column(name = "from_username")
    private String fromUsername;

    @Column(name = "txid")
    private String txid;

    @Column(name = "description")
    private String description;
    
    @Column(name = "status")
    private String status;
    
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

    public WalletUsdTransaction username(String username) {
        this.username = username;
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public WalletUsdTransaction amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public WalletUsdTransaction type(TransactionType type) {
        this.type = type;
        return this;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public WalletUsdTransaction fromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
        return this;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getTxid() {
        return txid;
    }

    public WalletUsdTransaction txid(String txid) {
        this.txid = txid;
        return this;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getDescription() {
		return description;
	}
    
    public void setDescription(String description) {
		this.description = description;
	}
    
    
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WalletUsdTransaction walletUsdTransaction = (WalletUsdTransaction) o;
        if (walletUsdTransaction.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), walletUsdTransaction.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "WalletUsdTransaction{" +
            "id=" + getId() +
            ", username='" + getUsername() + "'" +
            ", amount=" + getAmount() +
            ", type='" + getType() + "'" +
            ", fromUsername='" + getFromUsername() + "'" +
            ", txid='" + getTxid() + "'" +
            "}";
    }
}
