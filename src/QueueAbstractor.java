/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Madoda
 */
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public final class QueueAbstractor {
    private final TMVC tvModelChecker;  //init as null
    //PriorityQueue<Integer> pQueue = new PriorityQueue<Integer>();
    private final Queue<Task> abstractTaskQueue; //init
    private final Queue<Task> concreteTaskQueue; //init
    private final ArrayList<Task> terminatedTaskArray; //init
    private final ArrayList<TimedAutomata> automataArray; //init
    private final ArrayList<Processor> processorSet; //Init
    private ArrayList<StateZone> counterPath;
    private final int interval;
    private String label;
    //private TimedAutomata NTA;
    
    public QueueAbstractor(int k, boolean t, TaskGenerator tg, int procSz)    {
    	System.out.println("QueueAbstractor - QueueAbstractor(int k, boolean t, TaskGenerator tg, int procSz)");
        tvModelChecker = new TMVC();
        processorSet = generateProcessorSet(procSz);
        //concreteTaskQueue = generateRandomConcreteQueue(tg.getTaskSet().size());
        concreteTaskQueue = new LinkedList<>();
    	System.out.println("QueueAbstractor - QueueAbstractor(int k, boolean t, TaskGenerator tg, int procSz)");
        System.out.println("\tCreating an empty concreteTaskQueue (LinkedList<>())");
       
        
        System.out.println("\tAdding created tasks from TaskGenerator to the concreteTaskQueue...");
        for(Task task:tg.getTaskSet()) {
        	Task tk = new Task(task);
        	concreteTaskQueue.add(tk);
        }
        System.out.println("QueueAbstractor - QueueAbstractor(int k, boolean t, TaskGenerator tg, int procSz)");
        System.out.println("\tCreated terminatedTaskArray, counterPath & automataArray...");
        terminatedTaskArray = new ArrayList<>();
        counterPath = new ArrayList<>();
        automataArray = new ArrayList<>();
        

        System.out.println("\tCreating LinkedList<>() or PriorityQueue<>() depending on value t: " + true);
        if(t==true) {
        	abstractTaskQueue = new LinkedList<>();
            System.out.println("\tCreated LinkedList");
        }
        else {
            abstractTaskQueue =  new PriorityQueue<>();
            System.out.println("\tCreated PriorityQueue");
        }
/*        else if (t==2)
            abstractTaskQueue =  new PriorityQueue<>();
        else if (t==3)
            abstractTaskQueue =  new PriorityQueue<>();
        else
        	abstractTaskQueue = new LinkedList<>();
*/        
        System.out.println("QueueAbstractor - QueueAbstractor(int k, boolean t, TaskGenerator tg, int procSz)");
        System.out.println("\tCreated task with values: Task(100,100,100,100,100)");
        new Task("100",100,100,100,100);
        interval = setInterval(k);
        System.out.println("\tInterval set to: " + interval);
        label = tg.getLabel();
        System.out.println("\tLabel: " + label);
    }
    
    public QueueAbstractor()    {
        tvModelChecker = new TMVC();
        processorSet = new ArrayList<>();
        concreteTaskQueue = new LinkedList<>();
        terminatedTaskArray = new ArrayList<>();
        automataArray = new ArrayList<>();
        counterPath = new ArrayList<>();
        abstractTaskQueue = new LinkedList<>();
        new Task("",0,0,0,0);
        interval = 0;
    }
    

     
    public Queue<Task> generateFileConcreteQueue(String file) {
        Queue<Task> tempTaskList = new LinkedList<>();
        try {
            File myObj = new File(file);
            Scanner myReader = new Scanner(myObj);
            //while (myReader.hasNextLine()) {
            
            while (myReader.hasNext()) {
                String label = myReader.next();
                double inWCET = Double.parseDouble(myReader.next());
                double inDeadline = Double.parseDouble(myReader.next());
                double inPeriod = Double.parseDouble(myReader.next());
                double inOccurance = Double.parseDouble(myReader.next());
                Task task = new Task(label, inWCET,inDeadline,inPeriod,inOccurance);
                task.setTaskAutomata();
                tempTaskList.add(task); 
                System.out.println(label+" "+inWCET+ " " + inDeadline +" " +inPeriod);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
        return tempTaskList;
    }
    
    public ArrayList<Processor> generateProcessorSet(int m)    {
        System.out.println("QueueAbstractor - generateProcessorSet(int m) function: ");
        System.out.println("\tCreating Processors...");
        ArrayList<Processor> tempProcessorList = new ArrayList<>(); 
        for(int i=0; i<m; i++){
            //Setting timeslice to 10
            Processor processor = new Processor(Integer.toString(i),10);
            // processor.setProcessorAutomata();        //Redundant?
            System.out.println("QueueAbstractor - generateProcessorSet(int m) function: ");
            System.out.println("\tAdding to tempProcessorList...");
            tempProcessorList.add(processor);  
        }
        return tempProcessorList;
    }
    
    
    public void generateAbstractQueue(double abstractClock)    {
        System.out.println("QueueAbstractor - generateAbstractQueue() function");
        System.out.println("\tIs abstractTaskQueue not empty? => " + !abstractTaskQueue.isEmpty());
    	Task prevIteTask = new Task();
    	if(!abstractTaskQueue.isEmpty())	{
    		//prevIteTask = new Task(terminateTaskArray);
    		prevIteTask = abstractTaskQueue.remove();
            System.out.println("\tPopping from Abstract Task Queue and assigning to prevIteTask");
    	}

    	abstractTaskQueue.clear();
        System.out.println("\tClearing Abstract Queue - Starting FOR LOOP: 0 < " + interval);
        for (int i=0; i<interval; i++)  { //|| !concreteTaskQueue.isEmpty()
            if(concreteTaskQueue.isEmpty()) {
                System.out.println(String.format("\tConcreteTaskQueue is empty! Aborting iteration from i (%d) to interval (%d)", i, interval));
                break;
            }
            Task p = concreteTaskQueue.remove();
            System.out.println(String.format("\tRemoving task from concreteQueue:\n%s", p.toString()));
            System.out.println("\tTask in Spotlight: "+ p.toString()); 
            abstractTaskQueue.add(p);
            System.out.println(String.format("\tAdding task to abstractQueue:\n%s", p.toString()));

            TimedAutomata temp = new TimedAutomata(p.getTaskAutomata());
            System.out.println(String.format("\tCreated new TimedAutomata for Task:", p.toString()));

            //System.out.println("TEMP TA: "+temp.getClocks());
            //temp.getClocks().forEach(t -> {
            //	System.out.println("TEMP TA: "+temp.getClocks().toString());
            //});
            automataArray.add(temp);
            System.out.println(String.format("\tAdding newly created TimedAutomata to automataArray", p.toString()));
        }
        
        System.out.println(String.format("\tExiting loop..."));
        System.out.println(String.format("\tIs ConcreteTaskQueue empty? => %b", !concreteTaskQueue.isEmpty()));
        if(!concreteTaskQueue.isEmpty()){
            Task shade = new Task(concreteTaskQueue);
            System.out.println("\tCreating new Shade task: "+ shade.toString());
            System.out.println("\tAbstract Task in Spotlight: "+ shade.toString());
            abstractTaskQueue.add(shade);
            System.out.println("\tAdded Abstract Task to AbstractTaskQueue: "+ shade.toString());
            System.out.println("\tAdded Abstract Task to AutomataArray: "+ shade.toString());
            automataArray.add(shade.getTaskAutomata()); 
        }
        
        System.out.println("\tOutputting all tasks in concreteTaskQueue...");

        for(Task shade:concreteTaskQueue)
        	System.out.println("\tTask in Shade: "+ shade.toString());
        
        System.out.println("\tAdding ProcessorAutomata to AutomataArray...");
        
        processorSet.forEach((processorSet1) -> {
            TimedAutomata temp = new TimedAutomata(processorSet1.getAutomata());
            automataArray.add(temp);
            System.out.println("\tAdded Processor TA: "+ temp.toString());
        });
    }
    
    public void abstractionCounter()	{
    	
    }
    
    public void generateNTA(TimedAutomata NTA)  {
    	
        if(!automataArray.isEmpty())
            NTA = automataArray.get(0);
                
        for(int i = 1; i<automataArray.size();i++) {
            NTA = NTA.addTimedAutomata(automataArray.get(i));
        }
   
    }
    
    public boolean queueAbstraction() throws IOException {
        int threeValue = 1;
        int iteration = 0;
        double abstractZn = 0.0; //new ClockZone();
        writeOnPath("Iteration\tNTA Clocks\tNTA Stateset size\tNTA Transitions Size\n", "Output"+label+".txt"); 
        System.out.println("QueueAbstractor - queueAbstraction() function");
        System.out.println("\tWhile the concreteTaskQueue not empty");
        while(!concreteTaskQueue.isEmpty()) {
            //System.out.println("Highest Clock Value 11: "+ abstractZn);
            System.out.println("\tIteration: " + iteration);
            System.out.println("\tClearing AutomataArray...");
        	automataArray.clear();
//        	System.out.println("Abstract Queue CALLED WITH SIZE: "+ abstractTaskQueue.size());
            System.out.println("\tGenerating Abstract Queue...");
            generateAbstractQueue(abstractZn);
            System.out.println("QueueAbstractor - queueAbstraction() function");
            System.out.println("\tBack to while loop...");

            TimedAutomata NTA;
            NTA = new TimedAutomata(automataArray.get(0));
            System.out.println("\tCreating Network Timed Automata with this element:\n" + automataArray.get(0).toString());
//            System.out.println("Abstract Queue IS NOW SIZE: "+ abstractTaskQueue.size());
//            System.out.println("AUTOMATA GET  ");          
//            automataArray.get(0).print();
            
            System.out.println("\tIterating through AutomataArray...");
            for(int i=1;i<automataArray.size();++i) {
                NTA = NTA.addTimedAutomata(automataArray.get(i));
                System.out.println("\tAdding TA to NTA: " + automataArray.get(i).toString());
            }
            
            //System.out.println("NTA AFTER ");          
            //NTA.print();
            System.out.println("\tChecking 3V reachability...");
            threeValue = tvModelChecker.threeVReachability(NTA,abstractTaskQueue, counterPath); 
            //Add terminatedTaskArray if task has reached terminate state? 
            System.out.println("QueueAbstractor - queueAbstraction() function");
            System.out.println("\tThree V Reachability: " + threeValue);
            
            //terminatedTaskArray = new ArrayList<>();
            
            abstractZn = tvModelChecker.timeline;
            System.out.println("\tAbstractZn: " + abstractZn);
            System.out.println("\tIteration " + iteration + " completed");
            
            iteration++;
            //System.out.print(label+" FILE NAME");
            writeOnPath(iteration + ")\t\t\t\t" + NTA.getClocks().size() + "\t\t\t\t" + NTA.getStateSet().size() + "\t\t\t\t" + NTA.getTransitions().size() + "\n", "Output"+label+".txt"); 
            //System.out.print(iteration+" - "+NTA.getTransitions().size()+" | ");
            if(threeValue==0)  {
            	printCounterExample();
            	writeOnPath(iteration + ") Not Sched\n","Output"+label+".txt");
                return false;
            }
            //System.out.println("Highest Clock Value: "+ abstractZn);
            //updateConcreteQueue(concreteTaskQueue, abstractTaskQueue);
            
        }
        writeOnPath(iteration + ") Sched\n", "Output"+label+".txt");
        //System.out.println("Highest Clock Value : "+ abstractZn);
        
        //System.out.println();
        return true; 
    }
    
    public void printCounterExample()	{
    	System.out.println("Counter Example: "); 
    	for(StateZone cp: counterPath)	{
    		System.out.println(toString()+" --> "); 
    	}
    	System.out.println();
    }
    
    public Queue<Task> getConcreteTaskQueue() {
        return concreteTaskQueue;
    }
    
    public ArrayList<Processor> getProsessorSet()    {
        return processorSet;
    }
    
    public ArrayList<TimedAutomata> getAutomataArray()    {
        return automataArray;
    }
    
    public Queue<Task> getAbstractTaskQueue() {
        return abstractTaskQueue;
    }
   
    private int setInterval(int k){
         //Interval should be less than n: queue size
        return (k >0) ? k : 0;
    }
    
    public void print() {
        //System.out.println("Concrete Task Front " );
        //System.out.print(concreteTaskQueue.size());
        //System.out.println("Next is abstract Task array");
        
        //for(Task x : abstractTaskArray)
        //    x.print();
    }
    
    public static void writeOnPath(String fileContent, String pathString) throws IOException {
        Files.write(Paths.get(pathString), fileContent.getBytes(), StandardOpenOption.APPEND);
    }

	public ArrayList<Task> getTerminatedTaskArray() {
		return terminatedTaskArray;
	}
    
}
