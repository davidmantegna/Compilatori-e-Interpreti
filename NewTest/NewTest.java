package NewTest;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import parserNew.FOOLLexer;
import parserNew.FOOLParser;

import java.io.IOException;

public class NewTest {
    public static void main(String[] args) {

        try { //RILEVAZIONE INPUT
            System.out.println("Rilevazione Input...\n");
            String fileName = "prova.fool";
            CharStream input = CharStreams.fromFileName(fileName);

            System.out.println("Analisi Lessicale...\n");

            System.out.println("input: " + input);

            FOOLLexer lexer = new FOOLLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);


            //SIMPLISTIC BUT WRONG CHECK OF THE LEXER ERRORS
            if (lexer.lexicalErrors > 0) {
                System.out.println("The program was not in the right format. Exiting the compilation process now");
            }

            System.out.println("Analisi Sintattica...\n");
            FOOLParser parser = new FOOLParser(tokens);
            FOOLParser.ProgContext progContext = parser.prog(); //parser.prog riutilizzato


            /*if (parser.getNumberOfSyntaxErrors() > 0){
                throw new ParserException("Errori rilevati: " + parser.getNumberOfSyntaxErrors() + "\n");
            }*/

            ParseTree tree = progContext;

            System.out.println("Sto per visualizzare l'AST...\n");
            //show AST in console
            System.out.println(tree.toStringTree(parser) + "\n");

            System.out.println("Analisi Semantica...\n");

            /*FOOLVisitorImpl visitor = new FOOLVisitorImpl();

            INode ast = visitor.visit(progContext); //generazione AST

            SymbolTable env = new SymbolTable();
            ArrayList<String> err = ast.checkSemantics(env);


            if (err.size() > 0) {
                throw new SemanticException(err);
            }
            */
            System.out.println("--------------------------");

        } catch (IOException e) {
            System.out.println(e.getMessage() + "\n\n");
            System.out.println("stack" + e.getStackTrace());
        }
    }
}
