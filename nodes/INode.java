package nodes;

import type.IType;
import exceptions.TypeException;
import util.Semantic.SymbolTable;


import java.util.ArrayList;

public interface INode {

    ArrayList<String> checkSemantics(SymbolTable env);

    IType typeCheck() throws TypeException;

    String codeGeneration();
}
