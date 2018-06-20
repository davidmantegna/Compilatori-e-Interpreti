package util.Semantic;


import type.IType;
import exceptions.MultipleIDException;
import exceptions.UndeclaredIDException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class SymbolTable {

    //implementazione con lista di HashTable
    private LinkedList<HashMap<String, SymbolTableEntry>> symTable;

    //offset della entry rispetto all'area di memoria in cui è definita
    private int offset;

    //variabile che memorizza la entry dell'ultima classe, in modo tale da potervi accedere con this
    private SymbolTableEntry classEntryforThis;

    public SymbolTable() {
        this.symTable = new LinkedList<>();
        this.offset = 0;
        this.classEntryforThis = null;
    }

    //Il Nesting Level è ottenibile semplicemente così
    public int getNestingLevel() {
        return symTable.size() - 1;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int n) {
        offset = n;
    }

    public void increaseOffset() {
        offset = offset + 1;
    }

    public void decreaseOffset() {
        offset = offset - 1;
    }

    public LinkedList<HashMap<String, SymbolTableEntry>> getSymtable() {
        return symTable;
    }

    public SymbolTableEntry getClassEntryforThis() {
        return classEntryforThis;
    }

    public IType getTypeOf(String id) throws UndeclaredIDException {
        return processUse(id).getType();
    }

    //scope entry: si aggiunge un nuovo livello di scope
    public void pushHashMap(HashMap hm) {
        symTable.add(hm);
    }

    //on scope exit: si rimuove il livello di scope più esterno, ovvero l'ultima HashMap aggiunta
    public void popHashMap() {
        symTable.remove(getNestingLevel());
    }

    //aggiunge nell'hashmap piú esterna la coppia (ID, STentry) dove la STentry è composta da
    //un tipo e un offset. Se l'ID è già presente, significa che l'identificativo è già
    // definito e viene lanciata l'eccezione MultipleIDException
    public SymbolTable processDeclaration(String id, IType type, int offset) throws MultipleIDException {
        SymbolTableEntry nuovaEntry = new SymbolTableEntry(getNestingLevel(), type, offset);
        System.out.print("\t\t\033[31;1mprocessDeclaration: \033[0m" + nuovaEntry.toString() + "\n");
        checkProcessDeclaration(nuovaEntry, id, type);
        return this;
    }

    //come processDeclaration ma utilizzata specificatamente per le classi

    public SymbolTable processDeclarationforClass(String id, IType type, int offset, boolean inside) throws MultipleIDException {
        SymbolTableEntry nuovaEntry = new SymbolTableEntry(getNestingLevel(), type, offset, inside);
        System.out.print("\t\t\033[31;1mprocessDeclarationforClass: \033[0m" + nuovaEntry.toString() + "\n");
        checkProcessDeclaration(nuovaEntry, id, type);
        return this;
    }


    //aggiorna l'attributo type della SymbolTableEntry con chiave IDType
    //è utilizzato per aggiornare il supertipo delle classi (dopo tutte le classdec)
    public SymbolTable setDeclarationType(String id, IType newtype, int offset) throws UndeclaredIDException {
        SymbolTableEntry nuovaEntry = new SymbolTableEntry(getNestingLevel(), newtype, offset);
        SymbolTableEntry vecchiaEntry = symTable.get(getNestingLevel()).replace(id, nuovaEntry);
/*        if (newtype instanceof ClassType) {
            classEntryforThis = nuovaEntry;
        }*/
        if (vecchiaEntry == null) {
            throw new UndeclaredIDException(id);
        }
        return this;
    }

    //scorre la lista di hashtable e cerca la SymbolTableEntry con chiave ID
    //se non presente, l'ID non è definito e viene lanciata l'eccezione
    public SymbolTableEntry processUse(String id) throws UndeclaredIDException {
        ListIterator<HashMap<String, SymbolTableEntry>> li = symTable.listIterator(symTable.size());
        System.out.println("Size symbol table: " + symTable.size());
        while (li.hasPrevious()) {
            HashMap<String, SymbolTableEntry> current = li.previous();
            if (current.containsKey(id)) {
                return current.get(id);
            }
        }
        throw new UndeclaredIDException(id);
    }
 // verifico se id (identificatore) utilizzato nell in è stato precedentemente dichiarato
    //uguale a processUse ma ignora le entry di tipo funzione
    public SymbolTableEntry processUseIgnoreArrow(String id) throws UndeclaredIDException {
        ListIterator<HashMap<String, SymbolTableEntry>> li = symTable.listIterator(symTable.size());
        while (li.hasPrevious()) {
            HashMap<String, SymbolTableEntry> current = li.previous();
            // TODO ArrowType
            if (current.containsKey(id) /*&& !(current.get(id).getType() instanceof ArrowType)*/) {
                return current.get(id);
            }
        }
        throw new UndeclaredIDException(id);
    }

    //funzione ausiliaria per processDeclaration
    private void checkProcessDeclaration(SymbolTableEntry nuovaEntry, String id, IType type) throws MultipleIDException {
        SymbolTableEntry vecchiaEntry = symTable
                .get(this.symTable.size() - 1)
                .put(id, nuovaEntry);
        /*if (type instanceof ClassType) {
            classEntryforThis = nuovaEntry;
        }*/
        if (vecchiaEntry != null) {
            throw new MultipleIDException(id);
        }
    }


    public void printSymbolTable(String call) {
        System.out.println("Linked list content: " + call + " : " + symTable);
    }

    @Override
    public String toString() {
        return "\033[32;1;2mSymbolTable\033[0m{ " +
                "symTable= " + symTable +
                ", offset= " + offset +
                ", classEntryforThis= " + classEntryforThis +
                " }";
    }
}
