/* It is in this file, specifically the replacePage function that will
   be called by MemoryManagement when there is a page fault.  The 
   users of this program should rewrite PageFault to implement the 
   page replacement algorithm.
*/

// This PageFault file is an example of the FIFO Page Replacement
// Algorithm as described in the Memory Management section.
package lab3;

import java.util.*;

public class PageFault {
    private static CircularIterator<Page> iterator = null;

    /**
     * The page replacement algorithm for the memory management simulator.
     * This method gets called whenever a page needs to be replaced.
     * <p>
     * The page replacement algorithm included with the simulator is
     * FIFO (first-in first-out).  A while or for loop should be used
     * to search through the current memory contents for a candidate
     * replacement page.  In the case of FIFO the while loop is used
     * to find the proper page while making sure that virtPageNum is
     * not exceeded.
     * <pre>
     *   Page page = ( Page ) mem.elementAt( oldestPage )
     * </pre>
     * This line brings the contents of the Page at oldestPage (a
     * specified integer) from the mem vector into the page object.
     * Next recall the contents of the target page, replacePageNum.
     * Set the physical memory address of the page to be added equal
     * to the page to be removed.
     * <pre>
     *   controlPanel.removePhysicalPage( oldestPage )
     * </pre>
     * Once a page is removed from memory it must also be reflected
     * graphically.  This line does so by removing the physical page
     * at the oldestPage value.  The page which will be added into
     * memory must also be displayed through the addPhysicalPage
     * function call.  One must also remember to reset the values of
     * the page which has just been removed from memory.
     *
     * @param mem            is the vector which contains the contents of the pages
     *                       in memory being simulated.  mem should be searched to find the
     *                       proper page to remove, and modified to reflect any changes.
     * @param virtualPageNum is the requested page which caused the
     *                       page fault.
     * @param controlPanel   represents the graphical element of the
     *                       simulator, and allows one to modify the current display.
     */
    public static void replacePage(Vector mem, int virtualPageNum, ControlPanel controlPanel,
                                   Set<Integer> physicalUnloaded, List<Page> workingSet, int tau, int ioLimit) {
        int evictionPageId = -1;
        int physicalNewPageNum = -1;

        if (!physicalUnloaded.isEmpty()) {
            physicalNewPageNum = physicalUnloaded.iterator().next();
            physicalUnloaded.remove(physicalNewPageNum);

            for (Object pageObject : mem) {
                Page currentPage = (Page) pageObject;

                if (currentPage.id != virtualPageNum && currentPage.physical == physicalNewPageNum) {
                    evictionPageId = currentPage.id;
                    break;
                }
            }

            workingSet.add((Page) mem.get(virtualPageNum));
        } else {
            if (iterator == null) {
                iterator = new CircularIterator<>(workingSet);
            }

            int currentIOLimit = ioLimit;
            int cleanPageId = -1;
            int indexIOLimitReload = iterator.previousIndex() + 1;

            while (true) {
                Page workingSetCurrentPage = iterator.get();

                if (workingSetCurrentPage.R == 1) {
                    workingSetCurrentPage.R = 0;
                }

                if (workingSetCurrentPage.M == 0) {
                    cleanPageId = workingSetCurrentPage.id;
                }

                if (workingSetCurrentPage.R == 0 && workingSetCurrentPage.lastTouchTime > tau) {
                    if (workingSetCurrentPage.M == 0) {
                        evictionPageId = workingSetCurrentPage.id;
                        break;
                    } else {
                        if (currentIOLimit > 0) {
                            currentIOLimit--;
                            workingSetCurrentPage.M = 0;
                        }
                    }
                }

                if (iterator.nextIndex() == indexIOLimitReload) {
                    if (currentIOLimit < ioLimit) {
                        if (cleanPageId != -1) {
                            evictionPageId = cleanPageId;
                        } else {
                            evictionPageId = workingSetCurrentPage.id;
                        }
                        break;
                    }

                    currentIOLimit = ioLimit;
                }
                iterator.next();
            }

            physicalNewPageNum = ((Page) mem.get(evictionPageId)).physical;
            iterator.set((Page) mem.get(virtualPageNum));
        }

        ((Page) mem.get(evictionPageId)).reset();
        ((Page) mem.get(virtualPageNum)).physical = physicalNewPageNum;

        controlPanel.removePhysicalPage(evictionPageId);
        controlPanel.addPhysicalPage(virtualPageNum, physicalNewPageNum);
    }
}
