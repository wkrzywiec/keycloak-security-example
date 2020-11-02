package io.wkrzywiec.keycloak.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class MovieController {

    ConcurrentHashMap<Long, Movie> movies;

    public MovieController() {
        movies = new ConcurrentHashMap<>()
        {{
            put(1L, new Movie("Star Wars: A New Hope", "George Lucas", 1977));
            put(2L, new Movie("Star Wars: The Empire Strikes Back", "George Lucas", 1980));
            put(3L, new Movie("Star Wars: Return of the Jedi", "George Lucas", 1983));
        }};
    }

    @GetMapping("/movies")
    public List<Movie> getAllMovies(){
        return new ArrayList<>(movies.values());
    }

    @GetMapping("/movies/{id}")
    public Movie getMovieById(@PathVariable("id") String id){
        return movies.get(Long.valueOf(id));
    }
}
