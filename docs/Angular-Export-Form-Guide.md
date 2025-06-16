# 📋 Guide Frontend Angular - Composant d'Export d'Indicateurs

## 🎯 Objectif

Créer un composant Angular qui permet aux utilisateurs de configurer et exporter des indicateurs avec des options avancées de groupement et de sélection de colonnes.

---


---

## 📝 Structure du DTO Request

```typescript
interface IndicateurExportRequestDto {
  columnsToExport: string[] | null;  // null = toutes les colonnes
  groupBy: 'NONE' | 'BY_DOMAINE' | 'BY_SOURCE';
  fileName: string;
  activeOnly: boolean;
}
```

---

## 🏗️ Structure Recommandée du Composant

### 1. **Fichiers à Créer**

```
src/app/components/export/
├── indicateur-export-form/
│   ├── indicateur-export-form.component.ts
│   ├── indicateur-export-form.component.html
│   ├── indicateur-export-form.component.scss
│   └── indicateur-export-form.component.spec.ts
├── models/
│   └── export-request.interface.ts
└── services/
    └── export.service.ts
```

---

## 🎨 Interface TypeScript

### export-request.interface.ts
```typescript
export interface ExportRequestDto {
  columnsToExport: string[] | null;
  groupBy: GroupingType;
  fileName: string;
  activeOnly: boolean;
}

export enum GroupingType {
  NONE = 'NONE',
  BY_DOMAINE = 'BY_DOMAINE', 
  BY_SOURCE = 'BY_SOURCE'
}

export interface ColumnOption {
  key: string;
  label: string;
  category: 'base' | 'metier' | 'technique' | 'hierarchie';
  description?: string;
}

export interface ExportFormConfig {
  availableColumns: ColumnOption[];
  defaultGroupBy: GroupingType;
  defaultFileName: string;
  showActiveFilter: boolean;
}
```

---

## 📊 Configuration des Colonnes Disponibles

```typescript
export const AVAILABLE_COLUMNS: ColumnOption[] = [
  // Colonnes de Base
  { key: 'ID', label: 'Identifiant', category: 'base', description: 'ID unique de l\'indicateur' },
  { key: 'INDICATEUR', label: 'Nom', category: 'base', description: 'Nom de l\'indicateur' },
  { key: 'DESCRIPTION', label: 'Description', category: 'base', description: 'Description détaillée' },
  { key: 'ABREVIATION', label: 'Abréviation', category: 'base', description: 'Forme courte du nom' },
  
  // Colonnes Métier
  { key: 'CATEGORIE', label: 'Catégorie', category: 'metier', description: 'Catégorie métier' },
  { key: 'SOURCE', label: 'Source', category: 'metier', description: 'Source des données' },
  { key: 'UNITE', label: 'Unité', category: 'metier', description: 'Unité de mesure' },
  { key: 'REGLE_CALCUL', label: 'Règle de Calcul', category: 'metier', description: 'Formule de calcul' },
  
  // Colonnes Techniques
  { key: 'TYPE_GRAPHE', label: 'Type Graphique', category: 'technique', description: 'Type de visualisation' },
  { key: 'TYPE_TB', label: 'Type Tableau de Bord', category: 'technique', description: 'Type TB' },
  { key: 'ACTIF', label: 'Statut', category: 'technique', description: 'Actif/Inactif' },
  { key: 'HAS_DATA', label: 'A des Données', category: 'technique', description: 'Présence de données' },
  
  // Colonnes Hiérarchie
  { key: 'ESPACES', label: 'Espaces', category: 'hierarchie', description: 'Espaces associés' },
  { key: 'DOMAINES', label: 'Domaines', category: 'hierarchie', description: 'Domaines associés' },
  { key: 'SOUS_DOMAINES', label: 'Sous-Domaines', category: 'hierarchie', description: 'Sous-domaines associés' }
];

export const GROUPING_OPTIONS = [
  { value: GroupingType.NONE, label: 'Aucun groupement', description: 'Toutes les données dans une feuille' },
  { value: GroupingType.BY_DOMAINE, label: 'Par Domaine', description: 'Une feuille par domaine' },
  { value: GroupingType.BY_SOURCE, label: 'Par Source', description: 'Une feuille par source de données' }
];
```

---

## 🎨 Template HTML Recommandé

### Structure du Formulaire

```html
<div class="export-form-container">
  
  <!-- En-tête -->
  <div class="export-header">
    <h3>
      <mat-icon>file_download</mat-icon>
      Exporter les Indicateurs
    </h3>
    <p class="subtitle">Configurez votre export avec les options avancées</p>
  </div>

  <form [formGroup]="exportForm" (ngSubmit)="onExport()">
    
    <!-- Section 1: Options Générales -->
    <mat-card class="form-section">
      <mat-card-header>
        <mat-card-title>🎯 Options Générales</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        
        <!-- Nom du fichier -->
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nom du fichier</mat-label>
          <input matInput formControlName="fileName" placeholder="ex: export_indicateurs_2025">
          <mat-hint>Le fichier sera téléchargé avec l'extension .xlsx</mat-hint>
        </mat-form-field>

        <!-- Filtrage actifs seulement -->
        <div class="checkbox-group">
          <mat-checkbox formControlName="activeOnly">
            <span class="checkbox-label">
              <mat-icon>check_circle</mat-icon>
              Exporter seulement les indicateurs actifs
            </span>
          </mat-checkbox>
        </div>

      </mat-card-content>
    </mat-card>

    <!-- Section 2: Type de Groupement -->
    <mat-card class="form-section">
      <mat-card-header>
        <mat-card-title>📊 Type de Groupement</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        
        <mat-radio-group formControlName="groupBy" class="grouping-options">
          <div *ngFor="let option of groupingOptions" class="radio-option">
            <mat-radio-button [value]="option.value">
              <div class="radio-content">
                <span class="option-label">{{ option.label }}</span>
                <span class="option-description">{{ option.description }}</span>
              </div>
            </mat-radio-button>
          </div>
        </mat-radio-group>

      </mat-card-content>
    </mat-card>

    <!-- Section 3: Sélection des Colonnes -->
    <mat-card class="form-section">
      <mat-card-header>
        <mat-card-title>📋 Colonnes à Exporter</mat-card-title>
        <div class="header-actions">
          <button type="button" mat-stroked-button (click)="selectAllColumns()">
            <mat-icon>select_all</mat-icon>
            Tout Sélectionner
          </button>
          <button type="button" mat-stroked-button (click)="clearAllColumns()">
            <mat-icon>clear</mat-icon>
            Tout Désélectionner
          </button>
        </div>
      </mat-card-header>
      <mat-card-content>

        <!-- Option: Toutes les colonnes -->
        <div class="all-columns-option">
          <mat-checkbox 
            [checked]="isAllColumnsSelected" 
            (change)="onAllColumnsToggle($event)">
            <span class="checkbox-label">
              <mat-icon>view_column</mat-icon>
              <strong>Toutes les colonnes disponibles</strong>
            </span>
          </mat-checkbox>
          <p class="option-hint">Inclut automatiquement toutes les colonnes existantes</p>
        </div>

        <mat-divider></mat-divider>

        <!-- Sélection par catégorie -->
        <div class="columns-selection" *ngIf="!isAllColumnsSelected">
          
          <!-- Filtres par catégorie -->
          <div class="category-filters">
            <mat-chip-list>
              <mat-chip 
                *ngFor="let category of categories" 
                [selected]="selectedCategory === category.key"
                (click)="selectCategory(category.key)">
                <mat-icon>{{ category.icon }}</mat-icon>
                {{ category.label }}
              </mat-chip>
            </mat-chip-list>
          </div>

          <!-- Colonnes par catégorie -->
          <div class="columns-grid">
            <div *ngFor="let column of getFilteredColumns()" class="column-item">
              <mat-checkbox 
                [value]="column.key"
                [checked]="isColumnSelected(column.key)"
                (change)="onColumnToggle(column.key, $event)">
                <div class="column-content">
                  <span class="column-label">{{ column.label }}</span>
                  <span class="column-description" *ngIf="column.description">
                    {{ column.description }}
                  </span>
                </div>
              </mat-checkbox>
            </div>
          </div>

        </div>

      </mat-card-content>
    </mat-card>

    <!-- Section 4: Aperçu de la Configuration -->
    <mat-card class="form-section preview-section">
      <mat-card-header>
        <mat-card-title>👁️ Aperçu de l'Export</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        
        <div class="preview-grid">
          <div class="preview-item">
            <strong>Nom du fichier:</strong>
            <span>{{ exportForm.get('fileName')?.value || 'export_indicateurs' }}.xlsx</span>
          </div>
          
          <div class="preview-item">
            <strong>Type de groupement:</strong>
            <span>{{ getGroupingLabel() }}</span>
          </div>
          
          <div class="preview-item">
            <strong>Filtrage:</strong>
            <span>{{ exportForm.get('activeOnly')?.value ? 'Actifs seulement' : 'Tous les indicateurs' }}</span>
          </div>
          
          <div class="preview-item">
            <strong>Colonnes sélectionnées:</strong>
            <span>{{ getSelectedColumnsCount() }} colonne(s)</span>
          </div>
        </div>

        <!-- Liste des colonnes sélectionnées -->
        <div class="selected-columns" *ngIf="!isAllColumnsSelected && getSelectedColumns().length > 0">
          <h4>Colonnes qui seront exportées:</h4>
          <mat-chip-list>
            <mat-chip *ngFor="let column of getSelectedColumns()">
              {{ column.label }}
              <mat-icon matChipRemove (click)="removeColumn(column.key)">cancel</mat-icon>
            </mat-chip>
          </mat-chip-list>
        </div>

      </mat-card-content>
    </mat-card>

    <!-- Actions -->
    <div class="form-actions">
      <button 
        type="button" 
        mat-stroked-button 
        (click)="onReset()"
        [disabled]="isExporting">
        <mat-icon>refresh</mat-icon>
        Réinitialiser
      </button>
      
      <button 
        type="submit" 
        mat-raised-button 
        color="primary"
        [disabled]="!exportForm.valid || isExporting">
        
        <mat-icon *ngIf="!isExporting">file_download</mat-icon>
        <mat-spinner *ngIf="isExporting" diameter="20"></mat-spinner>
        
        {{ isExporting ? 'Export en cours...' : 'Exporter' }}
      </button>
    </div>

  </form>

  <!-- Messages d'état -->
  <div class="status-messages" *ngIf="statusMessage">
    <mat-card [ngClass]="statusType">
      <mat-card-content>
        <div class="status-content">
          <mat-icon>{{ getStatusIcon() }}</mat-icon>
          <span>{{ statusMessage }}</span>
        </div>
      </mat-card-content>
    </mat-card>
  </div>

</div>
```

---

## 🔧 Component TypeScript

### Structure du Component

```typescript
@Component({
  selector: 'app-indicateur-export-form',
  templateUrl: './indicateur-export-form.component.html',
  styleUrls: ['./indicateur-export-form.component.scss']
})
export class IndicateurExportFormComponent implements OnInit {
  
  exportForm: FormGroup;
  isExporting = false;
  statusMessage = '';
  statusType: 'success' | 'error' | 'info' = 'info';

  // Configuration
  availableColumns = AVAILABLE_COLUMNS;
  groupingOptions = GROUPING_OPTIONS;
  categories = [
    { key: 'all', label: 'Toutes', icon: 'view_column' },
    { key: 'base', label: 'Base', icon: 'info' },
    { key: 'metier', label: 'Métier', icon: 'business' },
    { key: 'technique', label: 'Technique', icon: 'settings' },
    { key: 'hierarchie', label: 'Hiérarchie', icon: 'account_tree' }
  ];

  selectedCategory = 'all';
  selectedColumns: string[] = [];
  isAllColumnsSelected = true;

  constructor(
    private fb: FormBuilder,
    private exportService: ExportService,
    private snackBar: MatSnackBar
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.loadDefaultConfiguration();
  }

  private initForm(): void {
    this.exportForm = this.fb.group({
      fileName: ['export_indicateurs_' + this.getCurrentDate(), [Validators.required]],
      activeOnly: [false],
      groupBy: [GroupingType.NONE]
    });
  }

  // Méthodes pour la gestion des colonnes
  onAllColumnsToggle(event: MatCheckboxChange): void {
    this.isAllColumnsSelected = event.checked;
    if (!event.checked && this.selectedColumns.length === 0) {
      // Sélectionner quelques colonnes par défaut
      this.selectedColumns = ['ID', 'INDICATEUR', 'DESCRIPTION', 'ACTIF'];
    }
  }

  onColumnToggle(columnKey: string, event: MatCheckboxChange): void {
    if (event.checked) {
      if (!this.selectedColumns.includes(columnKey)) {
        this.selectedColumns.push(columnKey);
      }
    } else {
      this.selectedColumns = this.selectedColumns.filter(col => col !== columnKey);
    }
  }

  isColumnSelected(columnKey: string): boolean {
    return this.selectedColumns.includes(columnKey);
  }

  getSelectedColumns(): ColumnOption[] {
    return this.availableColumns.filter(col => 
      this.selectedColumns.includes(col.key)
    );
  }

  getSelectedColumnsCount(): number {
    return this.isAllColumnsSelected ? this.availableColumns.length : this.selectedColumns.length;
  }

  selectAllColumns(): void {
    this.isAllColumnsSelected = true;
  }

  clearAllColumns(): void {
    this.isAllColumnsSelected = false;
    this.selectedColumns = [];
  }

  // Gestion des catégories
  selectCategory(category: string): void {
    this.selectedCategory = category;
  }

  getFilteredColumns(): ColumnOption[] {
    if (this.selectedCategory === 'all') {
      return this.availableColumns;
    }
    return this.availableColumns.filter(col => col.category === this.selectedCategory);
  }

  // Méthodes utilitaires
  getGroupingLabel(): string {
    const selected = this.groupingOptions.find(
      opt => opt.value === this.exportForm.get('groupBy')?.value
    );
    return selected?.label || 'Aucun groupement';
  }

  getCurrentDate(): string {
    return new Date().toISOString().slice(0, 10).replace(/-/g, '');
  }

  // Export
  async onExport(): Promise<void> {
    if (!this.exportForm.valid) return;

    this.isExporting = true;
    this.statusMessage = 'Préparation de l\'export...';
    this.statusType = 'info';

    try {
      const exportRequest: ExportRequestDto = {
        columnsToExport: this.isAllColumnsSelected ? null : this.selectedColumns,
        groupBy: this.exportForm.get('groupBy')?.value,
        fileName: this.exportForm.get('fileName')?.value,
        activeOnly: this.exportForm.get('activeOnly')?.value
      };

      await this.exportService.exportIndicateurs(exportRequest);
      
      this.statusMessage = 'Export réussi ! Le fichier a été téléchargé.';
      this.statusType = 'success';
      
    } catch (error) {
      console.error('Erreur lors de l\'export:', error);
      this.statusMessage = 'Erreur lors de l\'export. Veuillez réessayer.';
      this.statusType = 'error';
    } finally {
      this.isExporting = false;
      setTimeout(() => this.statusMessage = '', 5000);
    }
  }

  onReset(): void {
    this.exportForm.reset();
    this.initForm();
    this.selectedColumns = [];
    this.isAllColumnsSelected = true;
    this.selectedCategory = 'all';
    this.statusMessage = '';
  }

  getStatusIcon(): string {
    switch (this.statusType) {
      case 'success': return 'check_circle';
      case 'error': return 'error';
      default: return 'info';
    }
  }
}
```

---

## 🎨 Styles SCSS Recommandés

```scss
.export-form-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;

  .export-header {
    text-align: center;
    margin-bottom: 30px;

    h3 {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      color: #1976d2;
    }

    .subtitle {
      color: #666;
      margin-top: 8px;
    }
  }

  .form-section {
    margin-bottom: 24px;

    mat-card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-actions {
        display: flex;
        gap: 8px;
      }
    }
  }

  .full-width {
    width: 100%;
  }

  .checkbox-group {
    margin: 16px 0;

    .checkbox-label {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .grouping-options {
    display: flex;
    flex-direction: column;
    gap: 16px;

    .radio-option {
      .radio-content {
        display: flex;
        flex-direction: column;

        .option-label {
          font-weight: 500;
        }

        .option-description {
          font-size: 0.9em;
          color: #666;
        }
      }
    }
  }

  .all-columns-option {
    margin-bottom: 16px;

    .option-hint {
      font-size: 0.9em;
      color: #666;
      margin-left: 32px;
      margin-top: 4px;
    }
  }

  .category-filters {
    margin: 16px 0;

    mat-chip {
      margin-right: 8px;
    }
  }

  .columns-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 12px;
    margin-top: 16px;

    .column-item {
      .column-content {
        display: flex;
        flex-direction: column;

        .column-label {
          font-weight: 500;
        }

        .column-description {
          font-size: 0.85em;
          color: #666;
        }
      }
    }
  }

  .preview-section {
    background-color: #f5f5f5;

    .preview-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 16px;

      .preview-item {
        display: flex;
        flex-direction: column;
        gap: 4px;

        strong {
          color: #1976d2;
        }
      }
    }

    .selected-columns {
      margin-top: 16px;

      h4 {
        margin-bottom: 8px;
        color: #1976d2;
      }
    }
  }

  .form-actions {
    display: flex;
    justify-content: space-between;
    margin-top: 24px;
    gap: 16px;

    button {
      min-width: 120px;
    }
  }

  .status-messages {
    margin-top: 16px;

    &.success {
      mat-card {
        background-color: #e8f5e8;
        border-left: 4px solid #4caf50;
      }
    }

    &.error {
      mat-card {
        background-color: #ffebee;
        border-left: 4px solid #f44336;
      }
    }

    &.info {
      mat-card {
        background-color: #e3f2fd;
        border-left: 4px solid #2196f3;
      }
    }

    .status-content {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }
}

// Responsive
@media (max-width: 768px) {
  .export-form-container {
    padding: 16px;

    .columns-grid {
      grid-template-columns: 1fr;
    }

    .preview-grid {
      grid-template-columns: 1fr;
    }

    .form-actions {
      flex-direction: column;
    }
  }
}
```

---

## 🚀 Service TypeScript

```typescript
@Injectable({
  providedIn: 'root'
})
export class ExportService {
  
  private readonly apiUrl = '/api/v1/indicateurs/audit';

  constructor(private http: HttpClient) {}

  async exportIndicateurs(request: ExportRequestDto): Promise<void> {
    try {
      const response = await this.http.post(
        `${this.apiUrl}/export/custom`,
        request,
        {
          responseType: 'blob',
          headers: {
            'Content-Type': 'application/json'
          }
        }
      ).toPromise();

      // Télécharger le fichier
      this.downloadFile(response!, request.fileName);
      
    } catch (error) {
      console.error('Erreur export:', error);
      throw error;
    }
  }

  private downloadFile(blob: Blob, fileName: string): void {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${fileName}.xlsx`;
    link.click();
    window.URL.revokeObjectURL(url);
  }
}
```

---

## 🎯 Fonctionnalités Clés à Implémenter

### ✅ **Must Have (Essentiel)**
1. **Sélection de colonnes** par checkbox avec catégories
2. **Options de groupement** (aucun, par domaine, par source)
3. **Nom de fichier personnalisable**
4. **Filtre actifs seulement**
5. **Bouton d'export avec loading**
6. **Gestion d'erreurs**

### 🔥 **Should Have (Recommandé)**
1. **Aperçu de la configuration**
2. **Sélection rapide** (tout/rien)
3. **Filtres par catégorie de colonnes**
4. **Messages de statut**
5. **Validation du formulaire**
6. **Design responsive**

### 💎 **Could Have (Bonus)**
1. **Sauvegarde de configurations**
2. **Templates d'export prédéfinis**
3. **Prévisualisation des données**
4. **Historique des exports**
5. **Export programmé**

---

## 🔧 Modules Angular Requis

```typescript
// Dans votre module
imports: [
  CommonModule,
  ReactiveFormsModule,
  MatCardModule,
  MatFormFieldModule,
  MatInputModule,
  MatCheckboxModule,
  MatRadioModule,
  MatButtonModule,
  MatIconModule,
  MatChipsModule,
  MatProgressSpinnerModule,
  MatSnackBarModule,
  MatDividerModule
]
```

---

## 🧪 Cas de Test à Prévoir

1. **Export avec toutes les colonnes**
2. **Export avec colonnes sélectionnées**
3. **Export groupé par domaine**
4. **Export groupé par source**
5. **Export actifs seulement**
6. **Gestion des erreurs serveur**
7. **Validation des champs requis**
8. **Responsive sur mobile**

---

Ce guide vous donne tout ce qu'il faut pour créer un composant d'export puissant et user-friendly ! 🚀
