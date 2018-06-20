package ast;

import Type.IType;
import exceptions.MultipleIDException;
import exceptions.TypeException;
import parserNew.FOOLParser.VardecContext;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class ParameterNode implements INode{

    private String ID;
    private IType type;
    private int offset;
    private boolean insideClass;
    private VardecContext vardecContext;

    public ParameterNode(String ID, IType type, int offset, VardecContext vardecContext) {
        this.ID = ID;
        this.type = type;
        this.offset = offset;
        this.insideClass = false;
        this.vardecContext = vardecContext;
    }

/*  //TODO parameterNode for class constructor
    public ParameterNode(String ID, IType type, int offset, boolean insideClass, VardecContext vardecContext) {
        this.ID = ID;
        this.type = type;
        this.offset = offset;
        this.insideClass = insideClass;
        this.vardecContext = vardecContext;
    }
*/

    public String getId(){
        return ID;
    }

    public IType getType(){
        return type;
    }

    @Override
    public IType typeCheck() throws TypeException {
        return null;
    }

    @Override
    public String codeGeneration() {
        return "";
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        ArrayList<String> res = new ArrayList<>();
        try {
            env.processDeclarationforClass(ID, type, offset, insideClass);
        } catch (MultipleIDException e) {
            res.add(e.getMessage());
        }
        return res;
    }
}
