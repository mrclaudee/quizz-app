# Quiz Application - Spring Boot

## 📋 Description du Projet

Application Spring Boot permettant de créer et gérer des quiz. Les utilisateurs peuvent ajouter des questions, créer des quiz à partir d'une catégorie, et soumettre leurs réponses pour obtenir un score.

## 🏗️ Architecture du Projet

### Structure en Couches (Layered Architecture)

Le projet suit une **architecture en couches** classique Spring Boot :

```
┌─────────────────────────────────────┐
│     Controller Layer                │  ← Gestion des requêtes HTTP
│  (QuestionController, QuizController)│
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     Service Layer                   │  ← Logique métier
│  (QuestionService, QuizService)     │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     Repository Layer                │  ← Accès aux données
│  (QuestionRepo, QuizRepo)           │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     Database (PostgreSQL/MySQL)     │  ← Persistance
└─────────────────────────────────────┘
```

### Avantages de cette Architecture

- **Séparation des responsabilités** : Chaque couche a un rôle précis
- **Testabilité** : Facile de tester chaque couche indépendamment
- **Maintenabilité** : Changements isolés dans une couche
- **Réutilisabilité** : La logique métier peut être appelée depuis différents controllers

---

## 📦 Structure des Packages

```
com.mrclaudee.quizapp/
├── controller/          # Endpoints REST (API)
├── service/            # Logique métier
├── repository/         # Accès base de données
├── model/              # Entités JPA
└── dto/                # Data Transfer Objects
```

---

## 🎯 Patterns et Concepts Utilisés

### 1. **Pattern MVC (Model-View-Controller)**

- **Model** : `Question.java`, `Quiz.java` (entités JPA)
- **Controller** : `QuestionController.java`, `QuizController.java`
- **Service** : Logique métier entre le Controller et le Repository

### 2. **Dependency Injection (DI)**

Spring injecte automatiquement les dépendances via le constructeur :

```java
public class QuestionController {
    private final QuestionService questionService;

    // Spring injecte automatiquement QuestionService
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }
}
```

**Pourquoi ?**
- Couplage faible entre les classes
- Facilite les tests (on peut injecter des mocks)
- Spring gère le cycle de vie des objets

### 3. **Repository Pattern**

Les repositories étendent `JpaRepository` :

```java
public interface QuestionRepo extends JpaRepository<Question, Integer> {
    List<Question> findAllByCategory(String category);
}
```

**Avantages :**
- Abstraction de l'accès aux données
- Spring génère automatiquement les méthodes CRUD
- Requêtes personnalisées via conventions de nommage ou `@Query`

### 4. **DTO Pattern (Data Transfer Object)**

Les DTOs (`QuestionDto`, `QuizResponseDto`) permettent de :
- **Masquer** certaines données sensibles (ex: `rightAnswer`)
- **Réduire** la quantité de données transférées
- **Découpler** le modèle de la base de données de l'API

Exemple :
```java
// Question (Entité complète)
Question {
    id, questionTitle, option1, option2, option3, option4, 
    rightAnswer, category, difficultyLevel
}

// QuestionDto (Exposé à l'utilisateur)
QuestionDto {
    id, questionTitle, option1, option2, option3, option4
    // ❌ Pas de rightAnswer ni category
}
```

### 5. **RESTful API Design**

| Méthode HTTP | Endpoint | Action |
|--------------|----------|--------|
| `GET` | `/question/allQuestions` | Récupérer toutes les questions |
| `GET` | `/question/category/{category}` | Filtrer par catégorie |
| `POST` | `/question/add` | Ajouter une question |
| `PUT` | `/question/update` | Modifier une question |
| `DELETE` | `/question/delete/{id}` | Supprimer une question |
| `POST` | `/quiz/create` | Créer un quiz |
| `GET` | `/quiz/get/{quizId}` | Récupérer les questions d'un quiz |
| `POST` | `/quiz/submit/{id}` | Soumettre les réponses |

---

## 🗂️ Détail des Composants

### **1. Controller Layer**

**Rôle :** Gérer les requêtes HTTP et déléguer la logique métier aux services.

#### QuestionController
```java
@RestController                    // Indique que c'est un controller REST
@RequestMapping("question")        // Préfixe pour tous les endpoints
public class QuestionController {
    
    @GetMapping("/allQuestions")   // GET /question/allQuestions
    public ResponseEntity<List<Question>> getAllQuestions() {
        return questionService.getAllQuestions();
    }
}
```

**Annotations clés :**
- `@RestController` : Combine `@Controller` + `@ResponseBody`
- `@RequestMapping` : Définit le chemin de base
- `@GetMapping`, `@PostMapping`, etc. : Mapping des méthodes HTTP
- `@PathVariable` : Récupère une variable de l'URL
- `@RequestBody` : Parse le JSON du body en objet Java
- `@RequestParam` : Récupère les query parameters (?param=value)

### **2. Service Layer**

**Rôle :** Contenir la logique métier et orchestrer les opérations.

#### QuizService - Création d'un Quiz
```java
public ResponseEntity<String> create(String category, int numQ, String title) {
    try {
        // 1. Récupérer des questions aléatoires
        List<Question> questions = questionRepo.findRandomQuestionsByCategory(category, numQ);
        
        // 2. Créer le quiz
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        
        // 3. Sauvegarder
        quizRepo.save(quiz);
        
        return new ResponseEntity<>("success", HttpStatus.OK);
    } catch (Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>("cannot create quiz", HttpStatus.BAD_REQUEST);
    }
}
```

**Pattern utilisé :** Try-Catch avec ResponseEntity pour gérer les erreurs.

#### QuizService - Calcul du Score
```java
public ResponseEntity<Integer> calculateResult(int id, List<QuizResponseDto> responses) {
    Optional<Quiz> quiz = quizRepo.findById(id);
    if (quiz.isPresent()) {
        List<Question> questions = quiz.get().getQuestions();
        int right = 0;
        
        // Comparaison des réponses
        for (int i = 0; i < responses.size(); i++) {
            if (responses.get(i).getResponse().equals(questions.get(i).getRightAnswer()))
                right++;
        }
        return new ResponseEntity<>(right, HttpStatus.OK);
    }
    return new ResponseEntity<>(0, HttpStatus.BAD_REQUEST);
}
```

### **3. Repository Layer**

**Rôle :** Gérer l'accès aux données avec JPA.

#### Requête Personnalisée avec @Query
```java
@Query(value = "SELECT * FROM question q WHERE q.category=:category ORDER BY RANDOM() LIMIT :numQ", 
       nativeQuery = true)
List<Question> findRandomQuestionsByCategory(String category, int numQ);
```

**Explications :**
- `nativeQuery = true` : Requête SQL native (pas JPQL)
- `:category` et `:numQ` : Paramètres liés aux arguments de la méthode
- `ORDER BY RANDOM()` : Sélection aléatoire (PostgreSQL)

### **4. Model Layer**

#### Relations JPA

```java
@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    @ManyToMany  // Une question peut être dans plusieurs quiz
    private List<Question> questions;
}
```

**Annotations JPA :**
- `@Entity` : Indique que c'est une table
- `@Id` : Clé primaire
- `@GeneratedValue` : Auto-incrémentation
- `@ManyToMany` : Relation plusieurs-à-plusieurs (crée une table de jointure)

#### Lombok Annotations
```java
@Data                 // Génère getters, setters, toString, equals, hashCode
@AllArgsConstructor   // Constructeur avec tous les paramètres
@NoArgsConstructor    // Constructeur vide (requis par JPA)
@Builder              // Pattern Builder pour créer des objets
```

---

## 🔄 Flow d'une Requête Complète

### Exemple : Créer un Quiz

```
1. Client HTTP
   POST /quiz/create?category=Java&numQ=5&title=Quiz Java
   
2. QuizController (@RestController)
   ↓ Reçoit la requête
   ↓ @PostMapping("/create")
   ↓ Extrait les @RequestParam
   
3. QuizService (@Service)
   ↓ create(category, numQ, title)
   ↓ Appelle questionRepo.findRandomQuestionsByCategory()
   
4. QuestionRepo (@Repository)
   ↓ Exécute la requête SQL
   ↓ Retourne List<Question>
   
5. QuizService
   ↓ Crée un objet Quiz
   ↓ quizRepo.save(quiz)
   
6. QuizRepo
   ↓ JPA INSERT INTO quiz...
   
7. QuizService
   ↓ return ResponseEntity("success", 200)
   
8. QuizController
   ↓ Retourne la ResponseEntity
   
9. Client HTTP
   ← Reçoit { "success" } avec status 200
```

---

## 🚀 Concepts Spring Boot à Retenir

### 1. **Inversion of Control (IoC)**
Spring gère les objets (beans) et leurs dépendances automatiquement.

### 2. **Spring Boot Auto-Configuration**
Configuration automatique basée sur les dépendances du `pom.xml` :
- `spring-boot-starter-data-jpa` → Configure JPA/Hibernate
- `spring-boot-starter-web` → Configure Tomcat, Jackson, etc.

### 3. **ResponseEntity**
Permet de contrôler finement la réponse HTTP :
```java
return new ResponseEntity<>(data, HttpStatus.OK);
return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
```

### 4. **Optional<T>**
Évite les `NullPointerException` :
```java
Optional<Quiz> quiz = quizRepo.findById(id);
if (quiz.isPresent()) {
    Quiz q = quiz.get();
}
```

---

## 📚 Points d'Amélioration Possibles

1. **Gestion des erreurs** : Utiliser `@ControllerAdvice` pour centraliser
2. **Validation** : Ajouter `@Valid` et `@NotNull` sur les DTOs
3. **Pagination** : Utiliser `Pageable` pour les grandes listes
4. **Sécurité** : Ajouter Spring Security pour l'authentification
5. **Tests** : Ajouter des tests unitaires avec JUnit et Mockito
6. **Documentation** : Intégrer Swagger/OpenAPI

---

## 🛠️ Technologies Utilisées

- **Spring Boot** : Framework principal
- **Spring Data JPA** : Abstraction de la persistance
- **Hibernate** : ORM (Object-Relational Mapping)
- **Lombok** : Réduction du boilerplate code
- **Base de données** : PostgreSQL/MySQL (via JDBC)

---

## 📖 Ressources pour Apprendre

- [Spring Boot Official Docs](https://spring.io/projects/spring-boot)
- [Baeldung Spring Tutorials](https://www.baeldung.com/spring-tutorial)
- [JPA / Hibernate Guide](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

---

## 🎓 Conclusion

Ce projet démontre les **fondamentaux de Spring Boot** :
- Architecture en couches
- Injection de dépendances
- JPA et relations entre entités
- API REST avec ResponseEntity
- Pattern DTO pour sécuriser les données

C'est une excellente base pour comprendre comment structurer une application Spring Boot professionnelle ! 🚀