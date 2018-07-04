package util.VM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DispatchTable {

    //Hashmap di tutte le dispatch table
    private static HashMap<String, ArrayList<DispatchTableEntry>> dispatchTables = new HashMap<>();

    public static void addDispatchTable(String classID, ArrayList<DispatchTableEntry> dt) {
        dispatchTables.put(classID, dt);
    }

    // Viene ritornata una copia della dispatch table (così non si modifica, per riferimento, quella del padre)
    public static ArrayList<DispatchTableEntry> getDispatchTable(String classID) {
        ArrayList<DispatchTableEntry> tmp = new ArrayList<>();
        ArrayList<DispatchTableEntry> dt = dispatchTables.get(classID);
        for (DispatchTableEntry dte : dt) {
            DispatchTableEntry tmp2 = new DispatchTableEntry(dte.getMethodID(), dte.getMethodLabel());
            tmp.add(tmp2);
        }
        return tmp;
    }

    public static String generaCodiceDispatchTable() {
        StringBuilder stringBuilder = new StringBuilder();
        // String è l'ID della classe, con un'arraylist di tutti i suoi metodi
        // Per ogni classe
        for (Map.Entry<String, ArrayList<DispatchTableEntry>> dt : dispatchTables.entrySet()) {
            //Genera le label delle classi
            stringBuilder.append("class" + dt.getKey() + ":\n"); // nuovaLabelPerDispatchTable
            // Per ogni elemento della dispatch table
            for (DispatchTableEntry entry : dispatchTables.get(dt.getKey())) {
                //Aggiunge sotto alla label della classe le label delle funzioni a cui si riferisce
                stringBuilder.append(entry.getMethodLabel());
            }
        }
        return stringBuilder.toString();
    }
    public static void reset() {

        dispatchTables = new HashMap<>();
    }
}
