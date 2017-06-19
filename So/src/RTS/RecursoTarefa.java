package RTS;

/*
modelo de recurso utilizado nas tarefas
*/
public class RecursoTarefa {
    private String id;//identificador
    private int tempoDeInicio;//tempo de inicio do uso do recurso dentro do clock da tarefa
    private int duracao;//quanto tempo o recurso é utilizado antes de ser liberado

    public RecursoTarefa(String id, int tempoDeInicio, int duracao) {
        this.id = id;
        this.tempoDeInicio = tempoDeInicio;
        this.duracao = duracao;
    }

    public String getId() {
        return id;
    }

    public int getTempoDeInicio() {
        return tempoDeInicio;
    }

    public int getDuracao() {
        return duracao;
    }
}
