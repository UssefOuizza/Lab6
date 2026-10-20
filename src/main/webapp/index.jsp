<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Application Gestion Commerciale - Hibernate &amp; JPA</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f0f4f8;
            margin: 0;
            padding: 40px 20px;
            color: #2d3748;
        }
        .card {
            max-width: 750px;
            margin: auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
        }
        h1 {
            color: #2b6cb0;
            margin-top: 0;
        }
        p {
            line-height: 1.6;
        }
        .btn {
            display: inline-block;
            background: #3182ce;
            color: white;
            padding: 12px 20px;
            text-decoration: none;
            border-radius: 6px;
            font-weight: 600;
            margin-top: 15px;
            transition: background 0.2s;
        }
        .btn:hover {
            background: #2b6cb0;
        }
        .features {
            margin-top: 25px;
            border-top: 1px solid #e2e8f0;
            padding-top: 20px;
        }
        ul {
            padding-left: 20px;
        }
        li {
            margin-bottom: 8px;
        }
    </style>
</head>
<body>
    <div class="card">
        <h1>Application Gestion Commerciale</h1>
        <p>Projet JEE standard utilisant Hibernate 5.6 &amp; JPA 2.2 avec MySQL 8.</p>
        
        <div class="features">
            <h3>Fonctionnalités implémentées :</h3>
            <ul>
                <li>Entités JPA : Categorie, Produit, Commande, CommandeProduit (clé composée)</li>
                <li>Héritage JOINED : User &rarr; Client, Fournisseur</li>
                <li>DAO &amp; Service génériques typés avec gestion sécurisée des transactions</li>
                <li>Requêtes avancées : Named Queries, Criteria API dynamique, pagination JPQL</li>
                <li>Exercice de synthèse : Rapport des ventes par catégorie agrégé avec Criteria API</li>
            </ul>
        </div>

        <a href="rapport-ventes" class="btn">Consulter le Rapport des Ventes (Servlet) &rarr;</a>
    </div>
</body>
</html>
