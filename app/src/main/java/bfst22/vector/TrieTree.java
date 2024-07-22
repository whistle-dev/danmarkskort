package bfst22.vector;

import java.util.ArrayList;
import java.util.Collections;

import javafx.geometry.Point2D;

//Initial code from geeksforgeeks.org.
public class TrieTree {

    public TrieTree() {

    }

    public static final int alhabet_size = 37; // antal symboler der bliver brugt, det danske alfabet+tal+mellemrum.

    // opretter trienode klassen, hver node har en arraylist af børn samt en bool
    // der afgør om det er en slutnode.
    // der bliver oprettet børn for alle symboler i alfabet og de bliver sat til
    // null.
    static class TrieNode {
        TrieNode[] children = new TrieNode[alhabet_size];
        Point2D cords;
        boolean endOfString;
        char character;

        public TrieNode() {
        }

        TrieNode(Point2D cords, char c) {
            this.character = c;
            endOfString = false;
            this.cords = cords;
            for (int i = 0; i < alhabet_size; i++) {
                children[i] = null;
            }
        }
    }

    // opretter root node som altid vil være null;
    private static TrieNode root = new TrieNode();

    // insert metode der tager en String som argument og indsætter denne i træet.
    // hver char i key bliver indsat efter den forrige og hver node har en parent
    // samt børn.
    public void insert(String key, Point2D cords) {
        key = replaceKey(key);
        int depth;
        int index;

        TrieNode parent = root;
        for (depth = 0; depth < key.length(); depth++) {
            index = key.charAt(depth) - 'a';
            if (key.charAt(depth) == 'ø')
                index -= 17;
            if (key.charAt(depth) == 'å')
                index += 3;
            if (key.charAt(depth) == ' ')
                index += 101;
            if (index < 0)
                index += 75;
            if (parent.children[index] == null)
                parent.children[index] = new TrieNode(cords, key.charAt(depth));

            parent = parent.children[index];
        }
        parent.endOfString = true;
    }

    // search metode, fungerer ligesom insert. metode bare hvor den tjekker hver
    // node og sammenligner med input.
    public boolean search(String key) {
        key = replaceKey(key);
        int depth;
        int index;
        TrieNode parent = root;
        for (depth = 0; depth < key.length(); depth++) {
            index = key.charAt(depth) - 'a';
            if (key.charAt(depth) == ' ')
                index += 101;
            if (index < 0)
                index += 75;
            if (parent.children[index] == null) {
                return false;
            }

            parent = parent.children[index];
        }
        return true;
    }

    // get cords på en specifik adresse, fungerer præcist ligesom search funktionen.
    public Point2D getCords(String key) {
        key = replaceKey(key);
        int depth;
        int index;
        TrieNode parent = root;
        for (depth = 0; depth < key.length(); depth++) {
            index = key.charAt(depth) - 'a';
            if (key.charAt(depth) == ' ')
                index += 101;
            if (index < 0)
                index += 75;
            if (parent.children[index] == null) {
                return null;
            }

            parent = parent.children[index];
        }
        return parent.cords;
    }

    // metode til at søge efter alle ord der indeholder bruger input i trietree.
    // bruger rekursiv dybde først søgning metoden til dette.
    // finder den node som er sidste character i inputtet og kalder derefter DFS
    // metoden med denne node, input og arraylist.
    public ArrayList<String> searchMultiple(String key) {
        ArrayList<String> words = new ArrayList<>();
        key = replaceKey(key);
        TrieNode currentNode = root;
        for (int i = 0; i < key.length(); i++) {
            inner: for (TrieNode n : currentNode.children) {
                if (n != null && n.character == key.charAt(i)) {
                    currentNode = n;
                    break inner;
                }
            }
        }
        words = DFS(key, currentNode, words);
        Collections.sort(words);
        return words;
    }

    // rekursiv dybde først søgning. Søger rekursivt igennem alle børn til
    // currentnode og tilføjer alle ord der matcher input til listen.
    static ArrayList<String> DFS(String key, TrieNode current, ArrayList<String> words) {
        if (current.endOfString) {
            String output = "";
            for (String word : key.toLowerCase().split("\\s+")) {
                output += word.replaceFirst(".", word.substring(0, 1).toUpperCase()) + " ";
            }
            words.add(output.replace("ae", "æ").replace("oe", "ø").replace("aa", "å").replace("Oe", "Ø")
                    .replace("Aa", "Å").replace("Ae", "Æ"));

        }
        for (TrieNode n : current.children) {
            if (n != null) {
                current = n;
                DFS(key + n.character, current, words);
            }
        }
        return words;
    }

    public String replaceKey(String key) {
        key = key.trim().toLowerCase();
        key = key.replace("æ", "ae").replace("ø", "oe").replace("å", "aa").replace("é", "e").replace("ü", "u")
                .replace("ö", "oe").replace("õ", "oe").replace("ä", "ae");
        return key;
    }
}
