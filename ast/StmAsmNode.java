package ast;

import Type.IType;
import exceptions.MultipleIDException;
import exceptions.TypeException;
import parserNew.FOOLParser.StmAssignmentContext;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class StmAsmNode implements INode {

    //TODO da testare
    private  String id;
    private IType type;
    private INode exp;
    private StmAssignmentContext ctx;

    public StmAsmNode(String id, IType t, INode e, StmAssignmentContext c) {
        this.type = t;
        this.exp = e;
        this.ctx = c;
        this.id = id;
    }

    @Override
    public String toPrint(String indent) {
        return indent +
                id +
                exp.toPrint(indent + " ");
    }

    @Override
    public IType typeCheck() throws TypeException {
        return exp.typeCheck();
    }

    @Override
    public String codeGeneration() {
        return exp.codeGeneration() + "\n";
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {

        System.out.print("StmAsmNode: checkSemantics -> \t");
        ArrayList<String> res = new ArrayList<>();

        res.addAll(exp.checkSemantics(env));

        return res;
    }
}
