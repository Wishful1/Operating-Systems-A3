/*
Name: Scott Lonsdale
Course: COMP2240
Student Number: C3303788
*/

import java.util.*;
import java.io.*;  

public class Process
{
	private String processName; //process Name as provided by the user
	private int processID; //base ID of the process
	private int currentPage; //current progress through the process
	private boolean blocked; //blocked status
	private ArrayList<Integer> pagesToAccess; //pages needed to be accessed
	private int numPageFaults; //total num of page faults
	private int finishTime; //turnaround time
	private ArrayList<Integer> pageFaultTimes; //times of the page faults

	public Process(int initID, String initName) //constructor with inputs for ID and name
	{
        processName = initName;
        processID = initID;
        currentPage = 0;
        blocked = false;
        pagesToAccess = new ArrayList<Integer>();
        numPageFaults = 0;
        finishTime = 0;
        pageFaultTimes = new ArrayList<Integer>();
	}

	public void reset() //reset all the output parts of the class
	{
		pageFaultTimes = new ArrayList<Integer>();
		numPageFaults = 0;
		finishTime = 0;
		currentPage = 0;
	}

	//getters for ID, name, blocked status and # of Faults
	public int getID()
	{
		return processID;
	}

    public String getName()
	{
		return processName;
	}

	public int getNumFaults()
	{
		return numPageFaults;
	}

	public boolean getBlocked()
	{
		return blocked;
	}

	public String getFaultTimes() //custom String output constructor for Fault Times
	{
	    String output = "{";
	    for(int i = 0; i < pageFaultTimes.size(); i++)
	    {
	    	if(i == pageFaultTimes.size() - 1) {output += pageFaultTimes.get(i);}
	    	else {output += pageFaultTimes.get(i) + ", ";}
	    }
	    output += "}";
	    return output;

	}

	public int getCurrentPage() //get the current page in the process
	{
        return pagesToAccess.get(currentPage);
	}

	public int getBlockStart() //gets the most recent fault start time
	{
		return pageFaultTimes.get(pageFaultTimes.size() - 1);
	}

	public int getFinish() //gets turnaround time
	{
		return finishTime;
	}

	public void addPage(int newPage) //adds page to the pagesToAccess
	{
		pagesToAccess.add(newPage);
	}

	public boolean moveToNextPage() //moves to the next page
	{
		if (currentPage == pagesToAccess.size() - 1) {return true;}
		else
		{
			this.currentPage++;
			return false;
		}
	}

	public void addFault(int faultTime) //adds fault to fault times and increments to numPageFaults
	{
		this.numPageFaults++;
		pageFaultTimes.add(faultTime);
		this.blocked = true;
	}


	public void setBlocked(boolean blockedStatus) //set Blocked Status
	{
		this.blocked = blockedStatus;
	}


	public void setFinish(int finishTime) //set Finished Status
	{
		this.finishTime = finishTime;
	}
}