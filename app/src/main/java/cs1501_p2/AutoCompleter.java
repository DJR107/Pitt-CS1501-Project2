/**
 * @author David Roberts (djr107)
 */

package cs1501_p2;

import java.util.*;
import java.io.*;

public class AutoCompleter implements AutoComplete_Inter
{
	/**
	 * DLB that stores the words in the dictionary
	 */
	public DLB dlb;

	/**
	 * User History that stores previously selected words
	 */
	public UserHistory uH;

	/**
	 * Constructor that accepts the file name of the dictionary
	 */
	public AutoCompleter(String dictFileName)
	{
		System.out.println("No UserHistory Found\n");
		dlb = new DLB();

		try
		{
			File dict = new File(dictFileName);
			Scanner dictScan = new Scanner(dict);
			while (dictScan.hasNextLine())
			{
				String wordToAdd = dictScan.nextLine();
				dlb.add(wordToAdd);
			}
			dictScan.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Dict file could not be found");
		}

		uH = new UserHistory();
	}

	/**
	 * Constructor that accepts the file name of the dictionary
	 */
	public AutoCompleter(String dictFileName, String userHistoryFileName)
	{
		System.out.println("UserHistory Found\n");

		dlb = new DLB();

		try
		{
			File dict = new File(dictFileName);
			Scanner dictScan = new Scanner(dict);
			while (dictScan.hasNextLine())
			{
				String wordToAdd = dictScan.nextLine();
				dlb.add(wordToAdd);
			}
			dictScan.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Dict file could not be found");
		}

		uH = new UserHistory();

		try
		{
			File userH = new File(userHistoryFileName);
			Scanner userHScan = new Scanner(userH);
			while (userHScan.hasNextLine())
			{
				String wordToAdd = userHScan.nextLine();
				uH.add(wordToAdd);
			}
			userHScan.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("UserH file could not be found");
		}
	}

	/**
	 * Produce up to 5 suggestions based on the current word the user has
	 * entered These suggestions should be pulled first from the user history
	 * dictionary then from the initial dictionary
	 *
	 * @param 	next char the user just entered
	 *
	 * @return	ArrayList<String> List of up to 5 words prefixed by cur
	 */	
	public ArrayList<String> nextChar(char next)
	{
		ArrayList<String> uHStrings = new ArrayList<String>();

		if (uH.count() > 0)
		{
			int hmm = uH.searchByChar(next);
			uHStrings = uH.suggest();
		}

		int hmmm = dlb.searchByChar(next);
		ArrayList<String> dlbStrings = dlb.suggest();

		for (String s : dlbStrings)
		{
			if (uHStrings.size() >= 5)
				break;
			else
				uHStrings.add(s);
		}
		return uHStrings;
	}

	/**
	 * Process the user having selected the current word
	 *
	 * @param 	cur String representing the text the user has entered so far
	 */
	public void finishWord(String cur)
	{
		dlb.resetByChar();
		uH.resetByChar();
		uH.add(cur);

		ArrayList<String> uHconts = uH.traverse();
		System.out.println("User History:");
		for (String s : uHconts) {
			System.out.println(s);
		}
		System.out.println();
	}

	/**
	 * Save the state of the user history to a file
	 *
	 * @param	fname String filename to write history state to
	 */
	public void saveUserHistory(String fname)
	{
		File userHistory = new File(fname);
		if (!userHistory.exists())
		{
			try
			{
				userHistory.createNewFile();
			}
			catch (Exception e)
			{
				System.out.println("Error on User History creation");
			}
		}

		try 
		{
    		FileWriter userHWrite = new FileWriter(fname, false);
    		ArrayList<String> uHconts = uH.traverse();
    		for (String s : uHconts) {
				userHWrite.write(s+"\n");
			}
    		userHWrite.close();
		} 
		catch (IOException e) 
		{
    		System.out.println("Error writing to file");
		}
	}
}