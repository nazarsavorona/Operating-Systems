package lab1;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void registerCancellation(){
        System.out.println("Press 'Q' at any time to cancel computation.");
        KeyListener listener;

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        try {
            GlobalScreen.registerNativeHook();
            listener = new KeyListener();
            GlobalScreen.addNativeKeyListener(listener);
        } catch (NativeHookException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        registerCancellation();

        if (args.length != 1) {
            return;
        }

        try {
            new Manager().evaluate(Integer.parseInt(args[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
