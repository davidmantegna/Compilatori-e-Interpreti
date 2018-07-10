package nodes;

import exceptions.MultipleIDException;
import exceptions.TypeException;
import symboltable.SymbolTable;
import type.FunType;
import type.IType;
import type.ObjectType;
import type.VoidType;

import java.util.ArrayList;

public class LetNode implements INode {
    private ArrayList<INode> declarationArrayList;


    public LetNode(ArrayList<INode> declarationArrayList) {
        this.declarationArrayList = declarationArrayList;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        System.out.print("LetNode: checkSemantics -> \n");
        ArrayList<String> res = new ArrayList<>();

        //CheckSemantic nella lista di dichiarazioni
        if (declarationArrayList.size() > 0) {
            env.setOffset(-1); //TODO era -2

            //Checksemantic nei figli
            for (INode n : declarationArrayList) {
                // utile per le funzioni mutuamente ricorsive
                if (n instanceof FunNode) {
                    FunNode funNode = (FunNode) n;
                    ArrayList<IType> parameterTypeArrayList = new ArrayList<>();
                    for (ParameterNode parameterNode : funNode.parameterNodeArrayList) {
                        parameterTypeArrayList.add(parameterNode.getType());
                    }
                    if (funNode.returnType instanceof ObjectType) {
                        ObjectType objectType = (ObjectType) funNode.returnType;
                        res.addAll(objectType.updateClassType(env));
                    }
                    try {
                        env.processDeclaration(funNode.idFunzione, new FunType(parameterTypeArrayList, funNode.returnType), env.getOffset());
                        env.decreaseOffset();
                    } catch (MultipleIDException e) {
                        res.add("La funzione " + funNode.idFunzione + " è già stata dichiarata");
                    }
                } else {
                    res.addAll(n.checkSemantics(env));
                }
            }

            for (INode n : declarationArrayList) {
                if (n instanceof FunNode) {
                    res.addAll(n.checkSemantics(env));
                }
            }
        }

        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.print("LetNode: typeCheck ->\t");
        for (INode dec : declarationArrayList) {
            dec.typeCheck();
        }
        return new VoidType();
    }

    @Override
    public String codeGeneration() {

        StringBuilder declCode = new StringBuilder();
        for (INode dec : declarationArrayList)
            declCode.append(dec.codeGeneration());
        return declCode.toString();
    }


}
