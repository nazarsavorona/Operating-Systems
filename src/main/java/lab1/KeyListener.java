package lab1;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class KeyListener implements NativeKeyListener {
    private Integer valF = null;
    private Integer valG = null;

    public Integer getResult() {
        return result;
    }

    private Integer result = null;

    public void setValF(Integer valF) {
        this.valF = valF;
    }

    public void setValG(Integer valG) {
        this.valG = valG;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_Q) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e1) {
                e1.printStackTrace();
            }
            System.out.println("Computation was cancelled");
            if (valF == null)
                System.out.println("Unable to compute f(x)");
            if (valG == null)
                System.out.println("Unable to compute g(x)");

            if (result != null)
                System.out.println("But result was computed: " + result);
            System.exit(0);
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
    }

}