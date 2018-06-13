package util;

import ast.INode;
import ast.SingleExpNode;
import parserNew.FOOLBaseVisitor;
import parserNew.FOOLParser.SingleExpContext;


public class FOOLVisitorImpl extends FOOLBaseVisitor<INode> {
    @Override
    public INode visitSingleExp(SingleExpContext singleExpContext) {

        //serve per la singola espressione print *qualcosa*
        return new SingleExpNode(visit(singleExpContext.exp()));
    }
}
