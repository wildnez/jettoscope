package org.hazelcast.jettoscope;

import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.*;
import com.hazelcast.jet.config.JetConfig;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.hazelcast.jet.Processors.streamFiles;

/**
 * Created by rahul on 04/05/17.
 */
public class Jettoscope {

    private JetInstance jet;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new Jettoscope().start();
    }

    private void start() throws ExecutionException, InterruptedException {
        setup();

        Job job = jet.newJob(buildDag());
        job.execute();
        while(true) {
           TimeUnit.SECONDS.sleep(5);
           printResults();
        }
    }

    private void printResults() {
        final IMap<String, ProcessedData> processedData = jet.getMap("test");
        processedData.entrySet().stream().forEach(e -> {
            ProcessedData data = e.getValue();
            if(data.getMemoryPercentage() < 15) {
                System.out.println("SERIOUS MEMORY ALERT.... CRASHING SOON..!!!");
                return;
            }
            if(data.getMemoryPercentage() < 20) {
                System.out.println("YOU IGNORED ME.. NOW YOUR JVM IS GONNA BLOW.. " +
                        "Memory available is " + data.getMemoryPercentage()+"%");
                return;
            }
            if(data.getMemoryPercentage() < 30) {
                System.out.println("Serious Alert. Memory available " + data.getMemoryPercentage()+" %");
                return;
            }
            if(data.getMemoryPercentage() < 40) {
                System.out.println("Memory Alert: memory available " + data.getMemoryPercentage()+" %. GC pauses likely.");
                return;
            }
            if(data.getMemoryPercentage() < 50) {
                System.out.println("Memory Alert: memory available " + data.getMemoryPercentage()+" %");
                return;
            }
        });

    }


    private DAG buildDag() {
        DAG dag = new DAG();

        Vertex file_stream = dag.newVertex("file-stream", streamFiles("/Users/rahul/jettoscope-data/1")).localParallelism(1);

        Vertex transform = dag.newVertex("transform", () -> new JettoscopeLinesP()).localParallelism(1);

        Vertex print = dag.newVertex("print", Processors.writeMap("test")).localParallelism(1);

        dag.edge(Edge.between(file_stream, transform));
        dag.edge(Edge.between(transform, print));

        return dag;
    }

    private void setup() {

        JetConfig config = new JetConfig();
        NetworkConfig networkConfig = config.getHazelcastConfig().getNetworkConfig();
        networkConfig.getJoin().getMulticastConfig().setEnabled(false);
        networkConfig.getJoin().getTcpIpConfig().setEnabled(true);
        networkConfig.getJoin().getTcpIpConfig().addMember("127.0.0.1:5705");
        networkConfig.setPort(5705);
        jet = Jet.newJetInstance(config);
    }
}
