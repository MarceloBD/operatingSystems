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
    List<fullProcess> processOut;
    public enum executed{
        Executado, Perdido
    }
    public output(){
        processOut = new ArrayList();
    }
    public void addOutput(fullProcess p){
        if(p==null)
            return;
        processOut.add(p);
    }
    public void printOutput(){
        System.out.println("Saída:");
        for(fullProcess full: processOut){
            executed state = this.wasExecuted(full);
            System.out.println("Processo "+full.getId()+" "+state);
        }
    }
    public executed wasExecuted(fullProcess full){
        if(full.getFinishedTime()==Integer.MAX_VALUE){
            return executed.Perdido;
        }
        else return executed.Executado;
    }
}