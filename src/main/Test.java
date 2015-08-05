package main;

import porter.Stemmer;
import processing.Preprocessor;

public class Test {

	public static void main(String[] args) {
		
		Preprocessor pp = new Preprocessor();
		Stemmer st = new Stemmer();
		
		st.add("caresses");
		st.stem();
		System.out.println(st.toString());
		
		st.add("parking");
		st.stem();
		System.out.println(st.toString());
		
		
		String test1 = pp.removeCodeSnippets("Esse e <oi> meu </oi> codigo <code> oiii <code> ahahahaha </code> oiii </code> lelele");
		System.out.println(test1);
		
		String test2 = pp.removeHtmlTags("Esse e meu codigo <a href=''> oiii <code> ahahahaha <code> oiii <code> lelele");
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
	}
}
