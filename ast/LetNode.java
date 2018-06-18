package ast;

import Type.IType;
import Type.VoidType;
import exceptions.TypeException;
import util.Semantic.SymbolTable;
import util.Semantic.SymbolTableEntry;

import java.util.ArrayList;
import java.util.HashMap;

public class LetNode implements INode {
    private ArrayList<INode> declarationArrayList;

    public LetNode(ArrayList<INode> declarationArrayList) {
        this.declarationArrayList = declarationArrayList;
    }

    @Override
    public String toPrint(String indent) {
        String declstr = "";
        for (INode dec : declarationArrayList) {
            declstr += dec.toPrint(indent + "\t");
        }
        return indent + "Let\n" + declstr;
    }

    @Override
    public IType typeCheck() throws TypeException {
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

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.println("LetNode: checkSemantics -> \t");
        ArrayList<String> res = new ArrayList<>();

        HashMap<String, SymbolTableEntry> hashMap = new HashMap<>();

        //entro in un nuovo livello di scope
        env.pushHashMap(hashMap);

        //CheckSemantic nella lista di dichiarazioni
        if (declarationArrayList.size() > 0) {
            env.setOffset(-2);
            //Checksemantic nei figli
            for (INode n : declarationArrayList)
                res.addAll(n.checkSemantics(env));
        }

        return res;
    }
}
