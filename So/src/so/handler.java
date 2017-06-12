package so;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/** Class control the process that will be arriving at the scheduler and its times of execution **/
public class handler{                            
    List<fullProcess> inputedL;    //List of associated processes and its resources        
    List<fullProcess> inCpu;       //List of process in cpu  
    changePriority changeP;
    output out;
    /** Sets the list as the inputed for testing **/
    public handler(List<fullProcess> inputedL, changePriority changeP){
        this.inputedL = inputedL;
        this.inCpu = new ArrayList();
        this.changeP = changeP;
    }
    /** Starts the control system **/
    public void run(){
        this.checkEndedResource();
        this.tryDeleteProcess();
        this.checkDeadline();
        this.tryInsertProcess();
        process p = this.requestResource(changeP.getProcess());    
        if(p != null){
       //     if(this.alreadyStored(changeP.getProcess()) == false)
                changeP.addStoredId(changeP.getProcess().getId());changeP.addStoredPrio(changeP.getProcess().getPriority());
         //   if(this.alreadyStored(p) == false)
                changeP.addStoredId(p.getId());                   changeP.addStoredPrio(p.getPriority());
            changeP.change(p); 
            changeP.getProcess().setBlocked(true);
        }
    }
    public void addOutput(fullProcess p){
        out.addOutput(p);
    }
    
    public void setOutput(output out){
        this.out = out;
    }
    public boolean alreadyStored(process p){
        Iterator<Integer> ip= changeP.getStoredId().iterator();
        while(ip.hasNext()){
            int pr = ip.next();
            if(pr == p.getId()){
                return true;
            }
        }
        return false;
    }
    public void wakeProcessForResource(){
        Iterator<process> ip = changeP.getQueue().iterator();
        while(ip.hasNext()){
            process p = ip.next();
            if(p.getBlocked() == true){
                if(changeP.areResourcesDifferent(changeP.getProcess(), p)){
                    p.setBlocked(false);
                    backPrio(p, backPrioCounter(p));
                    
                }
            }
        }
        this.backPrio(changeP.getProcess(),backPrioCounter(changeP.getProcess()));
    }
    
    public void trySetInsertTime(process proc){
        fullProcess full = this.getActualFullProcess(proc);
        if(full.getProcess().getExtime()==0){
            full.setInsertCpuTime(changeP.getTime());
            System.out.println("================"+ full.getInsertCpuTime());
        }
        
        
    }
    public void setFinishTime(fullProcess full){
        full.setFinishedTime(changeP.getTime());
        System.out.println("++++++++++++++"+full.getFinishedTime());
    }
    public void backPrio(process p, int c){
        if(p==null || c<1)
            return;
        Iterator<Integer> iid = changeP.getStoredId().iterator();
        Iterator<Integer> ipr = changeP.getStoredPrio().iterator();
        while(iid.hasNext()){
            int id = iid.next();
            int pr = ipr.next();
            if(p.getId()==id){
                if(c==1){
                    p.setPriority(pr);
                    iid.remove();
                    ipr.remove();
                    return;
                }
                else
                    c--;
            }
        }
    }
    
    public int backPrioCounter(process p){
        if(p==null)
            return -1;
        Iterator<Integer> iid = changeP.getStoredId().iterator();
        Iterator<Integer> ipr = changeP.getStoredPrio().iterator();
        int count = 0;
        int prio = -1;
        while(iid.hasNext() ){
            int id = iid.next();
            int pr = ipr.next();
            if(p.getId()==id){
                count++;
            }
        }
        return count;
    }
    /** Delete process if its executed time is equals to its weight**/
    public void tryDeleteProcess(){
        Iterator<fullProcess> it = inCpu.iterator();
        while(it.hasNext()){
            fullProcess full = it.next();
            if (full.getProcess().getId() == changeP.getProcess().getId()){
                if (changeP.getProcess().getExtime() == full.getWeight()){
                    changeP.deleteProcess();
                    this.setFinishTime(full);
                    out.addOutput(full);
                    it.remove();
                    this.wakeProcessForResource();
                }
            break;    
            } 
        }
    }
    /** Insert process if its starting time is the actual time **/
    public void tryInsertProcess(){
        Iterator<fullProcess> ir = inputedL.iterator();
        while(ir.hasNext()){
            fullProcess full = ir.next();
            if (full.getArriveTime() == changeP.getTime()){
                full.setPriority(-(changeP.getTime()+1) + (full.getProcess().getDline()+full.getArriveTime()));
             
                changeP.addQueue(full.getProcess());
                inCpu.add(full);
                System.out.println("Processo chegando: "+ full.getProcess().getId() + " prioridade: "+full.getProcess().getPriority());
                ir.remove();    
            }
        }
    }
    /** Checks if the procsses ended using a resource **/
    public void checkEndedResource(){
        fullProcess full = this.getActualFullProcess(changeP.getProcess());
        if(full == null)
            return;
        Iterator<resource> ir = full.getResource().iterator();
        while (ir.hasNext()){ 
            resource res = ir.next();
            if(res.getUsedtime() == res.getWeight()){
                Iterator<Integer> it = changeP.getProcess().getResource().iterator();
                while(it.hasNext()){
                    Integer r = it.next();
                    if(r == res.getId()){
                        it.remove();
                        this.wakeProcessForResource();
                    }
                }
                ir.remove();
 //???           if(changeP.areResourcesDifferent(changeP.getProcess(), changeP.getChange()) == true){
   //             changeP.changeProcessP(changeP.getProcess(), changeP.getChange());
          //      }
            }
        }
    }
    public void checkDeadline(){
        Iterator<fullProcess> fi = this.inCpu.iterator();
        while(fi.hasNext()){
            fullProcess full = fi.next();
            if(full.getProcess().getPriority()<0){
                System.out.println("Passou do deadline e não foi possivel executar");
                out.addOutput(full);
                this.deleteLostP(full.getProcess());
                fi.remove();
                changeP.addLostDline();
            }
        }
    
    }
    public void deleteLostP(process p){
        Iterator<process> ip = changeP.getQueue().iterator();
        while(ip.hasNext()){
            process pInQ = ip.next();
            if(pInQ.getId() == p.getId()){
                ip.remove();
                return;
            }
        }
        if(changeP.getProcess().getId() == p.getId()){
            changeP.deleteProcess();
        }
    }
   
    /** Try to alocate resouces to the process **/
    public process requestResource(process proc){
   //     System.out.println("Processo requistando recursos: "+proc.getId());
        fullProcess full = this.getActualFullProcess(proc); 
        if(full == null)
            return null;
        Iterator<resource> ir = full.getResource().iterator();
        while(ir.hasNext()){
            resource res = ir.next();
      //      System.out.println("Recurso a ser testado :"+res.getId());
                for (process p: changeP.getQueue()){
                    for(Integer r: p.getResource()){
                        if(r == res.getId()){
     //                       System.out.println("Recurso já alocado por outro processo");
                            
                            return p;
                        }
                    }
                }
            if(proc.getExtime() == res.getStart()){
        //        System.out.println("Recurso adicionado");
                proc.addResource(res.getId());
            }
        }
        return null;
    }
    
    /** Adds executed time to resouces **/
    public void addTimeRes(process p){    
        if(p == null || inCpu.isEmpty() || p.getId() == -1)
            return;
        fullProcess full = this.getActualFullProcess(p);
        if (full == null)
            return;
        for(resource res: full.getResource()){
            for(int r: p.getResource()){
                if(res.getId() == r){    
                    res.addExeTime();
                }
            }
        }
    }
    
    /** Gets fullProcess of the process executing **/
    public fullProcess getActualFullProcess(process p){
        for (fullProcess fullOfExe: this.inCpu){
            if(fullOfExe.getProcess().getId() == p.getId()){
               return fullOfExe;
            }
        }
        return null;
    }
}