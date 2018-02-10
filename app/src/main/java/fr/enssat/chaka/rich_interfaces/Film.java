package fr.enssat.chaka.rich_interfaces;

/**
 * Created by chaka on 18/01/18.
 */

public class Film {
    private String film_url;
    private String title;
    private String synopsis_url;

    public Film(String film, String title, String synopsis){
        this.film_url = film;
        this.title = title;
        this.synopsis_url = synopsis;
    }

    public Film(){}

    public String getFilm_url() {
        return film_url;
    }

    public void setFilm_url(String film_url) {
        this.film_url = film_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis_url() {
        return synopsis_url;
    }

    public void setSynopsis_url(String synopsis_url) {
        this.synopsis_url = synopsis_url;
    }

}
