package org.example.service;

import org.example.dao.ProduitDAO;
import org.example.dto.RapportVenteDTO;
import org.example.entities.Produit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class ProduitService extends CrudService<Produit, Long> {
    private final ProduitDAO produitDao;

    public ProduitService() {
        this(new ProduitDAO());
    }

    public ProduitService(ProduitDAO customProduitDao) {
        super(customProduitDao);
        this.produitDao = customProduitDao;
    }

    public List<Produit> findByCategorie(Long identifiantCategorie) {
        if (identifiantCategorie == null) {
            throw new IllegalArgumentException("L'identifiant de catégorie ne peut être nul");
        }
        return produitDao.findByCategorie(identifiantCategorie);
    }

    public List<Produit> searchByLibelle(String termeRecherche) {
        return produitDao.searchByLibelle(termeRecherche != null ? termeRecherche : "");
    }

    public List<Produit> search(String libelleRecherche, BigDecimal prixMin, BigDecimal prixMax) {
        return produitDao.search(libelleRecherche, prixMin, prixMax);
    }

    public List<Produit> findPage(int indexPage, int taillePage) {
        if (indexPage < 1 || taillePage < 1) {
            throw new IllegalArgumentException("Les paramètres de pagination doivent être strictement positifs");
        }
        return produitDao.findPage(indexPage, taillePage);
    }

    /**
     * Calcule le chiffre d'affaires par catégorie pour le mois précédent
     */
    public List<RapportVenteDTO> getRapportVentesMoisPrecedent() {
        YearMonth moisCourant = YearMonth.now();
        YearMonth moisPrecedent = moisCourant.minusMonths(1);
        LocalDate dateDebut = moisPrecedent.atDay(1);
        LocalDate dateFin = moisPrecedent.atEndOfMonth();

        return produitDao.getRapportVentesMensuel(dateDebut, dateFin);
    }

    /**
     * Calcule le chiffre d'affaires pour une période personnalisée
     */
    public List<RapportVenteDTO> getRapportVentesPeriode(LocalDate debut, LocalDate fin) {
        return produitDao.getRapportVentesMensuel(debut, fin);
    }
}
