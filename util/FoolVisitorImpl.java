package util;

import Type.IType;
import ast.*;
import exceptions.OperatorException;
import exceptions.TypeException;
import parserNew.FOOLBaseVisitor;
import parserNew.FOOLLexer;
import parserNew.FOOLParser;

import java.util.ArrayList;


public class FoolVisitorImpl extends FOOLBaseVisitor<INode> {
    @Override
    public INode visitSingleExp(FOOLParser.SingleExpContext singleExpContext) {
        System.out.print("visitSingleExp -> \t");
        //serve per la singola espressione print *qualcosa*
        return new SingleExpNode(visit(singleExpContext.exp()));
    }

    @Override
    public INode visitLetInExp(FOOLParser.LetInExpContext letInExpContext) {
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
    public INode visitClassExp(FOOLParser.ClassExpContext classExpContext) {
        return super.visitClassExp(classExpContext);
    }

    @Override
    public INode visitLet(FOOLParser.LetContext letContext) {
        System.out.print("visitLet -> \t");
        //resulting node of the right type
        LetNode res;

        //list of declarationsArrayList in @res
        ArrayList<INode> declarations = new ArrayList<INode>();

        //visit all nodes corresponding to declarationsArrayList inside the let context and store them in @declarationsArrayList
        //notice that the letInExpContext.let().dec() method returns a list, this is because of the use of * or + in the grammar
        //antlr detects this is a group and therefore returns a list
        for (FOOLParser.DecContext dc : letContext.dec()) {
            declarations.add(visit(dc));
        }
        res = new LetNode(declarations);

        return res;
    }

    @Override
    public INode visitIn(FOOLParser.InContext inContext) {
        System.out.print("visitIn -> \t");
        InNode res;
        try {
            res = new InNode(visit(inContext.exp()), "exp");
        } catch (NullPointerException e) {
            res = new InNode(visit(inContext.stms()), "stms");
        }

        return res;
    }

    @Override
    public INode visitLetnest(FOOLParser.LetnestContext letnestContext) {
        System.out.print("visitLetNest -> \t");
        return super.visitLetnest(letnestContext);
    }


    @Override
    public INode visitVardec(FOOLParser.VardecContext vardecContext) {
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
    public INode visitFun(FOOLParser.FunContext funContext) {
        return super.visitFun(funContext);
    }

    @Override
    public INode visitVarAssignment(FOOLParser.VarAssignmentContext varAssignmentContext) {
        return super.visitVarAssignment(varAssignmentContext);
    }

    @Override
    public INode visitFunDeclaration(FOOLParser.FunDeclarationContext funDeclarationContext) {
        return super.visitFunDeclaration(funDeclarationContext);
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
    public INode visitFuncall(FOOLParser.FuncallContext funcallContext) {
        System.out.print("visitFuncall -> \t");
        return super.visitFuncall(funcallContext);
    }

    @Override
    public INode visitNewexp(FOOLParser.NewexpContext newexpContext) {
        System.out.print("visitNewexp -> \t");
        return super.visitNewexp(newexpContext);
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
        return new BoolNode(Boolean.parseBoolean(boolValContext.getText()));
    }

    @Override
    public INode visitBaseExp(FOOLParser.BaseExpContext baseExpContext) {
        return super.visitBaseExp(baseExpContext);
    }

    @Override
    public INode visitIfExp(FOOLParser.IfExpContext ifExpContext) {

        INode cond = visit(ifExpContext.cond);
        INode then = visit(ifExpContext.thenBranch);
        INode el = visit(ifExpContext.elseBranch);

        return new IfNode(cond, then, el, ifExpContext);
    }

    @Override
    public INode visitVarExp(FOOLParser.VarExpContext varExpContext) {

        if (varExpContext.MINUS() == null) {
            return new VarExpNode(varExpContext.ID().getText(),varExpContext,false);
        } else {
            return new VarExpNode(varExpContext.ID().getText(),varExpContext,true);
        }
    }

    @Override
    public INode visitThisExp(FOOLParser.ThisExpContext ctx) {
        return super.visitThisExp(ctx);
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

    @Override
    public INode visitClassdec(FOOLParser.ClassdecContext ctx) {
        return super.visitClassdec(ctx);
    }
}
