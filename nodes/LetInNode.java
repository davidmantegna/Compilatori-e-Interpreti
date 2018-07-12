package nodes;

import codegen.VM.FunctionCode;
import exceptions.TypeException;
import symboltable.SymbolTable;
import type.IType;

import java.util.ArrayList;

public class LetInNode implements INode {

    private INode let;
    private INode stmExp;

    public LetInNode(INode let, INode stmExp) {
        this.let = let;
        this.stmExp = stmExp;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        //System.out.print("LetInNode: checkSemantics -> \n");
        ArrayList<String> res = new ArrayList<>();

        // entro in un nuovo livello di scope
        env.entryNewScope();
        //parte Let
        res.addAll(let.checkSemantics(env));

        //Parte stmexp
        res.addAll(stmExp.checkSemantics(env));

        //lascio il vecchio scope
        env.exitLastScope();

        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        //parte let
        let.typeCheck();
        //parte in
        return stmExp.typeCheck();
    }

    @Override
    public String codeGeneration() {

        return let.codeGeneration()
                + "\n"
                + stmExp.codeGeneration() + "halt\n"
                + FunctionCode.getFunctionsCode();
    }
}
