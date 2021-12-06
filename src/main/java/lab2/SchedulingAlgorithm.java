package lab2;

import java.util.*;
import java.io.*;
import java.util.stream.IntStream;

public class SchedulingAlgorithm {
    public static Results run(int runtime, Vector processVector, List queueList, Results result) {
        int comptime = 0;
        int currentProcess = 0;

        int size = processVector.size();
        int completed = 0;

        String resultsFile = "Summary-Processes";

        result.schedulingType = "Interactive";
        result.schedulingName = "Multiple Queues";

        boolean allProcessesDone = false;
        try {
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));

            int currentQueueLevel = 0;
            while (comptime < runtime && !allProcessesDone) {
                List list = (List<?>) queueList.get(currentQueueLevel);

                for (int j = 0; j < list.size(); j++) {
                    currentProcess = (Integer) list.get(j);

                    Process process = (Process) processVector.elementAt(currentProcess);
                    logProcessState(currentProcess, out, process, "registered");

                    while (comptime < runtime) {
                        if (process.cpudone >= process.cputime) {
                            completed++;
                            list.remove((Object) currentProcess);
                            j--;
                            logProcessState(currentProcess, out, process, "completed");

                            if (completed == size) {
                                result.compuTime = comptime;
                                out.close();
                                return result;
                            } else {
                                break;
                            }
                        }

                        if (process.ioblocking == process.ionext) {
                            logProcessState(currentProcess, out, process, "I/O blocked");
                            process.numblocked++;
                            process.ionext = 0;
                            process.ioblocking *= 2;

                            if (currentQueueLevel != queueList.size() - 1) {
                                ((List) queueList.get(currentQueueLevel + 1)).add(currentProcess);
                                list.remove((Object) currentProcess);
                                j--;
                                if (j < 0) {
                                    j = 0;
                                }
                            }

                            break;
                        }

                        process = (Process) processVector.elementAt(currentProcess);
                        process.cpudone++;

                        if (process.ioblocking > 0) {
                            process.ionext++;
                        }
                        comptime++;
                    }
                }

                if (comptime >= runtime) {
                    break;
                }
                currentQueueLevel = (currentQueueLevel + 1) % queueList.size();
            }

            out.close();
        } catch (IOException e) { /* Handle exceptions */ }

        result.compuTime = comptime;

        return result;
    }

    private static void logProcessState(int currentProcess, PrintStream out, Process process, String state) {
        out.println("Process: " + currentProcess + " " + state + "... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.ionext + ")");
    }
}
