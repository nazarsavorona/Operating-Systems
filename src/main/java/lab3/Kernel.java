package lab3;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Kernel extends Thread {
    // The number of virtual pages must be fixed at 63 due to
    // dependencies in the GUI
    private static int virtPageNum = 63;

    private String output = null;
    private static final String lineSeparator =
            System.getProperty("line.separator");
    private String command_file;
    private String config_file;
    private ControlPanel controlPanel;
    private Vector memVector = new Vector();
    private Vector instructVector = new Vector();
    private String status;
    private int tick = 1;
    private int ioLimit = 0;
    private int tau = 0;
    private boolean doStdoutLog = false;
    private boolean doFileLog = false;
    private boolean expandPhysicalMemory = false;
    private boolean isModifiedAlgorithm = true;
    public int timeout = 500;
    public int runs;
    public int runcycles;
    public long block = (int) Math.pow(2, 12);
    public static byte addressradix = 10;
    private Set<Integer> physicalMapped = new HashSet<>();
    private List<Page> workingSet = new ArrayList<>();
    private int pageFaultCount = 0;
    private PageFault pageFaultHandler = new PageFault();

    public Kernel(boolean isModifiedAlgorithm) {
        super();
        this.isModifiedAlgorithm = isModifiedAlgorithm;
    }

    public void init(String commands, String config) {
        File f = new File(commands);
        command_file = commands;
        config_file = config;
        String line;
        String tmp = null;
        String command = "";
        byte R = 0;
        byte M = 0;
        int i = 0;
        int j = 0;
        int id = 0;
        int physical = 0;
        int physical_count = 0;
        int inMemTime = 0;
        int lastTouchTime = 0;
        int map_count = 0;
        double power = 14;
        long high = 0;
        long low = 0;
        long addr = 0;
        long address_limit = block * virtPageNum - 1;

        if (config != null) {
            f = new File(config);
            try {
                DataInputStream in = new DataInputStream(new FileInputStream(f));
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("numpages")) {
                        StringTokenizer st = new StringTokenizer(line);
                        while (st.hasMoreTokens()) {
                            tmp = st.nextToken();
                            virtPageNum = Common.s2i(st.nextToken());
                            if (virtPageNum < 2 || virtPageNum > 64) {
                                System.out.println("MemoryManagement: numpages out of bounds.");
                                System.exit(-1);
                            }
                            address_limit = block * virtPageNum - 1;
                        }
                    }

                    if (line.startsWith("iolimit")) {
                        StringTokenizer st = new StringTokenizer(line);
                        while (st.hasMoreTokens()) {
                            tmp = st.nextToken();
                            ioLimit = Common.s2i(st.nextToken());
                        }
                    }

                    if (line.startsWith("timeout")) {
                        StringTokenizer st = new StringTokenizer(line);
                        while (st.hasMoreTokens()) {
                            tmp = st.nextToken();
                            timeout = Common.s2i(st.nextToken());
                        }
                    }

                    if (line.startsWith("tau")) {
                        StringTokenizer st = new StringTokenizer(line);
                        while (st.hasMoreTokens()) {
                            tmp = st.nextToken();
                            tau = Common.s2i(st.nextToken());
                        }
                    }

                    if (line.startsWith("tick")) {
                        StringTokenizer st = new StringTokenizer(line);
                        while (st.hasMoreTokens()) {
                            tmp = st.nextToken();
                            tick = Common.s2i(st.nextToken());
                        }
                    }
                }
                in.close();
            } catch (IOException e) { /* Handle exceptions */ }
            for (i = 0; i < virtPageNum; i++) {
                high = (block * (i + 1)) - 1;
                low = block * i;
                memVector.addElement(new Page(i, -1, R, M, 0, 0, high, low));
            }
            try {
                DataInputStream in = new DataInputStream(new FileInputStream(f));
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("memset")) {
                        StringTokenizer st = new StringTokenizer(line);
                        st.nextToken();
                        while (st.hasMoreTokens()) {
                            id = Common.s2i(st.nextToken());
                            tmp = st.nextToken();
                            if (tmp.startsWith("x")) {
                                physical = -1;
                            } else {
                                physical = Common.s2i(tmp);
                            }
                            if ((0 > id || id >= virtPageNum) || (-1 > physical || physical > (virtPageNum / 2))) {
                                System.out.println("MemoryManagement: Invalid page value in " + config);
                                System.exit(-1);
                            }
                            R = Common.s2b(st.nextToken());
                            if (R < 0 || R > 1) {
                                System.out.println("MemoryManagement: Invalid R value in " + config);
                                System.exit(-1);
                            }
                            M = Common.s2b(st.nextToken());
                            if (M < 0 || M > 1) {
                                System.out.println("MemoryManagement: Invalid M value in " + config);
                                System.exit(-1);
                            }
                            inMemTime = Common.s2i(st.nextToken());
                            if (inMemTime < 0) {
                                System.out.println("MemoryManagement: Invalid inMemTime in " + config);
                                System.exit(-1);
                            }
                            lastTouchTime = Common.s2i(st.nextToken());
                            if (lastTouchTime < 0) {
                                System.out.println("MemoryManagement: Invalid lastTouchTime in " + config);
                                System.exit(-1);
                            }
                            Page page = (Page) memVector.elementAt(id);
                            page.physical = physical;
                            page.R = R;
                            page.M = M;
                            page.inMemTime = inMemTime;
                            page.lastTouchTime = lastTouchTime;
                        }
                    }
                    if (line.startsWith("expand_physical_memory")) {
                        StringTokenizer st = new StringTokenizer(line);
                        while (st.hasMoreTokens()) {
                            if (st.nextToken().startsWith("true")) {
                                expandPhysicalMemory = true;
                            }
                        }
                    }
                    if (line.startsWith("enable_logging")) {
                        StringTokenizer st = new StringTokenizer(line);
                        while (st.hasMoreTokens()) {
                            if (st.nextToken().startsWith("true")) {
                                doStdoutLog = true;
                            }
                        }
                    }
                    if (line.startsWith("log_file")) {
                        StringTokenizer st = new StringTokenizer(line);
                        while (st.hasMoreTokens()) {
                            tmp = st.nextToken();
                        }
                        if (tmp.startsWith("log_file")) {
                            doFileLog = false;
                            output = "tracefile";
                        } else {
                            doFileLog = true;
                            doStdoutLog = false;
                            output = tmp;
                        }
                    }
                    if (line.startsWith("pagesize")) {
                        StringTokenizer st = new StringTokenizer(line);
                        while (st.hasMoreTokens()) {
                            tmp = st.nextToken();
                            tmp = st.nextToken();
                            if (tmp.startsWith("power")) {
                                power = (double) Integer.parseInt(st.nextToken());
                                block = (int) Math.pow(2, power);
                            } else {
                                block = Long.parseLong(tmp, 10);
                            }
                            address_limit = block * virtPageNum - 1;
                        }
                        if (block < 64 || block > Math.pow(2, 26)) {
                            System.out.println("MemoryManagement: pagesize is out of bounds");
                            System.exit(-1);
                        }
                        for (i = 0; i < virtPageNum; i++) {
                            Page page = (Page) memVector.elementAt(i);
                            page.high = (block * (i + 1)) - 1;
                            page.low = block * i;
                        }
                    }
                    if (line.startsWith("addressradix")) {
                        StringTokenizer st = new StringTokenizer(line);
                        while (st.hasMoreTokens()) {
                            tmp = st.nextToken();
                            tmp = st.nextToken();
                            addressradix = Byte.parseByte(tmp);
                            if (addressradix < 0 || addressradix > 20) {
                                System.out.println("MemoryManagement: addressradix out of bounds.");
                                System.exit(-1);
                            }
                        }
                    }
                }
                in.close();
            } catch (IOException e) { /* Handle exceptions */ }
        }
        f = new File(commands);
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(f));
            while ((line = in.readLine()) != null) {
                if (line.startsWith("READ") || line.startsWith("WRITE")) {
                    if (line.startsWith("READ")) {
                        command = "READ";
                    }
                    if (line.startsWith("WRITE")) {
                        command = "WRITE";
                    }
                    StringTokenizer st = new StringTokenizer(line);
                    tmp = st.nextToken();
                    tmp = st.nextToken();
                    if (tmp.startsWith("random")) {
                        instructVector.addElement(new Instruction(command, Common.randomLong(address_limit)));
                    } else {
                        if (tmp.startsWith("bin")) {
                            addr = Long.parseLong(st.nextToken(), 2);
                        } else if (tmp.startsWith("oct")) {
                            addr = Long.parseLong(st.nextToken(), 8);
                        } else if (tmp.startsWith("hex")) {
                            addr = Long.parseLong(st.nextToken(), 16);
                        } else {
                            addr = Long.parseLong(tmp);
                        }
                        if (0 > addr || addr > address_limit) {
                            System.out.println("MemoryManagement: " + addr + ", Address out of range in " + commands);
                            System.exit(-1);
                        }
                        instructVector.addElement(new Instruction(command, addr));
                    }
                }
            }
            in.close();
        } catch (IOException e) { /* Handle exceptions */ }
        runcycles = instructVector.size();
        if (runcycles < 1) {
            System.out.println("MemoryManagement: no instructions present for execution.");
            System.exit(-1);
        }
        if (doFileLog) {
            File trace = new File(output);
            trace.delete();
        }
        runs = 0;
        for (i = 0; i < virtPageNum; i++) {
            Page page = (Page) memVector.elementAt(i);
            if (page.physical != -1) {
                map_count++;
                physicalMapped.add(page.physical);
            }
            for (j = 0; j < virtPageNum; j++) {
                Page tmp_page = (Page) memVector.elementAt(j);
                if (tmp_page.physical == page.physical && page.physical >= 0) {
                    physical_count++;
                }
            }
            if (physical_count > 1) {
                System.out.println("MemoryManagement: Duplicate physical page's in " + config);
                System.exit(-1);
            }
            physical_count = 0;
        }
        if (expandPhysicalMemory && map_count < virtPageNum / 2) {
            for (i = 0; i < virtPageNum; i++) {
                Page page = (Page) memVector.elementAt(i);
                if (page.physical == -1 && map_count < virtPageNum / 2) {
                    int freePageIndex = 0;

                    for (int n = virtPageNum / 2; freePageIndex < n; freePageIndex++) {
                        if (!physicalMapped.contains(freePageIndex)) {
                            physicalMapped.add(freePageIndex);
                            break;
                        }
                    }

                    page.physical = freePageIndex;
                    map_count++;
                }
            }
        }

        for (i = 0; i < virtPageNum; i++) {
            Page page = (Page) memVector.elementAt(i);
            if (page.physical == -1) {
                controlPanel.removePhysicalPage(i);
            } else {
                workingSet.add(page);
                controlPanel.addPhysicalPage(page.id, page.physical);
            }
        }
        for (i = 0; i < instructVector.size(); i++) {
            high = block * virtPageNum;
            Instruction instruct = (Instruction) instructVector.elementAt(i);
            if (instruct.addr < 0 || instruct.addr > high) {
                System.out.println("MemoryManagement: Instruction (" + instruct.inst + " " + instruct.addr + ") out of bounds.");
                System.exit(-1);
            }
        }
    }

    public void setControlPanel(ControlPanel newControlPanel) {
        controlPanel = newControlPanel;
    }

    public void getPage(int pageNum) {
        Page page = (Page) memVector.elementAt(pageNum);
        controlPanel.paintPage(page);
    }

    private void printLogFile(String streamName, String message) {
        String line;
        String temp = "";

        File trace = new File(streamName);
        if (trace.exists()) {
            try {
                DataInputStream in = new DataInputStream(new FileInputStream(streamName));
                while ((line = in.readLine()) != null) {
                    temp = temp + line + lineSeparator;
                }
                in.close();
            } catch (IOException e) {
                /* Do nothing */
            }
        }
        try {
            PrintStream out = new PrintStream(new FileOutputStream(streamName));
            out.print(temp);
            out.print(message);
            out.close();
        } catch (IOException e) {
            /* Do nothing */
        }
    }

    public void run() {
        step();
        while (runs != runcycles) {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                /* Do nothing */
            }
            step();
        }
        logPageFaultCount();
    }

    private void logPageFaultCount() {
        String msg = "\n\nDate: " + (new SimpleDateFormat("dd/MM/yyyy")).format(new Date()) + "\n";

        if (isModifiedAlgorithm) {
            msg += "WSClock PRA";
        } else {
            msg += "FIFO PRA";
        }

        msg += " page fault count: " + Integer.toString(pageFaultCount) + "\n";
        msg += "Config file: " + config_file + "\n";
        msg += "Command file: " + command_file + "\n\n";

        logMessage(msg);
    }

    public void step() {
        int i = 0;

        Instruction instruct = (Instruction) instructVector.elementAt(runs);
        controlPanel.instructionValueLabel.setText(instruct.inst);
        controlPanel.addressValueLabel.setText(Long.toString(instruct.addr, addressradix));

        int virtualPageNumber = Virtual2Physical.pageNum(instruct.addr, virtPageNum, block);

        getPage(virtualPageNumber);

        if (controlPanel.pageFaultValueLabel.getText() == "YES") {
            controlPanel.pageFaultValueLabel.setText("NO");
        }

        Page page = (Page) memVector.elementAt(virtualPageNumber);

        if (instruct.inst.startsWith("READ")) {
            if (page.physical == -1) {
                logMessage("READ " + Long.toString(instruct.addr, addressradix) + " ... page fault");
                startReplacementAlgorithm(virtualPageNumber);
                controlPanel.pageFaultValueLabel.setText("YES");
            } else {
                if (!workingSet.contains(page)) {
                    workingSet.add(page);
                }
                page.R = 1;
                page.lastTouchTime = 0;
                logMessage("READ " + Long.toString(instruct.addr, addressradix) + " ... okay");
            }
        }
        if (instruct.inst.startsWith("WRITE")) {
            if (page.physical == -1) {
                logMessage("WRITE " + Long.toString(instruct.addr, addressradix) + " ... page fault");
                startReplacementAlgorithm(virtualPageNumber);
                controlPanel.pageFaultValueLabel.setText("YES");
            } else {
                if (!workingSet.contains(page)) {
                    workingSet.add(page);
                }
                page.M = 1;
                page.R = 1;
                page.lastTouchTime = 0;
                logMessage("WRITE " + Long.toString(instruct.addr, addressradix) + " ... okay");
            }
        }
        for (i = 0; i < workingSet.size(); i++) {
            Page pageIteration = workingSet.get(i);
            if (pageIteration.R == 1 && pageIteration.lastTouchTime == tick) {
                pageIteration.R = 0;
            }
            pageIteration.inMemTime++;
            pageIteration.lastTouchTime++;
        }
        runs++;
        controlPanel.timeValueLabel.setText(Integer.toString(runs) + " (ns)");
    }

    private void startReplacementAlgorithm(int virtualPageNumber) {
        pageFaultCount++;
        if (isModifiedAlgorithm) {
            pageFaultHandler.replacePage(memVector, virtualPageNumber, controlPanel, workingSet, tau, ioLimit);
        } else {
            pageFaultHandler.replacePage(memVector, virtPageNum, virtualPageNumber, controlPanel);
        }
    }

    private void logMessage(String msg) {
        if (doFileLog) {
            printLogFile(output, msg);
        }
        if (doStdoutLog) {
            System.out.println(msg);
        }
    }

    public void reset() {
        workingSet.clear();
        physicalMapped.clear();
        memVector.removeAllElements();
        instructVector.removeAllElements();
        controlPanel.statusValueLabel.setText("STOP");
        controlPanel.timeValueLabel.setText("0");
        controlPanel.instructionValueLabel.setText("NONE");
        controlPanel.addressValueLabel.setText("NULL");
        controlPanel.pageFaultValueLabel.setText("NO");
        controlPanel.virtualPageValueLabel.setText("x");
        controlPanel.physicalPageValueLabel.setText("0");
        controlPanel.RValueLabel.setText("0");
        controlPanel.MValueLabel.setText("0");
        controlPanel.inMemTimeValueLabel.setText("0");
        controlPanel.lastTouchTimeValueLabel.setText("0");
        controlPanel.lowValueLabel.setText("0");
        controlPanel.highValueLabel.setText("0");
        init(command_file, config_file);
    }
}
