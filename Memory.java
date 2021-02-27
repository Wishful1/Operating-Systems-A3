/*
Name: Scott Lonsdale
Course: COMP2240
Student Number: C3303788
*/

import java.util.*;
import java.io.*;  

public class Memory //the allocated memory for each process
{
    private int id; //id of the Process
    private int memSize; //allocated memory size
    private int currentClockPos; //current position of clock for clock policy
	private int[] allocatedMemory; //array of stored pages
	private int[] lastUpdated; //last update times for each memory position
	private int[] clockUseBits; //clock bits for clock policy

	public Memory(int initID, int memSize) //constructor with inputs for ID and allocated memory size
	{
        id = initID;
        this.memSize = memSize;
        currentClockPos = 0;
        allocatedMemory = new int[memSize];
        lastUpdated = new int[memSize];
        clockUseBits = new int[memSize];
	}

	public void reset() //reset all the output parts of the class
	{
        allocatedMemory = new int[memSize];
        lastUpdated = new int[memSize];
        clockUseBits = new int[memSize];
        currentClockPos = 0;
	}

	public boolean checkMem(int targetPage) //checks the array of stored pages for the target page and returns a bool according to the result
	{
		boolean inMemory = false;
		for (int i : allocatedMemory)
		{
			if(i == targetPage) {inMemory = true;}
		}
		return inMemory;
	}

	public void updateLastUsedTime(int targetPage, int cTime) //updates the lastUpdated time and clockBit for the target page memory position
	{
        for (int i = 0; i < allocatedMemory.length; i++)
        {
        	if(allocatedMemory[i] == targetPage)
        	{
                lastUpdated[i] = cTime;
                clockUseBits[i] = 1;
                break; 
        	}
        }
	}


	public void addPageToMem(int targetPage, int cTime, String policy) //adding the target page to memory
	{
		if(checkMem(0) == true) //if there is an empty position in memory, the target page is directly put into memory
		{
			for (int i = 0; i < allocatedMemory.length; i++)
            {
        	    if(allocatedMemory[i] == 0)
        	    {
                    allocatedMemory[i] = targetPage;
                    updateLastUsedTime(targetPage, cTime); 
                    break; 
        	    }
            }
		}
        else if (policy == "LRU") //uses the LRU policy, finds the least recently used mem position and switch it with the target page
        {
        	int lru = Integer.MAX_VALUE;
        	for (int i = 0; i < lastUpdated.length; i++) //finds the smallest lastUpdated time
        	{
        		if (lru == Integer.MAX_VALUE) {lru = i;}
        		else if (lastUpdated[i] != 0 && lastUpdated[i] < lastUpdated[lru])
        		{
        			lru = i;
        		}
        	}
        	allocatedMemory[lru] = targetPage;
            updateLastUsedTime(targetPage, cTime);
        }
        else if (policy == "Clock") //uses the Clock policy TODO: reimplement with a circular-linked linked list
        {
            boolean notFound = true;
            while (notFound)
            {
            	for (int i = currentClockPos; i < clockUseBits.length; i++) //from the most recent clock position, the process goes through the use bit array
            	{
            		if(clockUseBits[i] == 1) {clockUseBits[i] = 0;} //if the use bit is a 1, it is change to a 0
            		else if (clockUseBits[i] == 0) //if the use bit is a 0, switched with the target page
            		{
            			allocatedMemory[i] = targetPage;
                        updateLastUsedTime(targetPage, cTime);
                        currentClockPos = i+1;
                        notFound = false;
                        break;
            		}
            	}

            	if (notFound == false) {break;}

            	for (int i = 0; i < clockUseBits.length; i++) //second loop
            	{
            		if(clockUseBits[i] == 1) {clockUseBits[i] = 0;}
            		else if (clockUseBits[i] == 0)
            		{
            			allocatedMemory[i] = targetPage;
                        updateLastUsedTime(targetPage, cTime);
                        currentClockPos = i+1;
                        notFound = false;
                        break;
            		}
            	}
            }
        }
	}
}