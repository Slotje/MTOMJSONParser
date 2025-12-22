import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-json-viewer',
  templateUrl: './json-viewer.component.html',
  styleUrls: ['./json-viewer.component.scss']
})
export class JsonViewerComponent {
  @Input() data: any;

  get formattedJson(): string {
    return JSON.stringify(this.data, null, 2);
  }

  getFieldCount(): number {
    return this.data ? Object.keys(this.data).length : 0;
  }

  isArray(value: any): boolean {
    return Array.isArray(value);
  }

  asArray(value: any): any[] {
    return value as any[];
  }

  copyToClipboard(): void {
    navigator.clipboard.writeText(this.formattedJson).then(() => {
      alert('JSON copied to clipboard!');
    });
  }
}
