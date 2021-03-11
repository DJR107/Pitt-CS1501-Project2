/**
 * A user history of previously selected words DLB style
 * @author David Roberts (djr107)
 */

package cs1501_p2;

import java.util.*;

public class UserHistory implements Dict
{
	/**
	 * Start of the DLB
	 */
	private DLBNode head;

	/**
	 * Count of how many words are currently stored in the dictionary
	 */
	private int count;

	/**
	 * List of chars used in searchByChar
	 */
	private ArrayList<Character> searchByCharArr;

	/**
	 * Int that results from searchByChar(), used in suggest()
	 */
	private int searchByCharInt;

	public UserHistory()
	{
		head = null;
		count = 0;
		searchByCharArr = new ArrayList<Character>();
		searchByCharInt = 0;
	}

	/**
	 * Add a new word to the dictionary
	 *
	 * @param 	key New word to be added to the dictionary
	 */	
	public void add(String key)
	{
		//System.out.println("\nAdding: "+key);
		DLBNode curr = head;
		for (int i=0; i<key.length(); i++)
		{
			if (count == 0 && i == 0)
			{
				head = new DLBNode(key.charAt(i));
				curr = head;
			}
			//System.out.println("Comparing curr: "+curr.getLet()+" with char: "+key.charAt(i)+" at index: "+i);
			if (curr.getLet() == key.charAt(i))
			{
				//System.out.print("Down");
				if (curr.getDown() == null)
				{
					if (i != key.length()-1)
					{
						//System.out.println("\nNew node down");
						curr.setDown(new DLBNode(key.charAt(i+1)));
						curr = curr.getDown();
					}
				}
				else
				{
					if (i == key.length()-1)
					{
						try
						{
							int occurence = Integer.parseInt(String.valueOf(curr.getDown().getLet()));
							occurence++;
							//System.out.println("\nWord Already Here, Incrementing Occurence: "+occurence);
							curr.setDown(new DLBNode(Character.forDigit(occurence, 10)));
							//System.out.println("New Down: "+curr.getDown().getLet());
						}
						catch (Exception e)
						{
							//System.out.println(" w/ node: "+curr.getDown().getLet());
							//System.out.println("Looking down1");
							curr = curr.getDown();
						}
					}
					else
					{
						//System.out.println(" w/ node: "+curr.getDown().getLet());
						//System.out.println("Looking down2");
						curr = curr.getDown();
					}
				}
			}
			else
			{
				//System.out.print("Right");
				if (curr.getRight() == null)
				{
					//System.out.println("\nNew node to right");
					curr.setRight(new DLBNode(key.charAt(i)));
					curr = curr.getRight();
					if (i != key.length()-1)
						i--;
				}
				else
				{
					//System.out.println(" w/ node: "+curr.getRight().getLet());
					//System.out.println("Looking to right");
					curr = curr.getRight();
					i--;
				}
			}
		}
		if (curr.getDown() == null)
		{
			//System.out.println("\nNew Word");
			curr.setDown(new DLBNode('1'));
			count++;
		}
	}

	/**
	 * Check if the dictionary contains a word
	 *
	 * @param	key	Word to search the dictionary for
	 *
	 * @return	true if key is in the dictionary, false otherwise
	 */
	public boolean contains(String key)
	{
		DLBNode curr = head;

		for (int i=0; i<key.length(); i++)
		{
			if (curr.getLet() == key.charAt(i))
			{
				curr = curr.getDown();
			}
			else
			{
				if (curr.getRight() != null)
				{
					curr = curr.getRight();
					i--;
				}
				else
					return false;
			}
		}

		try
		{
			int occurence = Integer.parseInt(String.valueOf(curr.getLet()));
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	/**
	 * Check if a String is a valid prefix to a word in the dictionary
	 *
	 * @param	pre	Prefix to search the dictionary for
	 *
	 * @return	true if prefix is valid, false otherwise
	 */
	public boolean containsPrefix(String pre)
	{
		DLBNode curr = head;

		for (int i=0; i<pre.length(); i++)
		{
			if (curr.getLet() == pre.charAt(i))
			{
				curr = curr.getDown();
				if (i == pre.length()-1 && curr.getRight() != null)
					return true;
			}
			else
			{
				if (curr.getRight() != null)
				{
					curr = curr.getRight();
					i--;
				}
				else
					return false;
			}
		}
		return false;
	}

	/**
	 * Search for a word one character at a time
	 *
	 * @param	next Next character to search for
	 *
	 * @return	int value indicating result for current by-character search:
	 *				-1: not a valid word or prefix
	 *				 0: valid prefix, but not a valid word
	 *				 1: valid word, but not a valid prefix to any other words
	 *				 2: both valid word and a valid prefix to other words
	 */
	public int searchByChar(char next)
	{
		searchByCharArr.add(next);

		DLBNode curr = head;

		for (int i=0; i<searchByCharArr.size(); i++)
		{
			//System.out.println("Current node: "+curr.getLet()+" Current char: "+searchByCharArr.get(i)+" w/ index: "+i+" and size: "+searchByCharArr.size());
			if (curr.getLet() == searchByCharArr.get(i))
			{
				//System.out.println("Equals");
				curr = curr.getDown();
				if (i == searchByCharArr.size()-1)
				{
					//System.out.println("End charArr");
					try
					{
						int occurence = Integer.parseInt(String.valueOf(curr.getLet()));
						if (curr.getRight() == null)
							return 1;
						else if (curr.getRight() != null)
							return 1;
					}
					catch (Exception e)
					{
						return 0;
					}
				}
			}
			else
			{
				//System.out.println("Does not equal");
				if (curr.getRight() != null)
				{
					//System.out.println("Going right");
					curr = curr.getRight();
					i--;
				}
				else
					return -1;
			}
			//System.out.println("End loop");
		}
		//System.out.println("Returned -1");
		return -1;
	}

	/**
	 * Reset the state of the current by-character search
	 */
	public void resetByChar()
	{
		searchByCharArr = new ArrayList<Character>();
	}

	/**
	 * Suggest up to 5 words from the dictionary based on the current
	 * by-character search
	 * 
	 * @return	ArrayList<String> List of up to 5 words that are prefixed by
	 *			the current by-character search
	 */
	public ArrayList<String> suggest()
	{
		StringBuilder pre = new StringBuilder();
		for (int i=0; i<searchByCharArr.size(); i++)
		{
			pre.append(searchByCharArr.get(i));
		}
		//System.out.println("uH pre: "+pre.toString());

		ArrayList<String> strings = new ArrayList<String>();
		//System.out.println("searchByCharInt = "+searchByCharInt);
		if (searchByCharInt < 0)
			return strings;

		DLBNode curr = head;
		for (int i=0; i<searchByCharArr.size(); i++)
		{
			if (curr.getLet() == searchByCharArr.get(i))
			{
				curr = curr.getDown();
			}
			else
			{
				if (curr.getRight() != null)
				{
					curr = curr.getRight();
					i--;
				}
			}
		}

		try
		{
			int occurence = Integer.parseInt(String.valueOf(curr.getLet()));
			//System.out.println("Added: "+pre.toString());
			strings.add(pre.toString());
			curr = curr.getRight();
		}
		catch (Exception ignored)
		{}

		StringBuilder pre2 = pre;
		while (strings.size() < 5 && curr != null)
		{
			//System.out.println("Call");
			suggestRec(curr, pre2, strings);
			curr = curr.getRight();
		}

		ArrayList<String> strings2 = new ArrayList<String>();

		while (strings.size() > 0)
		{
			//System.out.println("String: "+strings.get(0));
			StringBuilder maybeString = new StringBuilder(strings.get(0));
			int occurence = Integer.parseInt(String.valueOf(strings.get(0).charAt(strings.get(0).length()-1)));
			for (String s : strings)
			{
				int occurence1 = Integer.parseInt(String.valueOf(s.charAt(s.length()-1)));
				//System.out.println("Occurence1: "+occurence1);
				if (occurence1 > occurence)
					maybeString = new StringBuilder(s);
			}
			//System.out.println("MaybeString: "+maybeString);
			strings.remove(maybeString.toString());
			maybeString.deleteCharAt(maybeString.length()-1);
			strings2.add(maybeString.toString());
		}

		return strings2;
	}

	/**
	 * Recursively searches through dict to suggest up to 5 words from the dictionary 
	 * based on the current by-character search
	 * 
	 * @param	curr Current node of the search
	 *
	 * @param   charArr Current string of chars that may or may be a valid word
	 *
	 * @param   strings List of valid words to return and suggest
	 */
	private void suggestRec(DLBNode curr, StringBuilder charArr, ArrayList<String> strings)
	{
		if (strings.size() < 5)
		{
			boolean dontAdd = false;
			//System.out.println("Current charArr: "+charArr.toString());
			charArr.append(curr.getLet());
			try
			{
				int occurence = Integer.parseInt(String.valueOf(curr.getLet()));
				if (!strings.contains(charArr.toString()) && !dontAdd)
				{
					//System.out.println("Added: "+charArr.toString());
					strings.add(charArr.toString());
					charArr.deleteCharAt(charArr.length()-1);
				}
				else
				{
					charArr.deleteCharAt(charArr.length()-1);
					//System.out.println("Not Added");	
				}	
			}
			catch (Exception e)
			{
				//System.out.println("Call1");
				suggestRec(curr.getDown(), charArr, strings);
				charArr.deleteCharAt(charArr.length()-1);
				dontAdd = true;
				//System.out.println("Popped w/ charArr: "+charArr.toString());
			}

			while (curr.getRight() != null)
			{
				curr = curr.getRight();
				//System.out.println("Call2");
				suggestRec(curr, charArr, strings);
			}
		}
	}

	/**
	 * List all of the words currently stored in the dictionary
	 * @return	ArrayList<String> List of all valid words in the dictionary
	 */
	public ArrayList<String> traverse()
	{
		DLBNode curr = head;

		ArrayList<String> strings = new ArrayList<String>();

		StringBuilder sb = new StringBuilder();
		traverseRec(curr, sb, strings);

		return strings;
	}

	/**
	 * Recursively traverses through UserHistory to find valid words
	 * 
	 * @param	curr Current node of the search
	 *
	 * @param   charArr Current string of chars that may or may be a valid word
	 *
	 * @param   strings List of valid words to return
	 */
	private void traverseRec(DLBNode curr, StringBuilder charArr, ArrayList<String> strings)
	{
		boolean dontAdd = false;
		//System.out.println("Current charArr: "+charArr.toString());
		//System.out.println("Current node: "+curr.getLet());
		charArr.append(curr.getLet());
		try
		{
			int occurence = Integer.parseInt(String.valueOf(curr.getLet()));
			//System.out.println("Valid Word");
			if (!strings.contains(charArr.toString()) && !dontAdd)
			{
				//System.out.println("Added: "+charArr.toString());
				strings.add(charArr.toString());
				charArr.deleteCharAt(charArr.length()-1);
			}
			else
			{
				charArr.deleteCharAt(charArr.length()-1);
				//System.out.println("Not Added");	
			}
		}
		catch (Exception e)
		{
			//System.out.println("Call1");
			traverseRec(curr.getDown(), charArr, strings);
			charArr.deleteCharAt(charArr.length()-1);
			dontAdd = true;
			//System.out.println("Popped w/ charArr: "+charArr.toString());
		}

		while (curr.getRight() != null)
		{
			curr = curr.getRight();
			//System.out.println("Call2");
			traverseRec(curr, charArr, strings);
		}
	}

	/**
	 * Count the number of words in the dictionary
	 *
	 * @return	int, the number of (distinct) words in the dictionary
	 */
	public int count()
	{
		return count;
	}

	/**
	 * Sets searchByCharInt to i
	 */
	public void setSearchByCharInt(int i)
	{
		searchByCharInt = i;
	}
}