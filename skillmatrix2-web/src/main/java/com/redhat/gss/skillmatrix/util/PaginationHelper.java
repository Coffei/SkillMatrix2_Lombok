package com.redhat.gss.skillmatrix.util;

import org.ajax4jsf.model.SequenceRange;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 8/20/13
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaginationHelper {

    //TODO: implement this and use instead of rich:dataScroller
    //TODO: document

    private int recordsPerPage;
    private int maxRecords;

    private List<RangeListener> onChangeListeners;

    public PaginationHelper(int recordsPerPage, int maxRecords) {
        // TODO: check validity
        this.recordsPerPage = recordsPerPage;
        this.maxRecords = maxRecords;

        currentPage = 1;
        offset = 0;

        onChangeListeners = new LinkedList<RangeListener>();
    }

    private int currentPage;
    private int offset;

    //TODO: this is not correct I think
    // there must be better way to generate this, THINK!
    public String[] listPages(int maxPages) {
        maxPages = Math.min(maxPages, getMaxPages()); //choose the lower from max displayed pages and how many pages there actually is

        if(maxPages < 1) //extreme causes
            return new String[0];
        if(maxPages == 1)
            return new String[]{String.valueOf(currentPage)};

        int maxVisiblePage;
        int minVisiblePage;
        if((currentPage - (maxPages/2)) < 1) { //we need to start with first page
            minVisiblePage = 1;
            maxVisiblePage = maxPages;
        }  else if(currentPage + (maxPages/2) > getMaxPages()) { //verify the on-edge situations
            maxVisiblePage = getMaxPages();
            minVisiblePage = maxVisiblePage - maxPages + 1;

        } else { //we need to start with currentPage - maxPages/2
            minVisiblePage = currentPage - (maxPages/2);
            maxVisiblePage = minVisiblePage + Math.max(maxPages - 1, 1);
        }


        String[] result = new String[maxVisiblePage - minVisiblePage + 1];
        for(int i = 0; i <= (maxVisiblePage - minVisiblePage); i++) {
            result[i] = String.valueOf(i + minVisiblePage);
        }

        return result;
    }

    public void goToPage(int page) {
        if(page > 0 && page <= getMaxPages()) { // is valid page
            this.currentPage = page;
            this.offset = (page - 1) * recordsPerPage;
        }

        fireOnChange();
    }

    public void goNext() {
        goToPage(currentPage+1);

        fireOnChange();
    }

    public void goBack() {
        goToPage(currentPage-1);

        fireOnChange();
    }

    public int getRecordsPerPage() {
        return recordsPerPage;
    }

    public int getMaxRecords() {
        return maxRecords;
    }

    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
        int maxPages = getMaxPages();

        if(currentPage > maxPages) { // current page is not valid anymore
            currentPage = 1; // default to first page, other option is to display the last page
        }

        fireOnChange();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getMaxPages() {
        return maxRecords % recordsPerPage == 0 ? maxRecords / recordsPerPage : (maxRecords / recordsPerPage) + 1;
    }



    //Range to display- this needs to get into the tablemodel
    public SequenceRange getRange() {
        return new SequenceRange(offset, recordsPerPage);
    }

    public void addOnChangeListener(RangeListener listener) {
        if(listener==null)
            return; //ignore wrong listeners
        onChangeListeners.add(listener);
    }

    private void fireOnChange() {
        for(RangeListener listener : onChangeListeners)
            listener.doListen(getRange());
    }

    public static interface RangeListener {
        void doListen(SequenceRange range);
    }
}
