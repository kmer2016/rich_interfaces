package fr.enssat.chaka.rich_interfaces;

/**
 * Created by chaka on 18/01/18.
 */

public class DatumKeyword {
    private String title;
    private String url;

    public DatumKeyword(String title, String url){
        this.title = title;
        this.url = url;
    }

    public DatumKeyword(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
