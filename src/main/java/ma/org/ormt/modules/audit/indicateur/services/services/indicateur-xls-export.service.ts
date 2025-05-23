import { Injectable } from '@angular/core';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

@Injectable({
  providedIn: 'root'
})
export class IndicateurXlsExportService {
  constructor() { }

  /**
   * Helper to build a file name for export: indicator name (no spaces), plus export datetime.
   */
  buildExportFileName(baseName: string, suffix = ''): string {
    const exportDate = new Date();
    const dateStr = exportDate.getFullYear().toString().padStart(4, '0') +
      (exportDate.getMonth() + 1).toString().padStart(2, '0') +
      exportDate.getDate().toString().padStart(2, '0') + '_'
      + exportDate.getHours().toString().padStart(2, '0')
      + exportDate.getMinutes().toString().padStart(2, '0')
      + exportDate.getSeconds().toString().padStart(2, '0');
    const base = baseName.replace(/\s+/g, '').replace(/[^\w]/gi, '');
    return `${base}${suffix ? '_' + suffix : ''}_${dateStr}.xlsx`;
  }

  /**
   * Export the provided sheets to Excel.
   * Each sheet: { label: string, data: any[][], colCount: number }
   */
  exportSheets(baseName: string, sheets: Array<{ label: string, data: any[][], colCount: number }>, fileSuffix = '') {
    if (!sheets || sheets.length === 0) return;
    try {
      const workbook = XLSX.utils.book_new();
      sheets.forEach(sheet => {
        if (sheet.data && sheet.data.length > 0) {
          const ws = XLSX.utils.aoa_to_sheet(sheet.data);
          this.formatWorksheet(ws, sheet.colCount);
          XLSX.utils.book_append_sheet(workbook, ws, sheet.label);
        }
      });
      const fileName = this.buildExportFileName(baseName, fileSuffix);
      const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
      const dataBlob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      saveAs(dataBlob, fileName);
    } catch (error) {
      console.error('Error exporting data to Excel:', error);
    }
  }

  private formatWorksheet(worksheet: any, numberOfColumns: number) {
    // Set column widths
    const columnWidths = [];
    for (let i = 0; i < numberOfColumns; i++) {
      columnWidths.push({ width: 20 }); // Set a default width for all columns
    }
    worksheet['!cols'] = columnWidths;

    // Apply any other formatting needed (e.g., font size, bold headers, etc.)
    const range = XLSX.utils.decode_range(worksheet['!ref']!);
    for (let C = range.s.c; C <= range.e.c; ++C) {
      const cell = worksheet[XLSX.utils.encode_cell({ r: range.s.r, c: C })];
      if (cell && cell.t === 's') {
        // Apply string formatting
        cell.z = '@';
      }
    }
  }
}
