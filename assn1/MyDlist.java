/*
 * Assignment 1 - COMP9024 - 2016s2
 * Oscar Arzamendia / z5104193
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class MyDlist extends DList {

	//Q1: This constructor creates an empty doubly linked list.
	public MyDlist(){
		super();
	}
	
	//Q2: This constructor creates a doubly linked list by reading all strings from a text file
	//		but if the argument is equals to "stdin" read from standard input
	public MyDlist(String f){
		if(f.equals("stdin")) {
			String input = null;
			Scanner scanner = new Scanner(System.in);  
			//Do until user inputs empty line
			while((input=scanner.nextLine()).isEmpty()==false) {
				//puts the input in a new node of MyDlist.
				DNode newNode = new DNode(input, null, null);
				this.addLast(newNode);
			}
			scanner.close();

		} else {

			String[] elements;
			String input = null;
			BufferedReader inputFile = null;
			
			try {
				//get the file
				inputFile = new BufferedReader(new FileReader(f));
			} catch (FileNotFoundException e) {
				System.out.println("There's no file "+f);
				return;
			}
			try {
				//read each line until there are no more lines
				while((input = inputFile.readLine())!=null){
					elements = input.split(" ");  //each word of the line is stored in an array.
					//each word will be a new Node added at the end.
					for (String word : elements){
						DNode newNode = new DNode(word,null,null);
						this.addLast(newNode);
					}
				}
			} catch (IOException e) {
				System.out.println("Error while reading the file");
				return;
			}
			
			//closing the file
			try {
				inputFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}


	//Q3. Print elements of a list on standard output 
	public void printList() {
		DNode current_node = this.header.next; // current is this class's header pointing to next node
			
		//While current is not at end of superclass print its value then move to the next one
		while (current_node != super.trailer) { 
			System.out.println(current_node.element); 
			current_node = current_node.next; 
		}
	}
	
	//Q4. Return copy of received doubly linked list "u"
	public static MyDlist cloneList(MyDlist u) {
		MyDlist clonedList = new MyDlist();
		
		//if u's size is 0 just return the new list
		if(u.size()!=0) {
			//get the node that is after the header sentinel
			DNode curNode = u.header.next;
			//while curNode isn't at the end, create a new DNode with the same element as the current node. 
			//Then pass this DNode to the end of the cloned list.
			while(curNode != u.trailer) {
				DNode newClone = new DNode(curNode.element, null, null);
				clonedList.addLast(newClone);
				curNode = curNode.next;
			}
		}
		return clonedList;
	}
	
	
	/*
	* Analysis of union = 6 + 6n+1 + 6n+1 + n^2 + 1
	*                   = n^2 + 12n + 9
	* F(n) = n^2
	*/
	public static MyDlist union(MyDlist u, MyDlist v) {
		//create a new empty MyDlist named union which will be the union of u and v
		MyDlist unionSet = new MyDlist();  //1 (instantiate)
		//create nodes to refer to the first Node of u and v
		DNode uNode = u.header.next;  //1 + 1  (instantiate and reference next)
		DNode vNode = v.header.next;  //1 + 1  (instantiate and reference next)
		//newNode will be used push Nodes into the union
		DNode newNode = null;  //1 (instantiate)
		//Analysis so far = 1 + 2 + 2 + 1 = 6
		
		//get each Node of MyDlist u, create an new DNode with the same element 
		//of current uNode and add it to the the union.
		while(uNode!=u.trailer) { //1 (comparison)
			newNode = new DNode(uNode.getElement(),null, null); //2 (instantiate and get node element)
			unionSet.addLast(newNode); //1 (append newNode to the end)
			uNode = uNode.next;  //2 (assign and reference next)
		}
		//Analysis so far: 1*(n+1) + 2n + 1n + 2n = 6n +1
		
		//Same operations as above but this time on MyDlist v
		while(vNode!=v.trailer) {
			newNode = new DNode(vNode.getElement(),null, null);
			unionSet.addLast(newNode);
			vNode = vNode.next;
		}
		//Analysis so far: 1*(n+1) + 2n + 1n + 2n = 6n +1
		
		eliminateDuplicates(unionSet);  // n^2 (see the analysis below).
		
		return unionSet; //1 

	  }
	  
	  
	 /*
	 * Analysis of eliminateDuplicates  = 7 + 3n + 16(n(n+1)/2) + 2n
	 *                              	= 8(n^2) + 13n + 7  
	 *	F(n) = O(n^2).
	 */
	 private static void eliminateDuplicates(MyDlist u) {
		//only check if not empty
		if(u.size() != 0 ) { //1 (comparison)
			//set the uNode to first node of the list u
			DNode uNode = u.header.next; //1 + 1  (instantiate and reference next)
			DNode uNodeNext = null;	//1   (instantiate and assign)
			DNode previous = null;	//1   (instantiate and assign)
			DNode next = null;		//1   (instantiate and assign)
			DNode dupli = null;		//1   (instantiate and assign)
			//Analysis so far = 7. 
		
			//go through the list element by element
			while(uNode != u.trailer) { //1 (comparison)
				uNodeNext = uNode.next; //2 (assign and reference to next)
				
				//compare the current element with all the elements after it
				while(uNodeNext != u.trailer) {
					//if the two nodes contain the same element then get rid of the latter node.
					if(uNode.getElement().equals(uNodeNext.getElement())) { //3 (access two values and compare them)
						//get the previous and the next node to point to each other
						previous = uNodeNext.prev;	//2 (get previous and assign)
						next = uNodeNext.next;		//2 (get next and assign)
						previous.setNext(next);		//1 (assign)
						next.setPrev(previous);		//1 (assign)
						u.size--;					//2 (get the value of size and set it to the new value)
						dupli = uNodeNext;			//1
					}
					//Analysis so far: On the 2nd while the number of comparisons gets smaller by 1 each time. So, 12 * n(n+1)/2 

					uNodeNext = uNodeNext.next; //2
					
					//if there was a duplicate element dereference its pointers and delete it
					if(dupli != null) {			//1
						dupli.setPrev(null);	//1
						dupli.setNext(null);	//1
						dupli = null;			//1
					}
					//Analysis so far: 4 * (n(n+1)/2) [because these operations are still being performed inside the 2nd while]

				} //end of 2nd while. Analysis of 2nd while: 16 * n(n+1)/2
				
				uNode = uNode.next; //2n (assign and reference to next)
				
			}
		}
	 }
	 
	 /*
	 * Analysis of eliminateDuplicates  = 13 + n+1 + 2n + m+1 + 8m + 2n + 1
	 *                                  = 9m + 5n + 16
	 *    But m is equals to n(n+1)/2:  = 9 n(n+1)/2 + 5n + 16  = (9/2)*n^2 + (19/2)*n + 16
	 *	F(n) = O(n^2).
	 */
	public static MyDlist intersection(MyDlist u, MyDlist v) {
		MyDlist intersect = new MyDlist();	//1 
		MyDlist smallList = null;			//1
		MyDlist bigList = null;				//1
			
		//function finds the smaller list to perform the intersection on
		if(u.size() < v.size()) { //3
			smallList = u; //1
			bigList = v; //1
		} else {
			smallList = v; //1
			bigList = u; //1
		}
		
		DNode sNode = smallList.header.next; //2 (assign and reference to next)
		DNode bNode = null;					 //1 

		while(sNode != smallList.trailer) {	// n+1
			/*for each node of the smaller list go through the bigger list
			 *and compare each element to see if they're the same.*/
			bNode = bigList.header.next;			//2n (assign and reference to next)
			
			while(bNode != bigList.trailer) { // There will be m iterations for every n iterations, and in the worst case m=n
				//if the elements are the same add to MyDlist intersect
				if(sNode.getElement().equals(bNode.getElement())) {	//3m  (2 references and 1 comparison)
					DNode newNode = new DNode(sNode.getElement(), null, null);	//2m (assignment and reference to element)
					intersect.addLast(newNode); //1m (add node to the list)
				}
				bNode = bNode.next; //2m (assign and reference to next)
				
			} //end of inner while
			
			sNode = sNode.next; //2n (assign and reference to next)
			
		} //end of outer while		
		
		return intersect; //1
		
	} 

	 
	 public static void main(String[] args) throws Exception{
		 
		   System.out.println("please type some strings, one string each line and an empty line for the end of input:");
		    /** Create the first doubly linked list
		    by reading all the strings from the standard input. */
		    MyDlist firstList = new MyDlist("stdin");
		    
		   /** Print all elememts in firstList */
		    firstList.printList();
		   
		   /** Create the second doubly linked list                         
		    by reading all the strings from the file myfile that contains some strings. */
		  
		   /** Replace the argument by the full path name of the text file */  
		    MyDlist secondList=new MyDlist("C:/Users/Oscar Arzamendia/workspace/9024_assn1/myfile.txt");

		   /** Print all elememts in secondList */        
		    //System.out.println("2nd list:");
		    secondList.printList();

		   /** Clone firstList */
		    MyDlist thirdList = cloneList(firstList);

		   /** Print all elements in thirdList. */
		    //System.out.println("3rd list:");
		    thirdList.printList();

		  /** Clone secondList */
		    MyDlist fourthList = cloneList(secondList);

		   /** Print all elements in fourthList. */
		    //System.out.println("4th list:");
		    fourthList.printList();
		    
		   /** Compute the union of firstList and secondList */
		    MyDlist fifthList = union(firstList, secondList);

		   /** Print all elements in thirdList. */ 
		    //System.out.println("\nUnion: ");
		    fifthList.printList(); 

		   /** Compute the intersection of thirdList and fourthList */
		    MyDlist sixthList = intersection(thirdList, fourthList);

		   /** Print all elements in fourthList. */
		    //System.out.println("\nIntersection:");
		    sixthList.printList();
		  }
	

}