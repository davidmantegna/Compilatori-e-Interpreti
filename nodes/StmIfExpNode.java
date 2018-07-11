package nodes;

import codegen.VM.Label;
import exceptions.TypeException;
import parser.FOOLParser.StmIfExpContext;
import symboltable.SymbolTable;
import type.BoolType;
import type.IType;

import java.util.ArrayList;

public class StmIfExpNode implements INode {

    private INode conditionNode;
    private INode stmsThen;
    private INode stmsElse;
    private StmIfExpContext ctx;

    public StmIfExpNode(INode cond, INode then, INode el, StmIfExpContext c) {
        this.conditionNode = cond;
        this.stmsThen = then;
        this.stmsElse = el;
        this.ctx = c;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("StmIfExpNode: checkSemantics -> \n" /*+ env.toString() + "\n"*/);
        ArrayList<String> result = new ArrayList<>();
        //checkSemantic sulla condizione
        result.addAll(conditionNode.checkSemantics(env));

        //checkSemantic sui rami then ed else
        result.addAll(stmsThen.checkSemantics(env));
        result.addAll(stmsElse.checkSemantics(env));
        return result;
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.print("StmIfExpNode: typeCheck -> \t");
        if (!conditionNode.typeCheck().isSubType(new BoolType()))
            throw new TypeException("Condizione non booleana", ctx);
        IType stmsThenType = stmsThen.typeCheck();
        IType stmsElType = stmsElse.typeCheck();
        if (stmsThenType.isSubType(stmsElType)) return stmsElType;
        else if (stmsElType.isSubType(stmsThenType)) return stmsElType;
        else throw new TypeException("Incompatibilità di tipo nel then e nell'else", ctx);
    }

    @Override
    public String codeGeneration() {

        String thenBranch = Label.nuovaLabelString("Then");
        String exit = Label.nuovaLabelString("Exit");
        return conditionNode.codeGeneration()
                + "push 1\n"
                + "beq " + thenBranch + "\n"
                + stmsElse.codeGeneration()
                + "b " + exit + "\n\n"
                + thenBranch + ":\n"
                + stmsThen.codeGeneration() + "\n\n"
                + exit + ":\n";
    }
}
