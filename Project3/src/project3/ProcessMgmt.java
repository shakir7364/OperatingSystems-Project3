package project3;
import java.util.LinkedList ;
import java.util.Random ; 

public class ProcessMgmt 
{
	public static void main(String args[]) 	
	{
		//#010	Initialization of fields and data structures	///////////////
		int QREADY__T	= 25 ;		
		final int BLOCKIO	= 3 ;		final int INTERRUPT	= 2 ;		
		final int COMPLETED	= 1 ; 		final int BLOCKPAGE	= 0 ;
		final int mem__T = 256;
				
		Random random__X	= new Random();
		CPU_event event		= new CPU_event();
		LinkedList<PCB> QMemOpen	= new LinkedList<PCB>();	//#0020 Create List QMemOpen
		LinkedList<PCB> QMemUsed	= new LinkedList<PCB>();	//#0030 Create List QMemUsed
		OpenMem om = new OpenMem();
		
		QMemOpen.add(new PCB(mem__T));	//#0032 Set the Open Memory
		DiskScheduler schedule;
		
		int CPU_runtime ;		int event__X ;
  
		LinkedList<PCB> QReady		= new LinkedList<PCB>();	//#005 Create the List for the Priority QReady
		
		PCB PCB_Running ; 		//#020 Create the field for: PCB_Running 
	
		//#030	
		
		for (int ii = 0; ii < QREADY__T; ii++)
		{
			QReady.add(new PCB()) ;		//#040 Add a new PCB object onto the LL 
		}

		///////////////////////////////////////////////////////////////////////
		//#080	===> end of Initialization 
		
		System.out.println("\n*****\t\t\tReady Queue\t\t\t*****");  
		for (PCB pcbLoop : QReady)		//#090 Loop that executes based on the no. of nodes in the LL
			System.out.println(pcbLoop.showPCB());		//#095 Print the PCB for an LL node
  
		//#0100	Process until the active processes are all completed	///////
		
		while (QReady.size() > 0)
		{
			//#0110	Next process to Run
		
			PCB_Running = QReady.removeFirst();			//#0140 Assign the first node from QReady to Running
			boolean memFound = om.findOpenMem(PCB_Running, QMemOpen, QMemUsed);	//Check for open memory
			if(memFound)
			{
				PCB_Running.set_state("Running");			//#0145 Set the state value to "Running"
				
				CPU_runtime	= random__X.nextInt(20) + 1;	//#0150 Get a random no. between 0 and 20
				
				//#0160 Tally and set the CPU used for the process
				PCB_Running.add_CPU_used(CPU_runtime);
	
				System.out.println("\n*****\t\t\tRunning Process\t\t\t*****");   	  
				System.out.println(PCB_Running.showPCB());
	
				//#0180 Tally and set the wait times for the processes in QReady
				for (PCB pcbLoop : QReady)
				{
					pcbLoop.add_timeWaiting(CPU_runtime);
				}				
				
				//#0190 Check if the process completed (CPU used exceeds the CPU max)
				if (PCB_Running.get_CPU_used() > PCB_Running.get_CPU_max())
				{
					PCB_Running.set_state("Completed");
					System.out.println("\n>>>>>\tProcess\t"
							+ PCB_Running.showPCB()
							+ "\t<<<<<" );
					QMemUsed.remove(PCB_Running);						//Remove PCB from QMemUsed after termination
					QMemOpen.add(new PCB(QMemOpen.remove().get_Limit() + PCB_Running.get_Limit()));
					continue;	// iterate to the next in the WHILE loop
				}
				
				//-----------------------------------------------------------------
				//	Process did not complete, so continue 
				//-----------------------------------------------------------------
				//#0200 Simulate the type of Block on the Process (I/O Block, Memory Paging Block, Interrupt)
				
				event__X	= event.get_CPU_event() ;
				
				if (event__X == COMPLETED)
				{
					PCB_Running.set_state("Completed");
					
					System.out.println("\n>>>>>\tProcess\t"
							+ PCB_Running.showPCB()
							+ "\t<<<<<" );
					QMemUsed.remove(PCB_Running);
					QMemOpen.add(new PCB(QMemOpen.remove().get_Limit() + PCB_Running.get_Limit()));
				}
				else
				{
					PCB_Running.set_state("Ready");	//#230 Set the state to "Ready" from "Running"
				
					switch (event__X)
					{
						case INTERRUPT :
						{
							 	//#240 Add the PCB to the START of the QReady
							System.out.println("*****\t\t\tContext Switch\tINTERRUPT event\t\t\t*****");  
							QReady.addFirst(PCB_Running);
							break;
						}				
						case BLOCKPAGE :
						{	
								//#242 Add to the top 3rd of QReady
							System.out.println("*****\t\t\tContext Switch\tPAGE event\t\t\t*****");
							QReady.add(2, PCB_Running);
							break;
						}
						case BLOCKIO :
						{
							//Disk scheduler goes here?
							schedule = new DiskScheduler();
							
							//#244 Add the PCB to the end of QReady	
							System.out.println("*****\t\t\tContext Switch\tI/O event\t\t\t*****");
							QReady.add(PCB_Running);
							
							break;
						}
						default :
						{
							System.out.println("*****\t\t\tContext Switch\tTIME event\t\t\t*****");
							QReady.add(PCB_Running);
							break;
						}
					}
				}
				
				//Print out QReady
				System.out.println("\n*****\t\t\tContext Switch\tReady Queue\t\t\t*****");   
				for (PCB pcbLoop : QReady)
					System.out.println(pcbLoop.showPCB());	
		
				//Print out QMemOpen and QMemUsed
				System.out.println("============");
				for (PCB loopI : QMemOpen)
					System.out.printf("\n@020 QOpen\t%s\n"	,loopI.showPCB());
				for (PCB loopI : QMemUsed)
					System.out.printf("@0200 QUsed\t%s\n"	,loopI.showPCB());
			}
			else
			{
				QReady.addLast(PCB_Running);
			}
		}	
	}
}
