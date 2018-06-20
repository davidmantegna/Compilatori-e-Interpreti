package nodes;

import type.IType;
import exceptions.TypeException;
import util.Semantic.SymbolTable;
import util.Semantic.SymbolTableEntry;
import util.VM.FunctionCode;

import java.util.ArrayList;
import java.util.HashMap;

public class LetInNode implements INode {

    private INode let;
    private INode stmExp;

    public LetInNode(INode let, INode stmExp) {
        this.let = let;
        this.stmExp = stmExp;
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
        return "push 0\n" +
                let.codeGeneration() +
                stmExp.codeGeneration() + "halt\n" +
                FunctionCode.getFunctionsCode();
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("LetInNode: checkSemantics -> \n\t" + env.toString() + "\n");
        ArrayList<String> res = new ArrayList<>();

        HashMap<String, SymbolTableEntry> hashMap = new HashMap<>();
        //entro in un nuovo livello di scope
        env.pushHashMap(hashMap);
        //parte Let
        res.addAll(let.checkSemantics(env));

        //Parte stmexp
        res.addAll(stmExp.checkSemantics(env));

        //lascio il vecchio scope
        env.popHashMap();

        return res;
    }
}
