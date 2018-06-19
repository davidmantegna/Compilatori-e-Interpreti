package ast;

import Type.BoolType;
import Type.IType;
import exceptions.TypeException;
import parserNew.FOOLParser.StmIfExpContext;
import util.Semantic.SymbolTable;
import util.VM.Label;

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
    public String toPrint(String indent) {
        return indent +
                conditionNode.toPrint(indent + "\t") +
                stmsThen.toPrint(indent + "\t") +
                stmsElse.toPrint(indent + "\t") ;
    }

    @Override
    public IType typeCheck() throws TypeException {
        if(!conditionNode.typeCheck().isSubType(new BoolType()))
            throw new TypeException("Condizione non booleana", ctx);
        IType stmsThenType = stmsThen.typeCheck();
        IType stmsElType = stmsElse.typeCheck();
        if(stmsThenType.isSubType(stmsElType)) return stmsElType ;
        else if(stmsElType .isSubType(stmsThenType)) return stmsElType ;
        else throw new TypeException("Incompatibilità di tipo nel then e nell'else", ctx);
    }

    @Override
    public String codeGeneration() {
        String thenBranch = Label.nuovaLabel();
        String exit = Label.nuovaLabel();
        return conditionNode.codeGeneration() +
                "push 1\n" +
                "beq" + thenBranch + "\n" +
                stmsThen.codeGeneration() +
                "b " + exit + "\n" +
                thenBranch + ":\n" +
                stmsElse.codeGeneration() +
                exit + ":\n";
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("StmIfExpNode: checkSemantics -> \t");
        ArrayList<String> result = new ArrayList<>();
        //checkSemantic sulla condizione
        result.addAll(conditionNode.checkSemantics(env));

        //checkSemantic sui rami then ed else
        result.addAll(stmsThen.checkSemantics(env));
        result.addAll(stmsElse.checkSemantics(env));
        return result;
    }
}
