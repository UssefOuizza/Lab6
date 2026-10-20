package org.example;

import org.example.dto.RapportVenteDTO;
import org.example.entities.*;
import org.example.service.CrudService;
import org.example.service.ProduitService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class ProduitDaoTest {
    private static CrudService<Categorie, Long> categorieCrudService;
    private static ProduitService produitService;
    private static CrudService<Commande, Long> commandeCrudService;
    private static CrudService<User, Long> userCrudService;

    @BeforeClass
    public static void initialiserServices() {
        categorieCrudService = new CrudService<>(Categorie.class);
        produitService = new ProduitService();
        commandeCrudService = new CrudService<>(Commande.class);
        userCrudService = new CrudService<>(User.class);
    }

    @Test
    public void testCreateProduitAvecCategorie() {
        String nomUnique = "Papeterie_" + System.currentTimeMillis();
        Categorie categoriePapeterie = new Categorie(nomUnique);
        categorieCrudService.create(categoriePapeterie);
        assertNotNull(categoriePapeterie.getId());

        Produit nouveauProduit = new Produit("Stylo Bille", new BigDecimal("1.20"), categoriePapeterie);
        produitService.create(nouveauProduit);
        assertNotNull(nouveauProduit.getId());

        Produit produitPersiste = produitService.getById(nouveauProduit.getId());
        assertNotNull(produitPersiste);
        assertEquals(nomUnique, produitPersiste.getCategorie().getNom());
    }

    @Test
    public void testRechercheEtPaginationProduits() {
        String nomUnique = "Informatique_" + System.currentTimeMillis();
        Categorie catInfo = new Categorie(nomUnique);
        categorieCrudService.create(catInfo);

        Produit ecran = new Produit("Écran 4K Ultra", new BigDecimal("350.00"), catInfo);
        Produit souris = new Produit("Souris Ergonomique", new BigDecimal("45.00"), catInfo);
        produitService.create(ecran);
        produitService.create(souris);

        // Test NamedQuery findByCategorie
        List<Produit> listeParCat = produitService.findByCategorie(catInfo.getId());
        assertTrue(listeParCat.size() >= 2);

        // Test Criteria API search
        List<Produit> resultatFiltre = produitService.search("souris", new BigDecimal("20.00"), new BigDecimal("60.00"));
        assertFalse(resultatFiltre.isEmpty());

        // Test Pagination
        List<Produit> premierePage = produitService.findPage(1, 1);
        assertEquals(1, premierePage.size());
    }

    @Test
    public void testHeritageJoinedUserClientFournisseur() {
        Client clientRecord = new Client("Amine Bennani", LocalDate.now());
        userCrudService.create(clientRecord);
        assertNotNull(clientRecord.getId());

        Fournisseur fournisseurRecord = new Fournisseur("Tech Supplies SARL", "12 Boulevard Zerktouni, Casablanca");
        userCrudService.create(fournisseurRecord);
        assertNotNull(fournisseurRecord.getId());

        User recupClient = userCrudService.getById(clientRecord.getId());
        assertTrue(recupClient instanceof Client);
        assertEquals("Amine Bennani", recupClient.getNom());
    }

    @Test
    public void testCommandeAvecCleComposeeEtRapportVentes() {
        Categorie catElectronique = new Categorie("Electronique_" + System.currentTimeMillis());
        categorieCrudService.create(catElectronique);

        Produit p1 = new Produit("Clavier Mécanique", new BigDecimal("80.00"), catElectronique);
        Produit p2 = new Produit("Casque Audio", new BigDecimal("120.00"), catElectronique);
        produitService.create(p1);
        produitService.create(p2);

        // Création d'une commande
        Commande commandeClient = new Commande(LocalDate.now().minusMonths(1)); // Date du mois précédent
        commandeClient.addProduit(p1, 2); // 80 * 2 = 160
        commandeClient.addProduit(p2, 1); // 120 * 1 = 120
        commandeCrudService.create(commandeClient);

        assertNotNull(commandeClient.getId());

        // Test de l'exercice de synthèse : rapport mensuel des ventes
        List<RapportVenteDTO> rapports = produitService.getRapportVentesMoisPrecedent();
        assertNotNull(rapports);
    }
}
