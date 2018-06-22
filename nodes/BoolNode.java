package nodes;


import type.BoolType;
import type.IType;
import exceptions.TypeException;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class BoolNode implements INode {

    private boolean value;

    public BoolNode(boolean value) {
        this.value = value;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("BoolNode: checkSemantics -> \n\t" + env.toString() + "\n");
        // new ArrayList<>() -> vuota perchè il chechSemantics è andato a buon fine. Quidi lista vuota di errori
        return new ArrayList<>(); //non crea livelli di scope
    }

    @Override
    public IType typeCheck() throws TypeException {
        return new BoolType();
    }

    @Override
    public String codeGeneration() {
        if (value)
            return "push " + 1 + "\n";
        else {
            return "push " + 0 + "\n";
        }
    }
}
