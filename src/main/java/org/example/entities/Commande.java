package org.example.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_commande")
    private LocalDate dateCommande;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommandeProduit> lignes = new ArrayList<>();

    public Commande() {
        this.dateCommande = LocalDate.now();
    }

    public Commande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }

    public void addProduit(Produit produitItem, int quantiteCmd) {
        CommandeProduit ligneCmd = new CommandeProduit(this, produitItem, quantiteCmd);
        lignes.add(ligneCmd);
    }

    public void removeProduit(Produit produitItem) {
        lignes.removeIf(ligne -> ligne.getProduit() != null && 
                                 ligne.getProduit().equals(produitItem));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }

    public List<CommandeProduit> getLignes() {
        return lignes;
    }

    public void setLignes(List<CommandeProduit> lignes) {
        this.lignes = lignes;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", dateCommande=" + dateCommande +
                ", nbLignes=" + (lignes != null ? lignes.size() : 0) +
                '}';
    }
}
