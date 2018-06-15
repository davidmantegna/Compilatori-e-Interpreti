package ast;

import Type.IType;
import exceptions.MultipleIDException;
import exceptions.TypeException;
import parserNew.FOOLParser.VarasmContext;
import util.Semantic.SymbolTable;

import java.util.ArrayList;

public class VarAsmNode implements INode{
    private String id;
    private IType type;
    private INode exp;
    private VarasmContext ctx;

    public VarAsmNode(String id, IType type, INode exp, VarasmContext ctx) {
        this.id = id;
        this.type = type;
        this.exp = exp;
        this.ctx = ctx;
    }

    public String getId(){
        return id;
    }

    @Override
    public String toPrint(String indent) {
        return null;
    }

    @Override
    public IType typeCheck() throws TypeException {
        //TODO aggiungere controllo per sottotipaggio quando si implementeranno le classi
        return type;
    }

    @Override
    public String codeGeneration() {
        return exp.codeGeneration();
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("VarAsmNode: checkSemantics -> \t");
        ArrayList<String> result = new ArrayList<>();

        // TODO aggiungere controllo per instanziazione di un nuovo oggetto

        result.addAll(exp.checkSemantics(env));

        try{
            env.processDeclaration(id,type,env.getOffset());
            //Perché decrementa offset nella symbolTable?!
            System.out.println("Decremento offset nella symbolTable...non so perché...");
            env.decreaseOffset();
            System.out.println("Ho decrementato offset nella symbolTable...a cazzo di cane...");
        }catch(MultipleIDException e){
            result.add(e.getMessage());
        }

        return result;
    }
}
