package symboltable;


import exceptions.MultipleIDException;
import exceptions.UndeclaredIDException;
import type.ClassType;
import type.FunType;
import type.IType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class SymbolTable {

    //implementazione con lista di HashTable
    private LinkedList<HashMap<String, SymbolTableEntry>> symTable;

    //offset della entry rispetto all'area di memoria in cui è definita
    private int offset;

    public SymbolTable() {
        this.symTable = new LinkedList<>();
        this.offset = 0;
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

    public void decreaseOffset() {
        offset = offset - 1;
    }

    public IType getTypeOf(String id) throws UndeclaredIDException {
        return processUse(id).getType();
    }

    //scope entry: si aggiunge un nuovo livello di scope
    public void entryNewScope() {
        HashMap<String, SymbolTableEntry> hm = new HashMap<>();
        symTable.add(hm);
    }

    //on scope exit: si rimuove il livello di scope più esterno, ovvero l'ultima HashMap aggiunta
    public void exitLastScope() {
        symTable.remove(getNestingLevel());
    }

    //aggiunge nell'hashmap piú esterna la coppia (ID, STentry) dove la STentry è composta da
    //un tipo e un offset. Se l'ID è già presente, significa che l'identificativo è già
    // definito e viene lanciata l'eccezione MultipleIDException
    public SymbolTable processDeclaration(String id, IType type, int offset) throws MultipleIDException {
        SymbolTableEntry nuovaEntry = new SymbolTableEntry(getNestingLevel(), type, offset);
        //System.out.print("\t\t\033[31;1mprocessDeclaration: " + id + " -> \033[0m" + nuovaEntry.toString() + "\n");
        checkProcessDeclaration(nuovaEntry, id, type);
        return this;
    }

    public SymbolTable processDeclarationClass(String id, IType type, int offset, boolean initialized, boolean insideClass) throws MultipleIDException {
        SymbolTableEntry nuovaEntry = new SymbolTableEntry(getNestingLevel(), type, offset, initialized, insideClass);
        //System.out.print("\t\t\033[31;1mprocessDeclarationClass: " + id + " -> \033[0m" + nuovaEntry.toString() + "\n");
        checkProcessDeclaration(nuovaEntry, id, type);
        return this;
    }

    //aggiorna l'attributo type della SymbolTableEntry con chiave IDType
    //è utilizzato per aggiornare il supertipo delle classi (dopo tutte le classdec)
    public SymbolTable setDeclarationType(String id, IType newtype, int offset) throws UndeclaredIDException {
        SymbolTableEntry nuovaEntry = new SymbolTableEntry(getNestingLevel(), newtype, offset);
        SymbolTableEntry vecchiaEntry = symTable.get(getNestingLevel()).replace(id, nuovaEntry);
        if (vecchiaEntry == null) {
            throw new UndeclaredIDException(id);
        }
        return this;
    }

    //scorre la lista di hashtable e cerca la SymbolTableEntry con chiave ID
    //se non presente, l'ID non è definito e viene lanciata l'eccezione
    public SymbolTableEntry processUse(String id) throws UndeclaredIDException {
        ListIterator<HashMap<String, SymbolTableEntry>> li = symTable.listIterator(symTable.size());

        while (li.hasPrevious()) {
            HashMap<String, SymbolTableEntry> current = li.previous();
            if (current.containsKey(id)) {
                return current.get(id);
            }
        }
        throw new UndeclaredIDException(id);
    }

    // verifico se id (identificatore) utilizzato nell in è stato precedentemente dichiarato
    // uguale a processUse ma ignora le entry di tipo funzione
    public SymbolTableEntry processUseIgnoreFun(String id) throws UndeclaredIDException {
        ListIterator<HashMap<String, SymbolTableEntry>> li = symTable.listIterator(symTable.size());
        while (li.hasPrevious()) {
            HashMap<String, SymbolTableEntry> current = li.previous();
            if (current.containsKey(id) && !(current.get(id).getType() instanceof FunType)) {
                return current.get(id);
            }
        }
        throw new UndeclaredIDException(id);
    }

    // controllo se l'identificativo del campo è duplicato, restituisco true se è presente e false altrimenti
    public Boolean checkFieldDeclaration(ClassType superClass, String id) {
        if (superClass != null) {
            ListIterator<Field> litr = superClass.getFields().listIterator();
            while (litr.hasNext()) {
                Field field = litr.next();
                if (field.getFieldID().equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    //funzione ausiliaria per processDeclaration
    private void checkProcessDeclaration(SymbolTableEntry nuovaEntry, String id, IType type) throws MultipleIDException {
        SymbolTableEntry vecchiaEntry = symTable
                .get(this.symTable.size() - 1)
                .put(id, nuovaEntry);
        if (vecchiaEntry != null) {
            // entro solo se voglio ridefinire un identificativo nello stesso scope
            throw new MultipleIDException(id, vecchiaEntry.getType().toPrint());
        }
    }

    @Override
    public String toString() {
        return "\033[32;1;2mSymbolTable\033[0m{ " +
                "symTable= " + symTable +
                ", offset= " + offset +
                " }";
    }
}
