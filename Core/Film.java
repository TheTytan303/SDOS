package Core;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public class Film implements Comparator<Film>, Comparable<Film>{
    private Integer id;
    private String title;
    private ConcurrentSkipListSet<Actor> actors;

    //------------------------------------------------------------- Geters
    public ConcurrentSkipListSet<Actor> getActors() {
        return actors;
    }
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    //------------------------------------------------------------- Seters
    public void setActors( ConcurrentSkipListSet<Actor> actors) {
        this.actors = actors;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    //------------------------------------------------------------- Constructors
    Film(){}
    Film(Film film){
        this.setId(film.id);
        this.setTitle(film.title);
        this.setActors(film.actors);
    }
    Film(int id, String title){
        this.setId(id);
        this.setTitle(title);
    }
    Film(int id, String title, ConcurrentSkipListSet<Actor> tab){
        this.setId(id);
        this.setTitle(title);
        this.setActors(tab);
    }

    //------------------------------------------------------------- Static
    public static Film searchForFilm(int id){
        String s = "tt"+String.format("%07d",id);
        Film returnVale;
        s = Actor.sendRequest("https://java.kisim.eu.org/movies/"+s);
        try {
            assert s != null;
            s=s.replaceFirst("tt","");
            s=s.replaceAll("nm","");
            returnVale=new ObjectMapper().readValue(s, Film.class);
        }
        catch (NullPointerException ignore){
            return null;
        }
        catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
        return returnVale;
    }
    public static List<Film> deserializeList(String s)  throws IOException {
        s=s.replace("\"id\":\"nm","\"id\":\"");
        s=s.replace("\"id\":\"tt","\"id\":\"");
        Film[] r=  new ObjectMapper().readValue(s, Film[].class);
        List<Film> returnVale = Arrays.asList(r);
        return returnVale;
    }

    //------------------------------------------------------------- private functions

    //------------------------------------------------------------- public functions

    //------------------------------------------------------------- overrides
    @Override
    public String toString(){
        String returnVale = getId()+" - "+getTitle() ;
        //for(Core.Actor a : this.actors){
        //    returnVale=returnVale+"\n"+a.toString();
        //}
        return returnVale;
    }
    @Override
    public int compare(Film o1, Film o2) {
        return Integer.compare(o1.id,o2.id);
    }
    @Override
    public int compareTo(Film o) {
        return this.id.compareTo(o.id);
    }
}
