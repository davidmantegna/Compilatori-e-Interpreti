package ast;

import Type.BoolType;
import Type.IType;
import exceptions.TypeException;
import parserNew.FOOLParser.IfExpContext;
import util.Semantic.SymbolTable;
import util.VM.Label;

import java.util.ArrayList;

public class IfNode implements INode{

    private INode conditionNode;
    private INode thenNode;
    private INode elseNode;
    private IfExpContext ctx;

    public IfNode(INode cond, INode then, INode el, IfExpContext c) {
        this.conditionNode = cond;
        this.thenNode = then;
        this.elseNode = el;
        this.ctx = c;
    }

    @Override
    public String toPrint(String indent) {
        return indent +
                conditionNode.toPrint(indent + " ") +
                thenNode.toPrint(indent + " ") +
                elseNode.toPrint(indent + " ") ;
    }

    @Override
    public IType typeCheck() throws TypeException {
        if(!conditionNode.typeCheck().isSubType(new BoolType()))
            throw new TypeException("Condizione non booleana", ctx);
        IType thenType = thenNode.typeCheck();
        IType elType = elseNode.typeCheck();
        if(thenType.isSubType(elType)) return elType;
        else if(elType.isSubType(thenType)) return elType;
        else throw new TypeException("Tipi non compatibili nel then e nell'else", ctx);
    }

    @Override
    public String codeGeneration() {
        String thenBranch = Label.nuovaLabel();
        String exit = Label.nuovaLabel();
        return conditionNode.codeGeneration() +
                "push 1\n" +
                "beq" + thenBranch + "\n" +
                elseNode.codeGeneration() +
                "b " + exit + "\n" +
                thenBranch + ":\n" +
                thenNode.codeGeneration() +
                exit + ":\n";
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        ArrayList<String> result = new ArrayList<>();

        //checkSemantic sulla condizione
        result.addAll(conditionNode.checkSemantics(env));

        //checkSemantic sui rami then ed else
        result.addAll(thenNode.checkSemantics(env));
        result.addAll(elseNode.checkSemantics(env));

        return result;
    }
}