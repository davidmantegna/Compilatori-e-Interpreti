package type;

public class VoidType implements IType {
    @Override
    public IDType getID() {
        return IDType.VOID;
    }

    @Override
    public boolean isSubType(IType t) {
        return getID() == t.getID();
    }

    @Override
    public String toPrint() {
        return "void";
    }
}
