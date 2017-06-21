/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class output {
    List<fullProcess> processOut;       //list of the processes after scheduler processing
    public enum executed{               //state as a result of the processessing
        Executado, Perdido
    }
    List<process> historic;             //list of the processes executing each time and their resources
    public output(){
        processOut = new ArrayList();
        historic = new ArrayList();
    }
    /** Adds a process to the output list **/
    public void addOutput(fullProcess p){
        if(p==null)
            return;
        processOut.add(p);
    }
    /** Prints all the outputed processes **/
    public void printOutput(){
        System.out.println("Saída:");
        for(fullProcess full: processOut){
            executed state = this.wasExecuted(full);
            System.out.println("Processo "+full.getId()+" "+state);
        }
    }
    /** Checks if the process was executed or was lost **/
    public executed wasExecuted(fullProcess full){
        if(full.getFinishedTime()==Integer.MAX_VALUE){
            return executed.Perdido;
        }
        else return executed.Executado;
    }
    /**Adds a process execution**/
    public void addHistoric(process p){
        process aux = new process(p.getId(),p.getDline(),p.getPriority());
        for(int i: p.getResource()){
            aux.addResource(i);
        }
        this.historic.add(aux);
    }
    /**Prints the historic of the processes that have been executed**/
    public void printHistoric(){
        int i = 1;
        for(process p: historic){
            System.out.println("---- Tempo: "+i+" Processo executado: "+p.getId()+" Recursos usados: "+p.getResource());
            i++;
        }
    }
}
