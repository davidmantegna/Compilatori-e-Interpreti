package nodes;

import exceptions.TypeException;
import symboltable.SymbolTable;
import type.IType;
import type.VoidType;

import java.util.ArrayList;

public class NullNode implements INode {

    public String classID;

    public NullNode(String classID) {
        this.classID = classID;
    }

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
        return "push 0\n" +
                "push class" + classID + "\n" +
                "new\n";
    }
}
