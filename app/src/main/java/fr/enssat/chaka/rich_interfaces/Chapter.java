package fr.enssat.chaka.rich_interfaces;

/**
 * Created by chaka on 11/01/18.
 */

public class Chapter {
    private String title;
    private int position;

    public Chapter(String title, int position){
        this.title = title;
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
