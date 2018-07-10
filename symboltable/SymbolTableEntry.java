package symboltable;

import type.IType;

public class SymbolTableEntry {

    private int nestingLevel; //nestinglevel
    private IType type;
    private int offset;
    private boolean initialaized;
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

    public SymbolTableEntry(int nestingLevel, IType type, int offset, boolean initialaized, boolean insideClass) {
        this.nestingLevel = nestingLevel;
        this.type = type;
        this.offset = offset;
        this.initialaized = initialaized;
        this.insideClass = insideClass;
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

    public boolean isInsideClass() {
        return insideClass;
    }

    public boolean isInitialaized() {
        return initialaized;
    }

    public void setInitialaized(boolean initialaized) {
        this.initialaized = initialaized;
    }

    @Override
    public String toString() {
        return "SymbolTableEntry {" +
                "nestingLevel= " + nestingLevel +
                ", type= " + type.toPrint() +
                ", offset= " + offset +
                ", istanziato= " + initialaized +
                ", insideClass= " + insideClass +
                "}";
    }
}
