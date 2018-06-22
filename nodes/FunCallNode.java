package nodes;

import exceptions.TypeException;
import exceptions.UndeclaredIDException;
import parser.FOOLParser.FuncallContext;
import type.FunType;
import type.IType;
import util.Semantic.SymbolTable;
import util.Semantic.SymbolTableEntry;

import java.util.ArrayList;

public class FunCallNode implements INode {

    protected String id;
    protected ArrayList<INode> argumentsArrayList;
    protected SymbolTableEntry entry = null;
    protected int calledNestingLevel;
    private FuncallContext funcallContext;

    public FunCallNode(String id, ArrayList<INode> argumentsArrayList, FuncallContext funcallContext) {
        this.id = id;
        this.argumentsArrayList = argumentsArrayList;
        this.funcallContext = funcallContext;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("FunCallNode: checkSemantics -> \n\t" + env.toString() + "\n");
        ArrayList<String> res = new ArrayList<>();

        try {

            entry = env.processUse(id);
            calledNestingLevel = env.getNestingLevel();

            for(INode argument : argumentsArrayList)
                res.addAll(argument.checkSemantics(env));

        }catch (UndeclaredIDException e){
            res.add(id + ": identificativo non definito\n");
        }

        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.print("FunCallNode: typeCheck ->\t");

        FunType funType;

        if(entry.getType().getID().equals(IType.IDType.FUN)) {
            funType = (FunType) entry.getType();
        } else {
            throw new TypeException("Invocazione di una non funzione " + id, funcallContext);
        }

        ArrayList<IType> arrowTypeArrayList = funType.getParametersTypeArrayList();
        if (!(arrowTypeArrayList.size() == argumentsArrayList.size())) {
            throw new TypeException("Numero errato di parametri nell'invocazione di " + id, funcallContext);
        }

        //Controllo che il tipo dei parametri sia lo stesso del tipo degli argomenti
        for (int i = 0; i < argumentsArrayList.size(); i++)
            if (!argumentsArrayList.get(i).typeCheck().isSubType(arrowTypeArrayList.get(i))) {
                throw new TypeException("Tipo errato per il parametro " + (i + 1) + " nell'invocazione di " + id, funcallContext);
            }

        return funType.getReturnType();
    }

    @Override
    public String codeGeneration() {
        StringBuilder parameterCode = new StringBuilder();
        //parametri in ordine inverso
        for (int i = argumentsArrayList.size() - 1; i >= 0; i--)
            parameterCode.append(argumentsArrayList.get(i).codeGeneration());

        //utilizzato per gestire le funzioni annidate
        StringBuilder getActivationRecord = new StringBuilder();
        for (int i = 0; i < calledNestingLevel - entry.getNestinglevel(); i++)
            getActivationRecord.append("lw\n");

        return "lfp\n" + //pusho frame pointer e parametri
                parameterCode +
                "lfp\n" + getActivationRecord + //pusho access link (lw consecutivamente)
                // così si potrà risalire la catena statica
                "push " + entry.getOffset() + "\n" + // pusho l'offset logico per
                // accedere al codice della funzione
                "lfp\n" + getActivationRecord + //risalgo la catena statica
                "add\n" + //$fp + offset
                "lw\n" + //tramite l'indirizzo accedo al codice della funzione
                "js\n";
    }

}
