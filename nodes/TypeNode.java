package nodes;

import exceptions.TypeException;
import exceptions.UndeclaredIDException;
import symboltable.SymbolTable;
import type.*;

import java.util.ArrayList;

public class TypeNode implements INode {


    private String assignedType;
    private IType type;

    public TypeNode(String assignedType) {
        this.assignedType = assignedType;
        this.type = verificaType(assignedType);
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        //System.out.print("TypeNode: checkSemantics -> \n");

        ArrayList<String> result = new ArrayList<>();
        try {
            this.type = env.processUse(assignedType).getType();
        } catch (UndeclaredIDException e) {
            System.out.println(e.getMessage());
            result.add("La classe '" + assignedType + "' non esiste");
        }
        return result;
    }

    @Override
    public IType typeCheck() throws TypeException {
        return type;
    }

    private IType verificaType(String assignedType) {
        switch (assignedType) {
            case "int":
                return new IntType();
            case "bool":
                return new BoolType();
            default:
                return new ObjectType(new ClassType(assignedType));
        }
    }

    @Override
    public String codeGeneration() {
        return "";
    }
}
