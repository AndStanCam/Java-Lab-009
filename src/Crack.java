package src;

import org.apache.commons.codec.digest.Crypt;
import src.User;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * @author Trevor Hartman
 * @author Ander Stanley-Camba
 *
 * @since Version 1.0 2023-03-31
 */



public class Crack {
    private final User[] users;
    private final String dictionary;

    public Crack(String shadowFile, String dictionary) throws FileNotFoundException {
        this.dictionary = dictionary;
        this.users = Crack.parseShadow(shadowFile);
    }

    public void crack() throws FileNotFoundException {
        File file = new File(dictionary);
        Scanner sc = new Scanner(file);
        String hash;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            for (User user : users) {
                try {
                    hash = Crypt.crypt(line, user.getPassHash());

                    if (user.getPassHash().contains("$") && Objects.equals(hash, user.getPassHash())) {
                        System.out.printf("Found password ' %s ' for user ' %s '\n", hash, user.getUsername());
                    }
                } catch (Exception e) {
                    // do nothing
                }
            }

        }

    }

    public static int getLineCount(String path) {
        int lineCount = 0;
        try (Stream<String> stream = Files.lines(Path.of(path), StandardCharsets.UTF_8)) {
            lineCount = (int)stream.count();
        } catch(IOException ignored) {}
        return lineCount;
    }

    public static User[] parseShadow(String shadowFile) throws FileNotFoundException {

        File file = new File(shadowFile);
        Scanner scanner = new Scanner(file);
        int lines = getLineCount(shadowFile);
        int k = 0;

        User[] userList = new User[lines];

        while (k < lines) {
            String line = scanner.nextLine();
            String[] lineArr = line.split(":");
            User user = new User(lineArr[0], lineArr[1]);
            userList[k] = user;

            k++;
        }
        return  userList;

    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Type the path to your shadow file: ");
        String shadowPath = sc.nextLine();
        System.out.print("Type the path to your dictionary file: ");
        String dictPath = sc.nextLine();

        Crack c = new Crack(shadowPath, dictPath);
        c.crack();
    }
}
