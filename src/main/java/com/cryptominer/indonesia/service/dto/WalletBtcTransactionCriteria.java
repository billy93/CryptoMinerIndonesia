package com.cryptominer.indonesia.service.dto;

import java.io.Serializable;
import com.cryptominer.indonesia.domain.enumeration.TransactionType;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.BigDecimalFilter;





/**
 * Criteria class for the WalletBtcTransaction entity. This class is used in WalletBtcTransactionResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /wallet-btc-transactions?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class WalletBtcTransactionCriteria implements Serializable {
    /**
     * Class for filtering TransactionType
     */
    public static class TransactionTypeFilter extends Filter<TransactionType> {
    }

    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter username;

    private BigDecimalFilter amount;

    private TransactionTypeFilter type;

    private StringFilter fromUsername;

    private StringFilter txid;

    public WalletBtcTransactionCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getUsername() {
        return username;
    }

    public void setUsername(StringFilter username) {
        this.username = username;
    }

    public BigDecimalFilter getAmount() {
        return amount;
    }

    public void setAmount(BigDecimalFilter amount) {
        this.amount = amount;
    }

    public TransactionTypeFilter getType() {
        return type;
    }

    public void setType(TransactionTypeFilter type) {
        this.type = type;
    }

    public StringFilter getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(StringFilter fromUsername) {
        this.fromUsername = fromUsername;
    }

    public StringFilter getTxid() {
        return txid;
    }

    public void setTxid(StringFilter txid) {
        this.txid = txid;
    }

    @Override
    public String toString() {
        return "WalletBtcTransactionCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (username != null ? "username=" + username + ", " : "") +
                (amount != null ? "amount=" + amount + ", " : "") +
                (type != null ? "type=" + type + ", " : "") +
                (fromUsername != null ? "fromUsername=" + fromUsername + ", " : "") +
                (txid != null ? "txid=" + txid + ", " : "") +
            "}";
    }

}
