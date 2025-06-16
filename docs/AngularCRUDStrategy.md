# Angular CRUD Strategy for Dynamic Dimensions

## 🎯 Backend CRUD API Support

### New Table Formats Available:
```typescript
// API endpoints
GET /api/v1/admin/indicateurs/{id}?tableFormat=crud     // For edit/delete with IDs
GET /api/v1/admin/indicateurs/{id}?tableFormat=create   // For create form templates
GET /api/v1/admin/indicateurs/{id}?tableFormat=all      // All formats in one call
```

### Response Structure:
```typescript
interface IndicateurDetailResponse {
  id: number;
  nom: string;
  unite: string;
  typeGraphe: string;
  indicateurDimensions: IndicateurDimension[];
  donnees: DonneeIndicateur[];
  
  // Table formats (optional based on request)
  pivotTableData?: string[][];     // For charts/analysis
  flatTableData?: string[][];      // For display
  crudTableData?: string[][];      // For edit/delete (includes IDs)
  createTemplateData?: string[][]; // For create form
}
```

## 🅰️ Angular Implementation Strategy

### 1. **Data Service**
```typescript
@Injectable({
  providedIn: 'root'
})
export class IndicateurDataService {
  
  getIndicateurForCRUD(id: number): Observable<IndicateurDetailResponse> {
    return this.http.get<RestResponse<IndicateurDetailResponse>>(
      `/api/v1/admin/indicateurs/${id}?tableFormat=all`
    ).pipe(map(response => response.data));
  }

  getCreateTemplate(id: number): Observable<string[][]> {
    return this.http.get<RestResponse<IndicateurDetailResponse>>(
      `/api/v1/admin/indicateurs/${id}?tableFormat=create`
    ).pipe(map(response => response.data.createTemplateData || []));
  }
}
```

### 2. **Dynamic Form Generation** 
```typescript
@Component({
  selector: 'app-indicateur-create',
  template: `
    <form [formGroup]="dynamicForm" (ngSubmit)="onSubmit()">
      <div *ngFor="let dimension of dimensions" class="form-group">
        <label>{{ dimension.libelle || dimension.nom }}</label>
        
        <!-- Dropdown for categorical dimensions -->
        <select *ngIf="dimension.type === 'categorical'" 
                [formControlName]="dimension.nom"
                class="form-control">
          <option value="">Sélectionner...</option>
          <option *ngFor="let value of getDimensionValues(dimension.nom)" 
                  [value]="value">{{ value }}</option>
        </select>
        
        <!-- Date picker for temporal dimensions -->
        <input *ngIf="dimension.type === 'temporal'" 
               type="date" 
               [formControlName]="dimension.nom"
               class="form-control">
               
        <!-- Text input for other types -->
        <input *ngIf="!dimension.type || dimension.type === 'text'" 
               type="text" 
               [formControlName]="dimension.nom"
               class="form-control">
      </div>
      
      <!-- Value input -->
      <div class="form-group">
        <label>Valeur ({{ indicateur.unite }})</label>
        <input type="number" 
               formControlName="valeur" 
               class="form-control" 
               [placeholder]="'Entrer la valeur en ' + indicateur.unite">
      </div>
      
      <button type="submit" [disabled]="!dynamicForm.valid">Créer</button>
    </form>
  `
})
export class IndicateurCreateComponent implements OnInit {
  dynamicForm: FormGroup;
  dimensions: IndicateurDimension[] = [];
  availableValues: Map<string, string[]> = new Map();

  constructor(
    private fb: FormBuilder,
    private service: IndicateurDataService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    const indicateurId = this.route.snapshot.params['id'];
    this.loadFormStructure(indicateurId);
  }

  private loadFormStructure(id: number) {
    this.service.getIndicateurForCRUD(id).subscribe(data => {
      this.dimensions = data.indicateurDimensions;
      this.buildForm();
      this.extractAvailableValues(data);
    });
  }

  private buildForm() {
    const formControls: any = {};
    
    this.dimensions.forEach(dim => {
      formControls[dim.dimension.nom] = ['', Validators.required];
    });
    
    formControls['valeur'] = ['', [Validators.required, Validators.pattern(/^\d+(\.\d{1,2})?$/)]];
    
    this.dynamicForm = this.fb.group(formControls);
  }

  private extractAvailableValues(data: IndicateurDetailResponse) {
    // Extract unique values from existing data for dropdowns
    this.dimensions.forEach(dim => {
      const values = new Set<string>();
      data.donnees?.forEach(donnee => {
        const dimValue = donnee.valeurDimensions?.find(
          vd => vd.dimension.nom === dim.dimension.nom
        );
        if (dimValue?.valeur) {
          values.add(dimValue.valeur);
        }
      });
      this.availableValues.set(dim.dimension.nom, Array.from(values).sort());
    });
  }

  getDimensionValues(dimensionNom: string): string[] {
    return this.availableValues.get(dimensionNom) || [];
  }

  onSubmit() {
    if (this.dynamicForm.valid) {
      const formValue = this.dynamicForm.value;
      // Create DonneeIndicateur object and send to backend
      const newDonnee = this.buildDonneeFromForm(formValue);
      // Call your CRUD API here
    }
  }
}
```

### 3. **Data Table with CRUD Operations**
```typescript
@Component({
  selector: 'app-indicateur-crud-table',
  template: `
    <nz-table #crudTable 
              [nzData]="tableData" 
              [nzShowPagination]="false"
              nzBordered>
      
      <!-- Dynamic headers -->
      <thead>
        <tr>
          <th *ngFor="let header of headers">{{ header }}</th>
          <th>Actions</th>
        </tr>
      </thead>
      
      <!-- Dynamic rows -->
      <tbody>
        <tr *ngFor="let row of tableData; trackBy: trackByRowId">
          <!-- Editable cells -->
          <td *ngFor="let cell of row; let i = index">
            <div *ngIf="!isEditingRow(row[0]); else editCell">
              {{ cell }}
            </div>
            <ng-template #editCell>
              <input *ngIf="i === row.length - 1" 
                     [(ngModel)]="editingValues[row[0]]"
                     class="form-control"
                     type="number">
              <span *ngIf="i !== row.length - 1">{{ cell }}</span>
            </ng-template>
          </td>
          
          <!-- Action buttons -->
          <td>
            <div *ngIf="!isEditingRow(row[0]); else editActions">
              <button nz-button nzType="link" (click)="startEdit(row)">
                <i nz-icon nzType="edit"></i>
              </button>
              <button nz-button nzType="link" nzDanger (click)="deleteRow(row[0])">
                <i nz-icon nzType="delete"></i>
              </button>
            </div>
            <ng-template #editActions>
              <button nz-button nzType="link" (click)="saveEdit(row[0])">
                <i nz-icon nzType="check"></i>
              </button>
              <button nz-button nzType="link" (click)="cancelEdit(row[0])">
                <i nz-icon nzType="close"></i>
              </button>
            </ng-template>
          </td>
        </tr>
      </tbody>
    </nz-table>
    
    <!-- Create new row button -->
    <button nz-button nzType="dashed" (click)="openCreateModal()">
      <i nz-icon nzType="plus"></i> Ajouter une nouvelle donnée
    </button>
  `
})
export class IndicateurCrudTableComponent implements OnInit {
  tableData: string[][] = [];
  headers: string[] = [];
  editingRows: Set<string> = new Set();
  editingValues: Map<string, string> = new Map();

  ngOnInit() {
    this.loadCrudData();
  }

  private loadCrudData() {
    const indicateurId = this.route.snapshot.params['id'];
    this.service.getIndicateurForCRUD(indicateurId).subscribe(data => {
      this.tableData = data.crudTableData?.slice(1) || []; // Remove header
      this.headers = data.crudTableData?.[0] || [];
    });
  }

  trackByRowId(index: number, row: string[]): string {
    return row[0]; // Use ID for tracking
  }

  isEditingRow(rowId: string): boolean {
    return this.editingRows.has(rowId);
  }

  startEdit(row: string[]) {
    const rowId = row[0];
    const valeur = row[row.length - 1]; // Last column is valeur
    this.editingRows.add(rowId);
    this.editingValues.set(rowId, valeur);
  }

  saveEdit(rowId: string) {
    const newValue = this.editingValues.get(rowId);
    if (newValue) {
      // Call update API
      this.updateDonnee(rowId, newValue).subscribe(() => {
        this.editingRows.delete(rowId);
        this.editingValues.delete(rowId);
        this.loadCrudData(); // Refresh data
      });
    }
  }

  cancelEdit(rowId: string) {
    this.editingRows.delete(rowId);
    this.editingValues.delete(rowId);
  }

  deleteRow(rowId: string) {
    // Show confirmation then delete
    this.modal.confirm({
      nzTitle: 'Confirmer la suppression',
      nzContent: 'Êtes-vous sûr de vouloir supprimer cette donnée?',
      nzOnOk: () => {
        return this.deleteDonnee(rowId).toPromise().then(() => {
          this.loadCrudData(); // Refresh data
        });
      }
    });
  }

  openCreateModal() {
    // Open modal with dynamic form
    const modal = this.modal.create({
      nzTitle: 'Ajouter une nouvelle donnée',
      nzContent: IndicateurCreateComponent,
      nzWidth: 600,
      nzComponentParams: {
        indicateurId: this.route.snapshot.params['id']
      }
    });
    
    modal.afterClose.subscribe(result => {
      if (result) {
        this.loadCrudData(); // Refresh data
      }
    });
  }
}
```

### 4. **CRUD API Calls**
```typescript
// In your service
updateDonnee(donneeId: string, newValue: string): Observable<any> {
  return this.http.put(`/api/v1/admin/donnees/${donneeId}`, {
    valeur: newValue
  });
}

deleteDonnee(donneeId: string): Observable<any> {
  return this.http.delete(`/api/v1/admin/donnees/${donneeId}`);
}

createDonnee(indicateurId: number, donneeData: any): Observable<any> {
  return this.http.post(`/api/v1/admin/indicateurs/${indicateurId}/donnees`, donneeData);
}
```

## 🎨 UI/UX Benefits

1. **Dynamic Forms**: Forms automatically adapt to any dimension combination
2. **Smart Validation**: Validates based on existing dimension values
3. **Inline Editing**: Edit values directly in the table
4. **Bulk Operations**: Select multiple rows for bulk delete
5. **Smart Defaults**: Pre-populate form fields based on existing patterns
6. **Contextual Help**: Show dimension descriptions and examples

## 🚀 Implementation Steps

1. ✅ **Backend CRUD API** - Already implemented
2. **Angular Service** - Create data service with CRUD methods
3. **Dynamic Form Component** - Build reusable form generator
4. **CRUD Table Component** - Create editable data table
5. **Modal/Dialog Integration** - For create/edit operations
6. **Validation & Error Handling** - Form validation and API error handling

This approach gives you a fully dynamic CRUD system that works with any dimension combination while maintaining type safety and good UX practices!
