package com.joc.moviecatalogservice.resources;

import com.joc.moviecatalogservice.models.CatalogItem;
import com.joc.moviecatalogservice.models.Movie;
import com.joc.moviecatalogservice.models.Rating;
import com.joc.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){

        UserRating userRating = restTemplate.getForObject("http://localhost:8083/ratingsdata/users/"+userId, UserRating.class);

        return userRating.getUserRating().stream().map(rating -> {
            // For each movie ID, call movie info service and get details
            Movie movie = restTemplate.getForObject("http://localhost:8082/movies/"+rating.getMovieId(), Movie.class);
            // Put them all together
            return new CatalogItem(movie.getName(), "Went to see about a girl", rating.getRating());
        })
        .collect(Collectors.toList());

        /* WebClient Example:
           Movie movie = webClientBuilder.build()
                   .get()
                   .uri("http://localhost:8082/movies/"+rating.getMovieId())
                   .retrieve()
                   .bodyToMono(Movie.class)
                   .block();
         */
    }
}
