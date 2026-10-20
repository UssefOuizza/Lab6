package org.example.dao;

import org.example.dto.RapportVenteDTO;
import org.example.entities.Categorie;
import org.example.entities.Commande;
import org.example.entities.CommandeProduit;
import org.example.entities.Produit;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO extends GenericDAO<Produit, Long> {

    public ProduitDAO() {
        super(Produit.class);
    }

    /**
     * Recherche de produits par catégorie via NamedQuery
     */
    public List<Produit> findByCategorie(Long identifiantCategorie) {
        EntityManager entityManager = getEntityManagerFactory().createEntityManager();
        try {
            return entityManager.createNamedQuery("Produit.findByCategorie", Produit.class)
                    .setParameter("catId", identifiantCategorie)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    /**
     * Recherche de produits par libellé via NamedQuery
     */
    public List<Produit> searchByLibelle(String termeRecherche) {
        EntityManager entityManager = getEntityManagerFactory().createEntityManager();
        try {
            return entityManager.createNamedQuery("Produit.searchByLibelle", Produit.class)
                    .setParameter("term", termeRecherche)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    /**
     * Recherche dynamique avec Criteria API
     */
    public List<Produit> search(String termeRecherche, BigDecimal prixPlancher, BigDecimal prixPlafond) {
        EntityManager entityManager = getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Produit> criteriaQuery = criteriaBuilder.createQuery(Produit.class);
            Root<Produit> produitRoot = criteriaQuery.from(Produit.class);

            List<Predicate> conditionsList = new ArrayList<>();
            if (termeRecherche != null && !termeRecherche.trim().isEmpty()) {
                conditionsList.add(criteriaBuilder.like(
                        criteriaBuilder.lower(produitRoot.get("libelle")),
                        "%" + termeRecherche.toLowerCase().trim() + "%"
                ));
            }
            if (prixPlancher != null) {
                conditionsList.add(criteriaBuilder.ge(produitRoot.get("prix"), prixPlancher));
            }
            if (prixPlafond != null) {
                conditionsList.add(criteriaBuilder.le(produitRoot.get("prix"), prixPlafond));
            }

            criteriaQuery.where(conditionsList.toArray(new Predicate[0]));
            return entityManager.createQuery(criteriaQuery).getResultList();
        } finally {
            entityManager.close();
        }
    }

    /**
     * Pagination avec JPQL
     */
    public List<Produit> findPage(int numeroPage, int taillePage) {
        EntityManager entityManager = getEntityManagerFactory().createEntityManager();
        try {
            int positionDepart = (numeroPage - 1) * taillePage;
            return entityManager.createQuery("SELECT p FROM Produit p ORDER BY p.id", Produit.class)
                    .setFirstResult(positionDepart)
                    .setMaxResults(taillePage)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    /**
     * Exercice de synthèse : rapport mensuel de ventes par catégorie via Criteria API
     * Utilise CriteriaBuilder.sum et CriteriaBuilder.prod pour calculer (prix * quantite)
     */
    public List<RapportVenteDTO> getRapportVentesMensuel(LocalDate debutPeriode, LocalDate finPeriode) {
        EntityManager entityManager = getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<RapportVenteDTO> criteriaQuery = criteriaBuilder.createQuery(RapportVenteDTO.class);

            Root<CommandeProduit> ligneRoot = criteriaQuery.from(CommandeProduit.class);
            Join<CommandeProduit, Commande> commandeJoin = ligneRoot.join("commande");
            Join<CommandeProduit, Produit> produitJoin = ligneRoot.join("produit");
            Join<Produit, Categorie> categorieJoin = produitJoin.join("categorie");

            // Calcul du montant par ligne : prix * quantite
            Expression<Number> multiplicationPrixQte = criteriaBuilder.prod(
                    produitJoin.<BigDecimal>get("prix"),
                    ligneRoot.<Integer>get("quantite")
            );

            // Somme totale par catégorie
            Expression<Number> chiffreAffairesTotal = criteriaBuilder.sum(multiplicationPrixQte);

            criteriaQuery.select(criteriaBuilder.construct(
                    RapportVenteDTO.class,
                    categorieJoin.get("nom"),
                    chiffreAffairesTotal
            ));

            List<Predicate> filtresPeriode = new ArrayList<>();
            if (debutPeriode != null && finPeriode != null) {
                filtresPeriode.add(criteriaBuilder.between(commandeJoin.get("dateCommande"), debutPeriode, finPeriode));
            } else if (debutPeriode != null) {
                filtresPeriode.add(criteriaBuilder.greaterThanOrEqualTo(commandeJoin.get("dateCommande"), debutPeriode));
            } else if (finPeriode != null) {
                filtresPeriode.add(criteriaBuilder.lessThanOrEqualTo(commandeJoin.get("dateCommande"), finPeriode));
            }

            if (!filtresPeriode.isEmpty()) {
                criteriaQuery.where(filtresPeriode.toArray(new Predicate[0]));
            }

            criteriaQuery.groupBy(categorieJoin.get("nom"));
            criteriaQuery.orderBy(criteriaBuilder.desc(chiffreAffairesTotal));

            return entityManager.createQuery(criteriaQuery).getResultList();
        } finally {
            entityManager.close();
        }
    }
}
