package so;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.ui.ApplicationFrame;

public class Visual extends ApplicationFrame {

    /**
     * Cria um gráfico de simulação de exeução do escalonador
     * @param title
     * @param taskNames
     * @param resourceNames
     * @param process
     * @param resources 
     */
    public Visual(String title, ArrayList<String> taskNames, ArrayList<String> resourceNames,
                  ArrayList<Integer> process, ArrayList<Integer> resources) {
        super("Escalonador Simulação");
        JPanel chartPanel = new ChartPanel(createChart(createDataset(process,resources,resourceNames),title,taskNames));
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 300));
        setContentPane(chartPanel);
    }

    private JFreeChart createChart(IntervalXYDataset dataset, String graphName, ArrayList<String> TaskNames) {
        JFreeChart chart = ChartFactory.createXYBarChart(graphName,
                                 "Tempo", true, "Tempo", dataset, PlotOrientation.HORIZONTAL,
                                  true, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRangePannable(true);
        String[] names = TaskNames.toArray(new String[0]);
        SymbolAxis xAxis = new SymbolAxis("Tarefa", names);
        xAxis.setGridBandsVisible(false);
        plot.setDomainAxis(xAxis);
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();   
        renderer.setUseYInterval(true);
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        ChartUtilities.applyCurrentTheme(chart);

        return chart;
    }

    private IntervalXYDataset createDataset(ArrayList<Integer> tarefas, ArrayList<Integer> recursos,
                                            ArrayList<String> nomeRecursos) {
        SimpleTimePeriod t;
        XYIntervalSeriesCollection dataset = new XYIntervalSeriesCollection();
        
        ArrayList<XYIntervalSeries> s = new ArrayList<XYIntervalSeries>();
        for(int i = 0; i < nomeRecursos.size()+1; i++) {
            if(i < nomeRecursos.size())
                s.add(new XYIntervalSeries(nomeRecursos.get(i)));
            else
                s.add(new XYIntervalSeries("Nenhum Recurso"));
        }
        //XYIntervalSeries s1 = new XYIntervalSeries("Um recurso");
        //XYIntervalSeries s2 = new XYIntervalSeries("Dois recursos");
        //XYIntervalSeries s3 = new XYIntervalSeries("Nenhum Recurso");
        
        for (int i = 0; i < recursos.size(); i++) {
            t = new SimpleTimePeriod(i, i+1);
            
            int rec = recursos.get(i);
            int tar = tarefas.get(i);
            if(rec != -1)
                concatena(s.get(rec),t,tar);
            else
                concatena(s.get(nomeRecursos.size()), t, tar);
        }
        
        for(XYIntervalSeries interval : s)
            dataset.addSeries(interval);
        return dataset;
    }

    private void concatena(XYIntervalSeries s, SimpleTimePeriod p0, int index) {
        s.add(index, index + 0.5,index , p0.getStartMillis(),p0.getStartMillis(),p0.getEndMillis());
    }
    
    /*
    public static void main(String[] args) {
        ArrayList<String> processNames = new ArrayList<>();
        processNames.add("Ta1");
        processNames.add("Ta2");
        processNames.add("Ta3");
        ArrayList<String> resourceNames = new ArrayList<>();
        resourceNames.add("Preto");
        resourceNames.add("Cinza");
        resourceNames.add("Mais Cinza");
        
        ArrayList<Integer> tarefas = new ArrayList<Integer> ();
        ArrayList<Integer> recursos = new ArrayList<Integer> ();
        
        tarefas.add(0);
        tarefas.add(2);
        tarefas.add(1);
        tarefas.add(2);
        tarefas.add(0);
        recursos.add(-1);
        recursos.add(1);
        recursos.add(-1);
        recursos.add(1);
        recursos.add(2);
        
        Visual demo = new Visual("Projeto",processNames,resourceNames,tarefas,recursos);
        
        demo.pack();
        demo.setVisible(true);
    }
    */
}