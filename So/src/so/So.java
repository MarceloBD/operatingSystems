package so;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/**
 * ALL THE CODE BELOW IS BASED ON THE ABSTRACT WRITTEN HERE https://pt.sharelatex.com/project/592a2e4280c9d4036cff4e73
 * UPDATE 07/06/2017 - ADDED A DESCRIPTION OF HOW THE ACTUAL CODE RUNS 
 */
public class So{
    public static void main(String[] args) {
        List<fullProcess> input = new ArrayList();
        /**      (new testproc(id, start, weight, deadline, new Arraylist(){{add(new resource(id, start, weight)}}**/
        input.add(new fullProcess(1,0,6,6, new ArrayList(){{add(new resource(0, 0, 3));add(new resource(1, 0, 3));}}));
        input.add(new fullProcess(2,0,6,6, new ArrayList(){{add(new resource(0, 1, 1));}}));
        input.add(new fullProcess(3,2,2,5, new ArrayList(){{add(new resource(0, 1, 1));}}));
        
        changePriority changeP = new changePriority();
        handler hand = new handler(input, changeP);
        output out = new output();
        hand.setOutput(out);
        
        boolean cpurunning = true;
        while(cpurunning == true){
           printSeparator();
           hand.run();
           changeP.run(hand);
           /*for the program stay not running forever*/
           if(changeP.getTime() == 50)
               break;
       }
       System.out.println("Processos perdidos: "+changeP.getLostDline());
       out.printOutput();
    }
    /** Separator for the cycles of processessing **/
    public static void printSeparator(){System.out.println("----------------------------------------");}
}
/** Class defining the process arriving at the scheduler **/
class process{                              
    int id;                 //identification
    int dline;              //deadline
    int priority;           //priority
    int exeTime;            //execution time
    List<Integer> resource; //list of resources
    boolean finished;       //if process is finished
    boolean blocked;        //if blocked and waiting resources
    /** Sets the parameters of the process **/
    public process(int id, int dline, int priority) {
        this.id = id;                   
        this.dline = dline;               
        this.priority = priority;       
        this.exeTime = 0;                 
        this.resource = new ArrayList();
        this.finished = false;
        this.blocked = false;
    }
    /** Updates priority of the process **/
    public void updatePrio(){this.priority--;}
    /** Adds execution time to process **/
    public void addExeTime(){this.exeTime++;}
    /** Just adds a resource in crescent order **/
    public void addResource(int id){
        for (int i = 0; i < this.resource.size(); i++) {
            if (this.resource.get(i) < id) 
                continue;
            else if (this.resource.get(i) == id){ 
                this.resource.add(i, id);
                return;
            }
            this.resource.add(i, id);
            return;
        }
        this.resource.add(id);
    }
    /** Getter and Setter methods **/
    public int getPriority(){return this.priority;}
    public int getId() {return id;}
    public int getDline() {return dline;}
    public List<Integer> getResource() {return resource;}
    public int getExtime(){return this.exeTime;}
    public boolean getDone(){return this.finished;}
    public void setDone(){this.finished = true;}
    public void setResource(List<Integer> resource) {this.resource = resource;}
    public void setPriority(int priority){this.priority = priority;}
    public void setBlocked(boolean bloc){this.blocked = bloc;}
    public boolean getBlocked(){return this.blocked;}
}
/** Class defining the resource's requests **/
class resource{             
    int id;         //identification
    int startTime;  //relative start time to the arriving time of the process
    int weight;     //weight 
    int exeTime;    //time spent on cpu   
    /** Sets the resource parameters **/
    public resource(int id, int start, int weight){
        this.id = id;               
        this.startTime = start;       
        this.weight = weight;       
        this.exeTime = 0;          
    }
    /** Adds used time for resource**/
    public void addExeTime(){this.exeTime++;}
    /** Gettter and setter methods **/
    public int getStart(){return this.startTime;}
    public int getUsedtime(){return this.exeTime;}
    public int getWeight(){return this.weight;}
    public int getId(){return this.id;}
}
/** Class that associates the process, resources and other parameters to control processes states **/
class fullProcess{            
    int arriveTime;             //arrive time 
    int weight;                 //weight
    process proc;               //process related to these parameters 
    int insetCpuTime;           //time the process was executed for the first time
    int finishedTime;           //time the process has ended its execution
    List<resource> resources;   //list of all resources used for the processs    
    /** Sets the parameters to control the process **/
    public fullProcess(int id, int arrive, int weight, int dline, List<resource> resources){        
        this.arriveTime = arrive;       
        this.weight = weight;       
        this.resources = resources; 
        this.proc = new process(id, dline, Integer.MAX_VALUE); 
        this.insetCpuTime = Integer.MAX_VALUE;
        this.finishedTime = Integer.MAX_VALUE;
    }
    /** Getter and Setter methods **/
    public int getArriveTime(){return this.arriveTime;}
    public int getId() {return proc.getId();}
    public int getDline() {return proc.getDline();}
    public int getWeight(){return this.weight;}
    public List<resource> getResource() {return this.resources;}
    public void setResource(List<Integer> resource) {proc.setResource(resource);}
    public process getProcess(){return this.proc;}
    public void setPriority(int prio){this.proc.setPriority(prio);}
    public void setProcess(process p){this.proc = p;}
    public void setFinishedTime(int t){this.finishedTime = t;}
    public int getFinishedTime(){return this.finishedTime;}
    public void setInsertCpuTime(int t){this.insetCpuTime = t;}
    public int getInsertCpuTime(){return this.insetCpuTime;}
}