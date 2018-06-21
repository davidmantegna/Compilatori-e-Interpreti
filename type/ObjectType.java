package type;

public class ObjectType implements IType {

    private ClassType classType;

    public ObjectType(ClassType classType) {
        this.classType = classType;
    }

    public ClassType getClassType() {
        return classType;
    }

    @Override
    public IDType getID() {
        return IDType.OBJECT;
    }

    @Override
    public boolean isSubType(IType t) {

        //TODO isSubType
        return false;
    }

    //TODO update ClassType


    @Override
    public String toPrint() {
        return null;
    }
}
