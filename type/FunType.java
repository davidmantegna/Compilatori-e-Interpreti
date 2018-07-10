package type;

import java.util.ArrayList;

public class FunType implements IType {

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

/*Il tipo di una funzione f1 e` sottotipo del tipo di una funzione f2 se il tipo ritornato da f1
    e` sottotipo del tipo ritornato da f2, se hanno il medesimo numero di parametri,
    e se ogni tipo di paramentro di f1 e` sopratipo del corrisponde tipo di parametro di f2. */

    @Override
    public boolean isSubType(IType t) {
        if (t instanceof FunType) {
            FunType funType = (FunType) t;
            boolean controllo = true;
            if (parametersTypeArrayList.size() == funType.getParametersTypeArrayList().size()) {
                //Controllo che tutti i parametri abbiano lo stesso tipo(supertype, come da cosegna)
                for (int i = 0; i < parametersTypeArrayList.size(); i++) {
                    controllo = controllo & (funType.getParametersTypeArrayList().get(i).isSubType(parametersTypeArrayList.get(i)));
                    printParameter(funType.getParametersTypeArrayList().get(i), parametersTypeArrayList.get(i), controllo);
                }
                //Controllo che anche il valore di ritorno della funzione
                controllo = controllo & returnType.isSubType(funType.getReturnType());
                printReturn(returnType, funType.getReturnType(), controllo);
            } else {
                controllo = false;
            }
            return controllo;
        } else {
            return false;
        }
    }

    @Override
    public String toPrint() {
        return "fun";
    }

    private void printParameter(IType paramA, IType paramB, boolean controllo) {
        String parA = controlloTipo(paramA);
        String parB = controlloTipo(paramB);
        System.err.println("\n\n\n" + "CONTROVARIANZA: Parametri: " + parB + " è sovratipo di " + parA + " -> " + controllo);
    }

    private void printReturn(IType paramA, IType paramB, boolean controllo) {
        String retA = controlloTipo(paramA);
        String retB = controlloTipo(paramB);
        System.err.println("COVARIANZA: \tRitorno: " + retB + " è sottotipo di " + retA + " -> " + controllo + "\n\n\n");
    }

    private String controlloTipo(IType param) {
        if (param instanceof ObjectType) {
            return ((ObjectType) param).getClassType().getClassID();
        } else {
            return param.getID().toString();
        }
    }
}
