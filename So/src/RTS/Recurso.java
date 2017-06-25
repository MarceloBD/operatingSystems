package RTS;

/*
classe que representa os recursos do sistema
*/
public class Recurso {
    public final static int BLOQUEADO = 0;//contantes de estado do recurso
    public final static int LIBERADO = 1;
    
    private String id;//identificador
    private int estado;//estado do recurso
    private int prioridade;//prioridade do recurso
    private Tarefa tarefa;//tarefa que alocou o recurso

    public Recurso(String id) {
        this.id = id;
        this.estado = Recurso.LIBERADO;
        this.prioridade = 1000;
        this.tarefa = null;
    }   
    
    public int bloquearRecurso(Tarefa tarefa){
        if(!estaBloqueado()){
            this.estado = Recurso.BLOQUEADO;
            this.tarefa = tarefa;
            return 0;
        }
        
        return 1;
    }
    
    public int liberarRecurso(){
        if(estaBloqueado()){
            this.estado = Recurso.LIBERADO;
            this.tarefa = null;
            return 0;
        }
        
        return 1;
    }
    
    public boolean estaBloqueado(){
        if(this.estado == Recurso.LIBERADO)
            return false;
        
        return true;
    }

    public String getId() {
        return id;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public Tarefa getTarefa() {
        return tarefa;
    }
    
    
}
