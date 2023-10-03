/*
 * the MapReduce functionality implemented in this program takes a single large text file to map i.e. split it into small chunks and then assign 1 to all the found words
 * then reduces by adding count values to each unique words
 */

package io.grpc.filesystem.task2;

import java.util.*;
import java.util.stream.Collectors;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map.Entry;

import io.grpc.filesystem.task2.Mapper;

public class MapReduce {

    public static String makeChunks(String inputFilePath) throws IOException {
        int count = 1;
        int size = 500;
        File f = new File(inputFilePath);
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String l = br.readLine();

            while (l != null) {
                File newFile = new File(f.getParent() + "/temp", "chunk"
                        + String.format("%03d", count++) + ".txt");
                try (OutputStream out = new BufferedOutputStream(new FileOutputStream(newFile))) {
                    int fileSize = 0;
                    while (l != null) {
                        byte[] bytes = (l + System.lineSeparator()).getBytes(Charset.defaultCharset());
                        if (fileSize + bytes.length > size)
                            break;
                        out.write(bytes);
                        fileSize += bytes.length;
                        l = br.readLine();
                    }
                }
            }
        }
        return f.getParent() + "/temp";

    }

    /**
     * @param inputfilepath
     * @throws IOException
     */
    public static void map(String inputfilepath) throws IOException {

        /*
         * Insert your code here
         * Take a chunk and filter words (you could use "\\p{Punct}" for filtering punctuations and "^[a-zA-Z0-9]"
         * together for filtering the words), then split the sentences to take out words and assign "1" as the initial count.
         * Use the given mapper class to create the unsorted key-value pair.
         * Save the map output in a file named "map-chunk001", for example, in folder
         * path input/temp/map
         */

        System.out.println("mapping... " + inputfilepath);

        //create output file path
        int lastIndex = inputfilepath.lastIndexOf("/");
        String outputfilepath = inputfilepath.substring(0, lastIndex) + "/map" + inputfilepath.substring(lastIndex).replaceAll(".txt", "-map.txt");

        //read chunk files.
        String chunk = "";
        String line;
        FileReader fileReader = new FileReader(inputfilepath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        //read each line of the chunk file to construct the chunk string.
        while ((line = bufferedReader.readLine()) != null) {
            chunk = chunk + line + "\n";
        }

        //remove all punctuation, convert all words to lower case and trim spaces.
        String chunkClean = chunk.replaceAll("\\p{Punct}", "").toLowerCase().trim();

        //create a list of words from the clean chunk.
        String[] words = chunkClean.split("\\s+");

        //write each word with initial frequency 1 to text file in output folder
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputfilepath));

        for (String word : words) {
            outputWriter.write(word + ":1" + "\n");
        }
        outputWriter.close();

        System.out.println("chunk mapped and saved to: " + outputfilepath);

    }

    /**
     * @param inputfilepath
     * @param outputfilepath
     * @return
     * @throws IOException
     */
    public static void reduce(String inputfilepath, String outputfilepath) throws IOException {

        /*
         * Insert your code here
         * Take all the files in the map folder and reduce them to one file that shows
         * unique words with their counts as "the:64", for example.
         * Save the output of reduce function as output-task2.txt
         */

        System.out.println("reducing...");

        ArrayList<String> wordsList = new ArrayList<>();
        Map<String, Integer> wordFrequencies = new HashMap<>();
        inputfilepath = inputfilepath + "/map";

        File dir = new File(inputfilepath);

        //read all chunk-map files and add words into wordsList
        for (File file : dir.listFiles()) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(":");
                String word = parts[0];
                int count = Integer.parseInt(parts[1]);
                wordsList.add(word);
            }
        }

        //Count words
        for (String word : wordsList) {
            if (wordFrequencies.containsKey(word)) {
                wordFrequencies.put(word, wordFrequencies.get(word) + 1);
            } else {
                wordFrequencies.put(word, 1);
            }
        }

        //Sort wordFrequencies by descending key
        List<Entry<String, Integer>> sortedWordFrequencies = new ArrayList<>(wordFrequencies.entrySet());
        sortedWordFrequencies.sort(Entry.<String, Integer>comparingByValue().reversed());

        int i = 0;
        for (Entry<String, Integer> entry : sortedWordFrequencies) {
            System.out.print("zeile" + i + ": ");
            System.out.println(entry.getKey() + ": " + entry.getValue());
            i++;
        }

        //write each word with inital frequency 1 to textfile in output folder
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputfilepath));

        for (Entry<String, Integer> entry : sortedWordFrequencies) {
            outputWriter.write(entry.getKey() + ":" + entry.getValue() + "\n");
        }
        outputWriter.close();

        System.out.println("Chunks reduced and saved to: " + outputfilepath);

    }

    /**
     * Takes a text file as an input and returns counts of each word in a text file
     * "output-task2.txt"
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException { // update the main function if required
        String inputFilePath = args[0];
        String outputFilePath = args[1];
        String chunkpath = makeChunks(inputFilePath);
        File dir = new File(chunkpath);
        File[] directoyListing = dir.listFiles();
        if (directoyListing != null) {
            for (File f : directoyListing) {
                if (f.isFile()) {

                    map(f.getPath());

                }

            }

            reduce(chunkpath, outputFilePath);

        }

    }
}