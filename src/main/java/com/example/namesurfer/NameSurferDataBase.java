package com.example.namesurfer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class NameSurferDataBase implements NameSurferConstants{
    private final Map<String, NameSurferEntry> dataBase = new HashMap<>();

    public NameSurferDataBase(){
        loadDatabase();

    }

    // read database
    private void loadDatabase() { //chapter 15 page477
        int teller = 0;
        String keyname="";

        //Path productsPath = Paths.get("C:\\Users\\JosJa\\IdeaProjects\\NameSurfer\\src\\main\\java\\com\\example\\namesurfer\\names-data.txt");
        Path productsPath = Paths.get(NAMES_DATA_FILE);
        if(Files.exists(productsPath)) { // prevent FileNotFoundException
            File productsFile = productsPath.toFile();

            try (BufferedReader in = new BufferedReader(new FileReader(productsFile))){
                String line = in.readLine();

                while(line !=null) {// prevent the EOFException
                    String[]lineArr = parseLine(line);
                    NameSurferEntry entry = new NameSurferEntry(lineArr);
                    keyname = entry.getName().toLowerCase();
                    dataBase.put(keyname, entry);
                    line = in.readLine(); // dit is de volgende lijn die pas in de volgende lus opgeslagen wordt
                    keyname ="";
                    teller++;
                }

            }catch (IOException e){ // catch the IOException
                //System.out.println(e);
                String[] lineArr = {"Null","0","0","0","0","0","0","0","0","0","0","0","0"};
                dataBase.put("null", new NameSurferEntry(lineArr));
                System.out.println("Names_data_file not found");
            }
            System.out.println("aantal records ingelezen: "+teller);
            System.out.println("aantal records in database: " + dataBase.size());
        }else {
            System.out.println(productsPath.toAbsolutePath()+ " doesn't exist");
        }

    }
    private String[] parseLine(String str) {
        if(str.isEmpty())return new String[1];
        String[] res = str.split(" ");
        for (int i =1; i<res.length; i++){
            if (res[i].equals("0")){
                res[i]= "1000";
            }
        }
        return res;

    }
    public boolean dbContains(String name){
        //System.out.println("Op zoek naar: " +name);
        AtomicBoolean gevonden = new AtomicBoolean(false);

        //System.out.println("De lijst bevat: " + dataBase.size() + " namen.");
        if(dataBase.isEmpty()){
            return gevonden.get();
        }

        dataBase.forEach((k, v) ->{
            if(k.toLowerCase().equals(name)) {
               gevonden.set(true);
            }
        });
        //System.out.println(name + " niet gevonden");
        return gevonden.get();
    }
    
    public NameSurferEntry getEntry(String name){
        AtomicReference<NameSurferEntry> entr = new AtomicReference<>(null);
        
        if(dbContains(name)){
            dataBase.forEach((k, v)->{
                if(k.toLowerCase().equals(name)) {
                    entr.set((NameSurferEntry) v);
                    System.out.println(v.getName() + " gevonden");
                }
            });
            if (entr.get() == null)
                return new NameSurferEntry();
            //
        }else
            entr.set(new NameSurferEntry());

        return entr.get();
    }
    public int size(){
        return dataBase.size();
    }
}
