package nodes;

import type.IType;
import type.VoidType;
import parser.FOOLParser.StmAssignmentContext;
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
    public IType typeCheck(){
        System.out.print("StmAsmNode: typeCheck -> \t");
        return new VoidType();
    }

    @Override
    public String codeGeneration() {
        return exp.codeGeneration() + "\n";
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("StmAsmNode: checkSemantics -> \n\t" + env.toString() + "\n");
        ArrayList<String> res = new ArrayList<>();

        res.addAll(exp.checkSemantics(env));

        return res;
    }
}
