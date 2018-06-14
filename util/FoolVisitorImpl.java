package util;

import ast.BoolNode;
import ast.INode;
import ast.IntNode;
import ast.SingleExpNode;
import parserNew.FOOLBaseVisitor;
import parserNew.FOOLParser.SingleExpContext;
import parserNew.FOOLParser.IntValContext;
import parserNew.FOOLParser.BoolValContext;


public class FoolVisitorImpl extends FOOLBaseVisitor<INode> {
    @Override
    public INode visitSingleExp(SingleExpContext singleExpContext) {
        //serve per la singola espressione print *qualcosa*
        return new SingleExpNode(visit(singleExpContext.exp()));
    }

    @Override
    public INode visitIntVal(IntValContext intValContext) {
        if (intValContext.MINUS() == null) {
            return new IntNode(Integer.parseInt(intValContext.INTEGER().getText()));
        } else {//gestiamo il caso di numeri negativi
            return new IntNode(-Integer.parseInt(intValContext.INTEGER().getText()));
        }
    }

    @Override
    public INode visitBoolVal(BoolValContext boolValContext) {
        return new BoolNode(Boolean.parseBoolean(boolValContext.getText()));
    }
}
