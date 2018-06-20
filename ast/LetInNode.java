package ast;


import Type.IType;
import Type.VoidType;
import exceptions.TypeException;
import util.Semantic.SymbolTable;
import util.Semantic.SymbolTableEntry;
import util.VM.FunctionCode;


import java.util.ArrayList;
import java.util.HashMap;

public class LetInNode implements INode {
    private INode let;
    private INode in;

    public LetInNode(INode let, INode in) {
        this.let = let;
        this.in = in;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("LetInNode: checkSemantics -> \t");

        ArrayList<String> res = new ArrayList<String>();

        HashMap<String, SymbolTableEntry> hashMap = new HashMap<>();

        //entro in un nuovo livello di scope
        env.pushHashMap(hashMap);

        //parte Let
        res.addAll(let.checkSemantics(env));

        //Parte In
        res.addAll(in.checkSemantics(env));

        //lascio il vecchio scope
        env.popHashMap();

        return res;
    }


    @Override
    public IType typeCheck() throws TypeException {
        System.out.print("LetInNode: typeCheck -> \t");
        //parte let
        let.typeCheck();
        //parte in
        in.typeCheck();
        return new VoidType();
    }

    @Override
    public String codeGeneration() {
        return "push 0\n" +
                let.codeGeneration() +
                in.codeGeneration() + "halt\n" +
                FunctionCode.getFunctionsCode();
    }

}
