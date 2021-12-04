package lab3;


public class MemoryManagementProfiler {
    public static void main(String[] args) {
        new Thread(() -> {
            MemoryManagement management = new MemoryManagement();
            management.run(new String[]{"src/main/java/lab3/commands", "src/main/java/lab3/FIFO.conf", "false"});
            management.runKernel();
        }).start();

        new Thread(() -> {
            MemoryManagement management = new MemoryManagement();
            management.run(new String[]{"src/main/java/lab3/commands", "src/main/java/lab3/WSClock.conf", "true"});
            management.runKernel();
        }).start();
    }
}

