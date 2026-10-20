package org.example;

import org.example.dao.GenericDAO;
import org.example.dto.RapportVenteDTO;
import org.example.entities.*;
import org.example.service.CrudService;
import org.example.service.ProduitService;
import org.example.util.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MainApp {

    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println(" DEMARRAGE DE L'APPLICATION GESTION COMMERCIALE JEE");
        System.out.println("====================================================");

        try {
            // 1. Insertion via GenericDAO
            System.out.println("\n--- 1. Insertion via GenericDAO ---");
            GenericDAO<Categorie, Long> categorieDao = new GenericDAO<>(Categorie.class);
            Categorie categorieElectronique = new Categorie("Électronique_" + System.currentTimeMillis());
            categorieDao.create(categorieElectronique);
            System.out.println("Catégorie créée avec l'ID : " + categorieElectronique.getId());

            // 2. Utilisation du CrudService générique
            System.out.println("\n--- 2. Utilisation du CrudService générique ---");
            CrudService<Categorie, Long> categorieService = new CrudService<>(Categorie.class);
            Categorie categorieInformatique = new Categorie("Informatique_" + System.currentTimeMillis());
            categorieService.create(categorieInformatique);
            System.out.println("Nouvelle catégorie ID = " + categorieInformatique.getId());

            // 3. Modélisation Produit & Service spécialisé
            System.out.println("\n--- 3. Création de Produits associés aux Catégories ---");
            ProduitService produitService = new ProduitService();
            Produit produitUn = new Produit("Souris Optique Sans Fil", new BigDecimal("29.90"), categorieInformatique);
            Produit produitDeux = new Produit("Écran 27 Pouces IPS", new BigDecimal("199.99"), categorieInformatique);
            Produit produitTrois = new Produit("Smartphone Android 5G", new BigDecimal("499.00"), categorieElectronique);

            produitService.create(produitUn);
            produitService.create(produitDeux);
            produitService.create(produitTrois);
            System.out.println("Produits créés : " + produitUn.getId() + ", " + produitDeux.getId() + ", " + produitTrois.getId());

            // 4. Requêtes avancées : Named Queries, Criteria API, Pagination
            System.out.println("\n--- 4. Requêtes Avancées ---");
            System.out.println("a) NamedQuery findByCategorie :");
            List<Produit> produitsParCat = produitService.findByCategorie(categorieInformatique.getId());
            produitsParCat.forEach(prod -> System.out.println("  -> " + prod.getLibelle() + " (" + prod.getPrix() + " DH)"));

            System.out.println("b) Criteria API dynamique (search) :");
            List<Produit> produitsTrouves = produitService.search("souris", new BigDecimal("10.00"), new BigDecimal("50.00"));
            produitsTrouves.forEach(prod -> System.out.println("  -> Trouvé : " + prod.getLibelle()));

            System.out.println("c) Pagination avec JPQL (Page 1, taille 2) :");
            List<Produit> pageUn = produitService.findPage(1, 2);
            pageUn.forEach(prod -> System.out.println("  -> [Page 1] " + prod.getLibelle()));

            // 5. Héritage JPA - Stratégie JOINED
            System.out.println("\n--- 5. Héritage JOINED (User, Client, Fournisseur) ---");
            CrudService<User, Long> userService = new CrudService<>(User.class);
            Client clientUn = new Client("Karim El Amrani", LocalDate.of(2025, 3, 15));
            Fournisseur fournisseurUn = new Fournisseur("Logitech Maroc", "Zone Industrielle Ain Sebaa, Casablanca");
            userService.create(clientUn);
            userService.create(fournisseurUn);
            System.out.println("Client persisté avec ID = " + clientUn.getId() + ", date = " + clientUn.getDateInscription());
            System.out.println("Fournisseur persisté avec ID = " + fournisseurUn.getId() + ", adresse = " + fournisseurUn.getAdresse());

            // 6. Relation ManyToMany avec entité associative CommandeProduit
            System.out.println("\n--- 6. Commande et Clé Composée CommandeProduit ---");
            CrudService<Commande, Long> commandeService = new CrudService<>(Commande.class);
            Commande commandeClient = new Commande(LocalDate.now().minusMonths(1)); // Commande du mois passé pour le rapport
            commandeClient.addProduit(produitUn, 3);
            commandeClient.addProduit(produitDeux, 2);
            commandeService.create(commandeClient);
            System.out.println("Commande créée avec ID = " + commandeClient.getId() + " comprenant " + commandeClient.getLignes().size() + " lignes");

            // 7. Transaction explicite manuelle
            System.out.println("\n--- 7. Démonstration de Transaction explicite EntityTransaction ---");
            demonstrerTransactionExplicite(categorieInformatique.getId());

            // 8. Exercice de synthèse : Rapport des ventes par catégorie via Criteria API
            System.out.println("\n--- 8. Exercice de Synthèse : Rapport de Ventes Mensuel (Criteria API) ---");
            List<RapportVenteDTO> rapports = produitService.getRapportVentesMoisPrecedent();
            System.out.println("Chiffre d'affaires par catégorie pour le mois précédent :");
            for (RapportVenteDTO dto : rapports) {
                System.out.println("  [Catégorie: " + dto.getNomCategorie() + "] => CA Total = " + dto.getChiffreAffaires() + " DH");
            }

            System.out.println("\n====================================================");
            System.out.println(" TOUTES LES ETAPES DU TP ONT ETE EXECUTEES AVEC SUCCES");
            System.out.println("====================================================");

        } finally {
            GenericDAO.closeEntityManagerFactory();
            HibernateUtil.shutdown();
            try {
                com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Démonstration d'une transaction manuelle avec gestion sécurisée rollback / commit
     */
    private static void demonstrerTransactionExplicite(Long idCategorieReference) {
        EntityManagerFactory factory = GenericDAO.getEntityManagerFactory();
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction transactionManuelle = entityManager.getTransaction();

        try {
            transactionManuelle.begin();
            Categorie categorieRattachee = entityManager.find(Categorie.class, idCategorieReference);
            Produit produitManuel = new Produit("Clavier RGB", new BigDecimal("49.99"), categorieRattachee);
            entityManager.persist(produitManuel);

            // Simulation d'une règle de gestion : vérification prix valide
            if (produitManuel.getPrix().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Prix négatif non autorisé");
            }

            transactionManuelle.commit();
            System.out.println("Transaction validée avec succès : Produit ajouté ID = " + produitManuel.getId());
        } catch (Exception exceptionErreur) {
            if (transactionManuelle != null && transactionManuelle.isActive()) {
                transactionManuelle.rollback();
            }
            System.err.println("Transaction annulée : " + exceptionErreur.getMessage());
        } finally {
            entityManager.close();
        }
    }
}
