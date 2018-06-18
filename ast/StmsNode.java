package ast;

import Type.IType;
import exceptions.MultipleIDException;
import exceptions.TypeException;
import parserNew.FOOLParser.StmsContext;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class StmsNode implements INode {

    //TODO da testare
    private String id;
    private IType type;
    private INode stm;
    private StmsContext ctx;

    public StmsNode(String i, IType t, INode s, StmsContext c) {
        this.id = i;
        this.type = t;
        this.stm = s;
        this.ctx = c;
    }

    public String getId(){
        return this.id;
    }

    @Override
    public String toPrint(String indent) {
        return indent + stm.toPrint(indent + "  ");
    }

    @Override
    public IType typeCheck() throws TypeException {
        return type;
    }

    @Override
    public String codeGeneration() {
        return stm.codeGeneration();
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env){

        System.out.print("StmsNode: checkSemantics -> \t");
        ArrayList<String> res = new ArrayList<>();

        res.addAll(stm.checkSemantics(env));


        try{
            env.processDeclaration(id, type, env.getOffset());
            env.decreaseOffset();

        }catch (MultipleIDException e){
            res.add(e.getMessage());
        }

        return res;

    }
}
