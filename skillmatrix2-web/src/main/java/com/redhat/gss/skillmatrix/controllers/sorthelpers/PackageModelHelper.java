package com.redhat.gss.skillmatrix.controllers.sorthelpers;

import com.redhat.gss.skillmatrix.controllers.sorthelpers.util.Filter;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.PackageProducer;
import com.redhat.gss.skillmatrix.util.PaginationHelper;
import org.ajax4jsf.model.SequenceRange;
import org.richfaces.component.SortOrder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Model helper class for {@link com.redhat.gss.skillmatrix.model.Package}. This class has two functions:
 * <ul>
 *     <li>contains a RichFaces model and pagination helper and enables the bond between the two</li>
 *     <li>takes care of orders and filters</li>
 * </ul>
 * User: jtrantin
 * Date: 9/16/13
 * Time: 10:17 AM
 */
public abstract class PackageModelHelper implements Serializable {

    private PackagesModel model;
    private PaginationHelper pagination;

    private Map<String, Filter<PackageProducer>> filtersOrders;

    private SortOrder nameOrder = SortOrder.unsorted;
    private SortOrder sbrOrder = SortOrder.unsorted;

    public PackageModelHelper(int recordsPerPage) {
        this.filtersOrders = new HashMap<String, Filter<PackageProducer>>();
        this.model = new PackagesModel() {
            @Override
            protected PackageProducer getProducer() {
                return createProducer();
            }
        };

        this.pagination = new PaginationHelper(recordsPerPage, model.getRowCount());
        this.pagination.addOnChangeListener(new PaginationHelper.RangeListener() {
            @Override
            public void doListen(SequenceRange range) {
                PackageModelHelper.this.model.setRange(range);
            }
        });

        //set intial range
        this.model.setRange(this.pagination.getRange());
    }

    /**
     * This method must return valid {@link PackageProducer} implementation. The producer can already be modified (e.g.
     * some filters can be added).
     * @return valid PackageProducer impl.
     */
    protected abstract PackageProducer getProducer();

    private PackageProducer createProducer() {
        PackageProducer producer = getProducer();
        for(Filter filter : filtersOrders.values()) {
            filter.apply(producer);
        }

        if(pagination!= null) {
            pagination.setMaxRecords((int)producer.getCount());
        }

        return producer;
    }

    /**
     *
     * @return RichFaces model.
     */
    public PackagesModel getModel() {
        return model;
    }

    /**
     *
     * @return Pagination helper
     */
    public PaginationHelper getPagination() {
        return pagination;
    }

    /**
     * @return filter for name
     */
    public String getNameFilter() {
        Filter nameFilter = filtersOrders.get("nameFilter");
        if(nameFilter == null)
            return "";

        return nameFilter.getValue();
    }


    /**
     * Sets filter for name
     * @param nameFilter
     */
    public void setNameFilter(String nameFilter) {
        if(nameFilter==null || nameFilter.trim().isEmpty()) {
            filtersOrders.remove("nameFilter");
            return;
        }

        Filter filter = new Filter<PackageProducer>() {
            @Override
            public PackageProducer apply(PackageProducer producer) {
                return producer.filterName(this.value);
            }
        };
        filter.setValue(nameFilter.trim());

        filtersOrders.put("nameFilter", filter);
    }
    /**
     * @return filter for sbr
     */
    public String getSbrFilter() {
        Filter nameFilter = filtersOrders.get("sbrFilter");
        if(nameFilter == null)
            return "";

        return nameFilter.getValue();
    }
    /**
     * Sets filter for sbr
     * @param sbrFilter
     */
    public void setSbrFilter(String sbrFilter) {
        if(sbrFilter==null || sbrFilter.trim().isEmpty()) {
            filtersOrders.remove("sbrFilter");
            return;
        }

        Filter filter = new Filter<PackageProducer>() {
            @Override
            public PackageProducer apply(PackageProducer producer) {
                return producer.filterSbrName(this.value);
            }
        };
        filter.setValue(sbrFilter.trim());

        filtersOrders.put("sbrFilter", filter);
    }

    /**
     * @return name sort order
     */
    public SortOrder getNameOrder() {
        return nameOrder;
    }
    /**
     * Sets name sort order
     * @param nameOrder
     */
    public void setNameOrder(SortOrder nameOrder) {
        this.nameOrder = nameOrder;
    }


    /**
     * Sorts by name, sets all corresponding sort orders.
     */
    public void sortByName() {
        sbrOrder = SortOrder.unsorted;
        if(nameOrder.equals(SortOrder.ascending)) {
            setNameOrder(SortOrder.descending);
        } else {
            setNameOrder(SortOrder.ascending);
        }

        this.filtersOrders.put("order", new Filter<PackageProducer>() {
            @Override
            public PackageProducer apply(PackageProducer producer) {
                return producer.sortName(nameOrder == SortOrder.ascending);
            }
        });
    }

    /**
     * @return sbr sort order
     */
    public SortOrder getSbrOrder() {
        return sbrOrder;
    }

    /**
     * Sets sbr sort order
     * @param sbrOrder
     */
    public void setSbrOrder(SortOrder sbrOrder) {
        this.sbrOrder = sbrOrder;
    }


    /**
     * Sorts by sbr, sets all corresponding sort orders.
     */
    public void sortBySbr() {
        nameOrder = SortOrder.unsorted;
        if(sbrOrder.equals(SortOrder.ascending)) {
            setSbrOrder(SortOrder.descending);
        } else {
            setSbrOrder(SortOrder.ascending);
        }

        filtersOrders.put("order", new Filter<PackageProducer>() {
            @Override
            public PackageProducer apply(PackageProducer producer) {
                return producer.sortSbrName(sbrOrder == SortOrder.ascending);
            }
        });
    }
}
