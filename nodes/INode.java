package nodes;

import exceptions.TypeException;
import symboltable.SymbolTable;
import type.IType;

import java.util.ArrayList;

public interface INode {

    ArrayList<String> checkSemantics(SymbolTable env);

    IType typeCheck() throws TypeException;

    String codeGeneration();
}
