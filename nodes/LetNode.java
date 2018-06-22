package nodes;

import type.IType;
import type.VoidType;
import exceptions.TypeException;
import util.Semantic.SymbolTable;
import util.Semantic.SymbolTableEntry;

import java.util.ArrayList;
import java.util.HashMap;

public class LetNode implements INode {
    private ArrayList<INode> declarationArrayList;
    private String infoInvocation;

    public LetNode(ArrayList<INode> declarationArrayList, String infoInvocation) {
        this.declarationArrayList = declarationArrayList;
        this.infoInvocation = infoInvocation;
        System.out.println(infoInvocation);
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("LetNode: checkSemantics -> \n\t" + env.toString() + "\n");
        ArrayList<String> res = new ArrayList<>();

        //CheckSemantic nella lista di dichiarazioni
        if (declarationArrayList.size() > 0) {
            env.setOffset(-2);
            //Checksemantic nei figli
            for (INode n : declarationArrayList)
                res.addAll(n.checkSemantics(env));
        }

        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.print("LetNode: typeCheck ->\t");
        for (INode dec : declarationArrayList) {
            dec.typeCheck();
        }
        return new VoidType();
    }

    @Override
    public String codeGeneration() {
        StringBuilder declCode = new StringBuilder();
        for (INode dec : declarationArrayList)
            declCode.append(dec.codeGeneration());
        return declCode.toString();
    }
}
