package nodes;

import exceptions.TypeException;
import symboltable.SymbolTable;
import type.IType;
import type.VoidType;

import java.util.ArrayList;

public class NullNode implements INode {

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("NullNode: checkSemantics -> \n" /*+ env.toString() + "\n"*/);
        return new ArrayList<>(); //non crea livelli di scope
    }

    @Override
    public IType typeCheck() throws TypeException {
        return new VoidType();
    }

    @Override
    public String codeGeneration() {
        //TODO codegen
        return null;
    }
}
