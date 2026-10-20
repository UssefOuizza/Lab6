package org.example.web;

import org.example.dto.RapportVenteDTO;
import org.example.service.ProduitService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "RapportVentesServlet", urlPatterns = {"/rapport-ventes"})
public class RapportVentesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ProduitService produitService = new ProduitService();

    @Override
    protected void doGet(HttpServletRequest requete, HttpServletResponse reponse)
            throws ServletException, IOException {
        reponse.setContentType("text/html;charset=UTF-8");

        YearMonth moisPrecedent = YearMonth.now().minusMonths(1);
        String moisFormate = moisPrecedent.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        List<RapportVenteDTO> listeRapports = produitService.getRapportVentesMoisPrecedent();

        BigDecimal chiffreAffairesGlobal = BigDecimal.ZERO;
        for (RapportVenteDTO rapportItem : listeRapports) {
            if (rapportItem.getChiffreAffaires() != null) {
                chiffreAffairesGlobal = chiffreAffairesGlobal.add(rapportItem.getChiffreAffaires());
            }
        }

        try (PrintWriter outputWriter = reponse.getWriter()) {
            outputWriter.println("<!DOCTYPE html>");
            outputWriter.println("<html lang=\"fr\">");
            outputWriter.println("<head>");
            outputWriter.println("  <meta charset=\"UTF-8\">");
            outputWriter.println("  <title>Rapport des Ventes Mensuelles</title>");
            outputWriter.println("  <style>");
            outputWriter.println("    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 30px; background-color: #f4f6f9; color: #333; }");
            outputWriter.println("    .container { max-width: 800px; margin: auto; background: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }");
            outputWriter.println("    h1 { color: #1a365d; border-bottom: 2px solid #3182ce; padding-bottom: 10px; margin-top: 0; }");
            outputWriter.println("    .subtitle { color: #4a5568; margin-bottom: 20px; font-size: 1.1em; }");
            outputWriter.println("    table { width: 100%; border-collapse: collapse; margin-top: 15px; }");
            outputWriter.println("    th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #e2e8f0; }");
            outputWriter.println("    th { background-color: #2b6cb0; color: #fff; text-transform: uppercase; font-size: 0.85em; letter-spacing: 0.05em; }");
            outputWriter.println("    tr:nth-child(even) { background-color: #f8fafc; }");
            outputWriter.println("    tr:hover { background-color: #edf2f7; }");
            outputWriter.println("    .chiffre { text-align: right; font-weight: bold; color: #2d3748; }");
            outputWriter.println("    .total-row { font-weight: bold; background-color: #ebf8ff !important; font-size: 1.05em; }");
            outputWriter.println("    .empty-state { text-align: center; color: #718096; padding: 30px; font-style: italic; }");
            outputWriter.println("  </style>");
            outputWriter.println("</head>");
            outputWriter.println("<body>");
            outputWriter.println("  <div class=\"container\">");
            outputWriter.println("    <h1>Rapport Mensuel des Ventes par Catégorie</h1>");
            outputWriter.println("    <p class=\"subtitle\">Période analysée : <strong>" + moisFormate + "</strong></p>");

            if (listeRapports.isEmpty()) {
                outputWriter.println("    <div class=\"empty-state\">Aucune vente enregistrée pour le mois précédent.</div>");
            } else {
                outputWriter.println("    <table>");
                outputWriter.println("      <thead>");
                outputWriter.println("        <tr>");
                outputWriter.println("          <th>Catégorie</th>");
                outputWriter.println("          <th style=\"text-align: right;\">Chiffre d'Affaires Total (DH / &euro;)</th>");
                outputWriter.println("        </tr>");
                outputWriter.println("      </thead>");
                outputWriter.println("      <tbody>");

                for (RapportVenteDTO dto : listeRapports) {
                    outputWriter.println("        <tr>");
                    outputWriter.println("          <td>" + dto.getNomCategorie() + "</td>");
                    outputWriter.println("          <td class=\"chiffre\">" + String.format("%.2f", dto.getChiffreAffaires()) + "</td>");
                    outputWriter.println("        </tr>");
                }

                outputWriter.println("        <tr class=\"total-row\">");
                outputWriter.println("          <td>Total Global</td>");
                outputWriter.println("          <td class=\"chiffre\">" + String.format("%.2f", chiffreAffairesGlobal) + "</td>");
                outputWriter.println("        </tr>");
                outputWriter.println("      </tbody>");
                outputWriter.println("    </table>");
            }

            outputWriter.println("  </div>");
            outputWriter.println("</body>");
            outputWriter.println("</html>");
        }
    }
}
