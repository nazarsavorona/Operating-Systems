package lab3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class ControlPanel extends Frame {
    Kernel kernel;
    Button runButton = new Button("run");
    Button stepButton = new Button("step");
    Button resetButton = new Button("reset");
    Button exitButton = new Button("exit");
    int buttonCount = 64;
    List<Button> buttons = new ArrayList<>();
    List<Label> labels = new ArrayList<>();

    Label statusValueLabel = new Label("STOP", Label.LEFT);
    Label timeValueLabel = new Label("0", Label.LEFT);
    Label instructionValueLabel = new Label("NONE", Label.LEFT);
    Label addressValueLabel = new Label("NULL", Label.LEFT);
    Label pageFaultValueLabel = new Label("NO", Label.LEFT);
    Label virtualPageValueLabel = new Label("x", Label.LEFT);
    Label physicalPageValueLabel = new Label("0", Label.LEFT);
    Label RValueLabel = new Label("0", Label.LEFT);
    Label MValueLabel = new Label("0", Label.LEFT);
    Label inMemTimeValueLabel = new Label("0", Label.LEFT);
    Label lastTouchTimeValueLabel = new Label("0", Label.LEFT);
    Label lowValueLabel = new Label("0", Label.LEFT);
    Label highValueLabel = new Label("0", Label.LEFT);

    public ControlPanel() {
        super();
        for (int i = 0; i < buttonCount; i++) {
            buttons.add(new Button("page " + (i)));
            labels.add(new Label(null, Label.CENTER));
        }
    }

    public ControlPanel(String title) {
        super(title);
        for (int i = 0; i < buttonCount; i++) {
            buttons.add(new Button("page " + (i)));
            labels.add(new Label(null, Label.CENTER));
        }
    }

    public void init(Kernel useKernel, String commands, String config) {
        kernel = useKernel;
        kernel.setControlPanel(this);
        setLayout(null);
        setBackground(Color.white);
        setForeground(Color.black);
        resize(635, 545);
        setFont(new Font("Courier", 0, 12));

        runButton.setForeground(Color.blue);
        runButton.setBackground(Color.lightGray);
        runButton.reshape(0, 25, 70, 15);
        add(runButton);

        stepButton.setForeground(Color.blue);
        stepButton.setBackground(Color.lightGray);
        stepButton.reshape(70, 25, 70, 15);
        add(stepButton);

        resetButton.setForeground(Color.blue);
        resetButton.setBackground(Color.lightGray);
        resetButton.reshape(140, 25, 70, 15);
        add(resetButton);

        exitButton.setForeground(Color.blue);
        exitButton.setBackground(Color.lightGray);
        exitButton.reshape(210, 25, 70, 15);
        add(exitButton);

        for (int i = 0; i < buttonCount; i++) {
            Button button = buttons.get(i);
            button.reshape(140 * (i / (buttonCount / 2)), (i % (buttonCount / 2) + 2) * 15 + 25, 70, 15);
            button.setForeground(Color.magenta);
            button.setBackground(Color.lightGray);
            add(button);
        }

        statusValueLabel.reshape(345, 0 + 25, 100, 15);
        add(statusValueLabel);

        timeValueLabel.reshape(345, 15 + 25, 100, 15);
        add(timeValueLabel);

        instructionValueLabel.reshape(385, 45 + 25, 100, 15);
        add(instructionValueLabel);

        addressValueLabel.reshape(385, 60 + 25, 230, 15);
        add(addressValueLabel);

        pageFaultValueLabel.reshape(385, 90 + 25, 100, 15);
        add(pageFaultValueLabel);

        virtualPageValueLabel.reshape(395, 120 + 25, 200, 15);
        add(virtualPageValueLabel);

        physicalPageValueLabel.reshape(395, 135 + 25, 200, 15);
        add(physicalPageValueLabel);

        RValueLabel.reshape(395, 150 + 25, 200, 15);
        add(RValueLabel);

        MValueLabel.reshape(395, 165 + 25, 200, 15);
        add(MValueLabel);

        inMemTimeValueLabel.reshape(395, 180 + 25, 200, 15);
        add(inMemTimeValueLabel);

        lastTouchTimeValueLabel.reshape(395, 195 + 25, 200, 15);
        add(lastTouchTimeValueLabel);

        lowValueLabel.reshape(395, 210 + 25, 230, 15);
        add(lowValueLabel);

        highValueLabel.reshape(395, 225 + 25, 230, 15);
        add(highValueLabel);

        Label virtualOneLabel = new Label("virtual", Label.CENTER);
        virtualOneLabel.reshape(0, 15 + 25, 70, 15);
        add(virtualOneLabel);

        Label virtualTwoLabel = new Label("virtual", Label.CENTER);
        virtualTwoLabel.reshape(140, 15 + 25, 70, 15);
        add(virtualTwoLabel);

        Label physicalOneLabel = new Label("physical", Label.CENTER);
        physicalOneLabel.reshape(70, 15 + 25, 70, 15);
        add(physicalOneLabel);

        Label physicalTwoLabel = new Label("physical", Label.CENTER);
        physicalTwoLabel.reshape(210, 15 + 25, 70, 15);
        add(physicalTwoLabel);

        Label statusLabel = new Label("status: ", Label.LEFT);
        statusLabel.reshape(285, 0 + 25, 65, 15);
        add(statusLabel);

        Label timeLabel = new Label("time: ", Label.LEFT);
        timeLabel.reshape(285, 15 + 25, 50, 15);
        add(timeLabel);

        Label instructionLabel = new Label("instruction: ", Label.LEFT);
        instructionLabel.reshape(285, 45 + 25, 100, 15);
        add(instructionLabel);

        Label addressLabel = new Label("address: ", Label.LEFT);
        addressLabel.reshape(285, 60 + 25, 85, 15);
        add(addressLabel);

        Label pageFaultLabel = new Label("page fault: ", Label.LEFT);
        pageFaultLabel.reshape(285, 90 + 25, 100, 15);
        add(pageFaultLabel);

        Label virtualPageLabel = new Label("virtual page: ", Label.LEFT);
        virtualPageLabel.reshape(285, 120 + 25, 110, 15);
        add(virtualPageLabel);

        Label physicalPageLabel = new Label("physical page: ", Label.LEFT);
        physicalPageLabel.reshape(285, 135 + 25, 110, 15);
        add(physicalPageLabel);

        Label RLabel = new Label("R: ", Label.LEFT);
        RLabel.reshape(285, 150 + 25, 110, 15);
        add(RLabel);

        Label MLabel = new Label("M: ", Label.LEFT);
        MLabel.reshape(285, 165 + 25, 110, 15);
        add(MLabel);

        Label inMemTimeLabel = new Label("inMemTime: ", Label.LEFT);
        inMemTimeLabel.reshape(285, 180 + 25, 110, 15);
        add(inMemTimeLabel);

        Label lastTouchTimeLabel = new Label("lastTouchTime: ", Label.LEFT);
        lastTouchTimeLabel.reshape(285, 195 + 25, 110, 15);
        add(lastTouchTimeLabel);

        Label lowLabel = new Label("low: ", Label.LEFT);
        lowLabel.reshape(285, 210 + 25, 110, 15);
        add(lowLabel);

        Label highLabel = new Label("high: ", Label.LEFT);
        highLabel.reshape(285, 225 + 25, 110, 15);
        add(highLabel);

        for (int i = 0; i < buttonCount; i++) {
            Label label = labels.get(i);

            label.reshape(((i) / (buttonCount / 2)) * 140 + 70,
                    ((i) % (buttonCount / 2) + 2) * 15 + 25, 60, 15);
            label.setForeground(Color.red);
            label.setFont(new Font("Courier", 0, 10));
            add(label);
        }

        kernel.init(commands, config);

        show();
    }

    public void paintPage(Page page) {
        virtualPageValueLabel.setText(Integer.toString(page.id));
        physicalPageValueLabel.setText(Integer.toString(page.physical));
        RValueLabel.setText(Integer.toString(page.R));
        MValueLabel.setText(Integer.toString(page.M));
        inMemTimeValueLabel.setText(Integer.toString(page.inMemTime));
        lastTouchTimeValueLabel.setText(Integer.toString(page.lastTouchTime));
        lowValueLabel.setText(Long.toString(page.low, Kernel.addressradix));
        highValueLabel.setText(Long.toString(page.high, Kernel.addressradix));
    }

    public void setStatus(String status) {
        statusValueLabel.setText(status);
    }

    public void addPhysicalPage(int pageNum, int physical) {
        labels.get(pageNum).setText("page " + physical);
    }

    public void removePhysicalPage(int physicalPage) {
        labels.get(physicalPage).setText(null);
    }

    public boolean action(Event e, Object arg) {
        if (e.target == runButton) {
            setStatus("RUN");
            runButton.disable();
            stepButton.disable();
            resetButton.disable();
            kernel.run();
            setStatus("STOP");
            resetButton.enable();
            return true;
        } else if (e.target == stepButton) {
            setStatus("STEP");
            kernel.step();
            if (kernel.runcycles == kernel.runs) {
                stepButton.disable();
                runButton.disable();
            }
            setStatus("STOP");
            return true;
        } else if (e.target == resetButton) {
            kernel.reset();
            runButton.enable();
            stepButton.enable();
            return true;
        } else if (e.target == exitButton) {
            System.exit(0);
            return true;
        } else {
            for (int i = 0; i < buttonCount; i++) {
                if (e.target == buttons.get(i)) {
                    kernel.getPage(i);
                    return true;
                }
            }
            return false;
        }
    }
}
