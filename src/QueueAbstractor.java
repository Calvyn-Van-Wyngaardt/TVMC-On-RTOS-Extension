/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Madoda with extensions made by Calvyn
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
    private final LinkedList<Entry> iterationEntries;               //When something fails, we backtrack to use a different permutation of tasks
    private LinkedList<Task> tempPool;                               // Pool that stores the periodic tasks
    private LinkedList<Task> repeatingTasks;                         // Queue that will be used to check for pattern
    private final ArrayList<Task> terminatedTaskArray; //init
    private final ArrayList<TimedAutomata> automataArray; //init
    private final ArrayList<Processor> processorSet; //Init
    private ArrayList<StateZone> counterPath;
    private final int interval;
    private String label;
    private boolean patternDetected;
    private boolean periodicTasksPresent = false;
    private HashMap<String, Double> originalDeadlineValues;
    
    public QueueAbstractor(int k, boolean t, TaskGenerator tg, int procSz)    {
        tempPool = new LinkedList<>();
        originalDeadlineValues = new HashMap<>();
        repeatingTasks = new LinkedList<>();
    	patternDetected = false;
        tvModelChecker = new TMVC();
        processorSet = generateProcessorSet(procSz);
        //concreteTaskQueue = generateRandomConcreteQueue(tg.getTaskSet().size());
        concreteTaskQueue = new LinkedList<>();
        iterationEntries = new LinkedList<>();
        
        for(Task task:tg.getTaskSet()) {
        	Task tk = new Task(task);
        	concreteTaskQueue.add(tk);

            if (tk.getPeriod() > 0) {
                // Check if task is already in hashmap...
                if (!originalDeadlineValues.containsKey(tk.getLabel())) {
                    originalDeadlineValues.put(tk.getLabel(), tk.getPeriod());
                }
            }
        }
        
        terminatedTaskArray = new ArrayList<>();
        counterPath = new ArrayList<>();
        automataArray = new ArrayList<>();
        
        if(t==true)
        	abstractTaskQueue = new LinkedList<>();
        else 
            abstractTaskQueue =  new PriorityQueue<>();
/*        else if (t==2)
            abstractTaskQueue =  new PriorityQueue<>();
        else if (t==3)
            abstractTaskQueue =  new PriorityQueue<>();
        else
        	abstractTaskQueue = new LinkedList<>();
*/        
        // new Task("100",100,100,100,100);
        interval = setInterval(k);
        label = tg.getLabel();
    }
    
    public QueueAbstractor()    {
        tempPool = new LinkedList<>();
        originalDeadlineValues = new HashMap<>();
        repeatingTasks = new LinkedList<>();
    	patternDetected = false;
        iterationEntries = new LinkedList<>();
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
        ArrayList<Processor> tempProcessorList = new ArrayList<>(); 
        for(int i=0; i<m; i++){
            Processor processor = new Processor(Integer.toString(i),0);
            processor.setProcessorAutomata();
            tempProcessorList.add(processor);  
        }
        return tempProcessorList;
    }
    
    public void generateAbstractQueue(double abstractClock) {
    	Task prevIteTask = new Task();
    	if(!abstractTaskQueue.isEmpty())	{
    		prevIteTask = abstractTaskQueue.remove();
    	}
    	abstractTaskQueue.clear();


        for (int i=0; i < interval; i++)  { //|| !concreteTaskQueue.isEmpty()
            if(concreteTaskQueue.isEmpty())
                break;
            Task p = concreteTaskQueue.remove();
            
            if (p.isPeriodic()) {
                periodicTasksPresent = true;

                //Update clock value to current period value...
                Task newTask = new Task(p);
                // Double deadlineValue = originalDeadlineValues.get(newTask.getLabel());
                // System.out.println(String.format("Adding timeline (%f) to deadline (%f) for task %s", tvModelChecker.getTimeline(), deadlineValue, newTask.getLabel()));
                // newTask.setDeadline(tvModelChecker.getTimeline() + deadlineValue);
                tempPool.add(newTask);
                repeatingTasks.add(newTask);
            }
            
            System.out.println("Task in Spotlight: "+ p.toString()); 
            abstractTaskQueue.add(p);
            TimedAutomata temp = new TimedAutomata(p.getTaskAutomata());
            automataArray.add(temp);
        }

        if (concreteTaskQueue.isEmpty()) {
            for (int i = 0; i < interval; i++) {
                if (i < tempPool.size()) {
                    Task t = tempPool.poll();
                    concreteTaskQueue.add(t);
                }
            }
        }
        
        if(!concreteTaskQueue.isEmpty()){
            Task shade = new Task(concreteTaskQueue);
            System.out.println("Abstract Task in Spotlight: "+ shade.toString());
            abstractTaskQueue.add(shade);
            automataArray.add(shade.getTaskAutomata());
        }
        
        for(Task shade:concreteTaskQueue)
        	System.out.println("Task in Shade: "+ shade.toString());
        
        processorSet.forEach((processorSet1) -> {
            TimedAutomata temp = new TimedAutomata(processorSet1.getAutomata());
            automataArray.add(temp);
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
        System.out.println("QueueAbstractor - [queueAbstraction]: Entered function");
        int threeValue = 1;
        int iteration = 0;
        double abstractZn = 0.0; //new ClockZone();
        boolean poolReady = false;

        System.out.println("QueueAbstractor - [queueAbstraction]: Entering while(concreteTaskQueue != empty)");
        while((!concreteTaskQueue.isEmpty() || !tempPool.isEmpty()) && !patternDetected) {
        	automataArray.clear();

            System.out.println("Current Concrete Queue:");
            for (Task t: concreteTaskQueue) {
                System.out.println("- " + t.toString());
            }


            Entry currEntry = new Entry();
            System.out.println("\tTemp pool size: " + tempPool.size());
            for (int i = 0; i < tempPool.size(); i++) {
                Task curr = tempPool.get(i);
                double currClockVal = curr.getTimedAutomata().getClocks().get(0).getValue();
                System.out.println(String.format("Task %s\n\tClock Value: %f\n\tPeriod Value: %f", tempPool.get(i).toString(), currClockVal, curr.getPeriod()));
                
                //Check to see if any tasks are ready to be moved into concreteQueue
                if (currClockVal >= curr.getPeriod()) {
                    // System.out.println("Clock values before reset:\n");
                    // for (Clock c: curr.getTimedAutomata().getClocks()) {
                        // System.out.println("\tClock: " + c.getValue());
                    // }
                    curr.getTimedAutomata().resetAllClocks();
                    
                    // System.out.println("Clock values after reset:\n");
                    // for (Clock c: curr.getTimedAutomata().getClocks()) {
                        // System.out.println("\tClock: " + c.getValue());
                    // }

                    tempPool.remove(i);
                    concreteTaskQueue.add(curr);
                    currEntry.addTask(curr);
                    poolReady = true;
                }
            }

            //Updates every task's clock values in the pool if no task is ready...
            if (!poolReady) {
                for (int i = 0; i < tempPool.size(); i++) {
                    Task curr = tempPool.get(i);
                    curr.getTimedAutomata().getClocks().get(0).update(1);
                }
            }

            System.out.println("Modified Concrete Queue:");
            for (Task t: concreteTaskQueue) {
                if (t.isPeriodic()) {
                    //Dynamically update deadline values for task...
                    Double deadlineValue = originalDeadlineValues.get(t.getLabel());
                    System.out.println(String.format("Adding timeline (%f) to deadline (%f) for task %s", tvModelChecker.getTimeline(), deadlineValue, t.getLabel()));
                    t.setDeadline(tvModelChecker.getTimeline() + deadlineValue);
                    tempPool.add(t);
                    repeatingTasks.add(t);
                }
                
                System.out.println("- " + t.toString());
            }

            generateAbstractQueue(abstractZn);
            TimedAutomata NTA;
            NTA = new TimedAutomata(automataArray.get(0));
            
            for(int i=1;i<automataArray.size();++i) {
                TimedAutomata t = automataArray.get(i);
                
                NTA = NTA.addTimedAutomata(t);
            }            

            threeValue = tvModelChecker.threeVReachability(NTA,abstractTaskQueue, counterPath); 

            abstractZn = tvModelChecker.timeline;
            iteration++;
            
            writeOnPath("Clocks= "+NTA.getClocks().size()+" States= "+NTA.getStateSet().size()+" Trans="+NTA.getTransitions().size()+"; \n", "Output"+label+".txt"); 

            if(threeValue==0)  {
                System.out.println("NOT SCHED: ");
            	printCounterExample();
            	writeOnPath("Ite= "+iteration+" ; "+" Not Sched \n","Output"+label+".txt");
                return false;
            } else if (threeValue == 1) {
                // Create new entry in LinkedList that contains the tasks just checked
                // Check LinkedList for a pattern...
                if (repeatingTasks.size() > 0) {
                    patternDetected = findPattern();
                }
            }

            //System.out.println("Highest Clock Value: "+ abstractZn);
            //updateConcreteQueue(concreteTaskQueue, abstractTaskQueue);
            String reached = (threeValue == 1) ? "unknown/true" : "false";
            System.out.println("QueueAbstractor - [queueAbstraction]: Reachability is " + reached);
        }

        System.out.println("QueueAbstractor - [queueAbstraction]: ConcreteQueue processed with no failed schedule");
        System.out.println("QueueAbstractor - [queueAbstraction]: Schedulability property: Schedulable");
 
        if (patternDetected) {
            writeOnPath("Ite= "+iteration+" ; "+" Sched - Pattern detected\n", "Output"+label+".txt");
        } else {
            writeOnPath("Ite= "+iteration+" ; "+" Sched \n", "Output"+label+".txt");
        }

        return true; 
    }

    public boolean findPattern() {
        Task firstTask = repeatingTasks.get(0);
        Stack<Integer> indices = new Stack<>();
        Integer[] indicesArr = new Integer[1];

        for (int i = 0; i < repeatingTasks.size(); i++) {
            if (firstTask.equals(repeatingTasks.get(i))) {
                indices.push(i);
            }
        }

        indicesArr = new Integer[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indicesArr[i] = indices.get(i);
        }

        for (int i = 1 ; i < indices.size(); i++) {
                //If the first task matches a task using indices
                int size = indicesArr[i];

                for (int j = 1; j < size; j++) {
                    if ((j < size) && (size + j) < repeatingTasks.size()) {
                        if (repeatingTasks.get(0+j).equals(repeatingTasks.get(size + j))) {
                            // patternLength += 1;
                            if (j == size - 1) {    //Last one checked and matches pattern
                                //One final check... 
                                // for (int k = 0; k < j; k++) {
                                //     if ((k + j) < repeatingTasks.size()) {
                                //         if (repeatingTasks.get(k+j).equals(repeatingTasks.get()))
                                //     }
                                // }
                                return true;
                            }
                        } else {
                            break;      //No match
                        }
                    }
                }
        }

        //TODO: Add check for LCM and expand 
        // Somehow go through the LinkedList and find a pattern?
        // Look at times of execution in the list:
        // E.G => [(1), (3), (1&2), (1), (3), (1&2), (1), (3), (1&2), ...]
        // What about LCM? 
            // Should we then technically only check after the LCM?
            // Or should we just skip to the LCM and then see?

        return false;
    }
    
    public void printCounterExample()	{
    	System.out.println("Counter Example: "); 
    	for(int i = 0; i < counterPath.size() - 1; i++) {
    		System.out.println("\n" + i + ") " + counterPath.get(i).toString() + " --> \n"); 
    	}
        System.out.println("\n" + (counterPath.size() - 1) + ") " + counterPath.get(counterPath.size()-1).toString());

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
