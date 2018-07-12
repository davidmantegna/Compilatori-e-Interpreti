import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.File;
import java.io.IOException;

public class TestCase {
    public static void main(String[] args) {
        try {
            System.out.println("Rilevazione Input...\n");
            String fileName = "prova";


            final File folder = new File("test/testCase");
            listFilesForFolder(folder, "10");

            String foolFileName = fileName + ".fool";

            CharStream input = CharStreams.fromFileName(foolFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listFilesForFolder(final File folder, String fileNumber) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry, fileNumber);
            } else {
                System.out.println(fileEntry.getName());
                if (fileEntry.getName().contains(fileNumber)) {
                    return fileEntry.getName();
                }
            }
        }
        return "";
    }
}
