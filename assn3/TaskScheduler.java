/**
 * 	Asignment 3 - Data Structures 2016s2
 * 	Oscar Arzamendia - z5104193
 * 
 * Refs. for calculation of time complexity of operations with heaps:
 * 		 https://en.wikipedia.org/wiki/Priority_queue#Usual_implementation
 *  	 http://www.utdallas.edu/~ravip/cs3345/slidesweb/node5.html
 * 
 */

package net.datastructures;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;



/**
 * TaskScheduler runs in O(n log n) time, because:
 * 		putTaskInPQueue(): n log n
 * 		findSchedule():	   n log n
 * 		outputFile():	   1
 * 		checkArguments():  1
 *  Therefore: (2n log n) + 2 =~ n log n
 * */
public class TaskScheduler {
	
	static String feasible_sched = new String();
	
	static void scheduler(String file1, String file2, int m) {
		HeapPriorityQueue<Integer, Task> rTimePQueue = new HeapPriorityQueue<Integer, Task>();
		
		//checks that the given arguments are valid
		if(!checkArguments(file1, file2, m)){
			return;
		}
		
		putTaskInPQueue(file1, rTimePQueue);
		
		feasible_sched = findSchedule (rTimePQueue, m); 
		
		if (!feasible_sched.equals("")) {
			//System.out.println(feasible_sched);
			outputFile (file2, feasible_sched); 
		}		
		
	};
	
	/** outputFile() method runs in constant time: O(1)
	 * 
	 */
	private static void outputFile(String file2, String fs) {
		File newFile = new File(file2 + ".txt");
		
		try { 
			
			if (newFile.exists()) { //if already exists delete it
				newFile.delete();
			}
			
			try {
				//create the file
				newFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}			

			FileWriter fw = null;
			BufferedWriter bw = null;
			// create FileWriter getting name of File from writeFile object
			fw = new FileWriter(newFile.getName(), true); 
			// create BufferedWriter to write through
			bw = new BufferedWriter(fw);
			//write the String that contains the feasible schedule (fs) into file2
			bw.write(fs);
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/** findSchedule() method runs in O( n log n) time (being n number of tasks)
	 *  Justification: 
	 *  - While loop that inserts into pq2 and removes from pq1: n * 2 log n = 2n log n
	 *  	insert() operation takes log n time
	 *  	removeMin() operation takes log n time 
	 *  - While loop that iterates through pq2 and removesMin() in every iteration: n log n
	 *  	removeMin takes log n time
	 *  So we have in total: 3n log n =~ n log n
	 */
	private static String findSchedule(HeapPriorityQueue<Integer, Task> pq1, int m) {
		// TODO Auto-generated method stub
		//pq2 will be used to construct the schedule
		HeapPriorityQueue<Integer, Task> pq2 = new HeapPriorityQueue<Integer, Task>();
		// create string where tasks will be appended into
		String fs = "";
		
		
		int time = 0;
		// while pq1 is not empty do the operations,
		// at the end of the loop increments time by 1.
		while (!pq1.isEmpty()) {
			
			//We'll insert tasks from pq1 to pq2 whenever their release time allows us to do it.
			while (!pq1.isEmpty() && pq1.min().getKey() == time) { 
				//In pq1 find the key with minimum value, and create 't' with the object stored in pq1
				Task t = pq1.min().getValue();

				//insert 't' into pq2 (deadline is key of pq2, not release time)
				pq2.insert(t.getDeadline(), t);
				
				//Remove task 't' from pq1 (i.e. task with minimum release time)
				pq1.removeMin();
				
			}

			//counter to check the amount of machine cores
			int counter = 1;
			//while loop to append tasks to the string 'fs'
			while (!pq2.isEmpty() && counter <= m) { 
				counter = counter + 1; // increase counter by 1 for every iteration
				//if at this point there exists a task which deadline is less or 
				// equal to the current start time, then that means that no feasible schedule exists
				// because there were not enough machine cores 'm' and there were tasks NOT started in 
				// the previous iteration
				if (pq2.min().getKey() <= time) {
					System.out.println("No feasible schedule exists.");
					//pq2 = new HeapPriorityQueue<Integer, Task>();
					//pq1 = new HeapPriorityQueue<Integer, Task>();
					//fs = "" ;
					return ""; //System.exit(0);
				} else { // if a feasible schedule exists
					
						String taskName = pq2.min().getValue().getTaskname(); 

						//construct fs by appending taskName and time
						fs = fs + taskName + " " + time + " ";

						//after appending to 'fs' remove the task from pq2
						pq2.removeMin();
					
				} 

			} //end-while (when pq2 empty or all 'm' are occupied)
			
			time = time+1; //increment start time
			
		} //end-while (when pq1 is empty)
		
		return fs;
		
	}


	/**
	 * putTaskInPQueue() method runs approximately in O(n log n) time
	 * 	Justification:
	 * 		The program will iterate through every line in the file [every task has 3 attributes i.e 3n (n being number of tasks) ]
	 * 		then for every task (every 3 attributes found) will insert into the heap based priority queue.
	 * 		Therefore, since building trees from existing sequences of elements takes O(n log n) time, we can say that it runs in O(n log n).
	 * 	Ref: https://en.wikipedia.org/wiki/Priority_queue#Usual_implementation
	 */
	private static void putTaskInPQueue(String file1, HeapPriorityQueue<Integer, Task> pq1) {
		// TODO Auto-generated method stub
		File inputFile = new File(file1); // declare file object to be read from file1 as readFile
		Scanner s;
		String lastTask = new String();
		
		try {
			s = new Scanner(inputFile);
			//int i = 0; //oz 
			while (s.hasNext()) { // until eof
				//i = i+1;
				//System.out.println("oz "+i);
				// declare a new Task every 3 attributes
				Task task = new Task(); 
				//task name shouldnt be just a number
				if (!s.hasNextInt()) { 
					lastTask = s.next();
					char c = lastTask.charAt(0);
					if (Character.isLetter(c)){ //must begin with a letter
						task.setTaskname(lastTask);
					} else {
						s.close(); 
						System.out.println("input error when reading the attributes of the task " + lastTask);
						System.exit(0);
					}
					 
					//release time must be a number
					if (s.hasNextInt()) { 
						
						task.setReleasetime(s.nextInt());
						 
						// 3rd attribute (deadline) should be a number
						if (!s.hasNextInt()) { 
							s.close(); 
							System.out.println("input error when reading the attributes of the task " + task.getTaskname());
							System.exit(0);
						} else {   
							// set deadline time
							task.setDeadline(s.nextInt());
						}
					} else {       // if release time is not a number
						s.close(); 
						System.out.format("input error when reading the attributes of the task %s\n", lastTask);
						System.exit(0);
					}
				} else {  //if task name is an integer
					s.close();
					System.out.format("input error the name of the task is not valid");
					System.exit(0);
				}
								
				// if 3 attributes succeed, add task to priority queue
				pq1.insert(task.getReleasetime(), task);
				
			} // end of while
			
			s.close(); 
			
		} catch (FileNotFoundException e) {
			System.out.println(file1 +" does not exist."); //oz. ask professor: should we print just 'file1' or the name of the file?
		}
		
	}

	/** checkArguments() method runs in constant time: O(1)
	 */
	private static boolean checkArguments(String file1, String file2, int m) {
		File readFile = new File(file1);
		//if file1 does not exist then exit the program
		if ((file1==null) || (!readFile.exists()) ){
			System.out.println(file1 + " does not exist");
			System.exit(0);
		}
		
		if (file2==null) {
			System.out.println("No name provided for file2");
			return false;
		}
		
		if (m<=0) {
			System.out.println("Can't create a schedule. At least 1 core needed.");
			return false;
		}
		
		return true;
	}
	
	public static void main(String[] args) throws Exception{
		
	    TaskScheduler.scheduler("samplefile1.txt", "feasibleschedule1", 4);
	   /** There is a feasible schedule on 4 cores */      
	    TaskScheduler.scheduler("samplefile1.txt", "feasibleschedule2", 3);
	   /** There is no feasible schedule on 3 cores */
	    TaskScheduler.scheduler("samplefile2.txt", "feasibleschedule3", 5);
	   /** There is a feasible scheduler on 5 cores */ 
	    TaskScheduler.scheduler("samplefile2.txt", "feasibleschedule4", 4);
	   /** There is no feasible schedule on 4 cores */

	   /** The sample task sets are sorted. You can shuffle the tasks and test your program again */  

	 }
	
}


//auxiliary class for Tasks to be inserted into priority queue
class Task {

	// Task attributes
	private String tname;
	private Integer rtime;
	private Integer dtime;

	// empty constructor 
	public Task() {	}

	// constructor with parameters
	public Task(String taskname, Integer releasetime, Integer deadlinetime) {
		super();
		this.tname = taskname;
		this.rtime = releasetime;
		this.dtime = deadlinetime;
	}

	public String getTaskname() {
		return tname;
	}

	public void setTaskname(String taskname) {
		this.tname = taskname;
	}

	public Integer getReleasetime() {
		return rtime;
	}

	public void setReleasetime(Integer releasetime) {
		this.rtime = releasetime;
	}

	public Integer getDeadline() {
		return dtime;
	}

	public void setDeadline(Integer deadlinetime) {
		this.dtime = deadlinetime;
	}
}
