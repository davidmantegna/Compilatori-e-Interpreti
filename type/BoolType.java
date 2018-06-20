package type;

public class BoolType implements IType {


    @Override
    public IDType getID() {
        return IDType.BOOL;
    }

    @Override
    public boolean isSubType(IType t) {
        return getID() == t.getID();
    }

    @Override
    public String toPrint() {
        return "bool";
    }
}
