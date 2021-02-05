package io.wkrzywiec.keycloak.backend.movie;

import io.wkrzywiec.keycloak.backend.infra.security.annotation.AllowedRoles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class MovieController {

    Map<Long, Movie> movies;

    public MovieController() {
        movies = Map.of(
                1L, new Movie("Star Wars: A New Hope", "George Lucas", 1977),
                2L, new Movie("Star Wars: The Empire Strikes Back", "George Lucas", 1980),
                3L, new Movie("Star Wars: Return of the Jedi", "George Lucas", 1983));
    }

    @GetMapping("/movies")
    @AllowedRoles("ADMIN")
    public List<Movie> getAllMovies(){
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.getAuthentication().getAuthorities().forEach(b -> log.info(b.toString()));
        return new ArrayList<>(movies.values());
    }

    @GetMapping("/movies/{id}")
    @AllowedRoles("VISITOR")
    public Movie getMovieById(@PathVariable("id") String id){
        return movies.get(Long.valueOf(id));
    }
}
