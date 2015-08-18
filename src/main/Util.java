package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;

/**
 * @author Marcel
 *
 */
public class Util {

	/**
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static boolean createFileWithFolders(String path) throws IOException {

		File file = new File(path);

		File parent = file.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
			throw new IllegalStateException("Couldn't create dir: " + parent);
		}

		return file.createNewFile();
	}

	/**
	 * @param record
	 * @return
	 */
	public static List<String> csvRecordToList(CSVRecord record) {

		List<String> result = new ArrayList<>();

		for (int i = 0; i < record.size(); i++) {
			result.add(record.get(i));
		}

		return result;
	}

	/**
	 * @param headerMap
	 * @return
	 */
	public static List<String> csvHeaderMapToList(Map<String, Integer> headerMap) {

		List<String> result = new ArrayList<>();

		for (String key : headerMap.keySet()) {
			result.add(key);
		}

		return result;
	}

	public static Map<String, List<CSVRecord>> createAnswersMapByParentId(List<CSVRecord> answerList){
		
		 Map<String, List<CSVRecord>> resultMap =  new HashMap<String, List<CSVRecord>>();
		 
		 String parentId;
		 List<CSVRecord> temp;
			for (CSVRecord answer : answerList) {
				parentId = answer.get("ParentId");
				
				if (resultMap.containsKey(parentId)){
					temp = resultMap.get(parentId);
					temp.add(answer);
					resultMap.put(parentId,temp);
				}else {
					temp = new ArrayList<>();
					temp.add(answer);
					resultMap.put(parentId,temp);
				}
			}
			
		 return resultMap;
	}
}
