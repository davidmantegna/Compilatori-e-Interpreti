package ast;

import Type.IType;
import Type.IntType;
import exceptions.TypeException;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class IntNode implements INode {

    private int value;

    public IntNode(int value) {
        this.value = value;
    }

    @Override
    public String toPrint(String indent) {
        return indent + "Int:"
                + Integer.toString(value) + "\n";
    }

    @Override
    public IType typeCheck() throws TypeException {
        return new IntType();
    }

    @Override
    public String codeGeneration() {
        return "push " + value + "\n";
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("IntNode: checkSemantics -> \t");
        return new ArrayList<>(); //non crea livelli di annidamento
    }
}
