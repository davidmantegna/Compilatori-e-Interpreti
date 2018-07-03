package util.Semantic;

import type.IType;

public class SymbolTableEntry {

    private int nestingLevel; //nestinglevel
    private IType type;
    private int offset;
    private boolean instanziato;

    public SymbolTableEntry(int n, int os) {
        nestingLevel = n;
        offset = os;
    }

    public SymbolTableEntry(int n, IType t, int os) {
        nestingLevel = n;
        type = t;
        offset = os;
    }

    public SymbolTableEntry(int nestingLevel, IType type, int offset, boolean instanziato) {
        this.nestingLevel = nestingLevel;
        this.type = type;
        this.offset = offset;
        this.instanziato = instanziato;
    }

    public IType getType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }

    public int getNestinglevel() {
        return nestingLevel;
    }

    public boolean isInstanziato() {
        return instanziato;
    }

    public void setInstanziato(boolean instanziato) {
        this.instanziato = instanziato;
    }

    @Override
    public String toString() {
        return "SymbolTableEntry {" +
                "nestingLevel= " + nestingLevel +
                ", type= " + type.toPrint() +
                ", offset= " + offset +
                ", istanziato= " + instanziato +
                "}";
    }
}
