package util;

import Type.IType;
import ast.*;
import exceptions.OperatorException;
import exceptions.TypeException;
import parserNew.FOOLBaseVisitor;
import parserNew.FOOLLexer;
import parserNew.FOOLParser.*;

import java.util.ArrayList;


public class FoolVisitorImpl extends FOOLBaseVisitor<INode> {


    @Override
    public INode visitSingleExp(SingleExpContext singleExpContext) {
        System.out.print("visitSingleExp -> \t");
        //serve per la singola espressione print *qualcosa*
        return new SingleExpNode(visit(singleExpContext.exp()));
    }

    @Override
    public INode visitLetInExp(LetInExpContext letInExpContext) {
        System.out.print("visitLetInExp -> \t");

        //resulting node of the right type
        LetInNode res;

        //visit let context
        INode let = visit(letInExpContext.let());
        //visit exp | stms context
        INode in = visit(letInExpContext.in());

        res = new LetInNode(let, in);

        return res;
    }

    @Override
    public INode visitLet(LetContext letContext) {
        System.out.print("visitLet -> \t");
        //resulting node of the right type
        LetNode res;

        //list of declarationsArrayList in @res
        ArrayList<INode> declarations = new ArrayList<INode>();

        //visit all nodes corresponding to declarationsArrayList inside the let context and store them in @declarationsArrayList
        //notice that the letInExpContext.let().dec() method returns a list, this is because of the use of * or + in the grammar
        //antlr detects this is a group and therefore returns a list
        for (DecContext dc : letContext.dec()) {
            declarations.add(visit(dc));
        }
        res = new LetNode(declarations);

        return res;
    }

    @Override
    public INode visitIn(InContext inContext) {
        System.out.print("visitIn -> \t");
        InNode res;

        res = new InNode(visit(inContext.exp()), "exp");

        if (res==null){
            res = new InNode(visit(inContext.stms()), "stms");
        }

        return res;
    }

    @Override
    public INode visitLetnest(LetnestContext letnestContext) {
        System.out.print("visitLetNest -> \t");
        return super.visitLetnest(letnestContext);
    }

    @Override
    public INode visitVardec(VardecContext vardecContext) {
        return super.visitVardec(vardecContext);
    }

    @Override
    public INode visitVarasm(VarasmContext varasmContext) {
        System.out.println("visitVarasm -> \t");
        //var declaration + assignment
        IType type;
        try {
            type = visit(varasmContext.vardec().type()).typeCheck();
        } catch (TypeException e) {
            return null;
        }

        INode expNode = visit(varasmContext.exp());

        return new VarAsmNode(varasmContext.vardec().ID().getText(), type, expNode, varasmContext);
    }

    @Override
    public INode visitFun(FunContext funContext) {
        return super.visitFun(funContext);
    }

    @Override
    public INode visitVarAssignment(VarAssignmentContext varAssignmentContext) {
        return super.visitVarAssignment(varAssignmentContext);
    }

    @Override
    public INode visitFunDeclaration(FunDeclarationContext funDeclarationContext) {
        return super.visitFunDeclaration(funDeclarationContext);
    }

    @Override
    public INode visitType(TypeContext typeContext) {
        //tutti i tipi gestiti da TypeNode
        return new TypeNode(typeContext.getText());
    }

    @Override
    public INode visitExp(ExpContext expContext) {
        System.out.print("visitExp -> \t");
        //check whether this is a simple or binary expression
        //notice here the necessity of having named elements in the grammar
        if (expContext.right == null) {
            //it is a simple expression
            return visit(expContext.left);
        } else {
            //it is a binary expression, you should visit left and right
            if (expContext.operator.getType() == FOOLLexer.PLUS) {
                return new ExpNode(visit(expContext.left), visit(expContext.right), expContext, "Plus");
            } else {
                return new ExpNode(visit(expContext.left), visit(expContext.right), expContext, "Minus");
            }
        }
    }

    @Override
    public INode visitTerm(TermContext termContext) {
        System.out.print("visitTerm -> \t");
        //check whether this is a simple or binary expression
        //notice here the necessity of having named elements in the grammar
        if (termContext.right == null) {
            //it is a simple expression
            return visit(termContext.left);
        } else {
            //it is a binary expression, you should visit left and right
            if (termContext.operator.getType() == FOOLLexer.TIMES) {
                return new TermNode(visit(termContext.left), visit(termContext.right), termContext, "Times");

            } else {
                return new TermNode(visit(termContext.left), visit(termContext.right), termContext, "Div");
            }
        }
    }

    @Override
    public INode visitFactor(FactorContext factorContext) {
        System.out.print("visitFactor -> \t");
        //check whether this is a simple or binary expression
        //notice here the necessity of having named elements in the grammar
        if (factorContext.right == null) {
            //it is a simple expression
            return visit(factorContext.left);
        } else {
            String operator = "";
            switch (factorContext.operator.getType()) {
                case FOOLLexer.AND:
                    operator = "And";
                    break;
                case FOOLLexer.OR:
                    operator = "Or";
                    break;
                case FOOLLexer.EQ:
                    operator = "Eq";
                    break;
                case FOOLLexer.GEQ:
                    operator = "GreaterEq";
                    break;
                case FOOLLexer.LEQ:
                    operator = "LessEq";
                    break;
                case FOOLLexer.GREATER:
                    operator = "Greater";
                    break;
                case FOOLLexer.LESS:
                    operator = "Less";
                    break;
                default:
                    try {
                        throw new OperatorException(factorContext.operator.getText());
                    } catch (OperatorException e) {
                        System.out.println(e.getMessage() + "\n\n");
                        System.out.println("stack" + e.getStackTrace());
                    }
                    break;
            }
            return new FactorNode(visit(factorContext.left), visit(factorContext.right), factorContext, operator);
        }
    }

    @Override
    public INode visitIntVal(IntValContext intValContext) {
        System.out.print("visitIntVal -> \t");
        if (intValContext.MINUS() == null) {
            return new IntNode(Integer.parseInt(intValContext.INTEGER().getText()));
        } else {//gestiamo il caso di numeri negativi
            return new IntNode(-Integer.parseInt(intValContext.INTEGER().getText()));
        }
    }

    @Override
    public INode visitBoolVal(BoolValContext boolValContext) {
        System.out.print("visitBoolVal -> \t");
        return new BoolNode(Boolean.parseBoolean(boolValContext.getText()));
    }

    @Override
    public INode visitBaseExp(BaseExpContext baseExpContext) {
        return super.visitBaseExp(baseExpContext);
    }

    @Override
    public INode visitIfExp(IfExpContext ifExpContext) {
        return super.visitIfExp(ifExpContext);
    }

    @Override
    public INode visitVarExp(VarExpContext varExpContext) {
        return super.visitVarExp(varExpContext);
    }

    @Override
    public INode visitFunExp(FunExpContext funExpContext) {
        return super.visitFunExp(funExpContext);
    }

    @Override
    public INode visitStms(StmsContext stmsContext) {
        return super.visitStms(stmsContext);
    }

    @Override
    public INode visitStmAssignment(StmAssignmentContext stmAssignmentContext) {
        return super.visitStmAssignment(stmAssignmentContext);
    }

    @Override
    public INode visitStmIfExp(StmIfExpContext stmIfExpContext) {
        return super.visitStmIfExp(stmIfExpContext);
    }
}
