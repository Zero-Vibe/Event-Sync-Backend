# EventSync - Backend API

Ce projet constitue le backend de l'application **EventSync**, une plateforme de gestion et de synchronisation d'événements en temps réel. Il est développé avec **Spring Boot**, sécurisé par **Spring Security** et **JWT**, et utilise **PostgreSQL** comme base de données principale avec un support **WebSocket** pour l'interactivité en direct.

## 🚀 Fonctionnalités Principales

* **Gestion des Utilisateurs & Authentification** : Inscription, connexion, sécurisation par jetons JWT, et vérification du statut d'authentification.
* **Gestion des Événements** : Création, modification, consultation et suppression d'événements planifiés.
* **Gestion des Sessions** : Planification de sessions/conférences spécifiques au sein d'un événement, associées à des salles et des intervenants.
* **Gestion des Salles (Rooms)** : Configuration et affectation des espaces physiques ou virtuels accueillant les sessions.
* **Gestion des Intervenants (Speakers)** : Profils détaillés avec biographie, photo (Base64) et liens vers les réseaux professionnels (GitHub, LinkedIn, etc.).
* **Interactivité en Temps Réel (Questions & Votes)** : Possibilité pour les utilisateurs de poser des questions sur les sessions (de façon nominative ou anonyme) et de voter pour les questions d'autres participants en temps réel via WebSockets.

---

## 🛠️ Stack Technique

* **Langage** : Java 21
* **Framework** : Spring Boot
* **Base de Données** : PostgreSQL
* **Sécurité** : JSON Web Tokens (JWT) & Crypto (Bcrypt)
* **Temps Réel** : Spring WebSocket (STOMP / SockJS)
* **Outils** : Maven Wrapper, Lombok, dotenv-java

---

## 📋 Prérequis & Configuration

### 1. Variables d'environnement
Créez un fichier `.env` à la racine du projet contenant les configurations suivantes :

```env
DATABASE_URL=jdbc:postgresql://localhost:5432/eventsync
DATABASE_USERNAME=votre_utilisateur
DATABASE_PASSWORD=votre_mot_de_passe
JWT_SECRET=votre_cle_secrete_jwt_super_longue_et_securisee
```

### 2. Base de données
Assurez-vous qu'une instance PostgreSQL est active et que la base de données configurée existe.

---

## 🔧 Lancement de l'Application

Utilisez le Maven Wrapper inclus pour compiler et exécuter le projet.

```bash
# Donner les permissions d'exécution (Linux/macOS)
chmod +x mvnw

# Lancer l'application en mode développement
./mvnw spring-boot:run
```
L'application sera accessible par défaut sur `http://localhost:8080`.

---

## 🔗 Liste des Points d'Accès API (Endpoints)

Toutes les requêtes nécessitant une authentification doivent inclure le header : `Authorization: Bearer <votre_token>`.

### 🔐 Authentification (`/auth`)
* `POST /auth/register` : Inscription d'un nouvel utilisateur.
* `POST /auth/login` : Connexion (Retourne un `access_token` au format snake_case).
* `POST /auth/authStatus` : Vérification de la validité du token actuel.

### 🏢 Événements (`/events`)
* `GET /events` : Récupérer la liste de tous les événements.
* `GET /events/{eventId}` : Récupérer les détails d'un événement.
* `POST /events` : Créer un événement *(Authentification requise)*.
* `PUT /events/{eventId}` : Modifier un événement *(Authentification requise)*.
* `DELETE /events/{eventId}` : Supprimer un événement *(Authentification requise)*.

### 📅 Sessions (`/events/{eventId}/sessions`)
* `GET /events/{eventId}/sessions` : Liste des sessions d'un événement (Filtre optionnel : `?roomId=...`).
* `GET /events/{eventId}/sessions/{sessionId}` : Détails d'une session.
* `POST /events/{eventId}/sessions` : Ajouter une session à un événement *(Authentification requise)*.
* `PUT /events/{eventId}/sessions/{sessionId}` : Modifier une session *(Authentification requise)*.
* `DELETE /events/{eventId}/sessions/{sessionId}` : Supprimer une session *(Authentification requise)*.

### 🎤 Intervenants (`/speakers`)
* `GET /speakers` : Récupérer la liste de tous les intervenants.
* `GET /speakers/{speakerId}` : Profil complet d'un intervenant.
* `POST /speakers` : Ajouter un intervenant (accepte une image `base64Picture` et une liste de `links`) *(Authentification requise)*.
* `PUT /speakers/{speakerId}` : Modifier un intervenant *(Authentification requise)*.
* `DELETE /speakers/{speakerId}` : Supprimer un intervenant *(Authentification requise)*.

### 🚪 Salles (`/rooms`)
* `GET /rooms` : Liste de toutes les salles.
* `GET /rooms/{roomId}` : Détails d'une salle spécifique.
* `POST /rooms` : Créer une nouvelle salle *(Authentification requise)*.
* `PUT /rooms/{roomId}` : Modifier le nom d'une salle *(Authentification requise)*.
* `DELETE /rooms/{roomId}` : Supprimer une salle *(Authentification requise)*.

### 💬 Questions & Votes (Temps Réel)
* `GET /events/{eventId}/sessions/{sessionId}/questions` : Récupérer les questions d'une session.
* `POST /events/{eventId}/sessions/{sessionId}/questions` : Soumettre une question *(Anonyme ou nominative)*.
* `POST /events/{eventId}/sessions/{sessionId}/questions/{questionId}/vote?upvote=true` : Voter (Pour ou contre) une question.

> **Note sur le WebSocket** : Le point de connexion WebSocket (STOMP) est configuré sur le chemin d'accès `/ws`.
