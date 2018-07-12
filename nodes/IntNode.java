package nodes;

import exceptions.TypeException;
import symboltable.SymbolTable;
import type.IType;
import type.IntType;

import java.util.ArrayList;

public class IntNode implements INode {

    private int value;

    public IntNode(int value) {
        this.value = value;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        //System.out.print("IntNode: checkSemantics -> \n");
        return new ArrayList<>(); //non crea livelli di annidamento
    }

    @Override
    public IType typeCheck() throws TypeException {
        return new IntType();
    }

    @Override
    public String codeGeneration() {
        return "push " + value + "\n";
    }


}
