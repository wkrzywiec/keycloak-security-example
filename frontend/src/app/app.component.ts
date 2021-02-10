import { Component } from '@angular/core';

export interface Movie {
  title: string;
  director: string;
  year: number;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'frontend';

  movies: Movie[] = [
    {title: 'Star  Wars: A New Hope', director: 'George Lucas', year: 1977},
    {title: 'Star Wars: The Empire Strikes Back', director: 'George Lucas', year: 1980},
    {title: 'Star Wars: Return of the Jedi', director: 'George Lucas', year: 1983}
  ]
  displayedColumns: string[] = ['title', 'director', 'year'];  
}
