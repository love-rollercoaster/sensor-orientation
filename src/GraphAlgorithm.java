import org.jgraph.JGraph;

public interface GraphAlgorithm<ResultType> {
    ResultType execute(JGraph graph);
}
