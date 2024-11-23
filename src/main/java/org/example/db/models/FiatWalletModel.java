// FIATWalletModel.java
package org.example.db.models;

import jakarta.persistence.*;
import org.example.currency.Owning;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "fiatWallets")
public class FiatWalletModel extends WalletModel {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "wallet_id")
    private List<OwningsModel> ownings;

    public FiatWalletModel() {
    }

    public FiatWalletModel(int fiatWalletId, float fiatWalletBalance, Date currentDate, List<Owning> fiatOwnings) {
        super(fiatWalletId, fiatWalletBalance, currentDate);
        this.ownings = fiatOwnings.stream()
                .map(owning -> new OwningsModel(owning.getOwningId(), owning.getAmount(), owning.getCoin(), new Date(), fiatWalletId))
                .collect(Collectors.toList());
    }

    // Getters and Setters
    public List<OwningsModel> getOwningsModel() {
        return ownings;
    }

    public List<Owning> getOwnings() {
        return ownings.stream()
                .map(owning -> new Owning(owning.getOwningId(), owning.getAmount(), owning.getCoin()))
                .collect(Collectors.toList());
    }

//    public void setOwnings(List<Owning> ownings) {
//        this.ownings = ownings.stream()
//                .map(owning -> new OwningsModel(owning.getOwningId(), owning.getAmount(), owning.getCoin(), fiatWalletId))
//                .collect(Collectors.toList());
//    }
}