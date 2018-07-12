package nodes;

import exceptions.TypeException;
import symboltable.SymbolTable;
import type.BoolType;
import type.IType;

import java.util.ArrayList;

public class NotNode implements INode {

    private INode value;

    public NotNode(INode value) {
        this.value = value;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        //System.out.print("NotNode: checkSemantics -> \n");
        return new ArrayList<>();
    }

    @Override
    public IType typeCheck() throws TypeException {
        //System.out.print("NotNode: typeCheck ->\t");
        return new BoolType();
    }

    @Override
    public String codeGeneration() {
        return value.codeGeneration();
    }
}