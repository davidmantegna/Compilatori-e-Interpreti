package ast;

import Type.BoolType;
import Type.IType;
import Type.IntType;
import exceptions.TypeException;
import exceptions.UndeclaredIDException;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class TypeNode implements INode {

    //TODO add ID for object orientation

    private String assignedType;
    private IType type;

    public TypeNode(String assignedType) {
        this.assignedType = assignedType;
        this.type = verificaType(assignedType);
    }

    @Override
    public String toPrint(String indent) {
        return indent + assignedType + "\n";
    }

    @Override
    public IType typeCheck() throws TypeException {
        return type;
    }

    @Override
    public String codeGeneration() {
        return " - TypeCOde - ";
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("TypeNode: checkSemantics -> \t");

        ArrayList<String> result = new ArrayList<>();
        try {
            this.type = env.processUse(assignedType).getType();
        } catch (UndeclaredIDException e) {
            System.out.println(e.getMessage());
            result.add("La classe '" + assignedType + "' non esiste");
        }
        return result;
    }

    private IType verificaType(String assignedType) {
        switch (assignedType) {
            case "int":
                return new IntType();
            case "bool":
                return new BoolType();
            default:
                // this.type = new ObjectType(new ClassType(assignedType));
                return null;
        }
    }
}
