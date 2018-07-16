package com.fileOperations;

import com.common.StatementResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Ref:: http://www.mkyong.com/java/how-to-write-to-file-in-java-bufferedwriter-example/
public class TextFileWriter {

    public static boolean writeTofile(List<StatementResult> statementResults,String fileName) {
        File f = new File(fileName);
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            if (!f.exists()) {
                f.createNewFile();

            }
            fw = new FileWriter(f.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            for (StatementResult result : statementResults) {
                bw.write(result.toString());
                bw.newLine();
            }
            System.out.println("DONE with file!!!" + f.getAbsoluteFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                System.out.println("Couldn't close file: " + f.getName());
            }
        }
        return true;
    }
}



