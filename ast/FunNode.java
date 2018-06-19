package ast;

import Type.IType;
import exceptions.TypeException;
import org.antlr.v4.runtime.ParserRuleContext;
import util.Semantic.SymbolTable;
import util.Semantic.SymbolTableEntry;
import util.VM.FunctionCode;
import util.VM.Label;

import java.util.ArrayList;
import java.util.HashMap;

public class FunNode implements INode {

    //protected perché vi possono accedere le sottoclassi, ovvero MethodNode
    protected String ID;
    protected IType returnType;
    protected ArrayList<ParameterNode> parameterNodeArrayList = new ArrayList<>();
    protected ArrayList<INode> declarationsArrayList;
    protected INode body;

    private ParserRuleContext parserRuleContext;

    public FunNode(String ID, IType returnType, ArrayList<ParameterNode> parameterNodeArrayList, ArrayList<INode> declarationsArrayList, INode body, ParserRuleContext parserRuleContext) {
        this.ID = ID;
        this.returnType = returnType;
        this.parameterNodeArrayList = parameterNodeArrayList;
        this.declarationsArrayList = declarationsArrayList;
        this.body = body;
        this.parserRuleContext = parserRuleContext;
    }

    public String getID() {
        return ID;
    }

    public ArrayList<ParameterNode> getParameterNodeArrayList() {
        return parameterNodeArrayList;
    }

    public IType getReturnType() {
        return returnType;
    }

    @Override
    public String toPrint(String indent) {
        String parlstr="";
        for (INode par:parameterNodeArrayList)
            parlstr+=par.toPrint(indent+"  ");
        String declstr="";
        if (declarationsArrayList!=null)
            for (INode dec:declarationsArrayList)
                declstr+=dec.toPrint(indent+"  ");
        return indent+"Fun:" + ID +"\n"
                +returnType.toPrint() +"  "
                +parlstr
                +declstr
                +body.toPrint(indent+"  ") ;
    }


    @Override
    public IType typeCheck() throws TypeException {
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
            throw new TypeException("Tipo incompatibile ritornato dalla funzione " + ID + " ritornato '" + bodyType + "', deve ritornare '" + returnType + "'", parserRuleContext);
        }

        //TODO implement ArrowType
        return null;//new ArrowType(paramsType, returnType);
    }


    @Override
    public String codeGeneration() {
        //variabili/funzioni dichiarate internamente
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

        return "push " + funLabel + "\n";
    }


    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        ArrayList<String> res = new ArrayList<>();

        ArrayList<IType> parameterTypeArrayList = new ArrayList<>();

        for (ParameterNode parameterNode : parameterNodeArrayList) {
            parameterTypeArrayList.add(parameterNode.getType());
        }

        /*try {   //TODO try catch object type
            // Se restituisco un'istanza di una classe, aggiorno le informazioni
            if ( returnType instanceof ObjectType) {
                ObjectType objectType = (ObjectType) returnType;
                res.addAll(objectType.updateClassType(env));
            }
            env.processDeclaration(ID, new ArrowType(parameterTypeArrayList, returnType), env.getOffset());
            env.decreaseOffset();
        } catch (MultipleIDException e) {
            res.add("La funzione " + ID + " è già stata dichiarata");
        }*/

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
}
