package ast;

import Type.IType;
import exceptions.TypeException;
import util.Semantic.SymbolTable;


import java.util.ArrayList;

public interface INode {

    //fa il type checking e ritorna:
    //  per una espressione, il suo tipo (oggetto BoolTypeNode o IntTypeNode)
    //  per una dichiarazione, "null"
    IType typeCheck() throws TypeException;

    String codeGeneration();

    ArrayList<String> checkSemantics(SymbolTable env);
}
