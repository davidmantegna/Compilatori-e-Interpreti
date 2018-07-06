package nodes;

import type.BoolType;
import type.IType;
import type.IntType;
import exceptions.TypeException;
import parser.FOOLParser.FactorContext;
import symboltable.SymbolTable;
import codegen.VM.Label;

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
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("FactorNode: checkSemantics -> \n" /*+ env.toString() + "\n"*/);
        //create the result
        ArrayList<String> res = new ArrayList<String>();

        //check semantics in the left and in the right exp

        res.addAll(leftNode.checkSemantics(env));
        res.addAll(rightNode.checkSemantics(env));

        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.println("FactorNode: typeCheck ->\t");

        IType leftType = leftNode.typeCheck();
        IType rightType = rightNode.typeCheck();

        if (operator.equals("And") || (operator.equals("Or"))) {
            if (!leftType.isSubType(new BoolType()) || !rightType.isSubType(new BoolType())) {
                throw new TypeException("Tipo incompatibile per " + operator + ". È richiesto un booleano.", factorContext);
            }
        } else {
            if (!leftType.isSubType(new IntType()) || !rightType.isSubType(new IntType())) {
                throw new TypeException("Tipo incompatibile per " + operator + ". È richiesto un intero.", factorContext);
            }
        }

        return new BoolType();
    }

    @Override
    public String codeGeneration() {
        String label = Label.nuovaLabel();
        String exit = Label.nuovaExitLabel();
        String codeGen = "";

        //TODO codegen AND, OR
        switch (operator) {
            case "And":
                codeGen = leftNode.codeGeneration()
                        + "push 0\n"
                        + "beq " + exit + "\n"
                        + rightNode.codeGeneration()
                        + "push 0\n"
                        + "beq " + exit + "\n"
                        + "b " + label + "\n\n"

                        + label + ":\n"
                        + "push 1\n\n"

                        + exit + ":\n"
                        + "push 0\n\n";
                break;
            case "Or":
                codeGen = leftNode.codeGeneration()
                        + "push 0\n"
                        + "beq " + label + "\n"
                        + "b " + exit + "\n\n"

                        + label + ":\n"
                        + rightNode.codeGeneration()
                        + "push 1\n"
                        + "beq " + exit + "\n"
                        + "push 0\n\n"

                        + exit + ":\n"
                        + "push 1\n\n";
                break;
            case "Eq":
                codeGen = leftNode.codeGeneration()
                        + rightNode.codeGeneration()
                        + "beq " + label + "\n"
                        + "b " + exit + "\n\n"
                        + label + ":\n"
                        + "push 1\n\n"
                        + exit + ":\n"
                        + "push 0\n\n";
                break;
            case "GreaterEq":
                codeGen = rightNode.codeGeneration()
                        + leftNode.codeGeneration()
                        + "bge " + label + "\n"
                        + "b " + exit + "\n\n"
                        + label + ":\n"
                        + "push 1\n\n"
                        + exit + ":\n"
                        + "push 0\n\n";
                break;
            case "LessEq":
                codeGen = rightNode.codeGeneration()
                        + leftNode.codeGeneration()
                        + "ble " + label + "\n"
                        + "b " + exit + "\n\n"
                        + label + ":\n"
                        + "push 1\n\n"
                        + exit + ":\n"
                        + "push 0\n\n";
                break;
            case "Greater":
                codeGen = rightNode.codeGeneration()
                        + leftNode.codeGeneration()
                        + "bgt " + label + "\n"
                        + "b " + exit + "\n\n"
                        + label + ":\n"
                        + "push 1\n\n"
                        + exit + ":\n"
                        + "push 0\n\n";
                break;
            case "Less":
                codeGen = rightNode.codeGeneration()
                        + leftNode.codeGeneration()
                        + "blt " + label + "\n"
                        + "b " + exit + "\n\n"
                        + label + ":\n"
                        + "push 1\n\n"
                        + exit + ":\n"
                        + "push 0\n\n";
                break;
            default:
                codeGen = "default";
                break;
        }
        return codeGen;
    }
}
