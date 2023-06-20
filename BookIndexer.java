import java.io.*;
import java.util.*;

class Word {
    private String word;
    private Set<Integer> pageNumbers;

    public Word(String word) {
        this.word = word;
        this.pageNumbers = new TreeSet<>();
    }

    public void addPageNumber(int pageNumber) {
        pageNumbers.add(pageNumber);
    }

    public String getWord() {
        return word;
    }

    public Set<Integer> getPageNumbers() {
        return pageNumbers;
    }

    public String getPageNumbersAsString() {
        StringBuilder sb = new StringBuilder();
        for (int pageNumber : pageNumbers) {
            sb.append(pageNumber).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1); // Remove the trailing comma
        }
        return sb.toString();
    }
}

class Page {
    private int pageNumber;
    private List<String> words;

    public Page(int pageNumber) {
        this.pageNumber = pageNumber;
        this.words = new ArrayList<>();
    }

    public void addWord(String word) {
        words.add(word);
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public List<String> getWords() {
        return words;
    }
}

class FileReaderUtil {
    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }
}

public class BookIndexer {
    private List<Page> pages;
    private Set<String> excludeWords;
    private Map<String, Word> index;

    public BookIndexer() {
        this.pages = new ArrayList<>();
        this.excludeWords = new HashSet<>();
        this.index = new TreeMap<>();
    }

    public void readPages(List<String> pageFiles) {
        for (int i = 0; i < pageFiles.size(); i++) {
            List<String> lines = FileReaderUtil.readLines(pageFiles.get(i));
            Page page = new Page(i + 1);

            for (String line : lines) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    String cleanedWord = word.toLowerCase().replaceAll("[^a-zA-Z]", "");
                    if (!cleanedWord.isEmpty() && !excludeWords.contains(cleanedWord)) {
                        page.addWord(cleanedWord);
                    }
                }
            }

            pages.add(page);
        }
    }

    public void readExcludeWords(String excludeWordsFile) {
        List<String> lines = FileReaderUtil.readLines(excludeWordsFile);
        for (String line : lines) {
            String[] words = line.split("\\s+");
            excludeWords.addAll(Arrays.asList(words));
        }
    }

    public void buildIndex() {
        for (Page page : pages) {
            for (String word : page.getWords()) {
                Word indexedWord = index.get(word);
                if (indexedWord == null) {
                    indexedWord = new Word(word);
                    index.put(word, indexedWord);
                }
                indexedWord.addPageNumber(page.getPageNumber());
            }
        }
    }

    public void writeIndexToFile(String outputFile) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (Map.Entry<String, Word> entry : index.entrySet()) {
                String word = entry.getKey();
                Word indexedWord = entry.getValue();
                writer.print(word + " : ");
                writer.println(indexedWord.getPageNumbersAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        List<String> pageFiles = Arrays.asList("D:\\Placements Prep\\Mithi company\\Page1.txt", "D:\\Placements Prep\\Mithi company\\Page2.txt", "D:\\Placements Prep\\Mithi company\\Page3.txt");
        String excludeWordsFile = "D:\\Placements Prep\\Mithi company\\exclude-words.txt";
        String outputFile = "index.txt";

        BookIndexer bookIndexer = new BookIndexer();
        bookIndexer.readPages(pageFiles);
        bookIndexer.readExcludeWords(excludeWordsFile);
        bookIndexer.buildIndex();
        bookIndexer.writeIndexToFile(outputFile);
    }
}
