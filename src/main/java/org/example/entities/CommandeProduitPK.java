package org.example.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CommandeProduitPK implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "commande_id")
    private Long commandeId;

    @Column(name = "produit_id")
    private Long produitId;

    public CommandeProduitPK() {}

    public CommandeProduitPK(Long commandeId, Long produitId) {
        this.commandeId = commandeId;
        this.produitId = produitId;
    }

    public Long getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(Long commandeId) {
        this.commandeId = commandeId;
    }

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;
        CommandeProduitPK that = (CommandeProduitPK) otherObject;
        return Objects.equals(commandeId, that.commandeId) &&
               Objects.equals(produitId, that.produitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandeId, produitId);
    }
}
