package nodes;

import codegen.VM.FunctionCode;
import codegen.VM.Label;
import exceptions.MultipleIDException;
import exceptions.UndeclaredIDException;
import org.antlr.v4.runtime.ParserRuleContext;
import symboltable.SymbolTable;
import symboltable.SymbolTableEntry;
import type.ClassType;
import type.FunType;
import type.IType;
import type.ObjectType;

import java.util.ArrayList;

public class MethodNode extends FunNode {

    private String idClass;

    public MethodNode(String id, IType returnType, ArrayList<ParameterNode> parameterNodeArrayList, ArrayList<INode> declarationsArrayList, INode body, ParserRuleContext parserRuleContext) {
        super(id, returnType, parameterNodeArrayList, declarationsArrayList, body, parserRuleContext);
    }

    public void setIdClass(String idClass) {
        this.idClass = idClass;
    }


    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("MethodNode: checkSemantics -> \n" /*+ env.toString() + "\n"*/);

        ArrayList<String> res = new ArrayList<>();

        ArrayList<IType> parameterTypeArrayList = new ArrayList<>();

        for (ParameterNode parameterNode : parameterNodeArrayList) {
            parameterTypeArrayList.add(parameterNode.getType());
        }

        // informazioni istanza classe
        if (returnType instanceof ObjectType) {
            ObjectType objectType = (ObjectType) returnType;
            res.addAll(objectType.updateClassType(env));
        }
        try {
            // aggiungo alla symboltable il metodo dichiarato
            env.processDeclaration(idFunzione, new FunType(parameterTypeArrayList, returnType), env.getOffset());
            env.decreaseOffset();
        } catch (MultipleIDException e) {
            res.add("Il metodo " + idFunzione + " è già stato dichiarato\n");
        }


        //cerco la entry in cui è situata la classe
        try {
            SymbolTableEntry classEntry = env.processUse(idClass);
            env.processDeclaration("this", new ObjectType((ClassType) classEntry.getType()), 0);
        } catch (MultipleIDException | UndeclaredIDException e) {
            e.printStackTrace();
        }

        // entro in un nuovo livello di scope
        env.entryNewScope();

        // checkSemantics di tutti i parametri
        for (ParameterNode parameter : parameterNodeArrayList) {
            res.addAll(parameter.checkSemantics(env));
        }

        // checkSemantics di tutte le dichiarazioni interne al metodo
        if (declarationsArrayList.size() > 0) {
            env.setOffset(-1);//TODO in precedenza era -2
            for (INode node : declarationsArrayList) {
                res.addAll(node.checkSemantics(env));
            }
        }

        // checkSemantics del corpo del metodo
        res.addAll(body.checkSemantics(env));

        //esco dal livello di scope
        env.exitLastScope();

        //ritorno eventuali errori rilevati
        return res;
    }

    @Override
    public String codeGeneration() {
        System.out.print("MethodNode: codeGeneration ->\t");
        // TODO codeGeneration

        //variabili dichiarate internamente
        StringBuilder localDeclarations = new StringBuilder();
        //variabili da togliere dallo stack al termine del record di attivazione
        StringBuilder popLocalDeclarations = new StringBuilder();
        if (declarationsArrayList.size() > 0)
            for (INode dec : declarationsArrayList) {
                localDeclarations.append(dec.codeGeneration());
                popLocalDeclarations.append("pop\n");
            }
        //parametri in input da togliere dallo stack al termine del record di attivazione
        StringBuilder popInputParameters = new StringBuilder();
        for (int i = 0; i < parameterNodeArrayList.size(); i++) {
            popInputParameters.append("pop\n");
        }
        String funLabel = Label.nuovaLabelMetodoString(idFunzione.toUpperCase());

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

        return funLabel + "\n";
    }
}
