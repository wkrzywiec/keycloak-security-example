import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, mergeMap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ConfigInitService {

  private config: any;

  constructor(private httpClient: HttpClient) {}

  public getConfig(): Observable<any> {
    return this.httpClient
        .get(this.getConfigFile(), {
          observe: 'response',
        })
        .pipe(
          catchError((error) => {
            console.log(error)
            return of(null)
          } ),
          mergeMap((response) => {
            if (response && response.body) {
              this.config = response.body;
              return of(this.config);
            } else {
              return of(null);
            }
          }));
  }

  private getConfigFile(): string {
    return environment.configFile
  }
}
