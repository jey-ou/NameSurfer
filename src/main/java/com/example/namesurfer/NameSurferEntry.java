package com.example.namesurfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NameSurferEntry {
    private final List<String> entry = new ArrayList<>();

    public NameSurferEntry() {}
    public NameSurferEntry(String[]strArr) {
        addStringArray(strArr);
    }

    public String get(int index) {
        if(entry.size()< index)return "";
        return entry.get(index);
    }

    private boolean addElement(String str) {
        if(str.isEmpty()) return false;
        return entry.add(str);
    }

    private boolean addStringArray(String[] arrStr) {
        if(arrStr == null || arrStr.length == 0) return false;
        entry.clear();
        return Collections.addAll(entry, arrStr);
    }

    public boolean contains(String str) {
        if(str == null || str.equals("")) return false;
        return entry.contains(str);
    }

    public int size() {
        return entry.size();
    }

    public String getName() {
        if(entry.size() ==0)return "";
        return entry.get(0);
    }

    private void clear() {
        if(entry.size()==0)return;
        entry.clear();
    }

    public ArrayList<Integer> getIntegerArray(){
        ArrayList<Integer> intArr = new ArrayList<>();
        String str = "";
        int z = 0;

        for(int i = 1; i< entry.size();i++) {
            str = entry.get(i);
            z = Integer.parseInt(str);
            intArr.add(z);
        }

        return intArr;
    }

    private String toStringOriginal() {
        String str="";
        if(entry.isEmpty())return str;

        for(String data:entry) {
            str += data;
            str +=" ";
        }
        str = str.substring(0, str.length()-1);
        return str;
    }

    /**
     * Geeft de inhoud weer van de entry zoals voorgeschreven
     * door de opgave
     * @return String met naam, [waarde1, ... waarde11]
     */
    public String toString() {
        String str = "";
        if(entry.isEmpty())return str;

        str += getName() + " [";
        for(int i = 1; i < entry.size(); i++) {
            str += entry.get(i ) + ", ";
        }
        str = str.substring(0, str.length()-2);
        str += "]";
        return str;
    }
}
