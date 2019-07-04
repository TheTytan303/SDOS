package Core;

import kotlin.Pair;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class Actor implements Comparator<Actor>, Comparable<Actor> {

    private Integer  id;
    private String name;
    private ConcurrentSkipListSet<Film> films;

    //------------------------------------------------------------- Getters
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public ConcurrentSkipListSet<Film> getFilms(){return this.films;}

    //------------------------------------------------------------- Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setFilms( ConcurrentSkipListSet<Film> f) {
        this.films = f;
    }

    //------------------------------------------------------------- Constructors
    public Actor(){films = new ConcurrentSkipListSet<>();}
    public Actor(int id, String name){
        this.setId(id);
        this.setName(name);
    }
    public Actor(Actor a){
        this.setId(a.getId());
        this.setName(a.getName());
    }
    public Actor(int id){
        this(Objects.requireNonNull(Actor.searchForActor(id)));
    }
    public Actor(String name) throws Exception{
        ArrayList<Actor> list = Actor.searchForActors(name);
        if(list.size() ==1){
            setName(list.get(0).getName());
            setId(list.get(0).getId());
        }
        else{
            if(list.size()!=0){
                throw new Exception("found more than one actor with that name");
            }
            throw new Exception("no actor found with that name");
        }
    }

    //------------------------------------------------------------- Static
    static public ArrayList<Actor> searchForActors(String name){
        name = name.replace(" ", "%20");
        String s;
        ArrayList<Actor> returnVale = new ArrayList<>();
        s = sendRequest("https://java.kisim.eu.org/actors/search/"+name);
        if(s==null){
            for(int i=0;i<5;i++){
                System.err.println("failed to get response, retry for " + (i+1) +" time");
                s= sendRequest("https://java.kisim.eu.org/actors/search/"+name);
            }
        }
        try {
            assert s != null;
            returnVale=new ArrayList<>(deserializeList(s));
        }
        catch (NullPointerException ignore){
        }
        catch (Exception e){
            System.err.println(e.getMessage());
            return null;
        }
        return returnVale;
    }
    public static Actor searchForActor(int id){
        String s1 = "nm"+String.format("%07d",id);
        Actor returnVale;
        String s = sendRequest("https://java.kisim.eu.org/actors/"+s1);
        if(s==null){
            for(int i=0;i<5;i++){
                System.err.println("failed to get response, retry for " + (i+1) +" time");
                s= sendRequest("https://java.kisim.eu.org/actors/"+s1);
            }
        }
        try {
            assert s != null;
            s=s.replaceFirst("nm","");
            returnVale=new ObjectMapper().readValue(s, Actor.class);
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
    public static String sendRequest (String link){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(link).build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            return response.body().string();
        }
        catch (Exception e){
            System.err.println(e.getMessage());

            return null;
        }
    }
    //ArrayList<Pair<Core.Actor, Core.Film>>
    static public String find(Actor a1, Actor a2){

        ConcurrentSkipListMap<Actor, ArrayList<Pair<Actor, Film>>> mapaL;
        ConcurrentSkipListMap<Actor, ArrayList<Pair<Actor, Film>>> mapaR;
        ConcurrentSkipListMap<Actor, ArrayList<Pair<Actor, Film>>> tmp;

        try{
            mapaL= a1.searchForFriends();
            if(mapaL.containsKey(a2)){
                return "-->["+a1+"]-||-"+mapaL.get(a2).get(0).getSecond()+" --> "+a2;
            }
            //for (Map.Entry<Core.Actor, ArrayList<Core.Film>> entry : mapa.entrySet()){
            //    System.out.println("["+entry.getKey() +"] \t\t -> ["+ entry.getValue()+"]");
            //}
            mapaR= a2.searchForFriends();
            for (Map.Entry<Actor, ArrayList<Pair<Actor, Film>>> entry : mapaR.entrySet()){
                if(mapaL.containsKey(entry.getKey())){
                    return buildUpReturnVale(mapaL, mapaR, entry);
                }
            }
            String s = growmapup(mapaL, mapaR);
            if(s != null){
                return s;
            }
            //System.out.println("mapaL size:" + mapaL.size());
            //System.out.println("mapaR size:" + mapaR.size());
            s = growmapup(mapaR, mapaL);
            if(s != null){
                return s;
            }
            //System.out.println("mapaL size:" + mapaL.size());
            //System.out.println("mapaR size:" + mapaR.size());
            s = growmapup(mapaL, mapaR);
            if(s != null){
                return s;
            }
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }


        return null;
    }
    public static List<Actor> deserializeList(String s)  throws IOException{
        s=s.replace("\"id\":\"nm","\"id\":\"");
        Actor[] r=  new ObjectMapper().readValue(s, Actor[].class);
        return new ArrayList<>(Arrays.asList(r));
    }
    //------------------------------------------------------------- private functions
    private static String growmapup(ConcurrentSkipListMap<Actor, ArrayList<Pair<Actor, Film>>> mapaL, ConcurrentSkipListMap<Actor, ArrayList<Pair<Actor, Film>>> mapaR) throws IOException{
        ConcurrentSkipListMap<Actor, ArrayList<Pair<Actor, Film>>> tmp = new ConcurrentSkipListMap<>(mapaL);
        for (Map.Entry<Actor, ArrayList<Pair<Actor, Film>>> entry : tmp.entrySet()){
            //for (Actor actor : tmp){
            ConcurrentSkipListMap<Actor, ArrayList<Pair<Actor, Film>>> mapa2 = entry.getKey().searchForFriends();
            //int i=0;
            for (Map.Entry<Actor, ArrayList<Pair<Actor, Film>>> entry2 : mapa2.entrySet()){
                ArrayList<Pair<Actor, Film>> val = new ArrayList<>(entry.getValue());
                val.addAll(entry2.getValue());
                if(mapaL.putIfAbsent(entry2.getKey(), val)==null){
                    if(mapaR.containsKey(entry2.getKey())){
                        return buildUpReturnVale(mapaL, mapaR, entry2);
                    }
                    //i++;
                }
            }
        }
        return null;
    }
    private static String buildUpReturnVale(ConcurrentSkipListMap<Actor, ArrayList<Pair<Actor, Film>>> mapaL, ConcurrentSkipListMap<Actor, ArrayList<Pair<Actor, Film>>> mapaR, Map.Entry<Actor, ArrayList<Pair<Actor, Film>>> entry){
        //mapaL.get(entry.getKey()).addAll(entry.getValue());
        String s = "";
        //s = a1.toString();
        for(Pair<Actor, Film> f : mapaL.get(entry.getKey())){
            s=s .concat("-->> ["+f.getFirst()+"] -->> ["+f.getSecond()+"]");
        }
        s=s.concat(" --> ["+entry.getKey());
        String d="";
        for(Pair<Actor, Film> f : mapaR.get(entry.getKey())){
            d="] -->> ["+f.getSecond()+"] -->> ["+f.getFirst()+"".concat(d);
        }
        d=d.concat("]");
        return (s.concat(d)).substring(4);
    }
    //------------------------------------------------------------- public functions
    public ConcurrentSkipListMap<Actor, ArrayList<Pair<Actor, Film>>> searchForFriends() throws IOException{
        if(this.films==null || this.films.size()==0){
            this.searchForFilms();
        }
        ConcurrentSkipListMap<Actor, ArrayList<Pair<Actor, Film>>> returnVale = new ConcurrentSkipListMap<>();
        System.out.print("[");
        for(int i=0; i<this.films.size();i++){
            System.out.print("_");
        }
        System.out.println("]");
        System.out.print("[");
        ExecutorService executor = Executors.newFixedThreadPool(16);
        for(Film f : this.films){
               executor.submit(new searchProcess(returnVale, f, this));
        }
        executor.shutdown();
        try{
            executor.awaitTermination(30, TimeUnit.DAYS);
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }
        System.out.println("]");
        System.out.println("Found: " + returnVale.size() + " unique actors");
        return returnVale;
    };
    public ConcurrentSkipListSet<Film> searchForFilms() throws IOException{
        if(this.films == null){
            this.films = new ConcurrentSkipListSet();
        }
        String s =  "nm"+String.format("%07d",id);
        s = sendRequest("https://java.kisim.eu.org/actors/"+ s +"/movies");
        //Core.Film[] films = new ObjectMapper().readValue(s, Core.Film[].class);
        List<Film> films= Film.deserializeList(s);
        //ArrayList<Core.Film> listOfFilms= new ArrayList<>(Arrays.asList(films));
        for(Film e: films){
            if(!e.getTitle().contains("Episode")){
                this.films.add(e);
            }
        }
        this.films.addAll(films);
        return this.films;
    }
    //------------------------------------------------------------- overrides
    @Override
    public String toString(){
        String returnVale = getId()+" - "+getName();
        //if(this.films!=null){
        //    for(Core.Film f : this.films){
        //        returnVale+="\n "+f;
        //    }
        //}
        return returnVale;
    }
    @Override
    public boolean equals(Object o){
        if(o==null){
            return false;
        }
        Actor a = (Actor) o;

        if(this.id == a.id){
            return true;
        }
        return false;
    }
    @Override
    public int compare(Actor o1, Actor o2) {
        return Integer.compare(o1.id,o2.id);
    }

    @Override
    public int compareTo(Actor o) {
        return this.id.compareTo(o.id);
    }
    //------------------------------------------------------------- Sub-Classes
    class searchProcess implements Runnable{
        ConcurrentSkipListMap<Actor, ArrayList<Pair<Actor, Film>>> returnVale;
        Film f;
        Actor root;
        public searchProcess(ConcurrentSkipListMap<Actor, ArrayList<Pair<Actor, Film>>> inputList, Film inputFilm, Actor rootinput){
            this.returnVale = inputList;
            f=inputFilm;
            root=rootinput;
        };
        public void run(){
            f=Film.searchForFilm(f.getId());
            for(Actor a: f.getActors()){
                ArrayList<Pair<Actor, Film>> films = new ArrayList<>();
                films.add(new Pair<>(root, f));
                returnVale.put(a,films);
            }
            System.out.print("-");
        }
    }
}
//System.out.println("mapa size:" + mapa.size());
        /*
        *  for (Map.Entry<Core.Actor, ArrayList<Core.Film>> entry : mapa.entrySet()){
                if(entry.getValue().size()==1){
                    ConcurrentSkipListMap<Core.Actor, ArrayList<Core.Film>> mapa2 = entry.getKey().searchForFriends();
                    for (Map.Entry<Core.Actor, ArrayList<Core.Film>> entry2 : mapa2.entrySet()){
                        ArrayList<Core.Film> val = new ArrayList<>(entry.getValue());
                        val.addAll(entry2.getValue());
                        if(entry2.getKey().getId() == a2.getId()){
                            return val;
                        }
                        mapa.put(entry2.getKey(), val);
                    }
                }
                System.out.println("mapa size:" + mapa.size());
            }
        *
        * */
/*
for (Map.Entry<Actor, ArrayList<Pair<Actor, Film>>> entry : mapaR.entrySet()){
                if(entry.getValue().size()==1){
                    ConcurrentSkipListMap<Actor, ArrayList<Pair<Actor, Film>>> mapa2 = entry.getKey().searchForFriends();
                    int i=0;
                    for (Map.Entry<Actor, ArrayList<Pair<Actor, Film>>> entry2 : mapa2.entrySet()){
                        ArrayList<Pair<Actor, Film>> val = new ArrayList<>(entry.getValue());
                        val.addAll(entry2.getValue());
                        if(mapaR.putIfAbsent(entry2.getKey(), val)==null){
                            i++;
                            if(mapaL.containsKey(entry2.getKey())){
                                return buildUpReturnVale(mapaL, mapaR, entry2);
                            }
                        }
                    }
                    System.out.println("do mapyR dodano " +i+ " unikalnych wpisów");
                    //System.out.println("do mapyR dodano " +i+ " unikalnych wpisów");
                }
                else {System.err.println("123");}
                //System.out.println("mapa size:" + mapa.size());
            }
* */