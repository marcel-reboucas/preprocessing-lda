package processing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;

import porter.Stemmer;

/**
 * @author Marcel
 *
 */
public class Preprocessor {

	static final String RESOURCES_FOLDER = "resources/";
	static final String STOP_WORDS_FILE = "stopwords.txt";

	private Stemmer porterStemmer;
	private List<String> stopWordList;

	public Preprocessor() {
		this.porterStemmer = new Stemmer();
		this.stopWordList = new ArrayList<String>();
		this.loadStopWordsFromFile();
	}

	/**
	 * This method will remove any text between code tags, including the tags.
	 * 
	 * @param text
	 *            The text with tags to be removed.
	 * @return The text without the code tags.
	 */
	public String removeCodeSnippets(String text) {

		String result = text;
		result = result.replaceAll("<code>(?s:.)*</code>", "");
		result = result.replaceAll("\\s+", " ");
		return result.trim();
	}

	/**
	 * This method removes all the html tags in the String, but keeps the text
	 * inside them.
	 * 
	 * @param text
	 *            The text with tags to be removed.
	 * @return The text without the html tags.
	 */
	public String removeHtmlTags(String text) {
		return Jsoup.parse(text).text().trim();
	}

	/**
	 * This method removes all the stop words from the text, using the
	 * stopWordList to know which words to remove.
	 * 
	 * @param text
	 *            The text with stop words to be removed.
	 * @return The text without the stop words.
	 */
	public String removeStopWords(String text) {

		String result = text;
		String[] words = text.split("\\s+");
		List<String> wordsList = Arrays.asList(words);

		for (String word : wordsList) {
			if (stopWordList.contains(word.toLowerCase())) {
				// doesn't match if followed by the characters in
				// undesiredMatches
				// ie. prevents I removing the I in I'm.
				String undesiredMatches = "'`";
				result = result.replaceAll("\\b" + word + "\\b(?!["
						+ undesiredMatches + "].*)", "");
			}
		}
		result = result.replaceAll("\\s+", " ");
		return result.trim();
	}

	/**
	 * This method removes the punctuation (leading and trailing) from the text.
	 * 
	 * @param text
	 *            The text with punctuation to be removed.
	 * @return The text without the punctuation.
	 */
	public String removePunctuation(String text) {

		String result = text;

		// removes the trailing punctuation (leaves + and _ characters).
		result = result.replaceAll("[\\p{Punct}&&[^+_]]\\B", "");
		// removes the leading punctuation (leaves + and _ characters).
		result = result.replaceAll("\\B[\\p{Punct}&&[^+_]]", "");
		return result;
	}

	/**
	 * This method applies the Porter Stemming Algorithm
	 * <code>(http://tartarus.org/martin/PorterStemmer/)</code> to every word in
	 * the text.
	 * 
	 * @param text
	 *            The text with words to be stemmed.
	 * @return The text with words without morphological and inflexional
	 *         endings.
	 */
	public String applyPorterStemmer(String text) {

		String result = text;
		String[] words = text.split("\\s+");
		List<String> wordsList = Arrays.asList(words);

		for (String word : wordsList) {

			// only if the word doesn't have any punctation: prevents an
			// undesirable effect on the regex
			if (!word.matches(".*[\\p{Punct}].*")) {
				porterStemmer.add(word);
				porterStemmer.stem();

				String stemmedWord = porterStemmer.toString();
				result = result.replaceAll("\\b" + word + "\\b", stemmedWord);
			}
		}

		return result.trim();
	}

	/**
	 * This method applies all the processing steps to the text.
	 * 
	 * @param text
	 * @return
	 */
	public String processString(String text) {

		String result = text.toLowerCase();

		result = this.removeCodeSnippets(result);
		result = this.removeHtmlTags(result);
		result = this.removeStopWords(result);
		result = this.removePunctuation(result);
		result = this.applyPorterStemmer(result);

		return result;
	}

	/**
	 * This method is responsible for reading the stopwords file and storing
	 * every word in it. The words can be acessed in the stopWordList list.
	 */
	private void loadStopWordsFromFile() {

		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(RESOURCES_FOLDER + STOP_WORDS_FILE);

		//System.out.println(RESOURCES_FOLDER + STOP_WORDS_FILE);

		try {
			
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String line = null;
			while ((line = br.readLine()) != null) {
				String[] words = line.split("\\ss*");

				for (String word : words) {
					this.stopWordList.add(word.toLowerCase());
				}
			}

		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("ERROR: Error reading a line in the file "
					+ RESOURCES_FOLDER + STOP_WORDS_FILE);
		} catch (NullPointerException e){
			
			System.out.println("ERROR: Error loading resources "
					+ RESOURCES_FOLDER + STOP_WORDS_FILE);
		}

	}
}
