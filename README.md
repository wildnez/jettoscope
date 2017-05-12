# Hazelcast Jettoscope

(work in progress)

This application latches on to Hazelcast Diagnostic logs and processes streaming data to analyse information provided in diagnostics logs e.g. heap memory stats, cpu stats, pending Hazelcast invocations, slow operations etc.

You need to provide qualified path of the directory where servers print its diagnostic logs and the file processor begins streaming incoming logs to Jet cluster.

