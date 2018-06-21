package type;

import util.Semantic.Field;
import util.Semantic.Method;

import java.util.ArrayList;

public class ClassType implements IType {

    private String classID = "";
    private ClassType superClassType = null;

    private ArrayList<Field> fields = new ArrayList<>();
    private ArrayList<Method> methods = new ArrayList<>();

    public ClassType(String classID, ClassType superClass, ArrayList<Field> fields, ArrayList<Method> methods) {
        this.classID = classID;
        this.superClassType = superClass;
        this.fields = fields;
        this.methods = methods;
    }

    public ClassType(String classID) {
        this.classID = classID;
    }

    public String getClassID() {
        return classID;
    }

    public ClassType getSuperClassType() {
        return superClassType;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }


    @Override
    public IDType getID() {
        return IDType.CLASS;
    }

    @Override
    public boolean isSubType(IType t) {

        //TODO isSubType
        return false;
    }

    @Override
    public String toPrint() {
        return null;
    }

    //TODO getOffsetOfMethod, methodsHashMapFromSuperClass, getTypeOfMethod
}


