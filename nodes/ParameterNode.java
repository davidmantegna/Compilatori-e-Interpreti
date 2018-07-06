package nodes;

import type.IType;
import exceptions.MultipleIDException;
import exceptions.TypeException;
import parser.FOOLParser.VardecContext;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class ParameterNode implements INode {

    private String idParameter;
    private IType type;
    private int offset;
    private VardecContext vardecContext;

    public ParameterNode(String ID, IType type, int offset, VardecContext vardecContext) {
        this.idParameter = ID;
        this.type = type;
        this.offset = offset;
        this.vardecContext = vardecContext;
    }

    public String getId() {
        return idParameter;
    }

    public IType getType() {
        return type;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("ParameterNode: checkSemantics -> \n" /*+ env.toString() + "\n"*/);
        ArrayList<String> res = new ArrayList<>();
        try {
            env.processDeclaration(idParameter, type, offset);
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
