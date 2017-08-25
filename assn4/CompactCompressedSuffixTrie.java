package assn4;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CompactCompressedSuffixTrie {
	private SuffixTrieNode root;
	private String dnaSeq;

	/** Question 1
	 * Constructor for compact compressed suffix trie. 
	 *  It invokes readFromFile() method and buildTrie() method. The former does
	 *   the majority of the work building the suffix trie.
	 * The auxiliary class SuffixTrieNode was created to help implementing this constructor. This
	 *  class uses java HashMap to store keys (starting char of the suffix) 
	 *   and values (node that contains the value of its edge and reference to its children). 
	 */
	public CompactCompressedSuffixTrie(String f) {

		// Reading the DNA sequence from file
		// and use '$' to indicate end of string
		this.dnaSeq = readFromFile(f) + '$'; 
		String suffix;
		this.root = new SuffixTrieNode();
		// suffixIndex will be set to -1 by default at the beginning
		this.root.setIndex(-1);
		// after every suffix inserted into suffix tree, decrease length of
		// dnaSeq by length of suffix without the '$'
		for (int i = this.dnaSeq.length() - 1; i >= 0; i--) {
			suffix = this.dnaSeq.substring(i);

			if (suffix.length() == 0)
				continue; //no need to insert it in this case
			
			this.buildTrie(this.root, suffix, i, 0, false);
		}
	}

	/**
	 *  buildTrie() method runs in O(e*n log n) time.
	 *  Justification: 
	 *  	#Traversing to insert into the tree will cost n log n time where n = number of nodes
	 *  	#Iterate through every character of the suffix (in the edge) will cost 'e' time 
	 *  		where e = length of suffix in the edge
	 *  
	 * Ref: https://www.youtube.com/watch?v=VA9m_l6LpwI
	 */
	private void buildTrie(SuffixTrieNode currentNode, String suffix, int index, int diffIndex, boolean fork) {

		if (currentNode.getChildren().size() != 0) { 
		//if node has children
			boolean match = false;
			for (SuffixTrieNode child : currentNode.getChildren().values()) {
				// If char of the string corresponds to suffix in the edge
				if ( child.getEdge().charAt(0) == suffix.charAt(0) 
						&& !(child.getEdge().length() == 0 || suffix.length() == 0) ) {

					int breakpoint = -1;
					for (int i = 1; i <= child.getEdge().length(); i++) {  // this will iterate through every char of the edge
						try {
							if (child.getEdge().charAt(i) != suffix.charAt(i)) {
								breakpoint = i;
								break;
							}
						} catch (Exception e) {
							//if the edge overflows
							breakpoint = i;
						}
					}
					// subtract the string from the breakpoint
					if (breakpoint != -1) {
						suffix = suffix.substring(breakpoint);
					}

					if (suffix.length() == 0) {
						continue;
					}

					match = true;
					// we will create a new fork (branch) for current node if it has children 
					// and there are still letters in the suffix
					if (child.getChildren().size() != 0 && breakpoint < child.getEdge().length()) {
						fork = true;
					} else {
						fork = false;
					}
					
					this.buildTrie(child, suffix, index, breakpoint, fork);
					currentNode.setIndex(index);
					break;
				}
			}

			// Create a new branch when nothing matches and suffix length is at least 1
			if (!match && suffix.length() >= 1) {
				SuffixTrieNode child = new SuffixTrieNode();
				child.setEdge(suffix);
				child.setIndex(index);
				currentNode.addChild(suffix.charAt(0), child);
				currentNode.setIndex(index);
			}
		} else {
		//If node does not have children
			if (fork) {
				//create a new fork in current node
				String edge = currentNode.getEdge();

				// get the current edge and update it
				// Note: diffIndex == breakpoint of prev 'recursion round'
				String updatedEdge = edge.substring(0, diffIndex);
				String newEdge = edge.substring(diffIndex);

				SuffixTrieNode child1 = new SuffixTrieNode();
				child1.setEdge(newEdge);
				child1.setIndex(currentNode.getIndex());
				child1.copyChild(currentNode);
				//add the child with suffix's 1st letter as a key, and childNode as a value
				currentNode.forkChild(newEdge.charAt(0), child1);

				SuffixTrieNode child2 = new SuffixTrieNode();
				child2.setEdge(suffix);
				child2.setIndex(index);
				//add the child with suffix's 1st letter as a key, and childNode as a value
				currentNode.addChild(suffix.charAt(0), child2);

				//updates currentNode's edge and index
				currentNode.setEdge(updatedEdge);
				currentNode.setIndex(index);

			} else {
				// empty node case
				if (currentNode == this.root) { // Append a new child to node if
					// current node is root

					SuffixTrieNode child = new SuffixTrieNode(); 
					child.setEdge(suffix);
					child.setIndex(index);
					currentNode.addChild(suffix.charAt(0), child);
					
				} else { // fork if the current node is a child
					/**
					 * Note to myself: I'm pretty much repeating what I have in if(fork==true) part above, 
					 * 		check if you can improve this later. (maybe put the code in a method idk)
					 */
					
					String edge = currentNode.getEdge();

					String updatedEdge = edge.substring(0, diffIndex);
					String newEdge = edge.substring(diffIndex);

					SuffixTrieNode child1 = new SuffixTrieNode();
					child1.setEdge(newEdge);
					child1.setIndex(currentNode.getIndex());
					currentNode.addChild(newEdge.charAt(0), child1);

					SuffixTrieNode child2 = new SuffixTrieNode();
					child2.setEdge(suffix);
					child2.setIndex(index);
					currentNode.addChild(suffix.charAt(0), child2);

					currentNode.setEdge(updatedEdge);
					currentNode.setIndex(index);
				}
			}
		}
	}
	// note: try to implement unkonnen algorithm if there's time (for bonus marks)  
	

	/** Question 2
	 * findString() method runs in O(|s|) time, where |s| represents 
	 * 		the length of the given string.
	 * Justification: It iterates through every character of the string and compares it
	 * 		to the edges that have a corresponding suffix only.
	 * 
	 */
	public int findString(String s) {
		SuffixTrieNode currentNode = this.root;
		int index = -1;
		boolean inEdge = false;
		int edgeIndex = 0;

		if (s.equals(this.dnaSeq))
			return 0; //if they're equal then the 's' is found at index 0
		
		// Traverse down the tree with looking for each character
		for (int i = 0; i < s.length(); i++) {
			// If length of edge > 1 character,
			// follow this edge character by character
			if (inEdge) {
				try {
					// if char matches look for the next one in the edge
					if (s.charAt(i) == currentNode.getEdge().charAt(edgeIndex)) {
						edgeIndex++;
						continue;
					} else {
						// Stop when char doesnt match with suffix in the edge
						return -1;
					}
				} catch (Exception e) {
					// when suffix in edge overflows, go back to prev char
					// to try next child node's edge
					inEdge = false;
					i--;
				}
			} else {
				try {
					//gets the char s[i] to get the node that character as a key.
					currentNode = currentNode.getChildren().get(s.charAt(i));
					if (currentNode.getEdge().length() > 1) {
						inEdge = true;
						edgeIndex = 1;
					}

					index = currentNode.getIndex();
				} catch (Exception e) {
					// If overflows occurs here that means
					// no match was found: 
					return -1;
				}
			}
		}

		return index;
	}

	/** Question 3
	 * similarityAnaliser() runs in O(mn).
	 * 
	 * This method reads file1 and file2, computes the longest common 
	 * subsequence between them, and writes it into file3.
	 * Finally, it uses the formulae given in the assignment to compute the degree of similarity
	 * 
	 * Note: I also took the liberty of considering the cases in which the file(s) were empty.
	 * 
	 * Justification for time complexity analysis:
	 * 	readFromFile(): n = |f1| ; m = |f2| ; 
	 *  computeLCS() = |f1||f2| = mn
	 *  writeToFile() = 1 
	 *  The other operations are primitive operations.
	 *  Therefore, f(n) = n + m + mn + 1 =~ mn
	 */
	public static float similarityAnalyser(String f1, String f2, String f3) throws IOException {

		// reading the inputs from the 2 files
		f1 = readFromFile(f1);
		f2 = readFromFile(f2);

		String LCS = "";

		LCS = computeLongestCommonSubsequence(f1,f2);
		
		// write LCS to file
		writeToFile(LCS, f3);

		/**
		 * Calculate degree of similarity of two DNA sequences 
		 */
		if (f1.isEmpty() && f2.isEmpty()) {
			//if both of them are empty then they're equal
			return 1;
		} else if (f1.isEmpty() || f2.isEmpty()) {
			//if just one of them is empty then they're not equal at all
			return 0;
		}
	
		// similarity = |LCS(f1,f2)|/max{|f1|,|f2|}
		float similarityDegree = (float) LCS.length();
		if ((f1.length() > f2.length())) {
			similarityDegree = similarityDegree / f1.length() ;
		} else {
			similarityDegree = similarityDegree / f2.length();
		}
		
		return similarityDegree;
	}

	
	/**
	 * computeLongestCommonSubsequence(): computes the LCS using dynamic programming technique
	 *  It runs in O(mn) time.
	 *  Being m = length of f1 & n = length of f2
	 * 
	 * This is because populating the matrix takes O(|f1|*|f2|)
	 *  and retrieving the LCS from it takes O(longest) where longest=max(|f1|,|f2|)
	 * 
	 * Thus, overall time complexity is O(|f1|*|f2|).
	 * 
	 * Ref: lecture slides & youtube.com
	 */
	private static String computeLongestCommonSubsequence(String f1, String f2) {
		String lcs = "";
		
		int row = f1.length();
		int col = f2.length();
		// every cell in the matrix will represent the length of LCS 
		// at position row (f1) and col (f2)
		int[][] matrix = new int[row + 1][col + 1];

		// Populates the matrix of LCS' lengths
		for (int i = row - 1; i >= 0; i--) {
			for (int j = col - 1; j >= 0; j--) {
				if (f1.charAt(i) == f2.charAt(j))
					matrix[i][j] = matrix[i + 1][j + 1] + 1;
				else
					matrix[i][j] = Math.max(matrix[i + 1][j], matrix[i][j + 1]);
			}
		}

		// Gets the LCS
		int i = 0, j = 0;
		while (i < row && j < col) {
			if (f1.charAt(i) == f2.charAt(j)) {
				lcs += f1.charAt(i);
				i++;
				j++;
			} else if (matrix[i + 1][j] >= matrix[i][j + 1]) {
				i++;
			} else {
				j++;
			}
		}

		//System.out.println("oz.lcs = "+ lcs);
		return lcs;
	}

	/** Auxiliary method to read files
	 * In the worst case every character can appear in one line,
	 * so we can say that this method runs in O(|file|) 
	 * where |file| is the length of the string in the file
	 */
	private static String readFromFile(String file) {
		String dna = "";
		try {
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(file));
			line = reader.readLine();
			while (line != null && !line.isEmpty()) {
				dna += line;
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			System.out.println(file + " does not exist.");
			e.printStackTrace();
		}
		return dna;
	}

	/** Auxiliary method to create files for q3
	 * runs in O(1) constant time
	 */
	private static void writeToFile(String lcs, String file) throws IOException {

		File writeFile = new File(file);
		try {
			
			//if already exists delete it
			if (writeFile.exists()) { 
				writeFile.delete();
			}

			try {
				// create file to be written
				writeFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			FileWriter fw = new FileWriter(writeFile.getName(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(lcs);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/** MAIN METHOD **/
	public static void main(String args[]) throws Exception {

		/**
		 * Construct a compact compressed suffix trie named trie1
		 */
		CompactCompressedSuffixTrie trie1 = new CompactCompressedSuffixTrie("file1.txt");

		System.out.println("ACTTCGTAAG is at: " + trie1.findString("ACTTCGTAAG"));

		System.out.println("AAAACAACTTCG is at: " + trie1.findString("AAAACAACTTCG"));

		System.out.println("ACTTCGTAAGGTT : " + trie1.findString("ACTTCGTAAGGTT"));

		System.out.println(CompactCompressedSuffixTrie.similarityAnalyser("file2.txt", "file3.txt", "file4.txt"));
	}

}

class SuffixTrieNode {

	private Map<Character, SuffixTrieNode> children; //to ensure uniqueness of keys 
	private String edge; //stores the suffix
	private int start;	 //stores the index in which the suffix begins at

	public SuffixTrieNode() {
		children = new HashMap<>();
		edge = "";
	}

	public String getEdge() {
		return this.edge;
	}

	public void setEdge(String edge) {
		this.edge = edge;
	}

	public int getIndex() {
		return this.start;
	}

	public void setIndex(int start) {
		this.start = start;
	}

	public void addChild(Character letter, SuffixTrieNode child) {
		this.children.put(letter, child);
	}

	public void forkChild(Character letter, SuffixTrieNode child) {
		this.children.clear();
		this.children.put(letter, child);
	}

	public void copyChild(SuffixTrieNode original) {
		for (SuffixTrieNode copy : original.getChildren().values()) {
			this.children.put(copy.getEdge().charAt(0), copy);
		}
	}
	
	public Map<Character, SuffixTrieNode> getChildren() {
		return this.children;
	}

}
