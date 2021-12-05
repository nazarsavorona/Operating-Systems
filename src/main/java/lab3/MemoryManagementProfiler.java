package lab3;


public class MemoryManagementProfiler {
    public static void main(String[] args) {

        if (args.length < 1 || args.length > 3) {
            System.out.println("Usage: 'java MemoryManagementProfiler <COMMAND FILE> <FIFO PRA PROPERTIES FILE> " +
                    "<WSClock PRA PROPERTIES FILE>");
            System.exit(-1);
        }

        new Thread(() -> {
            MemoryManagement management = new MemoryManagement();
            management.run(new String[]{args[0], args[1], "false"});
            management.runKernel();
        }).start();

        new Thread(() -> {
            MemoryManagement management = new MemoryManagement();
            management.run(new String[]{args[0], args[2], "true"});
            management.runKernel();
        }).start();
    }
}

