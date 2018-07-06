package nodes;

import type.IType;
import exceptions.TypeException;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class SingleExpNode implements INode {

    INode expression;

    public SingleExpNode(INode expression) {
        this.expression = expression;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("SingleExpNode: checkSemantics -> \n" /*+ env.toString() + "\n"*/);
        return expression.checkSemantics(env);
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.println("SingleExpNode: typeCheck ->\t");
        return expression.typeCheck();
    }

    @Override
    public String codeGeneration() {
        return expression.codeGeneration() + "halt\n";
    }
}
