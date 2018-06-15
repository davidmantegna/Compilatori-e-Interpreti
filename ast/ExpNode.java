package ast;

import Type.IType;
import Type.IntType;
import exceptions.TypeException;
import parserNew.FOOLParser;
import parserNew.FOOLParser.ExpContext;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class ExpNode implements INode {
    private INode leftNode;
    private INode rightNode;
    private ExpContext expContext;
    private String operation; // utilizzato per distinguere tra addizione e sottrazione

    public ExpNode(INode leftNode, INode rightNode, ExpContext expContext, String operation) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.expContext = expContext;
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
        if (operation.equals("Plus")) {
            return leftNode.codeGeneration() + rightNode.codeGeneration() + "add\n";
        } else {
            return leftNode.codeGeneration() + rightNode.codeGeneration() + "sub\n";
        }
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("ExpNode: checkSemantics -> \t");
        //create the result
        ArrayList<String> res = new ArrayList<String>();

        //check semantics in the left and in the right exp

        res.addAll(leftNode.checkSemantics(env));
        res.addAll(rightNode.checkSemantics(env));

        return res;
    }
}
