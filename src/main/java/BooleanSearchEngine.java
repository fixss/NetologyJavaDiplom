import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    Map<String, List<PageEntry>> indexWords = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        List<File> nameFile = new ArrayList<>(Arrays.asList(Objects.requireNonNull(pdfsDir.listFiles())));
        for (File file : nameFile) {
            var doc = new PdfDocument(new PdfReader(file));
            for (int j = 0; j < doc.getNumberOfPages(); j++) {
                int indexPage = j + 1;
                var text = PdfTextExtractor.getTextFromPage(doc.getPage(indexPage));
                var words = (text.toLowerCase().split("\\P{IsAlphabetic}+"));
                Map<String, Integer> freqs = new HashMap<>();
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }
                for (var word : freqs.entrySet()) {
                    List<PageEntry> wordCount;
                    if (indexWords.containsKey(word.getKey())) {
                        wordCount = indexWords.get(word.getKey());
                    } else {
                        wordCount = new ArrayList<>();
                    }
                    wordCount.add(new PageEntry(file.getName(), indexPage, word.getValue()));
                    wordCount.sort(Collections.reverseOrder());
                    indexWords.put(word.getKey(), wordCount);
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        return indexWords.get(word.toLowerCase());
    }
}
