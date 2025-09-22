

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;  // Import the File class
import java.io.UnsupportedEncodingException; // Import the Scanner class to read text files
import java.util.*;


public class TaskGenerator {
		String label; 
        ArrayList<Task> taskSet; //new ArrayList<>();
        int numberOfTasks;
        double utilization; 
        int seed;

	public TaskGenerator(String l, int setSize, double utilize, int _seed) {
		label = l;
        taskSet = new ArrayList<>();
        numberOfTasks = setSize;
        utilization = utilize;
        seed = _seed;        
	}
	
	public TaskGenerator(String filename, double utilize, int _seed) {
        System.out.println("TaskGenerator - TaskGenerator(String filename, double utilize, int _seed) function: ");

		label = filename;
        taskSet = new ArrayList<>();
        System.out.println("\tfileName: " + label);
        System.out.println("\ttaskSet: empty");
        readTaskSet(filename);
        numberOfTasks = taskSet.size();
        utilization = utilize;
        seed = _seed;
        System.out.println(label+" TASKSET SIZE: "+numberOfTasks+" "+seed+" "+utilization);
	}
	
	public String getLabel()    {
        return label;
    }
        
    public ArrayList<Task> getTaskSet()    {
    	return taskSet;
    } 
    
    public void setLabel(String l)    {
        label = l;
    }
         
        
        //Bini Enrico - Biasing Effects in Schedulability Measures
        private List<Double> uFitting()    {
            System.out.println("uFitting function:\n");
            List<Double> vectorU = new ArrayList<>();
            vectorU.add(0.0);
            double upLimit = utilization;
            System.out.println("upLimit: " + upLimit);
            //System.out.println("Task UTE: "+ upLimit);
            Random random = new Random(seed);
            for(int i=1; i<numberOfTasks-1;i++)    {
                double randUte = random.nextDouble()*upLimit;
                System.out.println("\t\tRandUTE Generated: " + randUte);
                vectorU.add(randUte);
                upLimit = upLimit-vectorU.get(i);
                System.out.println("\t\tupLimit Changed to: " + upLimit + "\n");
            }

            System.out.println("Printing vectorU:");
            for (double d : vectorU) {
                System.out.println("\t" + d);
            }

            vectorU.add(upLimit);
            return vectorU;
        }
        
        /*private int[][] gnerateMatrix(int  rowSize) {
            int colSize = Math.Rand(Math.random(1,row));
            int matrix[rowSize][colSize]; 
            for (int i=0;i<colSize;i++)
                matrix[i][0] = 

        }*/
        
        private double detPeriod()    {
              /*for(int i=0; i<size; i++)
                for(int j=0; j<size; j++)
                    Mij[i][j] = 0;
             
            double period = 1.0;
            Random rand = new Random();
            for(int i=0; Mij.size();i++)    {
                for(int j=0; j<Mij.size();j++)  {
                    p = Math.round(Math.floor(Math.random()*Mij[i,j]+1));
                    period = period * Mij[i,p];
                }
            }*/
            return 0;
        }
        
        public void readTaskSet(String fileName)	{
            System.out.println("TaskGenerator - readTaskSet(String fileName) function");
        	try {
                File myObj = new File("../tasksetInput/" + fileName);

                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
        	        
        	        String[] splited = data.trim().split("\\s+");
        	        //Task ts = new Task(splited[0], Integer.parseInt(splited[1]), Integer.parseInt(splited[2]), Integer.parseInt(splited[3]), 0); 
        	        //int i = Integer.parseInt(splited[0]);
                    System.out.println("\tCreating new task...");

        	        Task ts = new Task(splited[0], Integer.parseInt(splited[1]), Integer.parseInt(splited[2]), Integer.parseInt(splited[3]), 0); 
        	        //Task t = new Task("T"+i,wcet,period, deadline,occurance); //, occurance);   Task(String s, double w, double p, double d)
                    
                    System.out.println("TaskGenerator - readTaskSet(String fileName) function");
                    System.out.println("\tSetting Task Automata...");

                    ts.setTaskAutomata();
                    taskSet.add(ts);

                    System.out.println("TaskGenerator - readTaskSet(String fileName) function");
                    System.out.println("\t Added new task to taskSet...");
        	        
        	        //System.out.println(ts.getLabel()+" WCET "+ts.getWCET()+" DEADL"+ts.getDeadline());
        	        
        	      }
        	      myReader.close();
        	    } catch (FileNotFoundException e) {
        	      System.out.println("An error occurred.");
        	      e.printStackTrace();
        	    }
        }
        
        public void generateTaskSet(double periodmax, double periodmin, double periodStep) throws FileNotFoundException, UnsupportedEncodingException    {        	
            int currentLoad = 0;
            int i=0;
            List<Double> taskUtils;
            taskUtils = uFitting(); //(int numTask, double utilization, int seed)

            System.out.println("generateTaskSet function:");
            int whileLoopIteration = 0;

            while(currentLoad < utilization && i < numberOfTasks)    {
                double period = (int)(Math.round((Math.random()*(periodmax/periodStep-periodmin/periodStep)+periodmin/periodStep))*periodStep);
                double wcet = Math.round(taskUtils.get(i)*period)+1;
                //double occurrence = Math.Round(Math.random(o1,o2)*period);
                double occurance = 0; //Math.round(Math.random()*period);
                //double occurance = 0;
                double deadline = Math.round(Math.random()*(period-wcet) + wcet);//(period-wcet)*Math.random()*range)+wcet;
                //deadline = period = 100;
                System.out.println("\t(" + whileLoopIteration + ") While currentLoad (" + currentLoad + ")  < utilization ( " + utilization + ") && i (" + i + ") < numberOfTasks" + numberOfTasks + ")");
                System.out.println("\t\tPeriod: " + period);
                System.out.println("\t\twcet: " + wcet);
                System.out.println("\t\toccurance: " + occurance);
                System.out.println("\t\tdeadline: " + deadline);
                whileLoopIteration += 1;

                if(currentLoad + (wcet/period) <= 1){
                    System.out.println("\t\t=== New Task Created ===");
                    currentLoad = (int) (currentLoad + (wcet/period));
                    Task t = new Task("T"+i,wcet,period, deadline,occurance); //, occurance);   Task(String s, double w, double p, double d)
                    System.out.println("\t\tCurrentLoad: " + currentLoad);
                    System.out.println(t.toString());
                    t.setTaskAutomata();
                    taskSet.add(t);
                    i=i+1;
                    System.out.println("TaskGenerator - generateTaskSet(double periodmax, double periodmin, double periodStep) function");
                    System.out.println("\tAdded new task to taskSet");
                }
                
            }
            /*
            Collections.sort(taskSet, new Comparator<Object>() {
                @Override
    			public int compare(Object o1, Object o2) {
                	Task oA = (Task) o1;
                	Task oB = (Task) o2;
                	if(t==0)  	{	
                		System.out.println("First Come First Serve Queue: ");           	
                		return oA.getOccurance() < oB.getOccurance() ? -1 : 1;
                	}
                	else if (t==1) 	{	
                		System.out.println("Shortest Remaining Time First: ");
                		return oA.getDeadline() < oB.getDeadline() ? -1 : 1;
                	}
                	else if (t==2) 	{	
                		System.out.println("Longest Remaining Time First: ");
                		return oA.getDeadline() > oB.getDeadline() ? -1 : 1;
                	}
                	else if (t==3)		{	
                		System.out.println("Highest Response Ration Next: ");
                		return oA.getResponseRatio() < oB.getResponseRatio() ? -1 : 1;
                	}
                	else
                		return oA.getOccurance() < oB.getOccurance() ? -1 : 1;
    			}
            });
            
            for(Task t : taskSet)	{
            	writer.println(t.toString());
            }
            writer.close();*/
            
        }   
        
        public void taskSetSort(int t)		{
        	System.out.println("TaskGenerator - taskSetSort(int t) function");

        	PrintWriter writer = null;
			try {
				writer = new PrintWriter("STTTExpNo"+label+".txt", "UTF-8");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	Collections.sort(taskSet, new Comparator<Object>() {
                @Override
    			public int compare(Object o1, Object o2) {
                	Task oA = (Task) o1;
                	Task oB = (Task) o2;

                    // System.out.println("  Enter 1 - First Come First Serve Queue: ");
                    // System.out.println("  Enter 2 - Earliest Deadline First Queue: ");
                    // System.out.println("  Enter 3 - Longest Remaining Time First Queue: ");
                    // System.out.println("  Enter 4 - Highest Response Ration Next Queue: ");
                    // System.out.println("  Enter 5 - Round Robin (Preemptive): ");
                    // System.out.println("  Enter 6 - Fixed Priority (Preemptive): ");
                    System.out.println("TaskGenerator - taskSetSort(int t)");
                    System.out.println("\tt Value = " + t);

                    switch (t) {
                        case 1: {
                            System.out.println("\tFirst Come First Serve Queue: ");           	
                            return oA.getOccurance() < oB.getOccurance() ? -1 : 1;
                        }
                        case 2:  {
               		        System.out.println("\tShortest Deadline Time First: ");
                            return oA.getDeadline() < oB.getDeadline() ? -1 : 1;
                        }
                        case 3: {
               		        System.out.println("\tLongest Remaining Time First: ");
                            return oA.getWCET() > oB.getWCET() ? -1 : 1;
                        }
                        case 4:  {
                            System.out.println("\tHighest Response Ration Next: ");
                            return oA.getResponseRatio() < oB.getResponseRatio() ? -1 : 1;
                        }
                        case 5:  {
                            System.out.println("\tRound Robin sorting...");
                            return oA.getOccurance() < oB.getOccurance() ? -1 : 1;
                        }
                        default: {
                            System.out.println("\tDefault Scheduling...");
                            return oA.getOccurance() < oB.getOccurance() ? -1 : 1;
                        }
                    }
    			}
            });
            
            for(Task ti: taskSet)	{
            	writer.println(ti.toString());
            }
            writer.close();
            System.out.println("\tWrote to output file...");
        }
        
        
        
        
      //double period = detPeriod();
        //(double minPeriod, double maxPeriod, double stepPeriod, double minLoad, double maxLoad, double stepLoad, int numberTasks, int seed)

        //List<Task> taskSet = new ArrayList<Task>(); 
      //double deadline = Math.Round((period-wcet)*Math.random(d1,d2))+wcet;
        //double range = Math.random()*(period-wcet) + wcet; 
        //double wcet = Math.max(1, Math.round(Rand(u1,u2)*period));
        //double wcet = Math.max(1, Math.round(taskUtils.get(i-1)*period));
        //double wcet = Math.max(1, Math.round(taskUtils.get(i-1)*period));
      //numberTasks = i;
        //Collections.sort(taskSet);
        
        public void print() {
        	for(Task t: taskSet)
        		System.out.println(t.toString());
        }

	
	
}
