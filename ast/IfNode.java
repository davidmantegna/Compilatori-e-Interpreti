package ast;

import Type.BoolType;
import Type.IType;
import exceptions.TypeException;
import parserNew.FOOLParser.IfExpContext;
import util.Semantic.SymbolTable;
import util.VM.Label;

import java.util.ArrayList;

public class IfNode implements INode{

    private INode cond;
    private INode th;
    private INode el;
    private IfExpContext ctx;

    public IfNode(INode cnd, INode t, INode e, IfExpContext c) {
        this.cond = cnd;
        this.th = t;
        this.el = e;
        this.ctx = c;
    }

    @Override
    public String toPrint(String indent) {
        return indent + "If\n" +
        cond.toPrint(indent + " ") +
                th.toPrint(indent + " ") +
                el.toPrint(indent + " ") ;
    }

    @Override
    public IType typeCheck() throws TypeException {
        if(!cond.typeCheck().isSubType(new BoolType()))
            throw new TypeException("Condizione non booleana", ctx);
        IType thenType = th.typeCheck();
        IType elType = el.typeCheck();
        if(thenType.isSubType(elType)) return elType;
        else if(elType.isSubType(thenType)) return elType;
        else throw new TypeException("Tipi non compatibili nel then e nell'else", ctx);
    }

    @Override
    public String codeGeneration() {
        String thenBranch = Label.nuovaLabel();
        String exit = Label.nuovaLabel();
        return cond.codeGeneration() +
                "push 1\n" +
                "beq" + thenBranch + "\n" +
                el.codeGeneration() +
                "b " + exit + "\n" +
                thenBranch + ":\n" +
                th.codeGeneration() +
                exit + ":\n";
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        ArrayList<String> result = new ArrayList<>();

        //checkSemantic sulla condizione
        result.addAll(cond.checkSemantics(env));

        //checkSemantic sui rami then ed else
        result.addAll(th.checkSemantics(env));
        result.addAll(el.checkSemantics(env));

        return result;
    }
}