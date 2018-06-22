package nodes;

import exceptions.TypeException;
import type.IType;
import type.VoidType;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class NullNode implements INode {

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("NullNode: checkSemantics -> \n\t" + env.toString() + "\n");
        return new ArrayList<>(); //non crea livelli di scope
    }

    @Override
    public IType typeCheck() throws TypeException {
        return new VoidType();
    }

    @Override
    public String codeGeneration() {
        return null;
    }
}
