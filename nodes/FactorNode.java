package nodes;

import codegen.VM.Label;
import exceptions.TypeException;
import parser.FOOLParser.FactorContext;
import symboltable.SymbolTable;
import type.*;

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


        if (leftNode instanceof FunCallNode || rightNode instanceof FunCallNode) {// TODO non è possibile inserire delle funzioni con Return Void come condizione
            if (leftType instanceof VoidType || rightType instanceof VoidType) {
                throw new TypeException("Tipo incompatibile per " + operator + ". Non è possbile inserire delle funzioni con Return Void come condizione", factorContext);
            }
        }

        if (leftType instanceof ObjectType || rightType instanceof ObjectType) {// TODO non è possibile inserire degli oggetti come condizione
            throw new TypeException("Tipo incompatibile per " + operator + ". Non è possbile inserire degli Oggetti nella condizione", factorContext);
        }


        if (operator.equals("And") || (operator.equals("Or"))) {
            if (!leftType.isSubType(new BoolType()) || !rightType.isSubType(new BoolType())) {
                throw new TypeException("Tipo incompatibile per " + operator + ". È richiesto un booleano.", factorContext);
            }
        } else if (operator.equals("Eq")) {
            if (!leftType.isSubType(rightType)) {
                throw new TypeException("Tipo incompatibile per " + operator + ". È richiesto il medesimo tipo (int or bool).\nTipi passati: (op1: " + leftType.toPrint() + " op2: " + rightType.toPrint() + ")", factorContext);
            }
        } else {
            if (!leftType.isSubType(new IntType()) || !rightType.isSubType(new IntType())) {
                throw new TypeException("Tipo incompatibile per " + operator + ". È richiesto un intero.", factorContext);
            }
        }
        System.out.println("\nTipi compatibili per '" + operator + "'. (op1 = " + leftType.toPrint() + " op2 = " + rightType.toPrint() + ")\n"); // TODO al momento vengono accettati i seguenti tipi: int, bool, ID

        return new BoolType();
    }

    @Override
    public String codeGeneration() {
        String labelThen = Label.nuovaLabelString("Then");
        String exit = Label.nuovaLabelString("Exit");
        String codeGen = "";
        String condition = "";

        switch (operator) {
            case "And":
                codeGen = leftNode.codeGeneration()
                        + "push 0\n"
                        + "beq " + labelThen + "\n\n"
                        + rightNode.codeGeneration()
                        + "push 0\n"
                        + "beq " + labelThen + "\n\n"
                        + "push 1\n"
                        + "b " + exit + "\n\n"
                        + labelThen + ":\n"
                        + "push 0\n"
                        + exit + ":\n";
                break;
            case "Or":
                codeGen = leftNode.codeGeneration()
                        + "push 1\n"
                        + "beq " + labelThen + "\n\n"
                        + rightNode.codeGeneration()
                        + "push 1\n"
                        + "beq " + labelThen + "\n\n"
                        + "push 0\n"
                        + "b " + exit + "\n\n"
                        + labelThen + ":\n"
                        + "push 1\n"
                        + exit + ":\n";
                break;
            case "Eq":
                condition = "beq " + labelThen + "\n";
                break;
            case "GreaterEq":
                condition = "bge " + labelThen + "\n";
                break;
            case "LessEq":
                condition = "ble " + labelThen + "\n";
                break;
            case "Greater":
                condition = "bgt " + labelThen + "\n";
                break;
            case "Less":
                condition = "blt " + labelThen + "\n";
                break;
        }

        if (!operator.equals("And") && !operator.equals("Or")) {
            codeGen = rightNode.codeGeneration()
                    + leftNode.codeGeneration()
                    + condition
                    + "push 0\n"
                    + "b " + exit + "\n\n"
                    + labelThen + ":\n"
                    + "push 1\n\n"
                    + exit + ":\n";
        }
        return codeGen;
    }
}
