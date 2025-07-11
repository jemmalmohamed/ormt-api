# Guide d'Intégration Frontend - Système de Graphiques ORMT

## 📋 Vue d'ensemble

Ce guide décrit comment intégrer le nouveau système de configuration dynamique de graphiques dans votre application frontend Angular avec Chart.js.

## 🏗️ Architecture du Système

### Concepts Clés
- **GrapheType** : Types de graphiques disponibles (bar, line, pie, etc.)
- **GrapheMappingRule** : Règles de compatibilité entre indicateurs et types de graphiques
- **GrapheConfiguration** : Configurations personnalisées sauvegardées par les utilisateurs

### Flux de Données
```
Indicateur → Validation Compatibilité → Types Graphiques Disponibles → Configuration → Rendu Chart.js
```

## 🔗 Endpoints API Disponibles

### 1. Types de Graphiques (GrapheType Admin Controller)
```typescript
// Récupérer tous les types de graphiques disponibles
GET /api/v1/admin/graphetypes?pageIndex=0&pageSize=100&sortField=nom&sortDirection=ASC
Headers: Authorization: Bearer {token}
Permissions: 'indicateur:list'
Response: RestResponse<List<GrapheTypeDto>>

// Récupérer un type de graphique par ID
GET /api/v1/admin/graphetypes/{id}
Headers: Authorization: Bearer {token}
Permissions: 'indicateur:read'
Response: RestResponse<GrapheTypeDetailsDto>
```

### 2. Gestion des Configurations (si contrôleur implémenté)
```typescript
// Lister les configurations utilisateur
GET /api/v1/admin/graphe/configurations?userId={userId}&page=0&size=10
Response: Page<GrapheConfigurationDto>

// Créer une configuration
POST /api/v1/admin/graphe/configurations
Body: GrapheConfigurationRequestDto
Response: GrapheConfigurationDto

// Mettre à jour une configuration
PUT /api/v1/admin/graphe/configurations/{id}
Body: GrapheConfigurationRequestDto
Response: GrapheConfigurationDto

// Supprimer une configuration
DELETE /api/v1/admin/graphe/configurations/{id}
```

### 3. Règles de Mapping (si contrôleur implémenté)
```typescript
// Récupérer les règles de mapping pour un indicateur
GET /api/v1/admin/graphe/mapping-rules?indicateurId={indicateurId}
Response: List<GrapheMappingRuleDto>

// Valider la compatibilité (logique métier dans le service)
// À implémenter selon les besoins dans le contrôleur existant
```

## 📊 Types de Graphiques Disponibles

| Code | Nom | Chart.js Type | Min Dimensions | Max Dimensions | Temporel |
|------|-----|---------------|----------------|----------------|----------|
| `bar` | Graphique en Barres | `bar` | 1 | 3 | Non |
| `line` | Graphique Linéaire | `line` | 1 | 2 | Oui |
| `pie` | Graphique Circulaire | `pie` | 1 | 1 | Non |
| `doughnut` | Graphique en Anneau | `doughnut` | 1 | 1 | Non |
| `area` | Graphique en Aires | `line` (area: true) | 1 | 2 | Oui |
| `scatter` | Nuage de Points | `scatter` | 2 | 2 | Non |
| `radar` | Graphique Radar | `radar` | 1 | 3 | Non |
| `polar` | Graphique Polaire | `polarArea` | 1 | 1 | Non |

## 💻 Implémentation Frontend

### 1. Service Angular pour les Graphiques (avec Contrôleurs Admin Existants)

```typescript
// graphe.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface GrapheTypeDto {
  id: number;
  code: string;
  nom: string;
  description: string;
  chartJsType: string;
  minDimensions: number;
  maxDimensions: number;
  requiresTemporal: boolean;
  actif: boolean;
}

export interface GrapheTypeDetailsDto extends GrapheTypeDto {
  createdDate?: Date;
  lastModifiedDate?: Date;
}

export interface RestResponse<T> {
  data: T;
  message: string;
  status: string;
  totalElements?: number;
  totalPages?: number;
}

export interface GrapheConfigurationDto {
  id: number;
  indicateurId: number;
  grapheTypeId: number;
  userId: string;
  name: string;
  configuration: string; // JSON stringifié
  isDefault: boolean;
  isPublic: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class GrapheService {
  private readonly baseUrl = '/api/v1/admin';

  constructor(private http: HttpClient) {}

  // === TYPES DE GRAPHIQUES ===
  
  // Récupérer tous les types de graphiques disponibles
  getGrapheTypes(): Observable<GrapheTypeDto[]> {
    const params = {
      pageIndex: '0',
      pageSize: '100',
      sortField: 'nom',
      sortDirection: 'ASC'
    };

    return this.http.get<RestResponse<GrapheTypeDto[]>>(`${this.baseUrl}/graphetypes`, { params })
      .pipe(
        map(response => response.data || []),
        map(types => types.filter(type => type.actif)) // Filtrer seulement les types actifs
      );
  }

  // Récupérer un type de graphique par ID
  getGrapheTypeById(id: number): Observable<GrapheTypeDetailsDto> {
    return this.http.get<RestResponse<GrapheTypeDetailsDto>>(`${this.baseUrl}/graphetypes/${id}`)
      .pipe(
        map(response => response.data)
      );
  }

  // === LOGIQUE DE COMPATIBILITÉ ===
  
  // Récupérer les types compatibles pour un indicateur
  // Version simplifiée : retourne tous les types actifs
  // TODO: Ajouter la logique de compatibilité dans le contrôleur GrapheType ou créer une méthode dédiée
  getCompatibleTypes(indicateurId: number): Observable<GrapheTypeDto[]> {
    // Pour l'instant, utiliser tous les types actifs
    // Plus tard, on pourra ajouter un endpoint spécifique dans le contrôleur GrapheType
    return this.getGrapheTypes();
  }

  // Valider la compatibilité entre un indicateur et un type de graphique
  // Version simplifiée : valide si le type existe et est actif
  validateCompatibility(indicateurId: number, grapheTypeId: number): Observable<boolean> {
    return this.getGrapheTypeById(grapheTypeId).pipe(
      map(type => type && type.actif),
      map(isValid => isValid || false)
    );
  }

  // === CONFIGURATIONS (si contrôleur implémenté) ===
  
  // Récupérer les configurations utilisateur
  getUserConfigurations(userId: string, page = 0, size = 10): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/graphe/configurations`,
      { params: { userId, page: page.toString(), size: size.toString() } }
    );
  }

  // Créer une configuration
  createConfiguration(config: any): Observable<GrapheConfigurationDto> {
    return this.http.post<GrapheConfigurationDto>(
      `${this.baseUrl}/graphe/configurations`,
      config
    );
  }

  // Mettre à jour une configuration
  updateConfiguration(id: number, config: any): Observable<GrapheConfigurationDto> {
    return this.http.put<GrapheConfigurationDto>(
      `${this.baseUrl}/graphe/configurations/${id}`,
      config
    );
  }

  // Supprimer une configuration
  deleteConfiguration(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/graphe/configurations/${id}`);
  }

  // === MÉTHODES UTILITAIRES ===

  // Méthode pour obtenir la configuration par défaut d'un type de graphique
  getDefaultConfiguration(grapheType: GrapheTypeDto): any {
    const baseConfig = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: true,
          position: 'top'
        },
        title: {
          display: true,
          text: `Graphique ${grapheType.nom}`
        }
      }
    };

    // Configurations spécifiques par type
    switch (grapheType.code) {
      case 'line':
      case 'area':
        return {
          ...baseConfig,
          scales: {
            x: { display: true },
            y: { display: true, beginAtZero: true }
          },
          elements: {
            line: { tension: 0.4 }
          }
        };

      case 'bar':
        return {
          ...baseConfig,
          scales: {
            x: { display: true },
            y: { display: true, beginAtZero: true }
          }
        };

      case 'pie':
      case 'doughnut':
      case 'polar':
        return {
          ...baseConfig,
          plugins: {
            ...baseConfig.plugins,
            legend: {
              display: true,
              position: 'right'
            }
          }
        };

      default:
        return baseConfig;
    }
  }
}
```

### 2. Composant de Sélection de Graphique

```typescript
// graphe-selector.component.ts
import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { GrapheService, GrapheTypeDto } from './graphe.service';

@Component({
  selector: 'app-graphe-selector',
  template: `
    <div class="graphe-selector">
      <h4>Sélectionnez un type de graphique</h4>
      
      <div class="graphe-types-grid">
        <div 
          *ngFor="let type of compatibleTypes" 
          class="graphe-type-card"
          [class.selected]="selectedType?.id === type.id"
          (click)="selectType(type)">
          
          <div class="type-icon">
            <i [class]="getTypeIcon(type.code)"></i>
          </div>
          
          <h5>{{ type.nom }}</h5>
          <p>{{ type.description }}</p>
          
          <div class="type-details">
            <span class="badge">{{ type.chartJsType }}</span>
            <span *ngIf="type.requiresTemporal" class="badge temporal">Temporel</span>
          </div>
        </div>
      </div>

      <div *ngIf="selectedType" class="configuration-panel">
        <h5>Configuration de {{ selectedType.nom }}</h5>
        <button 
          class="btn btn-primary" 
          (click)="createChart()"
          [disabled]="!canCreateChart()">
          Créer le Graphique
        </button>
      </div>
    </div>
  `,
  styles: [`
    .graphe-types-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
      gap: 1rem;
      margin: 1rem 0;
    }

    .graphe-type-card {
      border: 2px solid #e0e0e0;
      border-radius: 8px;
      padding: 1rem;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .graphe-type-card:hover {
      border-color: #007bff;
      box-shadow: 0 4px 8px rgba(0,123,255,0.3);
    }

    .graphe-type-card.selected {
      border-color: #007bff;
      background-color: #f8f9fa;
    }

    .type-icon {
      font-size: 2rem;
      color: #007bff;
      margin-bottom: 0.5rem;
    }

    .badge {
      background-color: #6c757d;
      color: white;
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
      font-size: 0.8rem;
      margin: 0.25rem;
    }

    .badge.temporal {
      background-color: #28a745;
    }
  `]
})
export class GrapheSelectorComponent implements OnInit {
  @Input() indicateurId!: number;
  @Output() typeSelected = new EventEmitter<GrapheTypeDto>();
  @Output() chartCreated = new EventEmitter<any>();

  compatibleTypes: GrapheTypeDto[] = [];
  selectedType: GrapheTypeDto | null = null;

  constructor(private grapheService: GrapheService) {}

  ngOnInit() {
    if (this.indicateurId) {
      this.loadCompatibleTypes();
    }
  }

  loadCompatibleTypes() {
    this.grapheService.getCompatibleTypes(this.indicateurId).subscribe({
      next: (types) => {
        this.compatibleTypes = types;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des types compatibles:', error);
      }
    });
  }

  selectType(type: GrapheTypeDto) {
    this.selectedType = type;
    this.typeSelected.emit(type);
  }

  getTypeIcon(code: string): string {
    const icons = {
      'bar': 'fas fa-chart-bar',
      'line': 'fas fa-chart-line',
      'pie': 'fas fa-chart-pie',
      'doughnut': 'fas fa-chart-pie',
      'area': 'fas fa-chart-area',
      'scatter': 'fas fa-braille',
      'radar': 'fas fa-chart-line',
      'polar': 'fas fa-chart-pie'
    };
    return icons[code] || 'fas fa-chart-bar';
  }

  canCreateChart(): boolean {
    return this.selectedType !== null;
  }

  createChart() {
    if (this.selectedType) {
      const chartConfig = {
        indicateurId: this.indicateurId,
        grapheTypeId: this.selectedType.id,
        chartJsType: this.selectedType.chartJsType,
        configuration: this.getDefaultConfiguration()
      };
      
      this.chartCreated.emit(chartConfig);
    }
  }

  private getDefaultConfiguration(): any {
    if (!this.selectedType) return {};

    // Configuration par défaut basée sur le type de graphique
    const baseConfig = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: true,
          position: 'top'
        },
        title: {
          display: true,
          text: `Graphique ${this.selectedType.nom}`
        }
      }
    };

    // Configurations spécifiques par type
    switch (this.selectedType.code) {
      case 'line':
      case 'area':
        return {
          ...baseConfig,
          scales: {
            x: { display: true },
            y: { display: true, beginAtZero: true }
          },
          elements: {
            line: {
              tension: 0.4
            }
          }
        };

      case 'bar':
        return {
          ...baseConfig,
          scales: {
            x: { display: true },
            y: { display: true, beginAtZero: true }
          }
        };

      case 'pie':
      case 'doughnut':
      case 'polar':
        return {
          ...baseConfig,
          plugins: {
            ...baseConfig.plugins,
            legend: {
              display: true,
              position: 'right'
            }
          }
        };

      default:
        return baseConfig;
    }
  }
}
```

### 3. Composant de Visualisation avec Chart.js

```typescript
// chart-viewer.component.ts
import { Component, Input, ViewChild, ElementRef, OnChanges, SimpleChanges } from '@angular/core';
import { Chart, ChartConfiguration, ChartType } from 'chart.js';

@Component({
  selector: 'app-chart-viewer',
  template: `
    <div class="chart-container">
      <canvas #chartCanvas></canvas>
    </div>
  `,
  styles: [`
    .chart-container {
      position: relative;
      height: 400px;
      width: 100%;
    }
  `]
})
export class ChartViewerComponent implements OnChanges {
  @ViewChild('chartCanvas', { static: true }) chartCanvas!: ElementRef<HTMLCanvasElement>;
  @Input() chartData: any;
  @Input() chartConfig: any;
  @Input() chartType: ChartType = 'bar';

  private chart: Chart | null = null;

  ngOnChanges(changes: SimpleChanges) {
    if (changes['chartData'] || changes['chartConfig'] || changes['chartType']) {
      this.updateChart();
    }
  }

  private updateChart() {
    if (this.chart) {
      this.chart.destroy();
    }

    if (this.chartData && this.chartConfig) {
      const config: ChartConfiguration = {
        type: this.chartType,
        data: this.chartData,
        options: this.chartConfig
      };

      this.chart = new Chart(this.chartCanvas.nativeElement, config);
    }
  }
}
```

### 4. Utilisation dans un Composant Principal

```typescript
// indicateur-dashboard.component.ts
import { Component } from '@angular/core';
import { GrapheTypeDto } from './graphe.service';

@Component({
  selector: 'app-indicateur-dashboard',
  template: `
    <div class="dashboard">
      <h2>Tableau de Bord des Indicateurs</h2>
      
      <!-- Sélecteur d'indicateur -->
      <div class="indicator-selector">
        <select [(ngModel)]="selectedIndicateurId" (change)="onIndicateurChange()">
          <option value="">Sélectionnez un indicateur</option>
          <option *ngFor="let ind of indicateurs" [value]="ind.id">
            {{ ind.nom }}
          </option>
        </select>
      </div>

      <!-- Sélecteur de graphique -->
      <app-graphe-selector 
        *ngIf="selectedIndicateurId"
        [indicateurId]="selectedIndicateurId"
        (chartCreated)="onChartCreated($event)">
      </app-graphe-selector>

      <!-- Visualisation -->
      <app-chart-viewer
        *ngIf="currentChart"
        [chartData]="currentChart.data"
        [chartConfig]="currentChart.config"
        [chartType]="currentChart.type">
      </app-chart-viewer>
    </div>
  `
})
export class IndicateurDashboardComponent {
  selectedIndicateurId: number | null = null;
  indicateurs: any[] = []; // À charger depuis votre API
  currentChart: any = null;

  onIndicateurChange() {
    this.currentChart = null;
  }

  onChartCreated(chartConfig: any) {
    // Ici vous devez charger les données de l'indicateur
    // et les formater pour Chart.js
    this.loadChartData(chartConfig);
  }

  private loadChartData(chartConfig: any) {
    // Exemple de formatage des données
    const mockData = {
      labels: ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Jun'],
      datasets: [{
        label: 'Données de l\'indicateur',
        data: [12, 19, 3, 5, 2, 3],
        backgroundColor: 'rgba(54, 162, 235, 0.2)',
        borderColor: 'rgba(54, 162, 235, 1)',
        borderWidth: 1
      }]
    };

    this.currentChart = {
      data: mockData,
      config: chartConfig.configuration,
      type: chartConfig.chartJsType
    };
  }
}
```

## 🔧 Configuration et Installation

### 1. Installation des Dépendances

```bash
npm install chart.js ng2-charts
```

### 2. Configuration du Module avec Authentification

```typescript
// app.module.ts
import { NgChartsModule } from 'ng2-charts';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

// Intercepteur pour ajouter automatiquement le token d'authentification
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('authToken'); // Ou votre méthode de stockage
    
    if (token) {
      const authReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
      return next.handle(authReq);
    }
    
    return next.handle(req);
  }
}

@NgModule({
  imports: [
    NgChartsModule,
    HttpClientModule,
    // autres imports
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  // ...
})
export class AppModule { }
```

### 3. Gestion des Permissions

```typescript
// auth-guard.service.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class GrapheAuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(): boolean {
    // Vérifier si l'utilisateur a les permissions nécessaires
    const userPermissions = this.getUserPermissions();
    const hasIndicatorPermission = userPermissions.includes('indicateur:list') || 
                                 userPermissions.includes('indicateur:read');
    
    if (!hasIndicatorPermission) {
      this.router.navigate(['/unauthorized']);
      return false;
    }
    
    return true;
  }

  private getUserPermissions(): string[] {
    // Récupérer les permissions depuis votre service d'authentification
    const user = JSON.parse(localStorage.getItem('currentUser') || '{}');
    return user.permissions || [];
  }
}
```

## 📝 Exemples d'Utilisation

### Création d'une Configuration Personnalisée

```typescript
const customConfig = {
  indicateurId: 123,
  grapheTypeId: 1, // ID du type 'bar'
  userId: 'user123',
  name: 'Mon graphique personnalisé',
  configuration: JSON.stringify({
    responsive: true,
    plugins: {
      title: {
        display: true,
        text: 'Évolution des ventes'
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: function(value) {
            return value + ' €';
          }
        }
      }
    }
  }),
  isDefault: false,
  isPublic: true
};

this.grapheService.createConfiguration(customConfig).subscribe({
  next: (result) => console.log('Configuration créée:', result),
  error: (error) => console.error('Erreur:', error)
});
```

## 🚀 Bonnes Pratiques

1. **Validation côté Frontend** : Toujours valider la compatibilité avant de permettre la création
2. **Gestion d'Erreurs** : Implémenter une gestion robuste des erreurs API
3. **Performance** : Mettre en cache les types de graphiques pour éviter les appels répétés
4. **UX** : Fournir des aperçus visuels des types de graphiques
5. **Sauvegarde** : Permettre aux utilisateurs de sauvegarder leurs configurations favorites

## 📞 Support

Pour toute question sur l'intégration, consultez :
- La documentation Swagger : `/swagger-ui.html`
- Les endpoints existants : `/api/v1/admin/graphetypes` (nécessite authentification)
- Les logs backend pour le débogage

### Contrôleurs Utilisés

Ce guide utilise les contrôleurs admin existants de chaque modèle :
- **GrapheType** : `GrapheTypeAdminLoadController` et `GrapheTypeAdminCrudController`
- **GrapheConfiguration** : À implémenter selon le même pattern
- **GrapheMappingRule** : À implémenter selon le même pattern

### Permissions Requises

Pour utiliser les endpoints de graphiques, l'utilisateur doit avoir les permissions suivantes :
- `indicateur:list` - Pour lister les types de graphiques
- `indicateur:read` - Pour lire les détails d'un type de graphique  
- `graphe:create` - Pour créer des configurations (si implémenté)
- `graphe:update` - Pour modifier des configurations (si implémenté)
- `graphe:delete` - Pour supprimer des configurations (si implémenté)

### Améliorations Futures

Pour étendre les fonctionnalités, vous pouvez ajouter ces méthodes dans les contrôleurs existants :

1. **Dans GrapheTypeAdminLoadController** :
   ```java
   @GetMapping("/compatible/{indicateurId}")
   public ResponseEntity<RestResponse<List<GrapheTypeDto>>> getCompatibleTypes(@PathVariable Long indicateurId)
   ```

2. **Dans GrapheConfigurationAdminController** (à créer) :
   ```java
   @GetMapping("/by-user/{userId}")
   public ResponseEntity<Page<GrapheConfigurationDto>> getConfigurationsByUser(@PathVariable String userId)
   ```

3. **Dans GrapheMappingRuleAdminController** (à créer) :
   ```java
   @PostMapping("/validate-compatibility")
   public ResponseEntity<Boolean> validateCompatibility(@RequestBody CompatibilityRequest request)
   ```

### Gestion des Erreurs d'Authentification

```typescript
// Dans votre service ou composant
this.grapheService.getGrapheTypes().subscribe({
  next: (types) => {
    this.availableTypes = types;
  },
  error: (error) => {
    if (error.status === 401) {
      // Token expiré ou invalide
      this.router.navigate(['/login']);
    } else if (error.status === 403) {
      // Permissions insuffisantes
      this.showPermissionError();
    } else {
      console.error('Erreur lors du chargement des types:', error);
    }
  }
});
```

---

*Guide créé pour l'équipe frontend ORMT - Version 1.0*
