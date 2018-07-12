package symboltable;

import type.IType;

public class SymbolTableEntry {

    private int nestingLevel;
    private IType type;
    private int offset;
    private boolean initialized;
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

    public SymbolTableEntry(int nestingLevel, IType type, int offset, boolean initialized, boolean insideClass) {
        this.nestingLevel = nestingLevel;
        this.type = type;
        this.offset = offset;
        this.initialized = initialized;
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

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public String toString() {
        return "SymbolTableEntry {" +
                "nestingLevel= " + nestingLevel +
                ", type= " + type.toPrint() +
                ", offset= " + offset +
                ", istanziato= " + initialized +
                ", insideClass= " + insideClass +
                "}";
    }
}
