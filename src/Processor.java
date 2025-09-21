/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;

/**
 *
 * @author Madoda
 */

public final class Processor {
    //private Clock processorClock;
    private String label;
    private double quantumSlice;
//    private boolean inUse;
    private TimedAutomata processorAutomata;
    
    public Processor(String _label, double q) {
        System.out.println("Processor - Processor(String _label, double q) function:");
    	label = "Pr"+_label;
        System.out.println("\tLabel: " + label);
    	setQuantumSlice(q);
        System.out.println("\tQuantum Slice: " + q);
        setProcessorAutomata();
    }
    
    public Processor() {
//    	inUse = false;
    	label = "0";
    	setQuantumSlice(0);
        setProcessorAutomata();
    }
    
    public Processor(Processor other) {
        label = other.label;
        quantumSlice = other.quantumSlice;	
        processorAutomata = other.processorAutomata;
    }
    
    private void setQuantumSlice(double x)    {
        quantumSlice = x;
    }
    public TimedAutomata getAutomata()   {
        return processorAutomata;
    }
    
//    public boolean getInUse()   {
//        return inUse;
//    }
    
    public void setProcessorAutomata()   {
        System.out.println("Processor - setProcessorAutomata() function");
        System.out.println("\tCreating TimedAutomata...");
        
        processorAutomata = new TimedAutomata();

        System.out.println("Processor - setProcessorAutomata() function");
        System.out.println("\tCreating clock...");
        Clock clock = new Clock(0.0, "label");
        //processorAutomata.getClocks().add(clock);
        
        System.out.println("Processor - setProcessorAutomata() function");
        System.out.println("\tCreating TimedActions...");

        TimedAction acq = new TimedAction("acquire"+label, 0.0, true); //0
        TimedAction rel = new TimedAction("release"+label, 0.0, true);  //1
        processorAutomata.getTimedAction().add(acq);
        processorAutomata.getTimedAction().add(rel);
        
        System.out.println("Processor - setProcessorAutomata() function");
        System.out.println("\tAdding newly created TimedActions to processorAutomata...");
        
        //ClockConstraint window = new ClockConstraint("proQ", clock, quantumSlice, false);
        //processorAutomata.getClockConstraint().add(window);
        
        
        System.out.println("\tCreating timeslice used for preemption...");
        ArrayList<ClockConstraint> processingTimeSlice = new ArrayList<>(); //We do not use preemtion
        //processingTimeSlice.add(processorAutomata.getClockConstraint().get(0));
        
        System.out.println("\tCreating states 'Avail' and 'InUse'...");
        State avail= new State("Avail"+label,processingTimeSlice, true, false);
        State use= new State("InUse"+label, processingTimeSlice, false, false);
        
        System.out.println("Processor - setProcessorAutomata() function");
        System.out.println("\tAdding states 'Avail' and 'InUse' to processorAutomata");
        processorAutomata.getStateSet().add(avail);
        processorAutomata.getStateSet().add(use);
        
        //List of clock to reset in a transition
        System.out.println("\tCreating 'reset' and 'noResets' ArrayList<Clock>: both empty");
        ArrayList<Clock> resets = new ArrayList<>();
        ArrayList<Clock> noResets = new ArrayList<>();
        System.out.println("\tAdding clock created to 'resets' ArrayList<Clock>");
        resets.add(clock); 
        //delay2.add(2); 
        
        //(State source, State destination, ArrayList<ClockConstraint> guard, TimedAction act, ArrayList<Clock> resets)
        Transition availUse = new Transition(processorAutomata.getStateSet().get(0), processorAutomata.getStateSet().get(1), 
                processingTimeSlice, processorAutomata.getTimedAction().get(0), noResets);   
        Transition useAvail = new Transition(processorAutomata.getStateSet().get(1), processorAutomata.getStateSet().get(0), 
                processingTimeSlice, processorAutomata.getTimedAction().get(1), resets);      //acquire 
       

        System.out.println("Processor - setProcessorAutomata() function");
        System.out.println("\tAdding transitions 'availUse' and 'useAvail' to processorAutomata");         
        processorAutomata.getTransitions().add(availUse);
        processorAutomata.getTransitions().add(useAvail);
    }

    TimedAutomata getProcessorAutomata()    {
        return processorAutomata;
    }
    
    //public void print() {
        //System.out.println("PROCESSOR ATTRIBITES: "+ quantumSlice);
        //processorAutomata.print();
        //System.out.println();
    //} 
}
