package com.redhat.gss.skillmatrix.test.web.util;

import static org.junit.Assert.assertEquals;
import lombok.Getter;
import lombok.Setter;

import org.junit.Test;

import com.redhat.gss.skillmatrix.util.PaginationHelper;

/**
 * Test class for PaginationHelper
 * User: jtrantin
 * Date: 8/20/13
 * Time: 4:53 PM
 * @see com.redhat.gss.skillmatrix.util.PaginationHelper
 */

public class PaginationHelperTest {

    /**
     * Mock class, that simplifies maxRecords handling of PaginationHelper.
     */
    private static class FixedRecordsPaginationHelper extends PaginationHelper {

    	@Getter @Setter
        private int maxRecords;

        private FixedRecordsPaginationHelper(int recordsPerPage, int maxRecords) {
            super(recordsPerPage);
            this.maxRecords = maxRecords;
        }

    }

    @Test
    public void testMaxPages() throws Exception {
        PaginationHelper helper = new FixedRecordsPaginationHelper(20, 650);
        assertEquals("wrong max pages calculated", 33, helper.getMaxPages());

        helper = new FixedRecordsPaginationHelper(20, 15);
        assertEquals("wrong max pages calculated", 1, helper.getMaxPages());

        helper = new FixedRecordsPaginationHelper(10, 50);
        assertEquals("wrong max pages calculated", 5, helper.getMaxPages());
    }

    @Test
    public void testPaging() throws Exception {
        PaginationHelper helper = new FixedRecordsPaginationHelper(20, 650);
        helper.goToPage(20);

        assertEquals("wrong current page", 20, helper.getCurrentPage());
        assertEquals("wrong offset", 380, helper.getRange().getFirstRow());
        assertEquals("wrong rows", 20, helper.getRange().getRows());

        helper.goNext();
        helper.goNext();

        assertEquals("wrong current page", 22, helper.getCurrentPage());
        assertEquals("wrong offset", 420, helper.getRange().getFirstRow());
        assertEquals("wrong rows", 20, helper.getRange().getRows());

        helper.goBack();
        helper.goNext();
        helper.goBack();

        assertEquals("wrong current page", 21, helper.getCurrentPage());
        assertEquals("wrong offset", 400, helper.getRange().getFirstRow());
        assertEquals("wrong rows", 20, helper.getRange().getRows());

        helper.goToPage(35);

        assertEquals("wrong current page", 21, helper.getCurrentPage());

        helper.goToPage(33);
        assertEquals("wrong current page", 33, helper.getCurrentPage());
        assertEquals("wrong offset", 640, helper.getRange().getFirstRow());
        assertEquals("wrong rows", 20, helper.getRange().getRows());

        //cannot go over
        helper.goNext();
        assertEquals("wrong current page", 33, helper.getCurrentPage());

        helper.goToPage(1);
        assertEquals("wrong current page", 1, helper.getCurrentPage());

        //cannot go under
        helper.goBack();
        assertEquals("wrong current page", 1, helper.getCurrentPage());
    }

    @Test
    public void testListPages() throws Exception {
        PaginationHelper helper = new FixedRecordsPaginationHelper(20, 650);

        String[] pages = helper.listPages(5);
        assertEquals("wrong number of generated pages", 5, pages.length);
        assertEquals("wrong page generated", "1", pages[0]);
        assertEquals("wrong page generated", "2", pages[1]);
        assertEquals("wrong page generated", "3", pages[2]);
        assertEquals("wrong page generated", "4", pages[3]);
        assertEquals("wrong page generated", "5", pages[4]);

        helper.goToPage(20);
        pages = helper.listPages(3);
        assertEquals("wrong number of generated pages", 3, pages.length);
        assertEquals("wrong page generated", "19", pages[0]);
        assertEquals("wrong page generated", "20", pages[1]);
        assertEquals("wrong page generated", "21", pages[2]);

        helper.goToPage(32);
        pages = helper.listPages(5);
        assertEquals("wrong number of generated pages", 5, pages.length);
        assertEquals("wrong page generated", "29", pages[0]);
        assertEquals("wrong page generated", "30", pages[1]);
        assertEquals("wrong page generated", "31", pages[2]);
        assertEquals("wrong page generated", "32", pages[3]);
        assertEquals("wrong page generated", "33", pages[4]);

        helper.goToPage(31);
        pages = helper.listPages(5);
        assertEquals("wrong number of generated pages", 5, pages.length);
        assertEquals("wrong page generated", "29", pages[0]);
        assertEquals("wrong page generated", "30", pages[1]);
        assertEquals("wrong page generated", "31", pages[2]);
        assertEquals("wrong page generated", "32", pages[3]);
        assertEquals("wrong page generated", "33", pages[4]);

        helper.goToPage(30);
        pages = helper.listPages(5);
        assertEquals("wrong number of generated pages", 5, pages.length);
        assertEquals("wrong page generated", "28", pages[0]);
        assertEquals("wrong page generated", "29", pages[1]);
        assertEquals("wrong page generated", "30", pages[2]);
        assertEquals("wrong page generated", "31", pages[3]);
        assertEquals("wrong page generated", "32", pages[4]);

        helper = new FixedRecordsPaginationHelper(3, 12);
        helper.goToPage(4);
        pages = helper.listPages(5);
        assertEquals("wrong number of generated pages", 4, pages.length);
        assertEquals("wrong page generated", "1", pages[0]);
        assertEquals("wrong page generated", "2", pages[1]);
        assertEquals("wrong page generated", "3", pages[2]);
        assertEquals("wrong page generated", "4", pages[3]);

        helper.goToPage(3);
        pages = helper.listPages(5);
        assertEquals("wrong number of generated pages", 4, pages.length);
        assertEquals("wrong page generated", "1", pages[0]);
        assertEquals("wrong page generated", "2", pages[1]);
        assertEquals("wrong page generated", "3", pages[2]);
        assertEquals("wrong page generated", "4", pages[3]);

        helper.goToPage(2);
        pages = helper.listPages(5);
        assertEquals("wrong number of generated pages", 4, pages.length);
        assertEquals("wrong page generated", "1", pages[0]);
        assertEquals("wrong page generated", "2", pages[1]);
        assertEquals("wrong page generated", "3", pages[2]);
        assertEquals("wrong page generated", "4", pages[3]);


        helper = new FixedRecordsPaginationHelper(20, 10);
        pages = helper.listPages(5);
        assertEquals("wrong number of generated pages", 1, pages.length);
        assertEquals("wrong page generated", "1", pages[0]);

    }

    //TODO: improve, e.g. validity checks
}
