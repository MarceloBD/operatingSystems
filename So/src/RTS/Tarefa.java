package RTS;

import java.util.ArrayList;

/*
modelo de representação da tarefa
*/

public class Tarefa {
    public final static int BLOQUEADO = 0;//constantes de estado da tarefa
    public final static int PRONTO = 1;
    
    private String id;//identificador
    private int tempoChegada;//tempo de entrada da tarefa no processador
    private int deadline;//tempo limite para a execução
    private int prioridadeOriginal;//prioridade da tarefa quando ela chega
    private int prioridadeAtual;//prioridade da tarefa após uma troca
    private int estado;//estado da tarefa
    private ArrayList<RecursoTarefa> recursos;//lista de recursos que serão utilizados 
    private int duracao;//quanto tempo a tarefa deve ficar na cpu
    private int contadorDeCiclos = 0;//ciclos da tarefa

    public Tarefa(String id, int tempoChegada, int deadline, int duracao) {
        this.id = id;
        this.tempoChegada = tempoChegada;
        this.deadline = deadline;
        this.prioridadeOriginal = this.deadline - this.tempoChegada;
        this.prioridadeAtual = this.prioridadeOriginal;
        this.estado = Tarefa.PRONTO;
        this.recursos = new ArrayList<>();
        this.duracao = duracao;
    }

    public String getId() {
        return id;
    }

    public int getTempoChegada() {
        return tempoChegada;
    }

    public void setTempoChegada(int tempoChegada) {
        this.tempoChegada = tempoChegada;
    }

    public int getPrioridadeAtual() {
        return prioridadeAtual;
    }

    public void setPrioridadeAtual(int prioridadeAtual) {
        this.prioridadeAtual = prioridadeAtual;
    }

    public int getEstado() {
        return estado;
    }
    
    public int getDuracao() {
        return this.duracao;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getDeadline() {
        return deadline;
    }
    
    public int getContadorDeCiclos() {
        return contadorDeCiclos;
    }
    
    public void resetPrioridade(){
        this.prioridadeAtual = this.prioridadeOriginal;
    }
    
    public boolean estourouDeadline(int tempoFim){
        if(tempoFim <= this.deadline)
            return false;
        
        return true;
    }
    
    public void adicionarRecurso(Recurso recurso, int tempoInicio, int duracao){
        recursos.add(new RecursoTarefa(recurso.getId(), tempoInicio, duracao));
    }
    
    public int incrementaContador(){
        if(++contadorDeCiclos == duracao){
            return 1;
        }
        
        return 0;
    }

    public ArrayList<RecursoTarefa> getRecursos() {
        return recursos;
    }
    //verifica se a tarefa precisa alocar algum recurso no ciclo de clock atual
    public ArrayList<String> precisaAlocar(){
        ArrayList<String> ids = new ArrayList<>();
        for(RecursoTarefa rt : recursos){
            if(contadorDeCiclos == rt.getTempoDeInicio())
                ids.add(rt.getId());
        }
        
        return ids;
    }
    //verifica se a tarefa precisa liberar um recurso no ciclo de clock atual
    public ArrayList<String> precisaLiberar(){
        ArrayList<String> ids = new ArrayList<>();
        for(RecursoTarefa rt : recursos){
            if(contadorDeCiclos == (rt.getTempoDeInicio() + rt.getDuracao()))
                ids.add(rt.getId());
        }
        return ids;
    }
    
    @Override
    public String toString(){
        String s = "ID: " + this.id + "\n";
        s = s + "Chegada: " + this.tempoChegada + "\n";
        s = s + "Peso: " + this.duracao + "\n";
        s = s + "Deadline: " + this.deadline + "\n";
        s = s + "- Recursos:\n";
        for(int i = 0; i < this.recursos.size(); i++) {
            s = s + recursos.get(i).toString() + "\n|\n";
        }
        return s;
    }
}
