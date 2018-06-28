package nodes;

import exceptions.TypeException;
import exceptions.UndeclaredIDException;
import parser.FOOLParser;
import type.ClassType;
import type.FunType;
import type.IType;
import type.ObjectType;
import util.Semantic.SymbolTable;
import util.Semantic.SymbolTableEntry;

import java.util.ArrayList;

public class MethodCallNode extends FunCallNode {

    private String classID;
    private String methodID;
    private IType methodType;

    private int objectOffset;
    private int objectNestingLevel;
    private int methodOffset;
    private int nestinglevel;

    public MethodCallNode(String objID, String methodID, ArrayList<INode> argumentsArrayList, FOOLParser.MethodExpContext methodContext) {
        super(methodID, argumentsArrayList, methodContext.funcall());
        this.classID = objID;
        this.methodID = methodID;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        ArrayList<String> res = new ArrayList<>();

        nestinglevel = env.getNestingLevel();

        //prendo l'offset e il tipo di oggetto e metodo e poi effettuo vari controlli

        try {
            ClassType classType = null;
            // vengono calcolati gli offset dalla dispatch table per avere
            // accesso all'oggetto del metodo

            //prendo la STentry dell'oggetto dalla Symbol Table

            SymbolTableEntry objectSTentry = env.processUse(classID);
            IType objectType = objectSTentry.getType();
            objectOffset = objectSTentry.getOffset();
            objectNestingLevel = objectSTentry.getNestinglevel();

            // check che il metodo sia stato invocato da un oggetto
            if (objectType instanceof ObjectType) {
                classType = ((ObjectType) objectType).getClassType();
            } else {
                res.add("Il metodo " + methodID + " è invocato da un tipo che non è un oggetto");
                return res;
            }


            // se il metodo è invocato con this allora significa che la classe a cui il metodo
            // appartiene è nell'ultima entry inserita nella SymbolTable

            SymbolTableEntry classEntry;
            classEntry = env.processUse(classType.getClassID());


            ClassType objectClass = (ClassType) classEntry.getType();
            methodOffset = objectClass.getOffsetOfMethod(methodID);
            methodType = objectClass.getTypeOfMethod(methodID);

            // controllo che il metodo sia dichiarato all'interno della classe
            if (methodType == null) {
                res.add("L'oggetto " + classID + " non ha il metodo " + methodID);
            }

            FunType funType = (FunType) methodType;
            ArrayList<IType> funTypeArrayList = funType.getParametersTypeArrayList();
            //controllo che il metodo abbia il numero di parametri uguale a quello degli argomenti
            if (!(funTypeArrayList.size() == getArgumentsArrayList().size())) {
                res.add("Numero errato di parametri nell'invocazione di " + getId());
            }

            //CheckSemantic per ogni argomento
            for (INode node : getArgumentsArrayList())
                res.addAll(node.checkSemantics(env));

        } catch (UndeclaredIDException e) {
            res.add(e.getMessage());
        }

        return res;
    }


    @Override
    public IType typeCheck() throws TypeException {
        FunType funType = (FunType) methodType;
        ArrayList<IType> funTypeArrayList = funType.getParametersTypeArrayList();

        for (int i = 0; i < getArgumentsArrayList().size(); i++)
            if (!getArgumentsArrayList().get(i).typeCheck().isSubType(funTypeArrayList.get(i)))
                throw new TypeException("Tipo errato per il parametro " + (i + 1) + " nell'invocazione di " + getId(), getFuncallContext());
        return funType.getReturnType();
    }
}
