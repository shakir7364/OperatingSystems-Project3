package project3;

public class DiskScheduler 
{
	//Instance variables
	private int mainMem;
	private int[] discRequests;
	private int head;
	private int lowestDist;
	private int goal;
	
	//Constructor
	public DiskScheduler()
	{
		mainMem = 256;
		discRequests = new int[10];
		lowestDist = 500;
		schedule();
		
	}
	
	//Scheduler method
	private void schedule()
	{
		//Generate head and list of requests
		head = (int)(Math.random() * 501);
		for(int i = 0; i < 10; i++)
		{
			discRequests[i] = (int)(Math.random() * 501);
			System.out.println("Disk Request " + i + ": " + discRequests[i]);
		}
		goal = discRequests[9];
		
		//Loop till head reaches goal
		for(int i = 0; i < 10; i++)
		{
			//Find the smallest distance between head and a number from list
			findSmallestDist();
			System.out.println("Goal: " + goal + ", Head: " + head + ", Smallest Distance: " + lowestDist);
			
			//Change head to the value that is closest
			head -= lowestDist;
			
			//Check if head reached goal
			if(head == discRequests[9])
			{
				System.out.println("Disk request has been satisfied.");
				return;
			}
			
			//"remove" values head has already reached
			for(int k = 0; k < 10; k++)
				if(discRequests[k] == head)
					discRequests[k] = -500;
			
			//Print out list of requests
			for(int j = 0; j < 10; j++)
				System.out.println("Disk Request " + j + ": " + discRequests[j]);
		}
		
	}
	
	private void findSmallestDist()
	{
		lowestDist = 500;
		for(int i = 0; i < 10; i++)
			if(Math.abs(head - discRequests[i]) < Math.abs(lowestDist))
				lowestDist = head - discRequests[i];
	}
}
