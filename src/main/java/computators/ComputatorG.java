package computators;

import os.lab1.compfuncs.basic.IntOps;

import java.util.Optional;

public class ComputatorG {
    private static Integer compute(int x) throws InterruptedException {
        Optional<Integer> res = IntOps.trialG(x);
        if (res.isPresent())
            return res.get();

        throw new IllegalArgumentException("Could not compute g(x)");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Only one argument is required");
            return;
        }

        try {
            System.out.println(compute(Integer.parseInt(args[0])));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
