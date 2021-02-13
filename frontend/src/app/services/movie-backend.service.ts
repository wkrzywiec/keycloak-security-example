import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Movie {
  title: string;
  director: string;
  year: number;
}

export interface Problem {
  code: number;
  reason: string;
  timestamp: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class MovieBackendService {

  private readonly backendUrl = '/movies'

  constructor(private http: HttpClient) { }

  getAllMovies(): Observable<Array<Movie>> {
    return this.http.get<Array<Movie>>(this.backendUrl);
  }

  getMovieById(id: number): Observable<Movie> {
    return this.http.get<Movie>(this.backendUrl + '/' + id);
  }
}
