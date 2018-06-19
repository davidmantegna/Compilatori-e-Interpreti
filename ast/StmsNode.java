package ast;

import Type.IType;
import Type.VoidType;
import exceptions.TypeException;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class StmsNode implements INode {

    //TODO da testare
    private ArrayList<INode> stmsArrayList;

    public StmsNode(ArrayList<INode> stmsArrayList) {
        this.stmsArrayList = stmsArrayList;
    }

    @Override
    public String toPrint(String indent) {
        String stmsStr = "";
        for (INode stm: stmsArrayList)
            stmsStr += stm.toPrint(indent + "\t");

        return indent + "Stms\n" + stmsStr;
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.print("StmsNode: typeCheck -> \t");
        for (INode stm: stmsArrayList)
            stm.typeCheck();
        return new VoidType();
    }

    @Override
    public String codeGeneration() {
        StringBuilder stmsCode = new StringBuilder();
        for (INode stm : stmsArrayList)
            stmsCode.append(stm.codeGeneration());
        return stmsCode.toString();
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env){
        System.out.print("StmsNode: checkSemantics -> \t");
        ArrayList<String> res = new ArrayList<>();

        for (INode stm : stmsArrayList)
            res.addAll(stm.checkSemantics(env));


        return res;

    }
}
