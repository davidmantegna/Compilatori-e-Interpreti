package nodes;

import type.IType;
import type.IntType;
import exceptions.TypeException;
import parser.FOOLParser.TermContext;
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
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("TermNode: checkSemantics -> \n\t" + env.toString() + "\n");
        //create the result
        ArrayList<String> res = new ArrayList<String>();

        //check semantics in the left and in the right exp
        res.addAll(leftNode.checkSemantics(env));
        res.addAll(rightNode.checkSemantics(env));

        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.println("TermNode: typeCheck ->\t");
        if (!(leftNode.typeCheck().isSubType(new IntType()) && rightNode.typeCheck().isSubType(new IntType()))) {
            throw new TypeException("Tipo incompatibile per " + operation + ". È richiesto un intero.", termContext);
        }
        return new IntType();
    }

    @Override
    public String codeGeneration() {

        //TODO test codegen
        if (operation.equals("times")) {
            return leftNode.codeGeneration() + rightNode.codeGeneration() + "times\n";
        } else {
            return leftNode.codeGeneration() + rightNode.codeGeneration() + "div\n";
        }
    }
}
