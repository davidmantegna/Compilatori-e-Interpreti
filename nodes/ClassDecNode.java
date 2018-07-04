package nodes;

import exceptions.MultipleIDException;
import exceptions.TypeException;
import exceptions.UndeclaredIDException;
import type.ClassType;
import type.FunType;
import type.IType;
import util.Semantic.Field;
import util.Semantic.Method;
import util.Semantic.SymbolTable;
import util.Semantic.SymbolTableEntry;

import java.util.ArrayList;
import java.util.HashMap;

public class ClassDecNode implements INode {

    private ArrayList<ClassNode> classDeclarationsArrayList;
    private LetInNode letInNode;

    public ClassDecNode(ArrayList<ClassNode> classDeclarationsArrayList, LetInNode letInNode) {
        this.classDeclarationsArrayList = classDeclarationsArrayList;
        this.letInNode = letInNode;
    }

    @Override
    public ArrayList<String> checkSemantics(SymbolTable env) {
        ArrayList<String> res = new ArrayList<>();

        // crea un nuovo livello di scope
        HashMap<String, SymbolTableEntry> hm = new HashMap<>();
        env.pushHashMap(hm);

        for (ClassNode classNode : classDeclarationsArrayList) {
            try {
                // controllo se la classe è già stata definita
                // se non è dichiarata più volte inserisco nella SymbolTable solo info relative a idClass e idSuperClass
                ClassType classType = new ClassType(classNode.getIdClass(), new ClassType(classNode.getIdSuperClass()), new ArrayList<>(), new ArrayList<>());
                env.processDeclaration(classNode.getIdClass(), classType, 0);
            } catch (MultipleIDException e) {
                res.add("La classe '" + classNode.getIdClass() + "' è dichiarata più volte\n");
            }
        }

        //checkSemantic su ogni classe dichiarata
        for (ClassNode classNode : classDeclarationsArrayList) {
            res.addAll(classNode.checkSemantics(env));
        }

        //checksemantic sul letInNode
        res.addAll(letInNode.checkSemantics(env));

        //esco dal livello di scope
        env.popHashMap();

        return res;
    }

    @Override
    public IType typeCheck() throws TypeException {
        System.out.print("ClassDecNode: typeCheck ->\t");
        for (ClassNode classNode : classDeclarationsArrayList) {
            classNode.typeCheck();
        }

        return letInNode.typeCheck();
    }

    @Override
    public String codeGeneration() {
        return null;
    }
}
