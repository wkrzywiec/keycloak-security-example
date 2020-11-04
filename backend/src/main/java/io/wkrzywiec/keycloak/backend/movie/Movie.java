package io.wkrzywiec.keycloak.backend.movie;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Movie {

    private String title;
    private String director;
    private Integer year;
}
