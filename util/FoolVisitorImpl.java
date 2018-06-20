package util;

import type.IType;
import exceptions.OperatorException;
import exceptions.TypeException;
import nodes.*;
import parser.FOOLBaseVisitor;
import parser.FOOLLexer;
import parser.FOOLParser;
import parser.FOOLParser.LetContext;
import parser.FOOLParser.LetInExpContext;
import parser.FOOLParser.LetnestContext;
import parser.FOOLParser.SingleExpContext;

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
        LetInNode res;

        INode let = visit(letInExpContext.let());

        INode expStm;

        try {
            expStm = visit(letInExpContext.exp());
        } catch (NullPointerException e) {
            expStm = visit(letInExpContext.stms());
        }

        res = new LetInNode(let, expStm);
        return res;
    }

    @Override
    public INode visitClassExp(FOOLParser.ClassExpContext ctx) {
        return super.visitClassExp(ctx);
    }

    @Override
    public INode visitClassdec(FOOLParser.ClassdecContext ctx) {
        return super.visitClassdec(ctx);
    }

    @Override
    public INode visitLet(LetContext letContext) {
        System.out.print("visitLet -> \t");
        LetNode res;
        ArrayList<INode> declarations = new ArrayList<>();

        for (FOOLParser.DecContext dc : letContext.dec()) {
            declarations.add(visit(dc));
        }
        res = new LetNode(declarations, "visitLet");

        return res;
    }

    @Override
    public INode visitLetnest(LetnestContext letnestContext) {
        System.out.print("visitLetnest -> \t");
        LetNode res;
        ArrayList<INode> declarations = new ArrayList<>();

        for (FOOLParser.VarasmContext vc : letnestContext.varasm()) {
            declarations.add(visit(vc));
        }
        res = new LetNode(declarations, "visitLetnest");

        return res;
    }

    @Override
    public INode visitVardec(FOOLParser.VardecContext vardecContext) {
        System.out.print("visitVardec -> \t");
        return super.visitVardec(vardecContext);
    }

    @Override
    public INode visitVarasm(FOOLParser.VarasmContext varasmContext) {
        System.out.print("visitVarasm -> \t");
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
    public INode visitFun(FOOLParser.FunContext ctx) {
        return super.visitFun(ctx);
    }

    @Override
    public INode visitVarAssignment(FOOLParser.VarAssignmentContext ctx) {
        return super.visitVarAssignment(ctx);
    }

    @Override
    public INode visitFunDeclaration(FOOLParser.FunDeclarationContext ctx) {
        return super.visitFunDeclaration(ctx);
    }

    @Override
    public INode visitType(FOOLParser.TypeContext typeContext) {
        //tutti i tipi gestiti da TypeNode
        return new TypeNode(typeContext.getText());
    }

    @Override
    public INode visitExp(FOOLParser.ExpContext expContext) {
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
    public INode visitTerm(FOOLParser.TermContext termContext) {
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
    public INode visitFactor(FOOLParser.FactorContext factorContext) {
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
    public INode visitFuncall(FOOLParser.FuncallContext ctx) {
        return super.visitFuncall(ctx);
    }

    @Override
    public INode visitNewexp(FOOLParser.NewexpContext ctx) {
        return super.visitNewexp(ctx);
    }

    @Override
    public INode visitIntVal(FOOLParser.IntValContext intValContext) {
        System.out.print("visitIntVal -> \t");
        if (intValContext.MINUS() == null) {
            return new IntNode(Integer.parseInt(intValContext.INTEGER().getText()));
        } else {//gestiamo il caso di numeri negativi
            return new IntNode(-Integer.parseInt(intValContext.INTEGER().getText()));
        }
    }

    @Override
    public INode visitBoolVal(FOOLParser.BoolValContext boolValContext) {
        System.out.print("visitBoolVal -> \t");

        String text = boolValContext.getText().replace("!", "");

        if (boolValContext.NOT() == null) {
            return new BoolNode(Boolean.parseBoolean(boolValContext.getText()));
        } else {//gestiamo il caso di not
            return new NotNode(new BoolNode(Boolean.parseBoolean(boolValContext.getText())));
        }
    }

    @Override
    public INode visitBaseExp(FOOLParser.BaseExpContext baseExpContext) {
        System.out.print("visitBaseExp -> \t");
        return visit(baseExpContext.exp());
    }

    @Override
    public INode visitIfExp(FOOLParser.IfExpContext ifExpContext) {
        System.out.print("visitIfExp -> \t");
        INode cond = visit(ifExpContext.cond);
        INode then = visit(ifExpContext.thenBranch);
        INode el = visit(ifExpContext.elseBranch);

        return new IfNode(cond, then, el, ifExpContext);
    }

    @Override
    public INode visitVarExp(FOOLParser.VarExpContext varExpContext) {
        System.out.print("visitVarExp -> \t");
        Boolean isNeg = true;
        Boolean isNot = true;
        if (varExpContext.MINUS() == null) {
            isNeg = false;
        } else {
            isNot = false;
        }
        return new VarExpNode(varExpContext.ID().getText(), varExpContext, isNeg, isNot);
    }

    @Override
    public INode visitFunctionCall(FOOLParser.FunctionCallContext ctx) {
        return super.visitFunctionCall(ctx);
    }

    @Override
    public INode visitMethodExp(FOOLParser.MethodExpContext ctx) {
        return super.visitMethodExp(ctx);
    }

    @Override
    public INode visitNewFunction(FOOLParser.NewFunctionContext ctx) {
        return super.visitNewFunction(ctx);
    }

    @Override
    public INode visitStms(FOOLParser.StmsContext stmsContext) {
        System.out.print("visitStms -> \t");

        StmsNode res;

        ArrayList<INode> statements = new ArrayList<INode>();

        for (FOOLParser.StmContext stm : stmsContext.stm()) {
            statements.add(visit(stm));
        }
        res = new StmsNode(statements);

        return res;
    }

    @Override
    public INode visitStmAssignment(FOOLParser.StmAssignmentContext stmAsmContext) {
        System.out.print("visitStmAssignment -> \t");
        INode expNode = visit(stmAsmContext.exp());

        return new StmAsmNode(stmAsmContext.ID().getText(), expNode, stmAsmContext);
    }

    @Override
    public INode visitStmIfExp(FOOLParser.StmIfExpContext stmIfExpContext) {
        System.out.print("visitStmIfExp -> \t");
        INode cond = visit(stmIfExpContext.cond);
        INode stmThen = visit(stmIfExpContext.thenBranch);
        INode stmElse = visit(stmIfExpContext.elseBranch);

        return new StmIfExpNode(cond, stmThen, stmElse, stmIfExpContext);
    }

    @Override
    public INode visitMethod(FOOLParser.MethodContext ctx) {
        return super.visitMethod(ctx);
    }
}
