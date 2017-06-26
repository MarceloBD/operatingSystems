package RTS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import so.InterfaceManager;

public class TopoDePrioridade {
    private ArrayList<Tarefa> tarefasNaoIniciadas;//tarefas de entrada que nao estao ainda no processador
    private ArrayList<Tarefa> tarefasIniciadas;//tarefas que ja estao sendo escalonadas
    private ArrayList<Recurso> recursos;//lista de recursos do processador
    private HashMap<String, Recurso> recursosUtilizados;//quais recursos estao alocados no momento
    private ArrayList<Integer> topoPrioridade;//lista ordenada com os 
    private int ciclo;//ciclo do processador

    public TopoDePrioridade(ArrayList<Tarefa> tarefasNaoIniciadas, ArrayList<Recurso> recursos) {
        this.tarefasNaoIniciadas = new ArrayList<>(tarefasNaoIniciadas);
        this.recursos = new ArrayList<>(recursos);
        this.topoPrioridade = new ArrayList<>();
        this.topoPrioridade.add(1000);
        this.tarefasIniciadas = new ArrayList<>();
        this.ciclo = 0;
        this.recursosUtilizados = new HashMap<>();
    }
    
    private void ordenaNaoIniciadas(){
        ArrayList<Tarefa> tarefas = new ArrayList<>();
        
        while(tarefasNaoIniciadas.size() > 0){
            int menorPos = 0;
            for(int i = 0; i < tarefasNaoIniciadas.size(); i++){
                if(tarefasNaoIniciadas.get(i).getTempoChegada() < tarefasNaoIniciadas.get(menorPos).getTempoChegada())
                        menorPos = i;
            }
            
            tarefas.add(tarefasNaoIniciadas.remove(menorPos));
        }
        
        tarefasNaoIniciadas = tarefas;
    }
    
    private void ordenaPrioridade(){
        ArrayList<Tarefa> tarefas = new ArrayList<>();
        
        while(tarefasIniciadas.size() > 0){
            int menorPos = 0;
            for(int i = 0; i < tarefasIniciadas.size(); i++){
                if(tarefasIniciadas.get(i).getPrioridadeAtual()< tarefasIniciadas.get(menorPos).getPrioridadeAtual())
                        menorPos = i;
            }
            
            tarefas.add(tarefasIniciadas.remove(menorPos));
        }
        
        tarefasIniciadas = tarefas;
    }
    
    private void setPrioridadeRecursos(){
        for(Recurso r : recursos){
            for(Tarefa t : tarefasNaoIniciadas){
                if(t.getPrioridadeAtual() < r.getPrioridade()){
                    for(RecursoTarefa rt : t.getRecursos()){
                        if(rt.getId().equals(r.getId()))
                            r.setPrioridade(t.getPrioridadeAtual());
                    }                        
                }
            }
            System.out.println("Recurso: " + r.getId());
            System.out.println("Prioridade: "+ r.getPrioridade());
        }
        System.out.println("\n");
    }
    
    private void iniciaTarefas(){
        for(int i = 0; i < tarefasNaoIniciadas.size(); i++){
            if(tarefasNaoIniciadas.get(i).getTempoChegada() > this.ciclo)
                return;
            
            tarefasIniciadas.add(tarefasNaoIniciadas.remove(i));
        }
    }
    
    public void escalona(){
        ordenaNaoIniciadas();
        setPrioridadeRecursos();
        
        while(!tarefasIniciadas.isEmpty() || !tarefasNaoIniciadas.isEmpty()){//numero grande, só pra teste
            iniciaTarefas();//verifica se alguma tarefa deve entrar no processador nesse ciclo
            ordenaPrioridade();//ordena as tarefas em ordem de prioridade
            
            if(!tarefasIniciadas.isEmpty()){
                ordenaPrioridade();
                Tarefa tarefaAtual = tarefasIniciadas.remove(0);//tarefa com maior prioridade
                while(tarefaAtual.getEstado() == Tarefa.BLOQUEADO){//procura uma tarefa que pode ser executada
                    int flag = 0;
                    for(RecursoTarefa rt : tarefaAtual.getRecursos()){
                       if(recursosUtilizados.containsKey(rt.getId())){//recurso nao esta mais bloqueado
                           flag = 1;
                       }

                       if(flag == 1) break;
                    }
                    if(flag == 0){
                        tarefaAtual.setEstado(Tarefa.PRONTO);//tarefa pronta para executar
                        break;
                    }
                    tarefasIniciadas.add(tarefaAtual);
                    tarefaAtual = tarefasIniciadas.remove(0);//troca a tarefa, se precisar
                }
                int i = 0;
                while(i == 0){//garante q o processo certo vai executar
                    ArrayList<String> recursosParaAlocar = tarefaAtual.precisaAlocar();//verifica se a tarefa atual precisa alocar algum recurso
                    if(recursosParaAlocar.isEmpty()) i = 1;
                    for(String s : recursosParaAlocar){//para cada recurso que é preciso alocar
                        if(recursosUtilizados.containsKey(s)){//se ele ja estiver alocado
                            tarefaAtual.setEstado(Tarefa.BLOQUEADO);//bloqueia a tarefa
                            recursosUtilizados.get(s).getTarefa().setPrioridadeAtual(tarefaAtual.getPrioridadeAtual());//troca a prioridade da tarefa que alocou o recurso primeiro
                            tarefasIniciadas.add(tarefaAtual);//coloca a tarefa de novo na lista de tarefas
                            tarefaAtual = recursosUtilizados.get(s).getTarefa();//começa a executar a tarefa que tinha alocado o recurso
                            tarefasIniciadas.remove(tarefasIniciadas.indexOf(tarefaAtual));//remove ela da lista
                        }else{//o recurso esta livre
                            if(tarefaAtual.getPrioridadeAtual() <= this.topoPrioridade.get(0)){//prioridade é menor doq o topo de prioridade
                                Recurso recurso = null;
                                for(Recurso r : recursos){//procura o recurso na lista
                                    if(s.equals(r.getId())){
                                        recurso = r;
                                        break;
                                    }
                                }
                                recursos.remove(recursos.indexOf(recurso));
                                recursosUtilizados.put(recurso.getId(), recurso);//coloca ele no map de recursos utilizados
                                recursosUtilizados.get(s).bloquearRecurso(tarefaAtual);//bloqueia o recurso
                                recursos.remove(recursosUtilizados.get(s));
                                recursos.add(recursosUtilizados.get(s));
                                if(recursosUtilizados.get(s).getPrioridade() < this.topoPrioridade.get(0)){//maior prioridade
                                    this.topoPrioridade.add(recursosUtilizados.get(s).getPrioridade());//coloca a prioridade na lista de topo
                                    Collections.sort(this.topoPrioridade);
                                }
                                i = 1;
                            }else{//nao é mais prioritario
                                tarefaAtual.setEstado(Tarefa.BLOQUEADO);//mesma rotina de quando o recurso ja esta alocado
                                tarefasIniciadas.add(tarefaAtual);
                                tarefaAtual = tarefasIniciadas.remove(0);
                                while(tarefaAtual.getEstado() == Tarefa.BLOQUEADO){//procura uma tarefa que pode ser executada
                                    int flag = 0;
                                    for(RecursoTarefa rt : tarefaAtual.getRecursos()){
                                       if(recursosUtilizados.containsKey(rt.getId())){//recurso nao esta mais bloqueado
                                           flag = 1;
                                       }

                                       if(flag == 1) break;
                                    }
                                    if(flag == 0){
                                        tarefaAtual.setEstado(Tarefa.PRONTO);//tarefa pronta para executar
                                        break;
                                    }
                                    tarefasIniciadas.add(tarefaAtual);
                                    tarefaAtual = tarefasIniciadas.remove(0);//troca a tarefa, se precisar
                                }
                            }
                        }
                    }
                }
                String idRecurso = "-";
                for(RecursoTarefa rt:tarefaAtual.getRecursos()){
                    if(recursosUtilizados.containsKey(rt.getId())){
                        idRecurso = rt.getId();
                        break;
                    }
                }
                
                if(tarefaAtual.incrementaContador() == 1){//incrementa o contador da tarefa
                    
                    ArrayList<String> recursosParaLiberar = tarefaAtual.precisaLiberar();//tarefa terminou de usar um recurso
                    for(String s : recursosParaLiberar){//para cada recurso que é preciso liberar
                        if(recursosUtilizados.containsKey(s)){
                            Recurso r = recursosUtilizados.remove(s);
                            r.liberarRecurso();//libera o recurso
                            recursos.add(r);
                            tarefaAtual.resetPrioridade();//reset a prioridade da tarefa atual
                            if(this.topoPrioridade.contains(r.getPrioridade())){//se ess recurso participou do topo, retira ele da lista
                                this.topoPrioridade.remove(this.topoPrioridade.indexOf(r.getPrioridade()));
                                Collections.sort(this.topoPrioridade);
                            }
                        }
                    }
                    
                    InterfaceManager.getInstance().logger.addResponseTime(1, (double) (this.ciclo - tarefaAtual.getTempoChegada())/tarefaAtual.getDuracao());
                    ArrayList<Integer> listaRecursos = new ArrayList<Integer>();
                    listaRecursos.add(InterfaceManager.getInstance().findResourceID(idRecurso));
                    InterfaceManager.getInstance().logger.insertCurrentExecution(1, InterfaceManager.getInstance().findProcessID(tarefaAtual.getId()), listaRecursos);//info de processo e recurso utilizado
                    
                }else if(this.ciclo >= tarefaAtual.getDeadline()){//verifica se perdeu o deadline
                    
                    InterfaceManager.getInstance().logger.incrementDeadline(1);
                    System.out.println("deadline: " + tarefaAtual.getDeadline());
                    ArrayList<Integer> listaRecursos = new ArrayList<Integer>();
                    listaRecursos.add(-1);
                    InterfaceManager.getInstance().logger.insertCurrentExecution(1, -1, listaRecursos);//info de processo e recurso utilizado
                
                }else{
                    
                    ArrayList<Integer> listaRecursos = new ArrayList<Integer>();
                    listaRecursos.add(InterfaceManager.getInstance().findResourceID(idRecurso));
                    InterfaceManager.getInstance().logger.insertCurrentExecution(1, InterfaceManager.getInstance().findProcessID(tarefaAtual.getId()), listaRecursos);//info de processo e recurso utilizado
                    
                    if(tarefaAtual.getContadorDeCiclos() == 1){
                        InterfaceManager.getInstance().logger.addWaitTime(1, this.ciclo - tarefaAtual.getTempoChegada());
                    }
                    
                    ArrayList<String> recursosParaLiberar = tarefaAtual.precisaLiberar();//tarefa terminou de usar um recurso
                    for(String s : recursosParaLiberar){//para cada recurso que é preciso liberar
                        if(recursosUtilizados.containsKey(s)){
                            Recurso r = recursosUtilizados.remove(s);
                            r.liberarRecurso();//libera o recurso
                            recursos.add(r);
                            tarefaAtual.resetPrioridade();//reset a prioridade da tarefa atual
                            if(this.topoPrioridade.contains(r.getPrioridade())){//se ess recurso participou do topo, retira ele da lista
                                this.topoPrioridade.remove(this.topoPrioridade.indexOf(r.getPrioridade()));
                                Collections.sort(this.topoPrioridade);
                            }
                        }
                    }
                    System.out.println("Ciclo: " + this.ciclo);
                    System.out.println("Tarefa Atual: " + tarefaAtual.getId());
                    tarefasIniciadas.add(tarefaAtual);
                    ordenaPrioridade();
                }
                
                tarefaAtual = null;
            }else{
                ArrayList<Integer> listaRecursos = new ArrayList<Integer>();
                listaRecursos.add(-1);
                InterfaceManager.getInstance().logger.insertCurrentExecution(1, -1, listaRecursos);//info de processo e recurso utilizado
            }
            this.ciclo++;
            for(Tarefa t:tarefasIniciadas){
                System.out.println("Tarefa: " + t.getId());
                System.out.println("Prioridade: " + t.getPrioridadeAtual());
            }
            System.out.println("\n");
        }
        return;
    }
    
}
