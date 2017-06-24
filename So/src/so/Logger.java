/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so;

import java.util.ArrayList;

/**
 *
 * @author Arthur
 */
public class Logger {
    private ArrayList<ArrayList<Double>> responseTime;
    private ArrayList<ArrayList<Integer>> waitTime;
    private ArrayList<ArrayList<Integer>> processExecuting;
    private ArrayList<ArrayList<ArrayList<Integer>>> resourceExecuting;
    private double[] responseMean;
    private double[] responseDeviation;
    private double[] waitMean;
    private double[] waitDeviation;
    private int[] missedDeadlines;
    private boolean[] calculatedStats;
    
    public Logger() {
        responseTime = new ArrayList<ArrayList<Double>>();
        waitTime = new ArrayList<ArrayList<Integer>>();
        processExecuting = new ArrayList<ArrayList<Integer>>();
        resourceExecuting = new ArrayList<ArrayList<ArrayList<Integer>>>();
        for(int i = 0; i < 2; i++) {
            responseTime.add(new ArrayList<Double>());
            waitTime.add(new ArrayList<Integer>());
            processExecuting.add(new ArrayList<Integer>());
            resourceExecuting.add(new ArrayList<ArrayList<Integer>>());
        }
        responseMean = new double[2];
        responseDeviation = new double[2];
        waitMean = new double[2];
        waitDeviation = new double[2];
        missedDeadlines = new int[2];
        missedDeadlines[0] = missedDeadlines[1] = 0;
        calculatedStats = new boolean[2];
        calculatedStats[0] = calculatedStats[1] = false;
    }
    
    /**
     * Adiciona um novo tempo de resposta
     * @param id
     * @param responseT
     */
    public void addResponseTime(int id, double responseT) {
        responseTime.get(id).add(responseT);
    }
    
    /**
     * Adiciona um novo tempo de espera
     * @param id
     * @param waitT
     */
    public void addWaitTime(int id, int waitT) {
        waitTime.get(id).add(waitT);
    }
    
    /**
     * Calcula a media e a variancia daquele ID
     * @param id 
     */
    private void calculateValue(int id) {
        double sum = 0;
        for(double response : responseTime.get(id)) {
            sum += response;
        }
        responseMean[id] = sum / (double)responseTime.get(id).size();
        sum = 0;
        for(int response : waitTime.get(id)) {
            sum += response;
        }
        waitMean[id] = sum / (double)waitTime.get(id).size();
        
        double variance = 0;
        for(double response : responseTime.get(id)) {
            variance += Math.pow(response-responseMean[id],2);
        }
        variance /= (responseTime.get(id).size() - 1);
        responseDeviation[id] = Math.sqrt(variance);
        
        variance = 0;
        for(int response : waitTime.get(id)) {
            variance += Math.pow(response-waitMean[id],2);
        }
        variance /= (waitTime.get(id).size() - 1);
        waitDeviation[id] = Math.sqrt(variance);
        
        calculatedStats[id] = true;
    }
    
    /**
     * Define o valor de deadline
     * @param id
     * @param missedDeadlines 
     */
    public void setMissedDeadline(int id, int missedDeadlines) {
        this.missedDeadlines[id] = missedDeadlines;
    }
    
    /**
     * Incrementa a deadline por um valor unitário
     * @param id 
     */
    public void incrementDeadline(int id) {
        this.missedDeadlines[id]++;
    }
    
    /**
     * Adiciona o estado atual de execução para o histórico
     * @param id
     * @param processID
     * @param resourceID 
     */
    public void insertCurrentExecution(int id, int processID, ArrayList<Integer> resourceID){
        this.processExecuting.get(id).add(processID);
        this.resourceExecuting.get(id).add(resourceID);
    }
    
    public void printExecution(int id) {
        System.out.println("|-----------------------------|");
        System.out.println("Experimento " + id);
        for(int i = 0; i < processExecuting.get(id).size(); i++) {
            System.out.println("-> Instante " + i);
            if(resourceExecuting.get(id).get(i).get(0) != -1)
                System.out.println("Processo: " + processExecuting.get(id).get(i) +
                               ", Recurso: "+ resourceExecuting.get(id).get(i));
            else
                System.out.println("Processo: " + processExecuting.get(id).get(i));
        }
        System.out.println("|-----------------------------|");
    }
    
    //Getters ------------------------------------------------------------------
    public double getWaitMean(int id){
        if(!calculatedStats[id])
            calculateValue(id);
        return waitMean[id];
    }
    
    public double getResponseMean(int id){
        if(!calculatedStats[id])
            calculateValue(id);
        return responseMean[id];
    }
    
    public double getWaitDeviation(int id){
        if(!calculatedStats[id])
            calculateValue(id);
        return waitDeviation[id];
    }
    
    public double getResponseDeviation(int id){
        if(!calculatedStats[id])
            calculateValue(id);
        return responseDeviation[id];
    }
    
    public int getMissedDeadlines(int id) {
        return missedDeadlines[id];
    }
    
    public ArrayList<Integer> getProcessExecution(int id) {
        return this.processExecuting.get(id);
    }
    
    public ArrayList<ArrayList<Integer>> getResourceExecution(int id) {
        return this.resourceExecuting.get(id);
    }
}
