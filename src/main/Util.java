package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;

public class Util {

	public static boolean createFileWithFolders(String path) throws IOException {
		
		File file = new File(path);
		
		File parent = file.getParentFile();
		if(!parent.exists() && !parent.mkdirs()){
		    throw new IllegalStateException("Couldn't create dir: " + parent);
		}
		
		return file.createNewFile();
	}
	
	public static List<String> csvRecordToList(CSVRecord record){
		
		List<String> result = new ArrayList<>();
		
		for(int i = 0; i < record.size(); i++) {
			result.add(record.get(i));
		}
		
		return result;
	}
	
	public static List<String> csvHeaderMapToList(Map<String, Integer> headerMap){
		
		List<String> result = new ArrayList<>();
		
		for(String key : headerMap.keySet()){
			result.add(key);
		}
		
		return result;
	}
	
	public static List<CSVRecord> findAllRecordsWithParentId(List<CSVRecord> recordList, String parentId){
		
		List<CSVRecord> result = new ArrayList<>();
		
		for(CSVRecord record : recordList){
			if (record.get("ParentId").equals(parentId)){
				result.add(record);
			}
		}
		
		recordList.removeAll(result);
		return result;
	}
	
	
}
