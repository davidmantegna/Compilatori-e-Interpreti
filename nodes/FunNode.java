package nodes;

import exceptions.MultipleIDException;
import exceptions.TypeException;
import org.antlr.v4.runtime.ParserRuleContext;
import type.FunType;
import type.IType;
import type.ObjectType;
import util.Semantic.SymbolTable;
import util.Semantic.SymbolTableEntry;
import util.VM.FunctionCode;
import util.VM.Label;

import java.util.ArrayList;
import java.util.HashMap;

public class FunNode implements INode {

    //protected perché vi possono accedere le sottoclassi, ovvero MethodNode
    protected String idFunzione;
    protected IType returnType;
    protected ArrayList<ParameterNode> parameterNodeArrayList;
    protected ArrayList<INode> declarationsArrayList;
    protected INode body;
    private ParserRuleContext parserRuleContext;

    public FunNode(String id, IType returnType, ArrayList<ParameterNode> parameterNodeArrayList, ArrayList<INode> declarationsArrayList, INode body, ParserRuleContext parserRuleContext) {
        this.idFunzione = id;
        this.returnType = returnType;
        this.parameterNodeArrayList = parameterNodeArrayList;
        this.declarationsArrayList = declarationsArrayList;
        this.body = body;
        this.parserRuleContext = parserRuleContext;
    }

    public String getID() {
        return idFunzione;
    }

    public ArrayList<ParameterNode> getParameterNodeArrayList() {
        return parameterNodeArrayList;
    }

    public IType getReturnType() {
        return returnType;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("FunNode: checkSemantics -> \n\t" + env.toString() + "\n");
        ArrayList<String> res = new ArrayList<>();

        ArrayList<IType> parameterTypeArrayList = new ArrayList<>();

        for (ParameterNode parameterNode : parameterNodeArrayList) {
            parameterTypeArrayList.add(parameterNode.getType());
        }

        try {
            // Se restituisco un'istanza di una classe, aggiorno le informazioni
            if (returnType instanceof ObjectType) {
                ObjectType objectType = (ObjectType) returnType;
                res.addAll(objectType.updateClassType(env));
            }
            env.processDeclaration(idFunzione, new FunType(parameterTypeArrayList, returnType), env.getOffset());
            env.decreaseOffset();
        } catch (MultipleIDException e) {
            res.add("La funzione " + idFunzione + " è già stata dichiarata");
        }

        //entro in un nuovo livello di scope
        HashMap<String, SymbolTableEntry> hm = new HashMap<>();
        env.pushHashMap(hm);

        //checkSemantic di tutti i parametri
        for (ParameterNode param : parameterNodeArrayList) {
            res.addAll(param.checkSemantics(env));
        }

        //checkSemantic di tutte le dichiarazioni interne alla funzione
        if (declarationsArrayList.size() > 0) {
            env.setOffset(-2);
            for (INode n : declarationsArrayList)
                res.addAll(n.checkSemantics(env));
        }

        //checkSemantic del corpo della funzione
        res.addAll(body.checkSemantics(env));

        //esco dal livello di scope
        env.popHashMap();

        //ritorno eventuali errori rilevati
        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.print("FunNode: typeCheck ->\t");

        //typecheck di ogni parametro
        ArrayList<IType> paramsType = new ArrayList<>();
        for (ParameterNode param : parameterNodeArrayList) {
            paramsType.add(param.getType());
        }

        //typecheck di ogni dichiarazione
        if (declarationsArrayList.size() > 0) {
            for (INode dec : declarationsArrayList) {
                dec.typeCheck();
            }
        }

        //controllo che il corpo ritorni il tipo dichiarato dalla funzione
        IType bodyType = body.typeCheck();
        if (!bodyType.isSubType(returnType)) {
            throw new TypeException("Il tipo restituito dal corpo della funzione '" + idFunzione + "' è '" + body.typeCheck().toPrint() + "'. Il tipo richiesto è '" + returnType.toPrint() + "'\n", parserRuleContext);
        }

        return new FunType(paramsType, returnType);
    }

    @Override
    public String codeGeneration() {

        //TODO test codeGeneration

        /*//variabili/funzioni dichiarate internamente
        StringBuilder localDeclarations = new StringBuilder();
        //variabili/funzioni da togliere dallo stack al termine del record di attivazione
        StringBuilder popLocalDeclarations = new StringBuilder();
        if (declarationsArrayList.size() > 0)
            for (INode dec : declarationsArrayList) {
                localDeclarations.append(dec.codeGeneration());
                popLocalDeclarations.append("pop\n");
            }
        //parametri in input da togliere dallo stack al termine del record di attivazione
        StringBuilder popInputParameters = new StringBuilder();
        for (INode dec : parameterNodeArrayList)
            popInputParameters.append("pop\n");

        String funLabel = Label.nuovaLabelFunzione();

        //inserisco il codice della funzione in fondo al main, davanti alla label
        FunctionCode.insertFunctionsCode(funLabel + ":\n" +
                "cfp\n" + //$fp diventa uguale al valore di $sp
                "lra\n" + //push return address
                localDeclarations + //push dichiarazioni locali
                body.codeGeneration() +
                "srv\n" + //pop del return value
                popLocalDeclarations +
                "sra\n" + // pop del return address
                "pop\n" + // pop dell'access link, per ritornare al vecchio livello di scope
                popInputParameters +
                "sfp\n" +  // $fp diventa uguale al valore del control link
                "lrv\n" + // push del risultato
                "lra\n" + // push del return address
                "js\n" // jump al return address per continuare dall'istruzione dopo
        );

        return "push " + funLabel + "\n";*/
        return "";
    }
}
