package ast;

import Type.BoolType;
import Type.IType;
import Type.IntType;
import exceptions.TypeException;
import parserNew.FOOLParser.FactorContext;
import util.Semantic.SymbolTable;
import util.VM.Label;

import java.util.ArrayList;

public class FactorNode implements INode {
    private INode leftNode;
    private INode rightNode;
    private FactorContext factorContext;
    private String operator; // utilizzato per distinguere tra addizione e sottrazione

    public FactorNode(INode leftNode, INode rightNode, FactorContext factorContext, String operator) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.factorContext = factorContext;
        this.operator = operator;
    }

    @Override
    public String toPrint(String indent) {
        return indent + operator + "\n"
                + leftNode.toPrint(indent + "\t")
                + rightNode.toPrint(indent + "\t");
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.println("FactorNode: typeCheck ->\t");

        IType leftType = leftNode.typeCheck();
        IType rightType = rightNode.typeCheck();

        if(operator.equals("And")||(operator.equals("Or"))) {
            if (!leftType.isSubType(new BoolType()) || !rightType.isSubType(new BoolType())) {
                throw new TypeException("Tipo incompatibile per " + operator + ". È richiesto un booleano.", factorContext);
            }
        }
        else {
            if (!leftType.isSubType(new IntType()) || !rightType.isSubType(new IntType())) {
                throw new TypeException("Tipo incompatibile per " + operator + ". È richiesto un intero.", factorContext);
            }
        }

        return new BoolType();
    }

    @Override
    public String codeGeneration() {
        String label = Label.nuovaLabel();
        String exit = Label.nuovaLabel();
        String codeGen = "";

        switch (operator) {
            case "And":
                codeGen = leftNode.codeGeneration()
                        + "push 0\n"
                        + "beq " + label + "\n"
                        + rightNode.codeGeneration()
                        + "push 0\n"
                        + "beq " + label + "\n"
                        + "push 1\n"
                        + "b " + exit + "\n"
                        + label + ":\n"
                        + "push 0\n"
                        + exit + ":\n";
                break;
            case "Or":
                codeGen = leftNode.codeGeneration()
                        + "push 1\n"
                        + "beq " + label + "\n"
                        + rightNode.codeGeneration()
                        + "push 1\n"
                        + "beq " + label + "\n"
                        + "push 0\n"
                        + "b " + exit + "\n"
                        + label + ":\n"
                        + "push 1\n"
                        + exit + ":\n";
                break;
            case "Eq":
                codeGen = leftNode.codeGeneration() +
                        rightNode.codeGeneration() +
                        "beq " + label + "\n" +
                        "push 0\n" +
                        "b " + exit + "\n" +
                        label + ":\n" +
                        "push 1\n" +
                        exit + ":\n";
                break;
            case "GreaterEq":
                codeGen = rightNode.codeGeneration() +
                        leftNode.codeGeneration() +
                        "bleq " + label + "\n" +
                        "push 0\n" +
                        "b " + exit + "\n" +
                        label + ":\n" +
                        "push 1\n" +
                        exit + ":\n";
                break;
            case "LessEq":
                codeGen = leftNode.codeGeneration() +
                        rightNode.codeGeneration() +
                        "bleq " + label + "\n" +
                        "push 0\n" +
                        "b " + exit + "\n" +
                        label + ":\n" +
                        "push 1\n" +
                        exit + ":\n";
                break;
            case "Greater":
                codeGen = rightNode.codeGeneration() +
                        "push 1\n" +
                        "add\n" +
                        leftNode.codeGeneration() +
                        "bleq " + label + "\n" +
                        "push 0\n" +
                        "b " + exit + "\n" +
                        label + ":\n" +
                        "push 1\n" +
                        exit + ":\n";
                break;
            case "Less":
                codeGen = leftNode.codeGeneration() +
                        "push 1\n" +
                        "add\n" +
                        rightNode.codeGeneration() +
                        "bleq " + label + "\n" +
                        "push 0\n" +
                        "b " + exit + "\n" +
                        label + ":\n" +
                        "push 1\n" +
                        exit + ":\n";
                break;
            default:
                codeGen = "default";
                break;
        }
        return codeGen;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("FactorNode: checkSemantics -> \t");
        //create the result
        ArrayList<String> res = new ArrayList<String>();

        //check semantics in the left and in the right exp

        res.addAll(leftNode.checkSemantics(env));
        res.addAll(rightNode.checkSemantics(env));

        return res;
    }
}
