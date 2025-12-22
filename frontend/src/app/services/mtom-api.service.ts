import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ParsedMessage } from '../models/parsed-message.model';
import { ClientConfiguration } from '../models/client-configuration.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MtomApiService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /**
   * Convert MTOM XML to JSON
   */
  convertMtom(mtomXml: string, clientId: string): Observable<ParsedMessage> {
    const params = new HttpParams().set('clientId', clientId);

    return this.http.post<ParsedMessage>(
      `${this.apiUrl}/api/v1/mtom/convert`,
      mtomXml,
      {
        params,
        headers: {
          'Content-Type': 'application/xml'
        }
      }
    );
  }

  /**
   * Validate MTOM XML without full conversion
   */
  validateMtom(mtomXml: string, clientId: string): Observable<any> {
    const params = new HttpParams().set('clientId', clientId);

    return this.http.post(
      `${this.apiUrl}/api/v1/mtom/validate`,
      mtomXml,
      {
        params,
        headers: {
          'Content-Type': 'application/xml'
        }
      }
    );
  }

  /**
   * Get all client configurations
   */
  getAllConfigurations(): Observable<{ [key: string]: ClientConfiguration }> {
    return this.http.get<{ [key: string]: ClientConfiguration }>(
      `${this.apiUrl}/api/v1/config`
    );
  }

  /**
   * Get specific client configuration
   */
  getConfiguration(clientId: string): Observable<ClientConfiguration> {
    return this.http.get<ClientConfiguration>(
      `${this.apiUrl}/api/v1/config/${clientId}`
    );
  }

  /**
   * Create or update client configuration
   */
  saveConfiguration(config: ClientConfiguration): Observable<ClientConfiguration> {
    return this.http.post<ClientConfiguration>(
      `${this.apiUrl}/api/v1/config`,
      config
    );
  }

  /**
   * Update client configuration
   */
  updateConfiguration(clientId: string, config: ClientConfiguration): Observable<ClientConfiguration> {
    return this.http.put<ClientConfiguration>(
      `${this.apiUrl}/api/v1/config/${clientId}`,
      config
    );
  }

  /**
   * Delete client configuration
   */
  deleteConfiguration(clientId: string): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/api/v1/config/${clientId}`
    );
  }

  /**
   * Reload client configuration
   */
  reloadConfiguration(clientId: string): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/api/v1/config/${clientId}/reload`,
      {}
    );
  }

  /**
   * Reload all configurations
   */
  reloadAllConfigurations(): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/api/v1/config/reload-all`,
      {}
    );
  }
}
