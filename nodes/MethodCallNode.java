package nodes;

import exceptions.TypeException;
import exceptions.UndeclaredIDException;
import exceptions.UndeclaredMethodIDException;
import parser.FOOLParser;
import symboltable.SymbolTable;
import symboltable.SymbolTableEntry;
import type.ClassType;
import type.FunType;
import type.IType;
import type.ObjectType;

import java.util.ArrayList;

public class MethodCallNode extends FunCallNode {

    private String classID;
    private String methodID;
    private IType methodType;

    private int objectOffset;
    private int objectNestingLevel;
    private int methodOffset;
    private int nestinglevel;

    public MethodCallNode(String objID, String methodID, ArrayList<INode> argumentsArrayList, FOOLParser.FuncallContext funcallContext) {
        super(methodID, argumentsArrayList, funcallContext);
        this.classID = objID;
        this.methodID = methodID;
    }

    public MethodCallNode(String objID, String methodID, ArrayList<INode> argumentsArrayList, FOOLParser.MethodExpContext methodContext) {
        super(methodID, argumentsArrayList, methodContext.funcall());
        this.classID = objID;
        this.methodID = methodID;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        ArrayList<String> res = new ArrayList<>();
        nestinglevel = env.getNestingLevel();   //  level della chiamata del metodo

        // prendo l'offset e il tipo di oggetto e metodo e poi effettuo vari controlli
        try {
            ClassType classType = null;
            // ricavo informazioni del metodo per accedere alla DT
            if (classID.equals("this")) {
                objectOffset = 0;       // se si sta utilizzando this ci si riferisce per forza alla classe corrente	 perciò il valore di $fp è sempre 0
                objectNestingLevel = 3; // il livello 3 della entry corrisponde al Body di un metodo
                // prendo la STentry dell'oggetto dalla Symbol Table
                SymbolTableEntry objectSTentry = env.processUse(classID);
                if (objectSTentry.getType() instanceof ClassType) {
                    classType = (ClassType) objectSTentry.getType();
                }
            } else {
                // vengono calcolati gli offset dalla dispatch table per avere accesso all'oggetto del metodo
                // prendo la STentry dell'oggetto dalla Symbol Table
                SymbolTableEntry objectSTentry = env.processUse(classID);

                if (!objectSTentry.isInitialized()) {
                    res.add("L'oggetto '" + classID + "' non è stato ancora inizializzato!\n");
                }

                IType objectType = objectSTentry.getType();
                objectOffset = objectSTentry.getOffset();
                objectNestingLevel = objectSTentry.getNestinglevel();

                // check che il metodo sia stato invocato da un oggetto
                if (objectType instanceof ObjectType) {
                    classType = ((ObjectType) objectType).getClassType();
                } else {
                    res.add("Il metodo " + methodID + " è invocato da un tipo che non è un oggetto\n");
                    return res;
                }
            }


            SymbolTableEntry classEntry;
            classEntry = env.processUse(classType.getClassID());


            ClassType objectClass = (ClassType) classEntry.getType();
            methodOffset = objectClass.getOffsetOfMethod(methodID);
            methodType = objectClass.getTypeOfMethod(methodID);

            // controllo che il metodo sia dichiarato all'interno della classe
            if (methodType == null) {
                res.add("L'oggetto " + classID + " non ha il metodo " + methodID + "\n");
            }

            FunType funType = (FunType) methodType;
            ArrayList<IType> funTypeArrayList = funType.getParametersTypeArrayList();
            //controllo che il metodo abbia il numero di parametri uguale a quello degli argomenti
            if (!(funTypeArrayList.size() == getArgumentsArrayList().size())) {
                res.add("Numero errato di parametri nell'invocazione del metodo: " + getId() + "\n");
            }

            //CheckSemantic per ogni argomento
            int index = 1;
            for (INode argument : getArgumentsArrayList()) {
                if (argument instanceof FunCallNode) {
                    String args = "una funzione";
                    if (argument instanceof MethodCallNode) {
                        args = "un metodo";
                    }
                    res.add("Errore: Il metodo '" + id + "' ha " + args + " come " + index + "° parametro\n"); // Vietato passare Funzioni e Metodi come parametri
                } else {
                    res.addAll(argument.checkSemantics(env));
                }
                index++;
            }

        } catch (UndeclaredIDException | UndeclaredMethodIDException e) {
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
                throw new TypeException("Tipo errato per il parametro " + (i + 1) + " nell'invocazione del metodo " + getId() + "()", getFuncallContext());
        return funType.getReturnType();
    }

    @Override
    public String codeGeneration() {

        StringBuilder parameterCode = new StringBuilder();
        for (int i = argumentsArrayList.size() - 1; i >= 0; i--)
            parameterCode.append(argumentsArrayList.get(i).codeGeneration());

        StringBuilder lwActivationRecord = new StringBuilder();

        for (int i = 0; i < nestinglevel - objectNestingLevel; i++)
            lwActivationRecord.append("lw\n");

        return "lfp\n"                                  // pusho frame pointer e parametri
                + parameterCode
                + "push " + objectOffset + "\n"         // pusho l'offset logico dell'oggetto (dispatch table)
                + "lfp\n"
                + lwActivationRecord                    // pusho access link (lw consecutivamente) così si potrà risalire la catena statica
                + "add\n"                               // $fp + offset
                + "lw\n"                                // pusho indirizzo di memoria in cui si trova l'indirizzo della dispatch table
                + "copy\n"                              // copio
                + "lw\n"                                // pusho l'indirizzo della dispatch table
                + "push " + (methodOffset - 1) + "\n"   // pusho l'offset di dove si trova metodo rispetto all'inizio della dispatch table
                + "add" + "\n"                          // dispatch_table_start + offset
                + "loadc\n"                             // pusho il codice del metodo
                + "js\n";                               // jump all'istruzione dove e' definito il metodo e salvo $ra
    }


}
