package lab3;

import java.util.*;

public class PageFault {
    private CircularIterator<Page> iterator = null;

    // WSClock PRA
    public void replacePage(Vector mem, int virtualPageNum, ControlPanel controlPanel,
                            List<Page> workingSet, Set<Integer> physicalUnused, int tau, int ioLimit) {
        int evictionPageId = -1;
        int evictionPageWorkSetIndex = -1;
        int cleanPageWorkSetIndex = -1;
        int physicalNewPageNum = -1;

        if (!physicalUnused.isEmpty()) {
            physicalNewPageNum = physicalUnused.iterator().next();
            physicalUnused.remove(physicalNewPageNum);

            for (Object o : mem) {
                Page page = (Page) o;
                if (page.physical == physicalNewPageNum) {
                    evictionPageId = page.id;
                    break;
                }
            }

            workingSet.add((Page)mem.get(virtualPageNum));
        } else {

            if (iterator == null) {
                iterator = new CircularIterator<>(workingSet);
            }

            int currentIOLimit = ioLimit;
            int cleanPageId = -1;
            int indexIOLimitReload = iterator.index();

            boolean started = false;

            while (true) {
                Page workingSetCurrentPage = iterator.get();

                if (started && iterator.index() == indexIOLimitReload) {
                    if (currentIOLimit == ioLimit) {
                        if (cleanPageId != -1) {
                            evictionPageId = cleanPageId;
                            evictionPageWorkSetIndex = cleanPageWorkSetIndex;
                        } else {
                            evictionPageId = workingSetCurrentPage.id;
                            evictionPageWorkSetIndex = iterator.index();
                        }
                        break;
                    }

                    currentIOLimit = ioLimit;
                }

                started = true;

                if (workingSetCurrentPage.R == 1) {
                    workingSetCurrentPage.R = 0;
                    iterator.next();
                    continue;
                }

                if (workingSetCurrentPage.M == 0) {
                    cleanPageId = workingSetCurrentPage.id;
                    cleanPageWorkSetIndex = iterator.index();
                }

                if (workingSetCurrentPage.R == 0 && workingSetCurrentPage.lastTouchTime > tau) {
                    if (workingSetCurrentPage.M == 0) {
                        evictionPageId = workingSetCurrentPage.id;
                        evictionPageWorkSetIndex = iterator.index();
                        break;
                    } else {
                        if (currentIOLimit > 0) {
                            currentIOLimit--;
                            workingSetCurrentPage.M = 0;
                        }
                    }
                }

                iterator.next();
            }

            iterator.setIndex(evictionPageWorkSetIndex);
            iterator.next();

            physicalNewPageNum = ((Page) mem.get(evictionPageId)).physical;
            workingSet.set(evictionPageWorkSetIndex, (Page) mem.get(virtualPageNum));
        }

        ((Page) mem.get(evictionPageId)).reset();
        ((Page) mem.get(virtualPageNum)).physical = physicalNewPageNum;

        controlPanel.removePhysicalPage(evictionPageId);
        controlPanel.addPhysicalPage(virtualPageNum, physicalNewPageNum);
    }

    // FIFO PRA
    public void replacePage(Vector mem, int virtPageNum, int replacePageNum, ControlPanel controlPanel) {
        int count = 0;
        int oldestPage = -1;
        int oldestTime = 0;
        int firstPage = -1;
        boolean mapped = false;

        while (!(mapped) || count != virtPageNum) {
            Page page = (Page) mem.elementAt(count);
            if (page.physical != -1) {
                if (firstPage == -1) {
                    firstPage = count;
                }
                if (page.inMemTime > oldestTime) {
                    oldestTime = page.inMemTime;
                    oldestPage = count;
                    mapped = true;
                }
            }
            count++;
            if (count == virtPageNum) {
                mapped = true;
            }
        }
        if (oldestPage == -1) {
            oldestPage = firstPage;
        }
        Page page = (Page) mem.elementAt(oldestPage);
        Page nextpage = (Page) mem.elementAt(replacePageNum);
        controlPanel.removePhysicalPage(oldestPage);
        nextpage.physical = page.physical;
        controlPanel.addPhysicalPage(replacePageNum, nextpage.physical);
        page.inMemTime = 0;
        page.lastTouchTime = 0;
        page.R = 0;
        page.M = 0;
        page.physical = -1;
    }
}
