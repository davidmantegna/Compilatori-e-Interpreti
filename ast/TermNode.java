package ast;

import Type.IType;
import Type.IntType;
import exceptions.TypeException;
import parserNew.FOOLParser;
import parserNew.FOOLParser.TermContext;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class TermNode implements INode {
    private INode leftNode;
    private INode rightNode;
    private TermContext termContext;
    private String operation; // utilizzato per distinguere tra moltiplicazione e divisione

    public TermNode(INode leftNode, INode rightNode, TermContext termContext, String operation) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.termContext = termContext;
        this.operation = operation;
    }

    @Override
    public String toPrint(String indent) {
        return indent + operation + "\n"
                + leftNode.toPrint(indent + "  ")
                + rightNode.toPrint(indent + "  ");
    }

    @Override
    public IType typeCheck() throws TypeException {
        // TODO aggiungere sottotipaggio

        return new IntType();
    }

    @Override
    public String codeGeneration() {
        if (operation.equals("Times")) {
            return leftNode.codeGeneration() + rightNode.codeGeneration() + "mult\n";
        } else {
            return leftNode.codeGeneration() + rightNode.codeGeneration() + "div\n";
        }
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("TermNode: checkSemantics -> \t");
        //create the result
        ArrayList<String> res = new ArrayList<String>();

        //check semantics in the left and in the right exp
        res.addAll(leftNode.checkSemantics(env));
        res.addAll(rightNode.checkSemantics(env));

        return res;
    }
}
