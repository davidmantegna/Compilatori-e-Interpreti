package nodes;

import exceptions.MultipleIDException;
import exceptions.TypeException;
import parser.FOOLParser.VardecContext;
import symboltable.SymbolTable;
import type.IType;

import java.util.ArrayList;

public class ParameterNode implements INode {

    private String idParameter;
    private IType type;
    private int offset;
    private boolean insideClass;
    private VardecContext vardecContext;

    public ParameterNode(String idParameter, IType type, int offset, boolean insideClass, VardecContext vardecContext) {
        this.idParameter = idParameter;
        this.type = type;
        this.offset = offset;
        this.insideClass = insideClass;
        this.vardecContext = vardecContext;
    }

    public String getId() {
        return idParameter;
    }

    public IType getType() {
        return type;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("ParameterNode: checkSemantics -> \n");
        ArrayList<String> res = new ArrayList<>();
        try {
            env.processDeclarationClass(idParameter, type, offset, true, insideClass);
        } catch (MultipleIDException e) {
            res.add(e.getMessage());
        }
        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        return null;
    }

    @Override
    public String codeGeneration() {
        return "";
    }
}
