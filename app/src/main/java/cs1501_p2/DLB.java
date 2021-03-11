/**
 * A De La Briandais search trie implemented for Project 2
 * @author David Roberts (djr107)
 */

package cs1501_p2;

import java.util.*;

public class DLB implements Dict
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

	public DLB()
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
		//System.out.println("\nAdding "+key);
		if (count == 0)
		{
			//System.out.println("Added first word");
			head = new DLBNode(key.charAt(0));
			head.setDown(new DLBNode('*'));
		}
		else
		{
			DLBNode curr = head;
			for (int i=0; i<key.length(); i++)
			{
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
						//System.out.println(" w/ node: "+curr.getDown().getLet());
						//System.out.println("Looking down");
						curr = curr.getDown();
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
			//System.out.println("\nDone Word");
			curr.setDown(new DLBNode('*'));
		}
		count++;
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

		if (curr.getLet() == '*')
			return true;
		else
			return false;
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
			//System.out.println("\nComparing curr: "+curr.getLet()+" with char: "+pre.charAt(i)+" at index: "+i);
			if (curr.getLet() == pre.charAt(i))
			{
				//System.out.println("Down");
				curr = curr.getDown();
				if (i == pre.length()-1 && curr != null)
				{
					if (curr.getLet() != '*' || (curr.getLet() == '*' && curr.getRight() != null))
					{
						//System.out.println("Found it done");
						return true;
					}
				}
			}
			else
			{
				if (curr.getRight() != null)
				{
					//System.out.println("Right");
					curr = curr.getRight();
					i--;
				}
				else
				{
					//System.out.println("Not Found1");
					return false;
				}
			}
		}
		//System.out.println("Not Found2");
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
				if (i == (searchByCharArr.size()-1))
				{
					if (curr.getLet() != '*')
						return 0;
					else if (curr.getLet() == '*' && curr.getRight() == null)
						return 1;
					else if (curr.getLet() == '*' && curr.getRight() != null)
						return 2;
				}
				//System.out.println("Not end yet");
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
		}
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

		if (curr.getLet() == '*')
		{
			strings.add(pre.toString());
			//System.out.println("Added: "+pre.toString());
		}

		StringBuilder pre2 = pre;
		if (curr.getLet() == '*')
			curr = curr.getRight();
		if (curr != null)
		{
			//System.out.println("Call");
			suggestRec(curr, pre2, strings);
		}

		return strings;
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
			//System.out.println("Current node: "+curr.getLet());
			if (curr.getLet() != '*')
			{
				charArr.append(curr.getLet());
				//System.out.println("Call1");
				suggestRec(curr.getDown(), charArr, strings);
				charArr.deleteCharAt(charArr.length()-1);
				dontAdd = true;
				//System.out.println("Popped w/ charArr: "+charArr.toString());
			}

			if (!strings.contains(charArr.toString()) && !dontAdd)
			{
				strings.add(charArr.toString());
				dontAdd = false;
				//System.out.println("Added: "+charArr.toString());
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
	 *
	 * @return	ArrayList<String> List of all valid words in the dictionary
	 */
	public ArrayList<String> traverse()
	{
		DLBNode curr = head;

		ArrayList<String> strings = new ArrayList<String>();

		for (int i=0; i<52; i++)
		{
			StringBuilder sb = new StringBuilder();
			traverseRec(curr, sb, strings);
			
			if (curr.getRight() != null)
				curr = curr.getRight();
			else
				break;
		}

		return strings;
	}

	/**
	 * Recursively traverses through dict to find valid words
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
		if (curr.getLet() != '*')
		{
			charArr.append(curr.getLet());
			//System.out.println("Call1");
			traverseRec(curr.getDown(), charArr, strings);
			charArr.deleteCharAt(charArr.length()-1);
			dontAdd = true;
			//System.out.println("Popped w/ charArr: "+charArr.toString());
		}

		if (!strings.contains(charArr.toString()) && !dontAdd)
		{
			strings.add(charArr.toString());
			dontAdd = false;
			//System.out.println("Added: "+charArr.toString());
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