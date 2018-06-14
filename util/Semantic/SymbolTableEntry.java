package util.Semantic;

import Type.IType;

public class SymbolTableEntry {

    private int nestingLevel; //nestinglevel
    private IType type;
    private int offset;
    private boolean insideClass;

    public SymbolTableEntry(int n, int os) {
        nestingLevel = n;
        offset = os;
    }

    public SymbolTableEntry(int n, IType t, int os) {
        nestingLevel = n;
        type = t;
        offset = os;
    }

    public SymbolTableEntry(int n, IType t, int os, boolean b) {
        nestingLevel = n;
        type = t;
        offset = os;
        insideClass = b;
    }

    public IType getType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }

    public boolean getInsideClass() {return insideClass;}

    public int getNestinglevel() {
        return nestingLevel;
    }

}
