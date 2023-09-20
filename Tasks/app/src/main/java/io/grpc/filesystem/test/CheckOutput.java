package io.grpc.filesystem.test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/* 
 * Using this file you can check your output against the expected output file. For both the tasks 2 and 3 the answers are expected to be the same.
 * Please use the this file to compare your output against the expected output available at output/output.txt.
 * It's possible that the word arrangement with the same count in the text file provides incorrect results.
 * Please go over your complete output and try to rectify it.
 */

public class CheckOutput {
    public static void main(String[] args) throws IOException{
        String outputFilePath = args[0];
        BufferedReader bf1 = new BufferedReader(new FileReader("output/output.txt"));
        BufferedReader bf2 = new BufferedReader(new FileReader(outputFilePath));
        String l2 = bf2.readLine();
        String l1;
        boolean areSame = false;
        while((l1 = bf1.readLine()) != null || l2 != null){
            if (l1 == null && l2.isEmpty() || l1.isEmpty() && l2 == null){
                areSame = true;
            }else
            if(l1.equals(l2)){
                areSame = true;
            }else{
                areSame = false;
                break;
            }

            l2=bf2.readLine();

        }
        if (areSame){
            System.out.println("Both files are the same!");
        }
        else{
            System.out.println("These lines are not the same: "+l1+", "+l2);
        }
        bf1.close();
        bf2.close();

    }
}
