package com.redhat.gss.skillmatrix.util;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.extern.java.Log;

import org.ajax4jsf.model.SequenceRange;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 8/20/13
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Log
public abstract class PaginationHelper {

    //TODO: document
    @Getter
    private int recordsPerPage;


    private List<RangeListener> onChangeListeners;

    public PaginationHelper(int recordsPerPage) {
        // TODO: check validity
        this.recordsPerPage = recordsPerPage;

        currentPage = 1;
        offset = 0;

        onChangeListeners = new LinkedList<RangeListener>();
    }

    private int currentPage;
    private int offset;

    //TODO: this is not correct I think
    // there must be better way to generate this, THINK!
    public String[] listPages(int maxPages) {
        checkPageRange();
        maxPages = Math.min(maxPages, getMaxPages()); //choose the lower from max displayed pages and how many pages there actually is

        if(maxPages < 1) //extreme causes
            return new String[0];
        if(maxPages == 1) {
            return new String[]{String.valueOf(currentPage)};
        }

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

    private void checkPageRange() {
        if(currentPage > getMaxPages())
            goToPage(1);
    }

    

    public abstract int getMaxRecords();


    public int getCurrentPage() {
        checkPageRange();
        return currentPage;
    }

    public int getMaxPages() {
        int maxRecords = getMaxRecords();
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
