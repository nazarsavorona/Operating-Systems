import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Manager {
    private final SumOperation sum = new SumOperation();
    private final KeyListener keyListener = new KeyListener();
    private Timer promptTimer;

    private Process processF;
    private Process processG;

    private Integer f = null;
    private Integer g = null;

    private boolean inPrompt;

    private void startPrompt(int delay) {
        promptTimer = new Timer();
        promptTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                prompt();
            }
        }, delay);
    }

    private void prompt() {
        promptTimer.cancel();
        if (f != null && g != null) {
            inPrompt = false;
            cancelCase();
        }
        inPrompt = true;
        System.out.println("Prompt:\n1 - continue computation\n2 - continue computation without prompt\n3 - cancel computation");

        Scanner in = new Scanner(System.in);
        int i = in.nextInt();

        if (i == 1) {
            startPrompt(4000);
        } else if (i == 3) {
            cancelCase();
        }

        inPrompt = false;
    }

    public void evaluate(int arg) throws IOException {
        Runtime.getRuntime().addShutdownHook(new Thread(this::cancelCase));
        startPrompt(4000);

        try {
            System.out.println("Computing");
            int result = this.compute(arg);
            System.out.println("Result: " + result);

        } catch (TimeoutException e) {
            System.out.println("Computing timeout");

        } catch (IllegalArgumentException e) {
            System.out.println("Exception: " + e.getMessage().split("IllegalArgumentException: ")[1].replaceAll("\"", ""));

        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        cancelCase();
    }

    private int compute(int arg) throws IOException, InterruptedException, TimeoutException {
        List<String> args = List.of(String.valueOf(arg));

        ProcessBuilder processBuilderF = makeProcess("computators.ComputatorF", new ArrayList<>(), args);
        processF = processBuilderF.start();
        ProcessBuilder processBuilderG = makeProcess("computators.ComputatorG", new ArrayList<>(), args);
        processG = processBuilderG.start();

        if (!processF.waitFor(15, TimeUnit.SECONDS) || !processG.waitFor(15, TimeUnit.SECONDS)) {
            while (inPrompt)
                Thread.sleep(1000);
            throw new TimeoutException();
        }

        try {
            f = Integer.parseInt(Files.readAllLines(Paths.get("computators.ComputatorF" + "output.log")).get(0));
            keyListener.setValF(f);
            g = Integer.parseInt(Files.readAllLines(Paths.get("computators.ComputatorG" + "output.log")).get(0));
            keyListener.setValG(g);
        } catch (NumberFormatException e) {
            while (inPrompt)
                Thread.sleep(1000);
            throw new IllegalArgumentException(e.getMessage().split("For input string: ")[1]);
        }

        keyListener.setResult(sum.apply(f, g));
        while (inPrompt)
            Thread.sleep(1000);
        return keyListener.getResult();
    }

    private void cancelCase() {
        processF.destroyForcibly();
        processG.destroyForcibly();
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        System.out.println("Shutting down");
        Runtime.getRuntime().halt(0);
    }

    private static ProcessBuilder makeProcess(String className, List<String> jvmArgs, List<String> args) {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");

        List<String> command = new ArrayList<>();
        command.add(javaBin);
        command.addAll(jvmArgs);
        command.add("-cp");
        command.add(classpath);
        command.add(className);
        command.addAll(args);
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        File log = new File(className + ".output.log");
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(log);

        return processBuilder;
    }
}
