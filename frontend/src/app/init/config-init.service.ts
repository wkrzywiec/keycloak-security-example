import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, mergeMap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ConfigInitService {

  private readonly prodConfigFilePath = 'assets/config/config.json';
  private readonly devConfigFilePath = 'assets/config/config.dev.json';
  private config: any;

  private readonly keyclaokUrl: string;
  private readonly keycloakRealm: string;
  private readonly clientId: string;

  constructor(private httpClient: HttpClient) {
      this.getConfig();
      console.log(this.config)
      
      this.keyclaokUrl = this.getValue('KEYCLOAK_URL');
      this.keycloakRealm = this.getValue('KEYCLOAK_REALM');
      this.clientId = this.getValue('KEYCLOAK_CLIENT_ID');
   }

  public getKeycloakUrl() {
    return this.keyclaokUrl;
  }

  public getKeycloakRealm() {
    return this.keycloakRealm;
  }

  public getKeycloakClientId() {
    return this.clientId;
  }

  private getConfig(): Observable<any> {
    return this.httpClient
        .get(this.getConfigFile(), {
          observe: 'response',
        })
        .pipe(
          catchError((error) => of(null)),
          mergeMap((response) => {
            if (response && response.body) {
              this.config = response.body;
              return of(this.config);
            } else {
              return of(null);
            }
          })
        );
  }

  private getConfigFile(): string {
    return environment.production ? this.prodConfigFilePath : this.devConfigFilePath;
  }

  private getValue(key: string): any {
    return this.getConfig().pipe(
      mergeMap((config) => {
        if (config) {
          return config[key];
        }
        return null;
      })
    );
  }
}
