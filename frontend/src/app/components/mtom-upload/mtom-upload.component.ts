import { Component, OnInit } from '@angular/core';
import { MtomApiService } from '../../services/mtom-api.service';
import { ParsedMessage } from '../../models/parsed-message.model';
import { ClientConfiguration } from '../../models/client-configuration.model';
import { ErrorResponse } from '../../models/error-response.model';

@Component({
  selector: 'app-mtom-upload',
  templateUrl: './mtom-upload.component.html',
  styleUrls: ['./mtom-upload.component.scss']
})
export class MtomUploadComponent implements OnInit {
  selectedFile: File | null = null;
  selectedClientId: string = '';
  clients: ClientConfiguration[] = [];
  loading = false;
  parsedMessage: ParsedMessage | null = null;
  error: ErrorResponse | null = null;
  mtomContent: string = '';

  constructor(private mtomApiService: MtomApiService) {}

  ngOnInit(): void {
    this.loadClients();
  }

  loadClients(): void {
    this.mtomApiService.getAllConfigurations().subscribe({
      next: (configs) => {
        this.clients = Object.values(configs);
        if (this.clients.length > 0) {
          this.selectedClientId = this.clients[0].clientId;
        }
      },
      error: (err) => {
        console.error('Failed to load client configurations', err);
      }
    });
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
    if (!this.mtomContent || !this.selectedClientId) {
      alert('Please select a file and client');
      return;
    }

    this.loading = true;
    this.parsedMessage = null;
    this.error = null;

    this.mtomApiService.convertMtom(this.mtomContent, this.selectedClientId).subscribe({
      next: (result) => {
        this.parsedMessage = result;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error as ErrorResponse;
        this.loading = false;
      }
    });
  }

  validateMtom(): void {
    if (!this.mtomContent || !this.selectedClientId) {
      alert('Please select a file and client');
      return;
    }

    this.loading = true;
    this.error = null;

    this.mtomApiService.validateMtom(this.mtomContent, this.selectedClientId).subscribe({
      next: (result) => {
        alert('MTOM message is valid!');
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
    this.parsedMessage = null;
    this.error = null;
  }

  downloadJson(): void {
    if (!this.parsedMessage) return;

    const json = JSON.stringify(this.parsedMessage, null, 2);
    const blob = new Blob([json], { type: 'application/json' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${this.parsedMessage.messageId}.json`;
    link.click();
    window.URL.revokeObjectURL(url);
  }
}
