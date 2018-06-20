package ast;

import Type.IType;
import Type.VoidType;
import exceptions.TypeException;
import util.Semantic.SymbolTable;
import util.Semantic.SymbolTableEntry;

import java.util.ArrayList;
import java.util.HashMap;

public class LetnestNode implements INode {
    private ArrayList<INode> varAssignmentArrayList;

    public LetnestNode(ArrayList<INode> varAssignmentArrayList) {
        this.varAssignmentArrayList = varAssignmentArrayList;
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.print("LetnestNode: typeCheck ->\t");
        for (INode dec : varAssignmentArrayList) {
            dec.typeCheck();
        }
        return new VoidType();
    }

    @Override
    public String codeGeneration() {
        StringBuilder declCode = new StringBuilder();
        for (INode dec : varAssignmentArrayList)
            declCode.append(dec.codeGeneration());
        return declCode.toString();
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("LetnestNode: checkSemantics ->\t");
        ArrayList<String> res = new ArrayList<>();

        HashMap<String, SymbolTableEntry> hashMap = new HashMap<>();

        //entro in un nuovo livello di scope
        env.pushHashMap(hashMap);

        //CheckSemantic nella lista di dichiarazioni
        if (varAssignmentArrayList.size() > 0) {
            env.setOffset(-2);
            //Checksemantic nei figli
            for (INode n : varAssignmentArrayList)
                res.addAll(n.checkSemantics(env));
        }

        return res;
    }
}
