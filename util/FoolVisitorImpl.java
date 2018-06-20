package util;

import parser.FOOLParser.*;
import type.IType;
import exceptions.OperatorException;
import exceptions.TypeException;
import nodes.*;
import parser.FOOLBaseVisitor;
import parser.FOOLLexer;

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
    public INode visitClassExp(ClassExpContext classExpContext) {
        System.out.print("visitClassExp -> \t");
        return super.visitClassExp(classExpContext);
    }

    @Override
    public INode visitClassdec(ClassdecContext classdecContext) {
        System.out.print("visitClassdec -> \t");
        return super.visitClassdec(classdecContext);
    }

    @Override
    public INode visitLet(LetContext letContext) {
        System.out.print("visitLet -> \t");
        LetNode res;
        ArrayList<INode> declarations = new ArrayList<>();

        for (DecContext dc : letContext.dec()) {
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

        for (VarasmContext vc : letnestContext.varasm()) {
            declarations.add(visit(vc));
        }
        res = new LetNode(declarations, "visitLetnest");

        return res;
    }

    @Override
    public INode visitVardec(VardecContext vardecContext) {
        System.out.print("visitVardec -> \t");
        return super.visitVardec(vardecContext);
    }

    @Override
    public INode visitVarasm(VarasmContext varasmContext) {
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
    public INode visitFun(FunContext funContext) {
        System.out.print("visitFun -> \t");

        INode body;

        try {

            ArrayList<ParameterNode> params = new ArrayList<>();

            // add argument declarationsArrayList
            // we are getting a shortcut here by constructing directly the ParameterNode
            // this could be done differently by visiting instead the VardecContext
            for (int i = 0; i < funContext.vardec().size(); i++) {
                VardecContext vardecContext = funContext.vardec().get(i);
                params.add(new ParameterNode(vardecContext.ID().getText(), visit(vardecContext.type()).typeCheck(), i + 1, vardecContext));
            }

            // add body, create a list for the nested declarationsArrayList
            ArrayList<INode> declarations = new ArrayList<>();
            // check whether there are actually nested decs

            //TODO controllare tipo void
            System.out.println("\ncontrollo");
            if (funContext.exp() != null) {
                System.out.println("exp");
                body = visit(funContext.exp());

            } else {

                if (funContext.stms() != null) {
                    System.out.println("stms");
                    body = visit(funContext.stms());
                } else {

                    System.out.println("letnest");
                    // if there are visit each varasm and add it to the @innerDec list
                    for (VarasmContext varasms : funContext.letnest().varasm()) {
                        declarations.add(visit(varasms));
                    }

                    //visit letnest context
                    body = visit(funContext.letnest());

                }
            }

            return new FunNode(funContext.ID().getText(), visit(funContext.type()).typeCheck(), params, declarations, body, funContext);

        } catch (TypeException e) {
            return null;
        }

    }

    @Override
    public INode visitVarAssignment(VarAssignmentContext varAssignmentContext) {
        System.out.print("visitVarAssignment -> \t");
        return super.visitVarAssignment(varAssignmentContext);
    }

    @Override
    public INode visitFunDeclaration(FunDeclarationContext funDeclarationContext) {
        System.out.print("visitFunDeclaration -> \t");
        return super.visitFunDeclaration(funDeclarationContext);
    }

    @Override
    public INode visitType(TypeContext typeContext) {
        System.out.print("visitType -> \t");
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
    public INode visitFuncall(FuncallContext funcallContext) {
        System.out.print("visitFuncall -> \t");
        return super.visitFuncall(funcallContext);
    }

    @Override
    public INode visitNewexp(NewexpContext newexpContext) {
        System.out.print("visitNewexp -> \t");
        return super.visitNewexp(newexpContext);
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

        String text = boolValContext.getText().replace("!", "");

        if (boolValContext.NOT() == null) {
            return new BoolNode(Boolean.parseBoolean(boolValContext.getText()));
        } else {//gestiamo il caso di not
            return new NotNode(new BoolNode(Boolean.parseBoolean(boolValContext.getText())));
        }
    }

    @Override
    public INode visitBaseExp(BaseExpContext baseExpContext) {
        System.out.print("visitBaseExp -> \t");
        return visit(baseExpContext.exp());
    }

    @Override
    public INode visitIfExp(IfExpContext ifExpContext) {
        System.out.print("visitIfExp -> \t");
        INode cond = visit(ifExpContext.cond);
        INode then = visit(ifExpContext.thenBranch);
        INode el = visit(ifExpContext.elseBranch);

        return new IfNode(cond, then, el, ifExpContext);
    }

    @Override
    public INode visitVarExp(VarExpContext varExpContext) {
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
    public INode visitFunctionCall(FunctionCallContext functionCallContext) {
        System.out.print("visitFunctionCall -> \t");
        return super.visitFunctionCall(functionCallContext);
    }

    @Override
    public INode visitMethodExp(MethodExpContext methodExpContext) {
        System.out.print("visitMethodExp -> \t");
        return super.visitMethodExp(methodExpContext);
    }

    @Override
    public INode visitNewFunction(NewFunctionContext newFunctionContext) {
        System.out.print("visitNewFunction -> \t");
        return super.visitNewFunction(newFunctionContext);
    }

    @Override
    public INode visitStms(StmsContext stmsContext) {
        System.out.print("visitStms -> \t");

        StmsNode res;

        ArrayList<INode> statements = new ArrayList<INode>();

        for (StmContext stm : stmsContext.stm()) {
            statements.add(visit(stm));
        }
        res = new StmsNode(statements);

        return res;
    }

    @Override
    public INode visitStmAssignment(StmAssignmentContext stmAsmContext) {
        System.out.print("visitStmAssignment -> \t");
        INode expNode = visit(stmAsmContext.exp());

        return new StmAsmNode(stmAsmContext.ID().getText(), expNode, stmAsmContext);
    }

    @Override
    public INode visitStmIfExp(StmIfExpContext stmIfExpContext) {
        System.out.print("visitStmIfExp -> \t");
        INode cond = visit(stmIfExpContext.cond);
        INode stmThen = visit(stmIfExpContext.thenBranch);
        INode stmElse = visit(stmIfExpContext.elseBranch);

        return new StmIfExpNode(cond, stmThen, stmElse, stmIfExpContext);
    }

    @Override
    public INode visitMethod(MethodContext methodContext) {
        System.out.print("visitMethod -> \t");
        return super.visitMethod(methodContext);
    }
}
