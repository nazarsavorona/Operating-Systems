package lab2;

import java.util.*;
import java.io.*;
import java.util.stream.IntStream;

public class SchedulingAlgorithm {
    public static Results run(int runtime, Vector processVector, Results result) {
        int comptime = 0;
        int currentProcess = 0;

        int size = processVector.size();
        int completed = 0;

        String resultsFile = "Summary-Processes";

        result.schedulingType = "Interactive";
        result.schedulingName = "Multiple Queues";

        List queueList = new ArrayList<ArrayList<Integer>>();
        queueList.add(new ArrayList<Integer>());

        List currentList = (List) queueList.get(0);
        IntStream.range(0, size).forEach(currentList::add);

        try {
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));

            for (int k = 0; k < queueList.size(); k++) {
                List list = (List<?>) queueList.get(k);

                for (int j = 0; j < list.size(); j++) {
                    currentProcess = (Integer) list.get(j);

                    Process process = (Process) processVector.elementAt(currentProcess);
                    logProcessState(currentProcess, out, process, "registered");

                    while (comptime < runtime) {
                        if (process.cpudone == process.cputime) {
                            completed++;
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

                            if (queueList.size() < k + 2) {
                                queueList.add(new ArrayList<Integer>());
                            }

                            List nextList = (List) queueList.get(k + 1);
                            nextList.add(currentProcess);
                            list.remove((Object) currentProcess);
                            j--;

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
