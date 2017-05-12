package org.hazelcast.jettoscope;

import com.hazelcast.jet.AbstractProcessor;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rahul on 04/05/17.
 */
public class JettoscopeLinesP extends AbstractProcessor {

    private List<String> lines;
    private Map<String, ProcessedData> results;
    private String watermark;
    private BigDecimal freeMemory;
    private BigDecimal maxMemory;
    private float processCpuLoad;
    private long pendingOps;

    JettoscopeLinesP() {
        super();
        lines = new ArrayList<>();
        results = new HashMap<>();
    }

    protected boolean tryProcess0(@Nonnull Object item) throws Exception {
        String line = String.valueOf(item);
        lines.add(line);

        if(line.contains("thisAddress=")) {
            watermark = line.substring(line.indexOf("thisAddress=")+12);
            processLines(lines);

            results.put(watermark, buildProcessedData());

            emit(results.entrySet().iterator().next());
            lines.clear();
            results.clear();
            pendingOps = 0;
            processCpuLoad = 0F;
            freeMemory = maxMemory = null;
        }

        return true;
    }

    private ProcessedData buildProcessedData() {
        ProcessedData processedData = new ProcessedData();
        processedData.setMemoryPercentage(Math.round((freeMemory.doubleValue()/maxMemory.doubleValue())*100));
        processedData.setCpuUsage(processCpuLoad);
        processedData.setId(watermark);
        processedData.setPendingInvocations(pendingOps);
        return processedData;
    }

    private void processLines(List<String> lines) {
        lines.stream().forEach(s -> {
            if(s.contains("runtime.freeMemory")) {
                freeMemory = new BigDecimal(JettoscopeUtil.parseLong(s.substring(s.indexOf("runtime.freeMemory=")+19)));
            }
            if(s.contains("runtime.maxMemory")) {
                maxMemory = new BigDecimal(JettoscopeUtil.parseLong(s.substring(s.indexOf("runtime.maxMemory=")+18)));
            }
            if(s.contains("os.processCpuLoad")) {
                processCpuLoad = Float.valueOf(s.substring(s.indexOf("os.processCpuLoad=")+18));
            }
            if(s.contains("operation.invocations.pending")) {
                pendingOps = JettoscopeUtil.parseLong(s.substring(s.indexOf("operation.invocations.pending=")+"operation.invocations.pending=".length()));
            }

        });
    }
}

class ProcessedData implements Serializable {
    private String id;
    private double memoryPercentage;
    private float cpuUsage;
    private long pendingInvocations;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getMemoryPercentage() {
        return memoryPercentage;
    }

    public void setMemoryPercentage(double memoryPercentage) {
        this.memoryPercentage = memoryPercentage;
    }

    public float getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(float cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public long getPendingInvocations() {
        return pendingInvocations;
    }

    public void setPendingInvocations(long pendingInvocations) {
        this.pendingInvocations = pendingInvocations;
    }
}
