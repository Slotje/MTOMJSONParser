import { Component, OnInit } from '@angular/core';
import { MtomApiService } from '../../services/mtom-api.service';
import { ErrorResponse } from '../../models/error-response.model';

@Component({
  selector: 'app-mtom-upload',
  templateUrl: './mtom-upload.component.html',
  styleUrls: ['./mtom-upload.component.scss']
})
export class MtomUploadComponent implements OnInit {
  selectedFile: File | null = null;
  loading = false;
  parsedJson: any | null = null;  // Simple JSON object with all extracted fields
  error: ErrorResponse | null = null;
  mtomContent: string = '';

  constructor(private mtomApiService: MtomApiService) {}

  ngOnInit(): void {
    // No client configuration needed for automatic parsing
  }

  onFileSelected(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      this.selectedFile = target.files[0];
      this.readFileContent();
    }
  }

  readFileContent(): void {
    if (!this.selectedFile) return;

    const reader = new FileReader();
    reader.onload = (e) => {
      this.mtomContent = e.target?.result as string;
    };
    reader.readAsText(this.selectedFile);
  }

  convertMtom(): void {
    if (!this.mtomContent) {
      alert('Please select a file');
      return;
    }

    this.loading = true;
    this.parsedJson = null;
    this.error = null;

    this.mtomApiService.parseMtom(this.mtomContent).subscribe({
      next: (result) => {
        this.parsedJson = result;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error as ErrorResponse;
        this.loading = false;
      }
    });
  }

  clear(): void {
    this.selectedFile = null;
    this.mtomContent = '';
    this.parsedJson = null;
    this.error = null;
  }

  downloadJson(): void {
    if (!this.parsedJson) return;

    const json = JSON.stringify(this.parsedJson, null, 2);
    const blob = new Blob([json], { type: 'application/json' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    const timestamp = new Date().getTime();
    link.download = `mtom-converted-${timestamp}.json`;
    link.click();
    window.URL.revokeObjectURL(url);
  }
}
