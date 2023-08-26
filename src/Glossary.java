import java.util.Comparator;

import components.map.Map;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Easy-to-maintain glossary facility made using HTML files and tailored to the
 * requirements of the customer Cy Burnett for their textbook publishing site.
 *
 * @author Nicholas McCracken
 *
 */
public final class Glossary {

    /**
     * Compare {@code String}s in lexicographic order.
     */
    private static class StringLT implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private Glossary() {
    }

    /**
     * Inputs a list of terms and their definitions from the given file and
     * stores them in the given {@code Map}.
     *
     * @param fileName
     *            the name of the input file
     * @param termMap
     *            the term -> term map
     * @replaces termMap
     * @requires <pre>
     * [file named fileName exists but is not open, and has the
     *  format of one term (unique in the file) on a line followed by it's
     *  definition on the next line and empty lines separating each term
     *  definition pair]
     * </pre>
     * @ensures [termMap contains terms -> term mapping from file fileName]
     */
    private static void getTermMap(String fileName,
            Map<String, String> termMap) {
        assert fileName != null : "Violation of: fileName is not null";
        assert termMap != null : "Violation of: termMap is not null";

        // Open an input stream to read from the file.
        SimpleReader inFile = new SimpleReader1L(fileName);

        /*
         * Store each term definition pair in the map until the end of the file
         * is reached, then close the input stream.
         */
        while (!inFile.atEOS()) {
            String term = inFile.nextLine(), definition = inFile.nextLine(),
                    temp = inFile.nextLine();
            while (!temp.equals("")) {
                definition += temp;
                temp = inFile.nextLine();
            }
            termMap.add(term, definition);
        }
        // Close file input stream.
        inFile.close();
    }

    /**
     * Inputs a list of terms and their definitions from the given file and
     * stores them alphabetically in the given {@code Queue}.
     *
     * @param fileName
     *            the name of the input file
     * @param termQueue
     *            the term -> term queue
     * @replaces termQueue
     * @requires <pre>
     * [file named fileName exists but is not open, and has the
     *  format of one term (unique in the file) on a line followed by it's
     *  definition on the next line and empty lines separating each term
     *  definition pair]
     * </pre>
     * @ensures [Queue contains terms ordered alphabetically -> term queueing
     *          from file fileName]
     */
    private static void getTermQueue(String fileName, Queue<String> termQueue) {
        assert fileName != null : "Violation of: fileName is not null";
        assert termQueue != null : "Violation of: termQueue is not null";

        // Open an input stream to read from the file.
        SimpleReader inFile = new SimpleReader1L(fileName);

        /*
         * Store each term in the queue until the end of the file is reached,
         * then close the input stream.
         */
        while (!inFile.atEOS()) {
            String term = inFile.nextLine(), temp = inFile.nextLine();
            while (!temp.equals("")) {
                temp = inFile.nextLine();
            }
            termQueue.enqueue(term);
        }
        // Close file input stream.
        inFile.close();

        // Use comparator to sort the terms in alphabetical order.
        Comparator<String> alphabetize = new StringLT();
        termQueue.sort((a,b) -> a.compareTo(b));
    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param charSet
     *            the {@code Set} to be replaced
     * @replaces charSet
     * @ensures charSet = entries(str)
     */
    private static void generateElements(String str, Set<Character> charSet) {
        assert str != null : "Violation of: str is not null";
        assert charSet != null : "Violation of: charSet is not null";

        /*
         * Input each character of the string as a separate element in the
         * temporary set, then replace the formal parameter charSet with the
         * elements from the temporary set.
         */
        Set<Character> strEntries = charSet.newInstance();
        for (int i = 0; i < str.length(); i++) {
            strEntries.add(str.charAt(i));
        }
        charSet.transferFrom(strEntries);
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        /*
         * Determine if first character is word or separator which determines if
         * the string wordOrSeparator will contain a word or separators.
         */
        String wordOrSeparator = "";
        boolean initialCharacterIsSeparator = separators
                .contains(text.charAt(position));

        /*
         * Continuously add characters to the string wordOrSeparator until the
         * end of the formal parameter text is reached or the current character
         * is not of the same type (word or separator) as the initial character.
         */
        while (position < text.length()
                && initialCharacterIsSeparator == separators
                        .contains(text.charAt(position))) {
            wordOrSeparator += text.charAt(position);
            position++;
        }
        return wordOrSeparator;
    }

    /**
     * Generates a top-level index HTML file saved in {@code folder} which lists
     * each term, which is hyperlinked to it's own term page, in the glossary.
     *
     * @param folder
     *            the folder to store the file
     * @param termMap
     *            stores term definition pairs
     * @param termQueue
     *            stores terms ordered alphabetically
     * @clears termQueue
     * @requires [folder named folder exists in the project folder]
     * @ensures <pre>
     * [generates HTML file with each term in the glossary listed and
     * hyperlinked to it's own term page, saves HTML file in folder]
     * </pre>
     */
    private static void generateIndexPage(String folder,
            Map<String, String> termMap, Queue<String> termQueue) {
        assert folder != null : "Violation of: text is not null";
        assert termMap != null : "Violation of: termMap is not null";
        assert termQueue != null : "Violation of: text is not null";

        // Open an output stream to write to a file stored in folder.
        SimpleWriter fileOut = new SimpleWriter1L(folder + "/index.html");

        // Create opening tags including a title, heading, and ordered list.
        fileOut.println("<html>");
        fileOut.println("<head>");
        fileOut.println("<title>Glossary</title>");
        fileOut.println("</head>");
        fileOut.println("<body>");
        fileOut.println("<h1>Glossary</h1>");
        fileOut.println("<hr>");
        fileOut.println("<h2>Index</h2>");
        fileOut.println("<ul>");

        /*
         * For each term stored in termMap, add an item to the list for each
         * term, create another HTML file for a term page, and hyperlink the
         * term to it's corresponding page.
         */
        while (termQueue.length() > 0) {
            String term = termQueue.dequeue();

            generateTermPage(folder, term, termMap);

            fileOut.println("<li>");
            fileOut.print("<a href=\"");
            fileOut.print(term + ".html");
            fileOut.print("\">");
            fileOut.print(term);
            fileOut.println("</a>");
            fileOut.println("</li>");
        }
        // Close all opened tags and output stream.
        fileOut.println("</ul>");
        fileOut.println("</body>");
        fileOut.println("</html>");
        fileOut.close();
    }

    /**
     * Generates a term HTML file saved in {@code folder} which displays the
     * {@code term} and it's definition while hyperlinking any terms in the
     * definition that are stored in {@code termMap} to their own term page,
     * along with hyperlinking a return to the top-level index.
     *
     * @param folder
     *            the folder to store the file
     * @param term
     *            a word with a definition
     * @param termMap
     * @requires [folder named folder exists in the project folder]
     * @ensures <pre>
     * [generates HTML file with the {@code String} and it's corresponding
     * definition, hyperlinks any other terms that are stored in the glossary
     * to their own term page, hyperlinks a return to top-level index page,
     * saves HTML file in folder]
     * </pre>
     */
    private static void generateTermPage(String folder, String term,
            Map<String, String> termMap) {
        assert folder != null : "Violation of: text is not null";
        assert term != null : "Violation of: term is not null";
        assert termMap != null : "Violation of: termMap is not null";

        // Open an output stream to write to a file stored in folder.
        SimpleWriter fileOut = new SimpleWriter1L(
                folder + "/" + term + ".html");

        /*
         * Create opening tags including a title, a red, bold, and italicized
         * heading, and a paragraph.
         */
        fileOut.println("<html>");
        fileOut.println("<head>");
        fileOut.println("<title>" + term + "</title>");
        fileOut.println("</head>");
        fileOut.println("<body>");
        fileOut.println(
                "<h1 style=\"color:red;\"><b><i>" + term + "</i></b></h1>");
        fileOut.print("<p>");

        /*
         * Store separator characters in a set and pull the definition
         * corresponding to the term from termMap.
         */
        Set<Character> separators = new Set1L<Character>();
        generateElements(" ,.", separators);
        String definition = termMap.value(term);

        /*
         * Iterate through the definition to determine if any word or separators
         * corresponding to other terms in the glossary, and hyperlink the terms
         * to their own term page if so, otherwise print the word or separator.
         */
        for (int i = 0; i < definition.length(); i++) {
            String wordOrSeparator = nextWordOrSeparator(definition, i,
                    separators);

            if (termMap.hasKey(wordOrSeparator)) {
                fileOut.print("<a href=\"");
                fileOut.print(wordOrSeparator + ".html");
                fileOut.print("\">");
                fileOut.print(wordOrSeparator);
                fileOut.println("</a>");
            } else {
                fileOut.print(wordOrSeparator);
            }
            i += wordOrSeparator.length() - 1;
        }
        /*
         * Add a hyperlink to return to top-level index page and close all
         * opened tags and output stream.
         */
        fileOut.println("</p>");
        fileOut.println("<hr>");
        fileOut.print("<p>Return to ");
        fileOut.print("<a href=\"");
        fileOut.print("index.html");
        fileOut.print("\">");
        fileOut.print("index");
        fileOut.print("</a>");
        fileOut.print("</p>");
        fileOut.println("</body>");
        fileOut.println("</html>");
        fileOut.close();
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        // Open input and output streams to console.
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        /*
         * Prompt user for the name of an input file to read glossary terms and
         * definitions from and an output folder to store all generated glossary
         * HTML files in.
         */
        out.print("Enter the name of an input file with a .txt extension: ");
        String inputFile = in.nextLine();
        out.println();
        out.print("Enter the name of the folder to store all output files: ");
        String outputFolder = in.nextLine();

        // Store term definition pairs in a map.
        Map<String, String> glossaryTermMap = new Map1L<String, String>();
        getTermMap(inputFile, glossaryTermMap);

        // Store alphabetized terms in a queue.
        Queue<String> glossaryTermQueue = new Queue1L<String>();
        getTermQueue(inputFile, glossaryTermQueue);

        // Generate a top-level index HTML file to list all terms in glossary.
        generateIndexPage(outputFolder, glossaryTermMap, glossaryTermQueue);

        // Close input and output streams.
        in.close();
        out.close();
    }
}
