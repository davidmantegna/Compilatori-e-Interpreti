package ast;

import Type.IType;
import Type.VoidType;
import parserNew.FOOLParser.StmAssignmentContext;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class StmAsmNode implements INode {

    //TODO da testare
    private  String id;
    private INode exp;
    private StmAssignmentContext ctx;

    public StmAsmNode(String id, INode e, StmAssignmentContext c) {
        this.exp = e;
        this.ctx = c;
        this.id = id;
    }

    @Override
    public String toPrint(String indent) {
        return indent + "Var:\n"
                + "\t\t\t" + id + " "
                + exp.toPrint(indent);
    }

    @Override
    public IType typeCheck(){
        return new VoidType();
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
