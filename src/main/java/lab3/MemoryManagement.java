// The main MemoryManagement program, created by Alexander Reeder, 2000 Nov 19
package lab3;

import java.io.File;

public class MemoryManagement {
    private Kernel kernel;

    public static void main(String[] args) {
        MemoryManagement management = new MemoryManagement();
        management.run(args);
    }

    public void run(String[] args) {
        boolean isModifiedAlgorithm = true;

        if (args.length < 1 || args.length > 3) {
            System.out.println("Usage: 'java MemoryManagement <COMMAND FILE> <PROPERTIES FILE> [IS MODIFIED " +
                    "ALGORITHM USED]'");
            System.exit(-1);
        }

        if (args.length > 2) {
            isModifiedAlgorithm = Boolean.parseBoolean(args[2]);
        }

        File f = new File(args[0]);

        if (!(f.exists())) {
            System.out.println("MemoryM: error, file '" + f.getName() + "' does not exist.");
            System.exit(-1);
        }
        if (!(f.canRead())) {
            System.out.println("MemoryM: error, read of " + f.getName() + " failed.");
            System.exit(-1);
        }

        if (args.length == 2) {
            f = new File(args[1]);

            if (!(f.exists())) {
                System.out.println("MemoryM: error, file '" + f.getName() + "' does not exist.");
                System.exit(-1);
            }
            if (!(f.canRead())) {
                System.out.println("MemoryM: error, read of " + f.getName() + " failed.");
                System.exit(-1);
            }
        }

        kernel = new Kernel(isModifiedAlgorithm);
        ControlPanel controlPanel = new ControlPanel("Memory Management");
        if (args.length == 1) {
            controlPanel.init(kernel, args[0], null);
        } else {
            controlPanel.init(kernel, args[0], args[1]);
        }
    }

    public void runKernel() {
        kernel.run();
    }
}
