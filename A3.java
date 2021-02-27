/*
Name: Scott Lonsdale
Course: COMP2240
Student Number: C3303788
*/

import java.util.*;
import java.io.*;  


public class A3
{
	
	public static void main (String args[]) throws FileNotFoundException
	{
		int numFrames = Integer.parseInt(args[0]);
		int qSize = Integer.parseInt(args[1]);
		int processMemSize = numFrames/(args.length - 2); //calculates the allocated memory space for each process
		if (processMemSize > 50) {processMemSize = 50;} //caps at 50
		ArrayList<Memory> memoryArray = new ArrayList<Memory>(); //initialises the main memory and process arrays
		ArrayList<Process> processArray = new ArrayList<Process>();
		for (int i = 2; i < args.length; i++) //grabs all of the process arguments
		{
			Scanner input = new Scanner(new FileReader(args[i]));
			Process newProcess = new Process(i-1, args[i]); //initialises id and name 
			Memory newMemory = new Memory(i-1, processMemSize); //initialises id and memSize
			while (input.hasNext()) //reads the pages references inside the processes
			{
                if(input.hasNextInt() == true)
                {
                    newProcess.addPage(input.nextInt());
                }
                else {input.next();}
            }
            processArray.add(newProcess);
            memoryArray.add(newMemory);
		}
		simulate(processArray, memoryArray, qSize, "LRU"); //runs the sim with LRU policy
		System.out.println();
		System.out.println("-----------------------------------------------------------");
		System.out.println();
        for(Process i : processArray) {i.reset();} //resets the output on both the memory and process array
        for(Memory i : memoryArray) {i.reset();}
		simulate(processArray, memoryArray, qSize, "Clock"); //runs the sim with clock policy

	}


	public static void simulate(ArrayList<Process> processArray, ArrayList<Memory> memoryArray, int qSize, String policy) //main simulation method
	{
        int cTime = 0; //initialises time, overall finished state and the priority queue
        boolean notFinished = true;
        Process currentProcess;
        Memory currentMemory;
        Queue<Integer> priorityQueue = new LinkedList<>(); 
        while(notFinished)
        {
        	if(cTime == 0) //if the function has just started automatically puts all the processes into blocked state and runs a page fault
        	{
        		for (int i = 0; i < processArray.size(); i++)
        		{
        			currentProcess = processArray.get(i);
        			currentMemory = memoryArray.get(i);
        			currentProcess.addFault(cTime);
        			currentProcess.setBlocked(true);
        			currentMemory.addPageToMem(currentProcess.getCurrentPage(), cTime, policy);
        		}
        	}
            
            //checks if a process is unblocked and the overall finished state of the function
            notFinished = false;
            for (int i = 0; i < processArray.size(); i++)
            {
            	currentProcess = processArray.get(i);
            	if (currentProcess.getBlocked() == true)
            	{
            		if(cTime >= currentProcess.getBlockStart() + 6) //if process has finished addind/swapping in memory it is added back to the priority queue
            		{
            			currentProcess.setBlocked(false);
            			priorityQueue.add(i);
            		}
            	}
            	
            	if(currentProcess.getFinish() == 0) {notFinished = true;}
            }

            if (priorityQueue.size() != 0)
            {
            	int current = priorityQueue.remove(); //takes the next process in the priority queue
            	currentProcess = processArray.get(current);
            	currentMemory = memoryArray.get(current);
            	for (int i = 0; i < qSize; i++)
            	{
                    
                    for (int j = 0; j < processArray.size(); j++) //another finished blocked checker TODO: make a separate function for blocked finish checking
		            {
		            	Process currentCheckProcess = processArray.get(j);
		            	if (currentCheckProcess.getBlocked() == true)
		            	{
		            		if(cTime >= currentCheckProcess.getBlockStart() + 6)
		            		{
		            			currentCheckProcess.setBlocked(false);
		            			priorityQueue.add(j);
		            		}
		            	}
			        }

                    int currentPage = currentProcess.getCurrentPage();
                    boolean inMem = currentMemory.checkMem(currentPage); //checks if the current page of the current process is in memory
                    if(inMem) //if it is, the used time for that mem position is updated and the process is moved to the next page
                    {
                    	currentMemory.updateLastUsedTime(currentPage, cTime);
                    	boolean finished = currentProcess.moveToNextPage();
                    	if (finished == true) //if the process has finished it's final page, the finished boolean is checked, and turnaround time is added 
                    	{
                    		currentProcess.setFinish(cTime + 1);
                    		break;
                    	}
                    	else if (i != qSize - 1) {cTime++;}
                    }
                    else if (!inMem) //if not in mem, a block is started while adding a pault to the process
                    {
                        currentProcess.addFault(cTime);
        			    currentProcess.setBlocked(true);
        			    currentMemory.addPageToMem(currentProcess.getCurrentPage(), cTime, policy);
        			    cTime--;
        			    break;
                    }

                    if (i == qSize - 1) {priorityQueue.add(current);}
            	}
            }
            cTime++;
        }

        output(processArray, policy); //output function
	}

	public static void output(ArrayList<Process> processArray, String policy) //output to standard format
	{
        System.out.println(policy + " - Fixed:");
		System.out.println(String.format("%-5s%-18s%-17s%-10s%s", "PID", "Process Name", "Turnaround Time", "# Faults", "FaultTimes"));
		for (Process i : processArray)
		{
            System.out.println(String.format("%-5s%-18s%-17s%-10s%s", i.getID(), i.getName(), i.getFinish(), i.getNumFaults(), i.getFaultTimes()));
		}
	}
}