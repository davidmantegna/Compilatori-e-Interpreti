package Type;


public class IntType implements IType {
    @Override
    public IDType getID() {
        return IDType.INT;
    }

    @Override
    public boolean isSubType(IType t) {
        return getID() == t.getID();
    }

    @Override
    public String toPrint() {
        return "int";
    }
}
