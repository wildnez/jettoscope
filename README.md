# Hazelcast Jettoscope

(work in progress)

This application latches on to Hazelcast Diagnostic logs and processes streaming data to analyse information provided in diagnostics logs e.g. heap memory stats, cpu stats, pending Hazelcast invocations, slow operations etc.

You need to provide qualified path of the directory at https://github.com/wildnez/jettoscope/blob/master/src/main/java/org/hazelcast/jettoscope/Jettoscope.java#L65  where servers print its diagnostic logs. The Jet cluster when started, listens to any changes in log files and begins streaming data to Jet cluster.

The application currently only prints the analysis. Future version may store analytics result in a Hazelcast Map and further analyse the data over a sliding window to produce accurate predictions based on the performance of the running cluster.
