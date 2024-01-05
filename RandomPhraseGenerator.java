package comprehensive;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Sasha Rybalkina & Owen Ingle
 *
 * @version December 5, 2022
 */

public class RandomPhraseGenerator {
	
	private static HashMap<String, ArrayList<String[]>> map;
	Random rand = new Random();
	
	RandomPhraseGenerator(String filename) throws FileNotFoundException {
		map = generateMap(filename);
	}
	/**
	 * Method for building a hash map which contains all non-terminals in a given
	 * file as keys and all terminals in the file as values for their respective keys.
	 * 
	 * @param filename: The file used for building the hash map.
	 * @return: A hash map with all non-terminals as keys, and all terminals
	 * as values for their respective keys.
	 * @throws FileNotFoundException
	 */
	private HashMap<String, ArrayList<String[]>> generateMap(String filename) throws FileNotFoundException{
		HashMap<String, ArrayList<String[]>> map = new HashMap<String, ArrayList<String[]>>();
		String key = "";
		//List of all terminals for a specific non-terminal
		ArrayList<String[]> list = new ArrayList<String[]>();
		//Scans through the file line by line
		try (Scanner scanner = new Scanner(new File(filename))) {
			while (scanner.hasNextLine()) {
				//Turns each line in the file into a basic string array of terminals and non-terminals.
				String[] string = scanner.nextLine().split("<|>");
				//If the current line is an oppening bracket, sets the next line to be the current non-terminal key.
				if (string[0].equals("{")) {
					key = scanner.nextLine();
					key = key.substring(1, key.length()-1);
					//Adds all the terminals in the file that are under the key non-terminal to an array list
					while (!(string[0].equals("}"))) {
						string = scanner.nextLine().split("<|>");
						if (string[0].equals("}")) {
							break;
						}
						list.add(string);
					}
					map.put(key, list);
					list = new ArrayList<String[]>();
				}
			}
		}
		return map;
	}
	
	/**
	 * Randomly picks a phrase from the start key in the map, then replaces all non-terminals
	 * in the phrase with terminals.
	 * 
	 * @return: A randomly generated phrase.
	 */
	public String generatePhrase() {
		StringBuilder phrase = new StringBuilder();
		String[] base = map.get("start").get(rand.nextInt(map.get("start").size()));
		LinkedList<String> list = new LinkedList<String>();
		//Converts the start phrase into a linked list
		for (int i = 0; i < base.length; i++) {
			list.add(base[i]);
		}
		for (int i = 0; i < list.size(); i++) {
			//If the current string in the list is a key in the map, replaces the string with a random
			//value of the key in the map.
			if (map.containsKey(list.get(i))){
				int random = rand.nextInt(map.get(list.get(i)).size());
				String[] current = map.get(list.get(i)).get(random);
				list.set(i, current[0]);
				//Inserts all contents of the chosen value into the list if the chosen value has more than
				//one string
				for (int j = 1; j < current.length; j++) {
					list.add(j+i, current[j]);
				}
			}
			phrase.append(list.get(i));
		}
		return phrase.toString();
	}
	
	/**
	 * Main method for running the phrase generator.
	 * 
	 * @param args: the commands to run the phrase generator.
	 */
	public static void main(String[] args) {
		RandomPhraseGenerator pattern = null;
		try {
			pattern = new RandomPhraseGenerator(args[0]);
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}
		for (int i = 0; i < Integer.parseInt(args[1]); i++) {
			System.out.println(pattern.generatePhrase());
		}
	}
}