package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import porter.Stemmer;
import processing.Preprocessor;

public class Main {

	public final static String CSV_OUTPUT_FOLDER_NAME = "output";
	public final static String CSV_OUTPUT_PREPROCESSED_FOLDER_NAME = "output-preprocessed-files";
	public final static String CSV_OUTPUT_PREPROCESSED_WITH_ANSWERS_FOLDER_NAME = "output-preprocessed-files-with-answers";

	public static void main(String[] args) {

		Scanner in = new Scanner(System.in);

		System.out.println("\nSelect the Operation:");
		System.out.println("\n1 - Preprocess simple .txt file");
		System.out.println("\n2 - Break .csv file in multiple .csv files (without processing)");
		System.out.println("\n3 - Break .csv file in multiple .txt files (preprocessed title and body row)");
		System.out
				.println("\n4 - Break questions .csv file and answers .csv in multiple .txt files (preprocessed with answers)");
		System.out.println("\n5 - Run Test");
		String input = in.nextLine();

		if (input.equals("1")) {
			preprocessCommonFile();
		} else if (input.equals("2")) {
			breakCsvFileInMultipleCsvFiles();
		} else if (input.equals("3")) {
			breakCsvFileInMultipleProcessedTxtFiles();
		} else if (input.equals("4")) {
			breakCsvFileInMultipleProcessedTxtFilesWithAnswers();
		} else if (input.equals("5")) {
			test();
		} else {
			System.out.println("Invalid operation: " + input);
		}

		in.close();

	}

	public static void preprocessCommonFile() {

		Scanner in = new Scanner(System.in);

		try {
			System.out.println("\nFull path to the input file:");
			String inputFilePath = in.nextLine();
			System.out.println("\nFull path to the input file:");
			String outputFilePath = in.nextLine();

			System.out.println("Trying to load file");

			File inputFile = new File(inputFilePath);
			FileReader fileReader = new FileReader(inputFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			StringBuilder builder = new StringBuilder();
			String aux = "";

			while ((aux = bufferedReader.readLine()) != null) {
				builder.append(aux + "\n");
			}
			bufferedReader.close();

			String text = builder.toString();

			System.out.println("Processing...");
			Preprocessor pp = new Preprocessor();
			String preprocessedText = pp.processString(text);

			FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath);
			fileOutputStream.write(preprocessedText.getBytes());
			fileOutputStream.close();

			System.out.println("Processing ended.");
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File not found.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			in.close();
		}
	}

	public static void breakCsvFileInMultipleCsvFiles() {

		Scanner in = new Scanner(System.in);

		System.out.println("\nFull path to the input file:");
		String inputFilePath = in.nextLine();
		System.out.println("\nFull path to the output folder:");
		String outputFolderPath = in.nextLine();

		FileReader fileReader = null;
		CSVParser csvFileParser = null;

		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;

		// Create the CSVFormat object with the header mapping
		CSVFormat csvFileReaderFormat = CSVFormat.DEFAULT.withHeader();
		CSVFormat csvFileWriterFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");

		try {
			System.out.println("Trying to load file");

			fileReader = new FileReader(inputFilePath);
			csvFileParser = new CSVParser(fileReader, csvFileReaderFormat);

			System.out.println("File loaded!");

			Map<String, Integer> headerValues = csvFileParser.getHeaderMap();
			List<CSVRecord> csvRecords = csvFileParser.getRecords();

			String id;
			String outputFilePath;

			for (CSVRecord record : csvRecords) {

				// System.out.println(record.getRecordNumber());
				id = record.get("Id");

				if (id == null) {
					System.out.println("Error: Every .csv record must have an 'Id' value.");
					break;
				} else {

					outputFilePath = outputFolderPath + File.separator + CSV_OUTPUT_FOLDER_NAME + File.separator + id
							+ ".csv";
					Util.createFileWithFolders(outputFilePath);

					fileWriter = new FileWriter(outputFilePath);
					csvFilePrinter = new CSVPrinter(fileWriter, csvFileWriterFormat);

					csvFilePrinter.printRecord(Util.csvHeaderMapToList(headerValues));
					csvFilePrinter.printRecord(Util.csvRecordToList(record));

					fileWriter.flush();
					fileWriter.close();
					csvFilePrinter.close();
				}
			}

			System.out.println("Files created successfully!");
		} catch (FileNotFoundException e) {
			System.out.println("Error! File not found.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			in.close();

			try {
				fileReader.close();
				csvFileParser.close();
			} catch (IOException e) {
				System.out.println("Error while closing fileReader and csvFileParser.");
			}
		}

	}

	public static void breakCsvFileInMultipleProcessedTxtFiles() {
		Scanner in = new Scanner(System.in);

		System.out.println("\nFull path to the input file:");
		String inputFilePath = in.nextLine();
		System.out.println("\nFull path to the output folder:");
		String outputFolderPath = in.nextLine();

		FileReader fileReader = null;
		CSVParser csvFileParser = null;

		// Create the CSVFormat object with the header mapping
		CSVFormat csvFileReaderFormat = CSVFormat.DEFAULT.withHeader();

		try {
			System.out.println("Trying to load file");

			Preprocessor pp = new Preprocessor();

			fileReader = new FileReader(inputFilePath);
			csvFileParser = new CSVParser(fileReader, csvFileReaderFormat);

			FileOutputStream fileOutputStream;

			System.out.println("File loaded!");

			List<CSVRecord> csvRecords = csvFileParser.getRecords();

			String id;
			String bodyText;
			String outputFilePath;

			StringBuilder builder;
			for (CSVRecord record : csvRecords) {

				System.out.println(record.getRecordNumber());
				id = record.get("Id");
				bodyText = record.get("Body");

				if (id == null || bodyText == null) {
					System.out.println("Error: Every .csv record must have an 'Id' value and a 'Body' value.");
					break;
				} else {

					builder = new StringBuilder(bodyText);

					if ( record.get("Title") != null) {
						builder.insert(0,  record.get("Title") + " ");
					}

					outputFilePath = outputFolderPath + File.separator + CSV_OUTPUT_PREPROCESSED_FOLDER_NAME
							+ File.separator + id + ".txt";
					Util.createFileWithFolders(outputFilePath);

					String preprocessedText = pp.processString(builder.toString());

					fileOutputStream = new FileOutputStream(outputFilePath);
					fileOutputStream.write(preprocessedText.getBytes());
					fileOutputStream.close();
				}
			}

			System.out.println("Files created successfully!");
		} catch (FileNotFoundException e) {
			System.out.println("Error! File not found.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			in.close();

			try {
				fileReader.close();
				csvFileParser.close();
			} catch (IOException e) {
				System.out.println("Error while closing fileReader and csvFileParser.");
			}
		}

	}

	public static void breakCsvFileInMultipleProcessedTxtFilesWithAnswers() {
		Scanner in = new Scanner(System.in);

		System.out.println("\nFull path to the input file (Questions):");
		String inputFilePath = in.nextLine();
		System.out.println("\nFull path to the input file (Answers):");
		String inputFilePathAnswers = in.nextLine();
		System.out.println("\nFull path to the output folder:");
		String outputFolderPath = in.nextLine();

		FileReader fileReaderQuestions = null;
		CSVParser csvFileParserQuestions = null;

		FileReader fileReaderAnswers = null;
		CSVParser csvFileParserAnswers = null;

		// Create the CSVFormat object with the header mapping
		CSVFormat csvFileReaderFormat = CSVFormat.DEFAULT.withHeader();

		try {
			System.out.println("Trying to load files");

			Preprocessor pp = new Preprocessor();

			fileReaderQuestions = new FileReader(inputFilePath);
			csvFileParserQuestions = new CSVParser(fileReaderQuestions, csvFileReaderFormat);
			List<CSVRecord> csvQuestionsRecords = csvFileParserQuestions.getRecords();
			fileReaderQuestions.close();
			csvFileParserQuestions.close();

			fileReaderAnswers = new FileReader(inputFilePathAnswers);
			csvFileParserAnswers = new CSVParser(fileReaderAnswers, csvFileReaderFormat);
			List<CSVRecord> csvAnswers = csvFileParserAnswers.getRecords();
			fileReaderAnswers.close();
			csvFileParserAnswers.close();

			FileOutputStream fileOutputStream;

			System.out.println("Files loaded!");

			String id;
			String bodyText;
			String outputFilePath;

			StringBuilder builder;

			for (CSVRecord record : csvQuestionsRecords) {

				System.out.println(record.getRecordNumber());
				id = record.get("Id");

				bodyText = record.get("Body");

				if (id == null || bodyText == null) {
					System.out.println("Error: Every .csv record must have an 'Id' value and a 'Body' value.");
					break;
				} else {

					builder = new StringBuilder(bodyText);

					if (record.get("Title") != null) {
						builder.insert(0, record.get("Title") + " ");
					}

					List<CSVRecord> answerList = Util.findAllRecordsWithParentId(csvAnswers, id);

					for (CSVRecord answer : answerList) {
						builder.append(" " + answer.get("Body"));
					}

					outputFilePath = outputFolderPath + File.separator
							+ CSV_OUTPUT_PREPROCESSED_WITH_ANSWERS_FOLDER_NAME + File.separator + id + ".txt";
					Util.createFileWithFolders(outputFilePath);

					String preprocessedText = pp.processString(builder.toString());

					fileOutputStream = new FileOutputStream(outputFilePath);
					fileOutputStream.write(preprocessedText.getBytes());
					fileOutputStream.close();
				}
			}

			System.out.println("Files created successfully!");
		} catch (FileNotFoundException e) {
			System.out.println("Error! File not found.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			in.close();

			try {
				fileReaderQuestions.close();
				csvFileParserQuestions.close();
				fileReaderAnswers.close();
				csvFileParserAnswers.close();
			} catch (IOException e) {
				System.out.println("Error while closing fileReader and csvFileParser.");
			}
		}
	}

	public static void test() {

		Preprocessor pp = new Preprocessor();
		Stemmer st = new Stemmer();

		st.add("caresses");
		st.stem();
		System.out.println(st.toString());

		st.add("parking");
		st.stem();
		System.out.println(st.toString());

		String test1 = pp
				.removeCodeSnippets("Esse e <oi> meu </oi> codigo <code> oiii <code> ahahahaha </code> oiii </code> lelele");
		System.out.println(test1);

		String test2 = pp
				.removeHtmlTags("Esse e meu codigo <a href=''> oiii <code> ahahahaha <code> oiii <code> lelele");
		System.out.println(test2);

		String test3 = pp.removeStopWords("This is a text, with a lot of information! I'm so happy");
		System.out.println(test3);

		String test4 = pp.removePunctuation("This. is a text, with a lot of .information.com!");
		System.out.println(test4);

		String test5 = "<p>I've been having issues getting the C sockets API to work properly in "
				+ "	C++. Specifically, although I am including sys/socket.h, I still get compile "
				+ "time errors telling me that AF_INET is not defined. Am I missing something "
				+ "obvious, or could this be related to the fact that I'm doing this coding on "
				+ "z/OS and my problems are much more complicated? </p>";

		System.out.println();
		System.out.println(pp.processString(test5));

		String test6 = "XXXX<code> blabla </code> YYY <code> blabla2 </code>. ZZZ";
		System.out.println(pp.removeCodeSnippets(test6));
		System.out.println(pp.processString(test6));
	}

}
