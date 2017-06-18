package so;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**Class of the main method of change priority scheduler **/
public class changePriority{                          
    int time;                       //actual time 
    process proc;                   //process running
    List<process> priorityQueue;    //processes waiting            
    List<Integer> storedID;         //list of id of processes stored
    List<Integer> storedPrio;       //list of the last priorities of the processes  
    process newProcess;             //process that is tested to replace the actual
    final int nullProcessPrio = 
            Integer.MAX_VALUE;      //priority of a null process
    int lostDeadline;               //number of processes that have lost their deadlines
    /**Constructor**/
    public changePriority(){
        this.time = 0;
        this.priorityQueue = new ArrayList();
        this.proc = new process(-1,0, nullProcessPrio);
        this.newProcess = null;
        proc.setDone();
        this.storedPrio = new ArrayList();
        this.storedID = new ArrayList();
        this.lostDeadline = 0;
    }
    /** Scheduler itself, main function **/
    public void run(handler hand){
        int lowestPrio = this.getLowestPriority();
        if(lowestPrio == nullProcessPrio){this.cpuIsFree();}
        else if(newProcess != null){
            boolean isConflict = false;
            List<Integer> lastresources = newProcess.getResource();
            hand.requestResource(newProcess);
            isConflict = this.processCanBeChanged(); 
            if (isConflict == false){this.notConflict();}
            else{this.conflict(lastresources);}    
        }
        this.trySetInsertTime(hand, proc);
        proc.addExeTime();
        hand.addTimeRes(proc);
        time++;
        this.printScheduler();
        System.out.println("Recursos usados pelo processo atual: "+proc.getResource() + " Prioridade do processo atual: !"+this.proc.getPriority() +"! Processos em espera: "+this.priorityQueue );
        System.out.println("Processos com prioridade trocada: "+this.storedID);
        printPrio();
        this.updatePrio();
    }
    /** Makes all processes to lose -1 in priority each cicle **/
    public void updatePrio(){
        Iterator<process> ip = this.getQueue().iterator();
        while(ip.hasNext()){
            process p = ip.next();
            p.updatePrio();
        }
        this.proc.updatePrio();
        Iterator<Integer> ii = this.getStoredPrio().iterator();
        Iterator<Integer> id = this.storedID.iterator();
        List<Integer> aux = new ArrayList();
        while(ii.hasNext()){
            int d = id.next();
            int i = ii.next();
            i--;
            aux.add(i);
        }
        this.storedPrio = aux;
    }
    /** Prints the list of processes waiting and their priorities**/
    public void printPrio(){
        Iterator<process> ip = this.priorityQueue.iterator();
        while(ip.hasNext()){
            process p = ip.next();
            System.out.println("Processo aguardado: "+p.getId()+" Prioridade: "+p.getPriority());
        }
    }
    /** If its the first time the process will be executed, sets this time to its class **/
    public void trySetInsertTime(handler hand, process p){
        if(p == null||p.getId()==-1)
            return;
        hand.trySetInsertTime(p);
    }
    /** If there is not a process, set a null process as executing **/
    public void cpuIsFree(){
        if(proc.getId() != -1){this.setNullProcess();}
    }
    /** If there is not a process to be executed, it sets a defaut null process **/
    public void setNullProcess(){
        proc = new process(-1,-1,Integer.MAX_VALUE);
        proc.setDone();
    }
    /** Tests if the process can be changed. Return true if the process can be changed **/
    public boolean processCanBeChanged(){
            for(Integer ares : newProcess.getResource()){
                for(Integer pares: proc.getResource()){
                    if(ares == pares){
                        return true;
                    }
                }
            }
        return false;
    }
    /** If there is a conflict, resets the list of resources the process was using before **/
    public void conflict(List<Integer> lastresources){
        int priority;
        changeProcessP(proc, newProcess);
        newProcess.setResource(lastresources);
    }
    /** Change the priority of the process of the parameter with the actual process executing **/
    public void change(process p){
        if(this.proc.getPriority() < p.getPriority()){
            this.changeProcessP(this.proc, p);
        }
    } 
    /** If there isn't a conflict, sets the new process to be executed **/
    public void notConflict(){
        if(proc.getDone() != true)
            this.addQueue(proc);
            proc = newProcess;
            Iterator<process> it = priorityQueue.iterator();
                
            while(it.hasNext()){
            process aux = it.next();
            if(aux.getId() == newProcess.getId()){
                it.remove();
                break;
            }         
        }
    }
    /** Try to alocate resources for the process**/
    public void useNewResource(process p, handler conv){conv.requestResource(proc);}
     /** Gets the lowest priority value and sets paux process **/
    public int getLowestPriority(){
        int lowestPrio = Integer.MAX_VALUE;
        newProcess = null;
        if (proc.getDone() != true){
            lowestPrio = proc.getPriority();
        }
        for(process p: priorityQueue){
            if(p.getBlocked()==false){
                if(p.getPriority()<lowestPrio ){
                    lowestPrio = p.getPriority();
                    newProcess = p;
                }else if(p.getPriority()==lowestPrio&&newProcess==null||p.getPriority()==lowestPrio&&newProcess.getBlocked()==true){
                    if(proc.getBlocked()==true){
                        lowestPrio = p.getPriority();
                        newProcess = p;
                    }
                }
                else{
                    break;
                }
            }
        }
        return lowestPrio;
    }
    /** Prints the state of the scheduler **/
    public void printScheduler(){
        System.out.println("Processo executando: > "+this.proc.getId()+" < Tempo atual: |"+this.time+"| Tempo que processo já executou: "+proc.getExtime());
    }
    /** Changes the priorities of the processes **/
    public void changeProcessP(process p, process q){
        if (p == null || q == null)
            return;
        Integer aux = p.getPriority();
        p.setPriority(q.getPriority());
        q.setPriority(aux);
    }
    /** Check if the processes use differente resources **/
    public boolean areResourcesDifferent(process p, process q){
        for(Integer aux: p.getResource()){
            for(Integer bux: q.getResource()){
                if(aux == bux){
                    return false;
                }
            }
        }
        return true;
    }
    /** Adds the process to the waiting queue**/
    public void addQueue(process p){
        for (int i = 0; i < priorityQueue.size(); i++) {
            if (priorityQueue.get(i).getPriority() < p.getPriority()) 
                continue;
            else if (priorityQueue.get(i).getPriority() == p.getPriority()){ 
                priorityQueue.add(i, p);
                return;
            }
            priorityQueue.add(i, p);
            return;
        }
        this.priorityQueue.add(p);
    }
    /** Sets the state of the process to 'finished' **/
    public void deleteProcess(){this.proc.setDone();}
    /** Getter and Setter methods **/
    public List<process> getQueue(){return this.priorityQueue;}
    public int getTime(){return this.time;}
    public void addTime(){this.time++;}
    public process getProcess(){return proc;}   
    public void addStoredId(int id){this.storedID.add(id);}
    public void addStoredPrio(int prio){this.storedPrio.add(prio);}
    public List<Integer> getStoredId(){return this.storedID;}
    public List<Integer> getStoredPrio(){return this.storedPrio;}
    public void addLostDline(){this.lostDeadline++;}
    public int getLostDline(){return this.lostDeadline;}
}