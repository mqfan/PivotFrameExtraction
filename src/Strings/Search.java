package Strings;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Search {

	private String docAbstract = "";
	private ArrayList<String> keywords;
	private String articleName;
	
	public Search(String doc)
	{
		docAbstract = "";
		keywords = null;
		articleName = doc;
	}
	
	public void searchAb() throws IOException{
		Scanner scanner = new Scanner(new FileInputStream(articleName));
		try {
	      while (scanner.hasNextLine()){
	    	  docAbstract = docAbstract + " " + scanner.nextLine();
	      }
	    }
	    finally{
	      scanner.close();
	    }
		docAbstract = docAbstract.substring(1);
		populateKey();
		ArrayList<String> hits = reader();
		System.out.println(hits);

	} 
	
	public void populateKey()
	{
		keywords = new ArrayList<String>();
//		File keys = new File("C:\\Users\\fanq3\\workspace\\AbstractSearch");
		try {
			Scanner sc = new Scanner(new FileInputStream("keys.txt"));
			while (sc.hasNextLine()) {
				String s = sc.nextLine();
		    	
				keywords.add(s.toUpperCase());
		    	
		       // process the line.
		    }
			sc.close();
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
	}
	
	public ArrayList<String> reader()
	{
		ArrayList<String> hits = new ArrayList<String>();
		
		docAbstract = docAbstract.toUpperCase();
		int wordCount = docAbstract.split("\\s+").length;
		String[] words = new String[wordCount];
//		System.out.println(wordCount);
		
		for (int x = 0; x < wordCount; x++) {
			
//			System.out.println(docAbstract.indexOf(" "));
			if (docAbstract.contains(" "))
			{
				words[x] = docAbstract.substring(0, docAbstract.indexOf(" "));
				docAbstract = docAbstract.substring(docAbstract.indexOf(" ")+1);
			}
			else
			{
				words[x] = docAbstract;
			}
			//System.out.println(words[x]);
			if (words[x] != null)
			{
				for (int y = 0; y < keywords.size(); y++)
				{
					
					//System.out.println(words[x] + ", " + keywords.get(y));
					if (words[x].contains(keywords.get(y)))
					{
						//System.out.println(x + " " + words[x] + " " + keywords.get(y));
						hits.add(keywords.get(y));
						y = keywords.size();
					}
				}
			}
		}
		Cleaner(hits);
		return hits;
	}
	
	public static ArrayList<String> Cleaner(ArrayList<String> hits)
	{
		Set<String> noRepeats = new HashSet<>();
		noRepeats.addAll(hits);
		hits.clear();
		hits.addAll(noRepeats);
		return hits;
	}
	
}
