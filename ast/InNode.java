package ast;

import Type.IType;
import exceptions.TypeException;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class InNode implements INode {

    private INode val;
    private String string; // exp || stms

    public InNode(INode val, String string) {
        this.val = val;
        this.string = string;
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.print("InNode: typeCheck ->\t");
        return val.typeCheck();
    }

    @Override
    public String codeGeneration() {
        return val.codeGeneration();
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("InNode: checkSemantics -> \t");
        //create the result
        ArrayList<String> res = new ArrayList<String>();

        //check semantics in the val INode
        res.addAll(val.checkSemantics(env));
        return res;
    }
}
