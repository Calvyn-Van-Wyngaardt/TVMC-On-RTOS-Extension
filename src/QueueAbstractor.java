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
    private final LinkedList<Task> concreteTaskQueue; //init
    private final LinkedList<Entry> iterationEntries;                // When something fails, we backtrack to use a different permutation of tasks
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
    private HashMap<String, Double> originalPeriodValues;
    private HashMap<String, Double> periodDifferences;
    private HashMap<String, Double> deadlineDifferences;

    private int originalConcreteQueueSize;
    private Stack<Task> patternFound;
    private boolean dataCut = false;
    private TaskGenerator taskGen;
    
    public QueueAbstractor(int k, boolean t, TaskGenerator tg, int procSz)    {
        tempPool = new LinkedList<>();
        periodDifferences = new HashMap<>();
        deadlineDifferences = new HashMap<>();
        originalConcreteQueueSize = 0;
        originalDeadlineValues = new HashMap<>();
        repeatingTasks = new LinkedList<>();
    	patternDetected = false;
        tvModelChecker = new TMVC();
        processorSet = generateProcessorSet(procSz);
        patternFound = new Stack<>();
        concreteTaskQueue = new LinkedList<>();
        iterationEntries = new LinkedList<>();
        originalPeriodValues = new HashMap<>();
        taskGen = tg;

        for(Task task:tg.getTaskSet()) {
        	Task tk = new Task(task);
        	concreteTaskQueue.add(tk);

            if (tk.getPeriod() > 0) {
                // Check if task is already in hashmap...
                if (!originalDeadlineValues.containsKey(tk.getLabel())) {
                    originalDeadlineValues.put(tk.getLabel(), tk.getDeadline());
                }

                if (!originalPeriodValues.containsKey(tk.getLabel())) {
                    originalPeriodValues.put(tk.getLabel(), tk.getPeriod());
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
        originalConcreteQueueSize = 0;
        originalDeadlineValues = new HashMap<>();
        deadlineDifferences = new HashMap<>();
        repeatingTasks = new LinkedList<>();
    	patternDetected = false;
        iterationEntries = new LinkedList<>();
        tvModelChecker = new TMVC();
        patternFound = new Stack<>();
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

        // System.out.println("These are the tasks in the concrete Queue:");
        // for (Task t: concreteTaskQueue) {
        //     System.out.println(t.toString());
        // }

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
                // tempPool.add(newTask);
                repeatingTasks.add(newTask);
            }
            
            // System.out.println("Task in Spotlight: "+ p.toString()); 
            abstractTaskQueue.add(p);
            TimedAutomata temp = new TimedAutomata(p.getTaskAutomata());
            automataArray.add(temp);
        }

        // System.out.println("After putting some in spotlight, this remains: ");
        // for (Task t : concreteTaskQueue) {
        //     System.out.println(t.toString());
        // }

        // This then adds tasks that aren't ready...
        // if (concreteTaskQueue.isEmpty()) {
        //     for (int i = 0; i < interval; i++) {
        //         if (i < tempPool.size()) {
        //             Task t = tempPool.poll();
        //             concreteTaskQueue.add(t);
        //         }
        //     }
        // }
        
        if(!concreteTaskQueue.isEmpty()){
            Task shade = new Task(concreteTaskQueue);
            // System.out.println("Abstract Task in Spotlight: "+ shade.toString());
            abstractTaskQueue.add(shade);
            automataArray.add(shade.getTaskAutomata());
        }
        
        // for(Task shade:concreteTaskQueue)
        	// System.out.println("Task in Shade: "+ shade.toString());
        
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
        int threeValue = 1;
        int iteration = 0;
        double abstractZn = 0.0;
        originalConcreteQueueSize = concreteTaskQueue.size();
        int tasksProcessed = 0;

        // System.out.println("QueueAbstractor - [queueAbstraction]: Entering while(concreteTaskQueue != empty)");
        while((!concreteTaskQueue.isEmpty() || !tempPool.isEmpty()) && !patternDetected && tasksProcessed < 8000) {
        	automataArray.clear();
            Double currentTime = tvModelChecker.getTimeline();

            // System.out.println("Current Concrete Queue:");
            // for (Task t: concreteTaskQueue) {
            //     System.out.println("- " + t.toString());
            // }

            // System.out.println("Current Temp Pool:");
            // for (Task t: tempPool) {
            //     System.out.println("- " + t.toString());
            // }

            //Check for ready tasks and add them to the concreteQueue...
            for (int i = 0; i < tempPool.size(); i++) {
                Task curr = tempPool.get(i);

                if (currentTime >= curr.getPeriod()) {
                    // System.out.println("Adding the following task to the concreteQueue: " + curr.toString());
                    tempPool.remove(i);
                    concreteTaskQueue.add(curr);
                }
            }

            //If there are no ready tasks to check... then we increase timeline & tasks in queue...
            if (concreteTaskQueue.isEmpty()) 
            {
                double nearestExecTime = Double.MAX_VALUE;
                for (Task t :tempPool) {
                    if (t.getPeriod() < nearestExecTime) {
                        nearestExecTime = t.getPeriod();
                    }
                }
                double difference = nearestExecTime - currentTime;
                // System.out.println("Adding difference " + difference + " to timeline -- no ready tasks...");
                tvModelChecker.addToTimeline(difference);
            } 
            else 
            {
                //Else, there may be some tasks that are ready to be checked...
                //Entry currEntry = new Entry();

                //This updates the deadline to be correct for the next iteration...
                // System.out.println("Moving the following tasks to temp pool. Updated Deadline.");
                Stack<Task> tasksToBeChecked = new Stack<>();
                for (int i = 0; i < interval; i++) {
                    if (i < concreteTaskQueue.size()) {
                        Task t = concreteTaskQueue.get(i);
                        tasksToBeChecked.add(t);
                        if (t.isPeriodic()) {                            
                            
                            // Check to see if the current task has been processed yet...
                            // If it has, then it must follow logic of "Busy with rounds > 1..."
                            // If not, then "Busy with first round..."
                            if (t.getPeriod() != originalPeriodValues.get(t.getLabel())) {
                            //if (tasksProcessed >= originalConcreteQueueSize) {
                                // System.out.println("\t-Busy with rounds > 1...");
                                Task newTask = new Task(t);
                                //In this one we modify the value before checking takes place
                                // Double deadlineValue = originalDeadlineValues.get(t.getLabel());
                                // Double deadlineValue = newTask.getDeadline();
                                // System.out.println(String.format("Adding original period (%f) to deadline (%f) for task %s: ", originalPeriodValue, deadlineValue, t.getLabel()));
                                // System.out.println(String.format("Adding (period - wcet = %f - %f) to current deadline (%f) for task %s: ", newTask.getPeriod(), newTask.getWCET(), newTask.getDeadline(), t.getLabel()));
                                // System.out.println("Old deadline: " + newTask.getDeadline());
                                // t.setDeadline(originalPeriodValue + deadlineValue);
                                // newTask.setPeriod(newTask.getPeriod() + originalPeriodValue);
                                HashMap<String, Double> timeBetween = taskGen.getTimeBetweenSubTasks();
                                Double timeToAdd = 0.0;
                                
                                //TODO! This maps to null for other scheduling policies. Fix This...
                                if (timeBetween != null) {
                                    Collection<Double> values = timeBetween.values();
                                    
                                    for (String k: timeBetween.keySet()) {
                                        if (k.equals(newTask.getLabel())) {
                                            timeToAdd = timeBetween.get(k);
                                            break;
                                        }
                                    }
    
                                    timeToAdd += newTask.getWCET();
                                    // newTask.setDeadline(newTask.getDeadline() + (newTask.getPeriod() - newTask.getWCET()));
                                    newTask.setDeadline(newTask.getDeadline() + timeToAdd);
                                    newTask.setPeriod(newTask.getPeriod() + timeToAdd);
                                    
                                } else {
                                    // This one could be an issue...
                                    // System.out.println("Previous Period value for task: " + newTask.getPeriod());
                                    // newTask.setPeriod(newTask.getPeriod() + newTask.getWCET() + periodDifferences.get(newTask.getLabel()));
                                    // System.out.println("New period value for task: " + newTask.getPeriod());

                                    // System.out.println("Previous deadline for task: " + newTask.getDeadline());                                    
                                    // newTask.setDeadline(newTask.getPeriod() + deadlineDifferences.get(newTask.getLabel()));
                                    // System.out.println("New deadline for task: " + newTask.getDeadline());
                                    // newTask.setPeriod(currentTime + newTask.getWCET() + originalPeriodValues.get(newTask.getLabel()));
                                    // (We get the current timeline) + (how long it will take to run) + (only after that do we add that other period value) = new Period
                                    // newTask.setPeriod(currentTime + newTask.getPeriod());        ---> OLD!
                                }

                                // System.out.println("New deadline: " + newTask.getDeadline());
                                // System.out.println("Difference: " + newTask.getPeriod() + ((int) (newTask.getPeriod() - newTask.getWCET())));
                                // System.out.println("Time Added: " + timeToAdd);
                                // System.out.println("\t- " + newTask.toString());
                                // newTask.setuuid(t.getUUIDObject());
                                // newTask.setTaskAutomata();
                                
                                // tempPool.add(newTask);
                                // repeatingTasks.add(newTask);
                            } else {
                                // System.out.println("\t-Still busy with the first round...");
                                Task newTask = new Task(t);

                                // In this one we modify the value before placing it into the tempQueue to be checked.
                                Double deadlineValue = originalDeadlineValues.get(newTask.getLabel());
                                Double originalPeriodValue = newTask.getPeriod();
                                // // if (currentTime == 0 && i == 0) {
                                double diff = newTask.getDeadline() - currentTime;
                                deadlineDifferences.put(newTask.getLabel(), diff);
                                // // } else if (i > 0) {
                                //     // double diff = 
                                // // }
                                currentTime += newTask.getWCET();

                                // // System.out.println(String.format("Adding original period (%f) to deadline (%f) for task %s: ", originalPeriodValue, deadlineValue, newTask.getLabel()));
                                // // System.out.println(String.format("Adding wcet (%f) to period (%f) for task %s: ", newTask.getWCET(), originalPeriodValue, t.getLabel()));
                                // // System.out.println("Old deadline: " + newTask.getDeadline());
                                // // newTask.setDeadline(originalPeriodValue + (deadlineValue - tvModelChecker.getTimeline()));
                                Double periodDiff = currentTime + newTask.getWCET() + originalPeriodValue;
                                
                                if (currentTime > 0) {
                                    periodDiff = currentTime + originalPeriodValue;

                                }
                                
                                periodDifferences.put(newTask.getLabel(), periodDiff);
                                // periodDifferences.put(newTask.getLabel(), originalPeriodValue);
                                // System.out.println("Previous period value for task: " + newTask.getPeriod());
                                // System.out.println("CUrrTime: " + currentTime);
                                // System.out.println("Periodval (relative) tro add: " + originalPeriodValue);
                                // System.out.println("periodDiff: " + periodDiff);
                                // newTask.setPeriod(periodDiff);
                                // System.out.println("New period value for task: " + newTask.getPeriod());
                                // System.out.println("Previous deadline for task: " + newTask.getDeadline());
                                // newTask.setDeadline(diff + newTask.getPeriod());    //--> OLLDD!
                                // System.out.println("New deadline for task: " + newTask.getDeadline());
                                // newTask.setDeadline(currentTime + newCurrTime + originalPeriodValue + newTask.getWCET() + (newTask.getDeadline() - (currentTime + newCurrTime)));
                                // System.out.println("New deadline: " + newTask.getDeadline());
                                // System.out.println("\t- " + newTask.toString());
                                // newTask.setuuid(t.getUUIDObject());
                                // newTask.setTaskAutomata();


                                // System.out.println("New Task Automata: " + newTask.getTimedAutomata().toString());
                                // newTask.getTimedAutomata().print();
                                // tempPool.add(newTask);
                                // repeatingTasks.add(newTask);   
                            }
                        }
                        else {
                            currentTime += t.getWCET();
                        }
                    }
                } 
                currentTime = tvModelChecker.getTimeline();
    
                // System.out.println("About to generate abstract queue...");
                generateAbstractQueue(abstractZn);
                // System.out.println("Just generated abstract queue...");
                TimedAutomata NTA;
                NTA = new TimedAutomata(automataArray.get(0));
                
                for(int i=1;i<automataArray.size();++i) {
                    TimedAutomata t = automataArray.get(i);
                    
                    // System.out.println("Adding the following TimedAutomata: " + t.toString());
                    // t.print();
                    // System.out.println("========================");

                    // System.out.println("NTA BEFORE ADDITION: " + NTA.toString());
                    // NTA.print();
                    NTA = NTA.addTimedAutomata(t);
                    // System.out.println("----------------------" + NTA.toString());
                    // System.out.println("NTA AFTER ADDITION: " + NTA.toString());
                    // NTA.print();
                }
                
                // System.out.println("Time before: " + tvModelChecker.timeline);
                // Should update the timeline after this...
                
                // Right before we check the task's schedulability, we prepare for the next iteration
                // should the tasks be schedulable...
                Entry currEntry = new Entry();
                for (int k = 0; k < tasksToBeChecked.size(); k++) {
                    Double newTime = currentTime;
                    Task currTask = tasksToBeChecked.get(k);
                    if (currTask.isPeriodic()) {
                        for (Task t : tasksToBeChecked) {
                            
                            if (Double.valueOf(t.getTaskLabel()) < Double.valueOf(currTask.getTaskLabel())) {
                                newTime += t.getWCET();
                            }
                        }
    
                        //Here we set the new Period & Deadline values...
                        // System.out.println("currTask: " + currTask.getLabel());
                        // for (Map.Entry<String, Double> entry : originalPeriodValues.entrySet()) {
                        //     String key = entry.getKey();
                        //     Double value = entry.getValue();
                        //     // System.out.println("Key: " + key + "\tValue: " + value);
                        // } 



                        // System.out.println("ENTRY TIME!!! Old Period value: " + currTask.getPeriod());    
                        // System.out.println("-> Original Period Value: " + originalPeriodValues.get(currTask.getLabel()) + " for task " + currTask.getLabel());

                        currTask.setPeriod(newTime + currTask.getWCET() + originalPeriodValues.get(currTask.getLabel()));
                        // System.out.println("ENTRY TIME!!! New Period value: " + currTask.getPeriod());
                        currTask.setDeadline(currTask.getPeriod() + deadlineDifferences.get(currTask.getLabel()));
                        currTask.setTaskAutomata();
    
                        currEntry.addTask(currTask);
                        tempPool.add(currTask);
                        repeatingTasks.add(currTask);
                    }
                }
                
                currEntry.setTime(currentTime);
                // currEntry.getAllPossiblePermutations();
                // iterationEntries.add(currEntry);
                
                threeValue = tvModelChecker.threeVReachability(NTA, abstractTaskQueue, counterPath);
                // System.out.println("Time after: " + tvModelChecker.timeline);
                // Here we update the task period values...

                

                abstractZn = tvModelChecker.timeline;
                // double timeElapsed = tvModelChecker.getElapsedTime();
                // System.out.println("Timeline: " + abstractZn);
                // System.out.println("Time elapsed: " + timeElapsed);
                iteration++;

                for (int i = 0 ; i < interval; i++) {
                    if (i < concreteTaskQueue.size()) {
                        tasksProcessed += 1;
                    }
                    // System.out.println("\t\t\tTasks Processed: " + tasksProcessed);
                }

                writeOnPath("Clocks= "+NTA.getClocks().size()+" States= "+NTA.getStateSet().size()+" Trans="+NTA.getTransitions().size()+"; \n", "Output"+label+".txt"); 
    
                if(threeValue==0)  {
                    System.out.println("NOT SCHED: ");
                    printCounterExample();
                    writeOnPath("Ite= "+iteration+" ; "+" Not Sched \n","Output"+label+".txt");
                    return false;
                } else if (threeValue == 1) {
                    // Create new entry in LinkedList that contains the tasks just checked
                    // Check LinkedList for a pattern...
                    if (iteration > (originalConcreteQueueSize * 3) && repeatingTasks.size() > 0) {
                        patternDetected = findPattern();
                    }
                }
    
                //System.out.println("Highest Clock Value: "+ abstractZn);
                //updateConcreteQueue(concreteTaskQueue, abstractTaskQueue);
                String reached = (threeValue == 1) ? "unknown/true" : "false";
                // System.out.println("QueueAbstractor - [queueAbstraction]: Reachability is " + reached);
            }
        }

        // if (tasksProcessed >= 8000) {
            System.out.println("=============================");
            System.out.println("Chain of all repeated tasks: ");
            int i = 0;
            for (Task t: repeatingTasks) {
                System.out.println("\t- " + t.toString() + " => " + i);
                i += 1;
            }

            System.out.println("=============================");
            System.out.println("Pattern found:");
            for (int j = 1; j < patternFound.size(); j++) {
                System.out.println("\t" + j + ") - " + patternFound.get(j).getLabel());
            }
            
            
        // }

        // System.out.println("QueueAbstractor - [queueAbstraction]: ConcreteQueue processed with no failed schedule");
        // System.out.println("QueueAbstractor - [queueAbstraction]: Schedulability property: Schedulable");
 
        if (patternDetected) {
            writeOnPath("Ite= "+iteration+" ; "+" Sched - Pattern detected\n", "Output"+label+".txt");
        } else {
            writeOnPath("Ite= "+iteration+" ; "+" Sched \n", "Output"+label+".txt");
        }

        return true;
    }

    public boolean findPattern() {
        // System.out.println("QueueAbstractor - [findPattern]: Setting up indices...");
        // System.out.println("QueueAbstractor - [findPattern]: Num repeating tasks: " + repeatingTasks.size());
        
        // Instead of cutting the first 5% of records, we cut the first concreteQueue - 
        // with immediate tasks present - from the dataset

        if (!dataCut) {
            LinkedList<Task> newRepeatingTasks = new LinkedList<>();
            for (int idx = originalConcreteQueueSize; idx < repeatingTasks.size(); idx++) {
                newRepeatingTasks.add(repeatingTasks.get(idx));
            }
            
            repeatingTasks = newRepeatingTasks;

            dataCut = true;
        }
        Random rand = new Random();
        int index = rand.nextInt(0, repeatingTasks.size());
        Task firstTask = repeatingTasks.get(index);
        Stack<Integer> indices = new Stack<>();
        Integer[] indicesArr = new Integer[1];
        
        for (int i = 0; i < repeatingTasks.size(); i++) {
            // Task curr = repeatingTasks.get(i);
            // System.out.println("QueueAbstractor - [findPattern]: " + firstTask.getLabel() + " == " + curr.getLabel() + "?");
            if (firstTask.getLabel().equals(repeatingTasks.get(i).getLabel())) {
                indices.push(i);
                // System.out.println("QueueAbstractor - [findPattern]: true");
            }
            else {
                // System.out.println("QueueAbstractor - [findPattern]: false");
            }
        }


        // System.out.println("QueueAbstractor - [findPattern]: Indices Stack Size:" + repeatingTasks.size());
        // System.out.println("QueueAbstractor - [findPattern]: Indices Array: ");
        // String indicesString = "[";
        // for (Integer i: indices) {
        //     indicesString += " " + i;
        // }
        // indicesString += "]";
        // System.out.println(indicesString);

        //Find a way to also iterate over all the indices to compare not just the first element
        indicesArr = new Integer[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indicesArr[i] = indices.get(i);
            // System.out.println("Indice at: " + i + " = " + indices.get(i) + "; indicesArr[" + i + "] = " + indicesArr[i]);
        }
        
        //Keeps track of indicesIndex...
        int k = 0;

        Long startTime = System.currentTimeMillis();
        Long threshold = Long.valueOf(100);
        
        // 'k' is the index tracking the current Pos in indices array for 1st element to be compared.
        for (k = 0; k < indicesArr.length; k++) {
            
            for (int i = k+1 ; i < indicesArr.length; i++) {
                //If the first task matches a task using indices
                int size = indicesArr[i] - indicesArr[k];
                // System.out.println("QueueAbstractor - [findPattern]: " + String.format("%d-%d",k,i) + ") Size: " + size);
                
                if (k != i && size > 3) {
                    // System.out.println("QueueAbstractor - [findPattern]: " + String.format("%d-%d",k,i) + ") Comparing elements: (" + repeatingTasks.get(indicesArr[k]) + ")[" + indicesArr[k] + "] with " + repeatingTasks.get(indicesArr[i])+ ")[" + indicesArr[i] + "]");
                    // System.out.println(String.format("QueueAbstractor - [findPattern]: RepeatingTasks[%d]: %s", indicesArr[k], repeatingTasks.get(indicesArr[k]).toString() ));
                    // System.out.println(String.format("QueueAbstractor - [findPattern]: RepeatingTasks[%d]: %s", indicesArr[i], repeatingTasks.get(indicesArr[i]).toString() ));
                    boolean match = repeatingTasks.get(indicesArr[k]).getLabel().equals(repeatingTasks.get(indicesArr[i]).getLabel());
                    // System.out.println(String.format("QueueAbstractor - [findPattern]: Match: %b",  match));
                    
                    if (match) {
                        // System.out.println("====================================================");
                        // System.out.println("\tStarting to check element by element...");
                        //TIMEOUT Check
                        if (System.currentTimeMillis() - startTime > threshold) {
                            // System.out.println("TIMEOUT!");
                            return false;
                        }
                        // int j = 1;
                        // System.out.println("Adding the following task: " + repeatingTasks.get(indicesArr[k]));
                        patternFound.push(repeatingTasks.get(indicesArr[k]));
                        // System.out.println("Pattern Found SOFAR: ");
                        // for (Task t: patternFound) {
                            // System.out.println(t.toString());
                        // }
                        
                        boolean allElementsMatch = true;
                        for (int j = 1; j < size; j++) {
                            // System.out.println("J =========== " + j);
                            // System.out.println("SIZE =========== " + size);
                            if (indicesArr[i]+j < repeatingTasks.size()) {
                                // System.out.println("QueueAbstractor - [findPattern]: " + String.format("%d-%d",k,i) + ") Comparing (" + repeatingTasks.get(indicesArr[k]+j).toString() + String.format("[%d]", indicesArr[k]+j) + "to (" + repeatingTasks.get(indicesArr[i]+j) + ")" + String.format("[%d]", indicesArr[i]+j));
                                // System.out.println("QueueAbstractor - [findPattern]: " + String.format("%d-%d",k,i) + ") Match: " + repeatingTasks.get(indicesArr[k]+j).getLabel().equals(repeatingTasks.get(indicesArr[i]+j).getLabel()));
                                        
                                    //j is how many elements next are we checking
                                    //k is the index of the first element to compare.
                                    //i is the index of the second element to compare.
                                    if (indicesArr[k]+j < repeatingTasks.size() && indicesArr[i]+j < repeatingTasks.size())
                                    {
                                        if (repeatingTasks.get(indicesArr[k]+j).getLabel().equals(repeatingTasks.get(indicesArr[i]+j).getLabel())) 
                                        {
                                            // System.out.println(String.format("%s == %s", repeatingTasks.get(indicesArr[k]+j).getLabel(), repeatingTasks.get(indicesArr[i]+j).getLabel()));
                                            // System.out.println("Adding the following task: " + repeatingTasks.get(indicesArr[i]+j));
                                            patternFound.push(repeatingTasks.get(indicesArr[i]+j));
                                            // System.out.println("Pattern Found SOFAR: ");
                                            // for (Task t: patternFound) {
                                                // System.out.println(t.toString());
                                            // }
                                            // patternLength += 1;
                                            if (j == size - 1 && allElementsMatch) {
                                                System.out.println("QueueAbstractor - [findPattern]: " + String.format("%d-%d",i,k) + ") $$$- MATCH FOUND -$$$");
                                                return true;
                                            } else {
                                                // Keep going...
                                                // System.out.println("QueueAbstractor - [findPattern]: j != size - 1" + "; j = " + j + String.format(";size-1 = %d", size-1));
                                            }
                                        } 
                                        else 
                                        {
                                            // No Match
                                            // System.out.println(String.format("%s != %s", repeatingTasks.get(indicesArr[k]+j).getLabel(), repeatingTasks.get(indicesArr[i]+j).getLabel()));
                                            allElementsMatch = false;
                                            patternFound.clear();
                                            // System.out.println("QueueAbstractor - [findPattern]: " + String.format("%d-%d",i,k) + ") NO MATCH!");
                                        }
                                    } 
                                    else
                                    {
                                        //IndicesArr still too small... skip
                                        // System.out.println("WE WANTED TO CHECK...");
                                        if (indicesArr[k]+j >= repeatingTasks.size()) {
                                            // System.out.println("BUUUUUTTTTTT (k) => Indices[k]+j > repatingTasks.size()");
                                        } else if (indicesArr[i]+j >= repeatingTasks.size()) {
                                            // System.out.println("BUUUUUTTTTTT (i) => Indices[i]+j > repatingTasks.size()");
                                        }
                                        allElementsMatch = false;
                                        patternFound.clear();
                                    }
                            } else {
                                // System.out.println("INDEX OUT OF BOUNDS BECAUSE YOU'RE TRYING TO ACCES SOMETHING THAT DOESNT EXIST");
                                allElementsMatch = false;
                                patternFound.clear();
                            }
                        } // END-FOR 
                            // else {
                            //     System.out.println("QueueAbstractor - [findPattern]: (j < size) && (size + j) < repeatingTasks.size() => " + String.format("(%d < %d) && (%d + %d) < repeatingTasks.size() => %b", j, size, size, j, (j < size) && (size + j) < repeatingTasks.size()));
                            // }
                    } else {
                        patternFound.clear();
                    }
                     // END-IF
                        // else {
                        //     System.out.println("QueueAbstractor - [findPattern]: (size + j) > indicesArr[k]+j => " + String.format("%d > %d", (size+j), (indicesArr[k]+j)));
                        // }
                } //END-IF
                else {
                    patternFound.clear();
                    // System.out.println("k == i OR size < 3: Skipping...");
                }
            } //END-FOR
                
                // else {
                //     System.out.println("QueueAbstractor - [findPattern]: " + String.format("%d-%d",i,k) + ") Skipping... ");
                // }

        } //END-FOR

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
