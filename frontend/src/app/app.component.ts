import { Component, ViewChild } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTable } from '@angular/material/table';
import { Movie, MovieBackendService } from './services/movie-backend.service';
import { catchError } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
 
  movies: Movie[] = [
    {title: 'Star  Wars: A New Hope', director: 'George Lucas', year: 1977},
    {title: 'Star Wars: The Empire Strikes Back', director: 'George Lucas', year: 1980},
    {title: 'Star Wars: Return of the Jedi', director: 'George Lucas', year: 1983}
  ]
  displayedColumns: string[] = ['title', 'director', 'year']; 
  
  @ViewChild(MatTable) table: MatTable<any>;

  constructor(
    private backend: MovieBackendService,
    private snackBar: MatSnackBar) {
    
    }

  getAllMovies() {
    this.backend.getAllMovies().subscribe(
        
        response => {
          this.movies = response
          this.table.renderRows();          
        },

        error => {
          this.handleError(error.error)
        })
  }

  onMovieIdChange(event: any){
    this.getMovieById(event.value);
  }

  private getMovieById(id: number) {
    this.backend.getMovieById(id).subscribe(
          
        response => {
          this.movies = [response]
          this.table.renderRows();
        },
        
        error => {
          this.handleError(error.error)
        })
  }

  private handleError(error: any) {
    this.displayError(error.code + ' ' + error.reason + ". " + error.message)
  }

  private displayError(message: string) {
    this.snackBar.open(message, 'Close', { duration: 5000})
  }
}
