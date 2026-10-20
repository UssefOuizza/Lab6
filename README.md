# TP - Gestion Commerciale avec JEE & Hibernate / JPA

Application Java EE standard de gestion commerciale utilisant Hibernate 5.6, JPA 2.2 et MySQL 8.

---

## Captures d'écran et Résultats

### 1. Insertion et persistance des catégories
Insertion et persistance des catégories en base de données avec génération d'identifiants via GenericDAO et CrudService.

![Insertion des catégories](sc/Screenshot%202026-10-20%20112356.png)

---

### 2. Création des produits avec association ManyToOne
Création et enregistrement des produits associés à leur catégorie respective via la relation ManyToOne.

![Création des produits](sc/Screenshot%202026-10-20%20112429.png)

---

### 3. Recherche de produits via NamedQuery
Exécution d'une NamedQuery JPA pour récupérer l'ensemble des produits appartenant à une catégorie spécifique.

![NamedQuery par catégorie](sc/Screenshot%202026-10-20%20112446.png)

---

### 4. Recherche dynamique multi-critères avec Criteria API
Recherche dynamique et sécurisée par critères avec la Criteria API selon le libellé et une plage de prix.

![Recherche Criteria API](sc/Screenshot%202026-10-20%20112507.png)

---

### 5. Pagination des résultats avec JPQL
Pagination des produits avec JPQL en utilisant setFirstResult et setMaxResults pour limiter le volume de données chargées.

![Pagination JPQL](sc/Screenshot%202026-10-20%20112524.png)

---

### 6. Stratégie d'héritage JOINED
Mise en œuvre de l'héritage avec la stratégie JOINED pour la classe abstraite User et ses sous-classes Client et Fournisseur.

![Héritage JOINED](sc/Screenshot%202026-10-20%20112543.png)

---

### 7. Rapport mensuel des ventes (Exercice de synthèse)
Rendu du servlet dans le navigateur affichant le rapport mensuel de chiffre d'affaires agrégé avec CriteriaBuilder.sum et CriteriaBuilder.prod.

![Rapport des ventes Servlet](sc/Screenshot%202026-10-20%20113349.png)

---

## Instructions d'exécution

- **Exécuter la démonstration console :**
  ```bash
  mvn compile exec:java
  ```

- **Lancer l'application web (Servlet & JSP) :**
  ```bash
  mvn jetty:run
  ```
  Accédez à l'URL : `http://localhost:8080/rapport-ventes`
