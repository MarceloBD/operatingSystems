/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import RTS.Recurso;
import RTS.RecursoTarefa;
import RTS.Tarefa;
import RTS.TopoDePrioridade;
import java.util.List;
import static so.So.printSeparator;

/**
 * Classe responsável por gerenciar janela ativa e manter dados entre janelas
 * além de chamar os escalonadores e transmitir de volta os resultados.
 * É, ela faz quase tudo, mas por trás dos panos
 * 
 * @author Arsart
 */

public class InterfaceManager implements ActionListener{

    //Instancia singleton do objeto
    private static InterfaceManager instance;
    
    private javax.swing.JFrame actualWindow;
    private HashMap<Integer, String> processAlias;
    private HashMap<Integer, String> resourceAlias;
    private ArrayList<Tarefa> process;
    private ArrayList<Recurso> resources;
    public Logger logger;
    
    public static InterfaceManager getInstance() {
        if(instance == null) {
            instance = new InterfaceManager();
        }
        return instance;
    }
    
    private InterfaceManager(){
        processAlias = new HashMap<Integer, String>();
        resourceAlias = new HashMap<Integer, String>();
        process = new ArrayList<Tarefa>();
        resources = new ArrayList<Recurso>();
        logger = new Logger();
    }
    
    public void setCurrentWindow(javax.swing.JFrame newView){
        if(actualWindow != null)
            actualWindow.setVisible(false);
        actualWindow = newView;
        actualWindow.setVisible(true);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormEstatisticasGerais.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormEstatisticasGerais.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormEstatisticasGerais.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormEstatisticasGerais.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        
        FormTarefa fm = new FormTarefa(InterfaceManager.getInstance());
        InterfaceManager.getInstance().setCurrentWindow(fm);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if(ae.getActionCommand().equals("Tarefa_Adicionar")) {
            try{
                FormTarefa ft = (FormTarefa) actualWindow;
                getProcess(ft);
            }
            catch(Exception e) {
                System.out.println("Deu ruim. Janela errada");
            }
        }
        else if(ae.getActionCommand().equals("Tarefa_Proximo")){
            FormRecurso fr = new FormRecurso(this);
            this.setCurrentWindow(fr);
        }
        else if(ae.getActionCommand().equals("Recurso_Adicionar")) {
            try {
                FormRecurso fr = (FormRecurso) actualWindow;
                getResource(fr);
            }
            catch(Exception e) {
                System.out.println("Deu ruim. Janela errada");
            }
        }
        else if(ae.getActionCommand().equals("Recurso_Proximo")) {
            FormAtribuicao fa = new FormAtribuicao(this);
            this.setCurrentWindow(fa);
            fa.setTableValues(processAlias, resourceAlias);
        }
        else if(ae.getActionCommand().equals("Atribuicao_Adicionar")) {
            try {
                FormAtribuicao fa = (FormAtribuicao) this.actualWindow;
                linkResource(fa);
            }
            catch(Exception e) {
                System.out.println("Deu ruim. Janela errada");
            }
        }
        else if(ae.getActionCommand().equals("Atribuicao_Proximo")) {
            FormEstatisticasGerais feg = new FormEstatisticasGerais(this);
            this.setCurrentWindow(feg);
            //startPriorityExchange();
            startPriorityTop();
            //setStats(feg,0);
            setStats(feg,1);
            
            System.out.println("Nova janela");
        }
        else if(ae.getActionCommand().equals("Estatisticas_Encerrar")){
            System.exit(0);
        }
    }
    
    private void getProcess(FormTarefa ft) {
        String name = ft.getProcessName();
        int weight = ft.getWeight();
        int arrive = ft.getArrive();
        int deadline = ft.getDeadline();
        int id = ft.getID();
        processAlias.put(id, name);
        Tarefa t = new Tarefa(name, arrive, deadline, weight);
        process.add(t);
    }
    
    private void getResource(FormRecurso fr) {
        String name = fr.getResourceName();
        int id = fr.getID();
        
        resourceAlias.put(id, name);
        Recurso r = new Recurso(name);
        resources.add(r);
    }
    
    private void linkResource(FormAtribuicao fa) {
        int processID = fa.getProcessID();
        int resourceID = fa.getResourceID();
        int weight = fa.getWeight();
        int begin = fa.getBegin();
        
        process.get(processID).adicionarRecurso(resources.get(resourceID), begin, weight);
        
        System.out.println("Processo " + processID + ", Recurso " + resourceID);
        /*
        if(process.get(processID).getResource() == null)
            process.get(processID).setResource(new ArrayList<resource>());
        resource r = new resource(resourceID, begin, weight);
        process.get(processID).getResource().add(r);
        */
    }
    
    private ArrayList<fullProcess> convertProcess(ArrayList<Tarefa> at) {
        ArrayList<fullProcess> afp = new ArrayList<fullProcess>();
        for(int i = 0; i <  at.size(); i++) {
            Tarefa t = at.get(i);
            ArrayList<resource> ar = new ArrayList<resource>();
            for (int j = 0; j < t.getRecursos().size(); j++) {
                RecursoTarefa orgRec = t.getRecursos().get(j);
                int id = findResourceID(orgRec.getId());
                resource r = new resource(id,orgRec.getTempoDeInicio(),orgRec.getDuracao());
                ar.add(r);
            }
            fullProcess f = new fullProcess(i,t.getTempoChegada(),t.getDuracao(),t.getDeadline(),ar);
            afp.add(f);
        }
        return afp;
    }
    
    public int findResourceID(String name) {
        for(int i = 0; i < resourceAlias.size(); i++) {
            if(resourceAlias.get(i).equals(name))
                return i;
        }
        return -1;
    }
    
    public int findProcessID(String name) {
        for(int i = 0; i < processAlias.size(); i++) {
            if(processAlias.get(i).equals(name))
                return i;
        }
        return -1;
    }
    
    //Retorna quantas perdas ocorreram na troca de prioridade
    private void startPriorityExchange(){
        List<fullProcess> input = convertProcess(process);
        
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
       logger.setMissedDeadline(0, changeP.getLostDline());
       out.sendLog();
       //System.out.println("Processos perdidos: "+changeP.getLostDline());
       out.printOutput();
       out.printHistoric();
    }
    
    private void startPriorityTop(){
        TopoDePrioridade top = new TopoDePrioridade(process, resources);
        top.escalona();
    }
    
    private void setStats(FormEstatisticasGerais feg,int id) {
        double waitMean = logger.getWaitMean(id);
        double responseMean = logger.getResponseMean(id);
        double waitDeviation = logger.getWaitDeviation(id);
        double responseDeviation = logger.getResponseDeviation(id);
        int lostDeadlines = logger.getMissedDeadlines(id);
        
        feg.setStats(id, responseMean, waitMean, responseDeviation, waitDeviation, lostDeadlines);
        logger.printExecution(id);
    }
}
