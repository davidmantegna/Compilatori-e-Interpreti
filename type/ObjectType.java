package type;

import exceptions.UndeclaredIDException;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class ObjectType implements IType {

    private ClassType classType;

    public ObjectType(ClassType classType) {
        this.classType = classType;
    }

    public ClassType getClassType() {
        return classType;
    }

    // This is used to update the classType filling superType when needed
    public ArrayList<String> updateClassType(SymbolTable env) {
        ArrayList<String> res = new ArrayList<>();
        try {
            try {
                this.classType = (ClassType) env.getTypeOf(classType.getClassID());
            } catch (UndeclaredIDException e) {
                throw new UndeclaredIDException(classType.getClassID());
            }
        } catch (UndeclaredIDException e) {
            res.add(new String(e.getMessage()));
        }
        return res;
    }

    @Override
    public IDType getID() {
        return IDType.OBJECT;
    }

    @Override
    public boolean isSubType(IType type) {
        if (type instanceof ObjectType) {
            ObjectType it2 = (ObjectType) type;
            return classType.isSubType(it2.getClassType());
        } else {
            return false;
        }
    }

    @Override
    public String toPrint() {
        return "Object: " + classType.getClassID();
    }
}
