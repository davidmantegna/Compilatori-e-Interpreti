package nodes;

import exceptions.MultipleIDException;
import exceptions.TypeException;
import exceptions.UndeclaredIDException;
import org.antlr.v4.runtime.ParserRuleContext;
import type.ClassType;
import type.FunType;
import type.IType;
import type.ObjectType;
import util.Semantic.SymbolTable;
import util.Semantic.SymbolTableEntry;

import java.util.ArrayList;
import java.util.HashMap;

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
        System.out.print("MethodNode: checkSemantics -> \n\t" + env.toString() + "\n");

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

        // entro in un nuovo livello di scope
        HashMap<String, SymbolTableEntry> hm = new HashMap<>();
        env.pushHashMap(hm);

        // cerco la entry in cui è stata dichiarata la classe
        try {
            SymbolTableEntry classEntry = env.processUse(idClass);
            env.processDeclaration("this", new ObjectType((ClassType) classEntry.getType()), 0);
        } catch (MultipleIDException | UndeclaredIDException e) {
            System.out.println("MethodNode: exception-> " + e.getMessage());
        }

        // checkSemantics di tutti i parametri
        for (ParameterNode parameter:parameterNodeArrayList){
            res.addAll(parameter.checkSemantics(env));
        }

        // checkSemantics di tutte le dichiarazioni interne al metodo
        if(declarationsArrayList.size()>0){
            env.setOffset(-2);
            for (INode node: declarationsArrayList){
                res.addAll(node.checkSemantics(env));
            }
        }

        // checkSemantics del corpo del metodo
        res.addAll(body.checkSemantics(env));

        //esco dal livello di scope
        env.popHashMap();

        //ritorno eventuali errori rilevati
        return res;
    }

    @Override
    public String codeGeneration() {
        System.out.print("MethodNode: codeGeneration ->\t");
        // TODO codeGeneration MethodNode
        return super.codeGeneration();
    }
}
