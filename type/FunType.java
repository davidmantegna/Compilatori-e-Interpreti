package type;

import java.util.ArrayList;

public class FunType implements IType {

    //TODO implement arrowType

    private ArrayList<IType> parametersTypeArrayList;
    private IType returnType;

    public FunType(ArrayList<IType> parametersTypeArrayList, IType returnType) {
        this.parametersTypeArrayList = parametersTypeArrayList;
        this.returnType = returnType;
    }

    public ArrayList<IType> getParametersTypeArrayList() {
        return parametersTypeArrayList;
    }

    public IType getReturnType() {
        return returnType;
    }

    @Override
    public IDType getID() {
        return IDType.FUN;
    }

    @Override
    public boolean isSubType(IType t) {
        return false;
    }

    @Override
    public String toPrint() {
        return null;
    }
}
