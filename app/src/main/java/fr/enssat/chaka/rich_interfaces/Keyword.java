package fr.enssat.chaka.rich_interfaces;

import java.util.ArrayList;

/**
 * Created by chaka on 18/01/18.
 */

public class Keyword {
    private int position;
    private ArrayList<DatumKeyword> data;

    public Keyword(int position, ArrayList<DatumKeyword> data){
        this.position = position;
        this.data = data;
    }

    public Keyword(){
        data = new ArrayList<DatumKeyword>();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArrayList<DatumKeyword> getData() {
        return data;
    }

    public void setData(ArrayList<DatumKeyword> data) {
        this.data = data;
    }
}
