package com.redhat.gss.skillmatrix.controllers.sorthelpers;

import java.io.Serializable;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

import org.ajax4jsf.model.SequenceRange;
import org.richfaces.component.SortOrder;

import com.redhat.gss.skillmatrix.controllers.sorthelpers.util.Filter;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.SbrProducer;
import com.redhat.gss.skillmatrix.util.PaginationHelper;

/**
 * Model helper class for {@link com.redhat.gss.skillmatrix.model.Package}. This class has two functions:
 * <ul>
 *     <li>contains a RichFaces model and pagination helper and enables the bond between the two</li>
 *     <li>takes care of orders and filters</li>
 * </ul>
 * @author jtrantin
 *
 */
public abstract class SbrModelHelper implements Serializable {
    private static final long serialVersionUID = -3855996218668168469L;


    @Getter @Setter
    private SortOrder nameOrder = SortOrder.unsorted;
    @Getter @Setter
    private SortOrder membersOrder = SortOrder.unsorted;
    @Getter @Setter
    private SortOrder packagesOrder = SortOrder.unsorted;

    public HashMap<String, Filter<SbrProducer>> filtersOrders;


    /**
     * RichFaces model
     */
    @Getter
    private SbrsModel model;
    /**
     * Pagination Helper
     */
    @Getter
    private PaginationHelper pagination;

    /**
     * This method must return valid {@link SbrProducer} implementation. The producer can already be modified (e.g.
     * some filters can be added).
     * @return valid SbrProducer impl.
     */
    protected abstract SbrProducer getProducerFactory();


    public SbrModelHelper(int recordsPerPage) {
        this.filtersOrders = new HashMap<String, Filter<SbrProducer>>();
        this.model = new SbrsModel() {
            @Override
            protected SbrProducer getProducer() {
                return createProducerFactory();
            }
        };

        this.pagination = new PaginationHelper(recordsPerPage) {
            @Override
            public int getMaxRecords() {
                return (int)model.getProducer().getCount();
            }
        };

        this.pagination.addOnChangeListener(new PaginationHelper.RangeListener() {
            @Override
            public void doListen(SequenceRange range) {
                SbrModelHelper.this.model.setRange(range);
            }
        });

        //set initial range
        this.model.setRange(this.pagination.getRange());
    }

    private SbrProducer createProducerFactory() {
        SbrProducer producer = getProducerFactory();

        for(Filter filterOrder : filtersOrders.values()) {
            filterOrder.apply(producer); //apply all filters and orders
        }

        return producer;
    }


    /**
     * @return name filter
     */
    public String getNameFilter() {
        Filter nameFilter = filtersOrders.get("nameFilter");
        if(nameFilter==null)
            return "";

        return nameFilter.getValue();
    }

    /**
     * Sets name filter
     * @param nameFilter
     */
    public void setNameFilter(String nameFilter) {
        if(nameFilter==null)
            return;
        nameFilter = nameFilter.trim();
        if(nameFilter.isEmpty()){
            filtersOrders.remove("nameFilter");
            return;
        }

        Filter filter = new Filter<SbrProducer>() {

            @Override
            public SbrProducer apply(SbrProducer producer) {
                return producer.filterName(this.value);
            }
        };
        filter.setValue(nameFilter);

        filtersOrders.put("nameFilter", filter);

    }

    /**
     * Sorts by name and sets all corresponding sort orders.
     */
    public void sortByName() {
        membersOrder = packagesOrder = SortOrder.unsorted;
        if(nameOrder.equals(SortOrder.ascending)) {
            setNameOrder(SortOrder.descending);
        } else {
            setNameOrder(SortOrder.ascending);
        }

        this.filtersOrders.put("order", new Filter<SbrProducer>() {
            @Override
            public SbrProducer apply(SbrProducer producer) {
                return producer.sortName(nameOrder == SortOrder.ascending);
            }
        });
    }

    /**
     * Sorts by members and sets all corresponding sort orders.
     */
    public void sortByMembers() {
        nameOrder = packagesOrder = SortOrder.unsorted;
        if(membersOrder.equals(SortOrder.ascending)) {
            setMembersOrder(SortOrder.descending);
        } else {
            setMembersOrder(SortOrder.ascending);
        }

        this.filtersOrders.put("order", new Filter<SbrProducer>() {
            @Override
            public SbrProducer apply(SbrProducer producer) {
                return producer.sortMembersCount(membersOrder == SortOrder.ascending);
            }
        });
    }

    /**
     * Sorts by packages and sets all corresponding sort orders.
     */
    public void sortByPackages() {
        nameOrder = membersOrder = SortOrder.unsorted;
        if(packagesOrder.equals(SortOrder.ascending)) {
            setPackagesOrder(SortOrder.descending);
        } else {
            setPackagesOrder(SortOrder.ascending);
        }

        this.filtersOrders.put("order", new Filter<SbrProducer>() {
            @Override
            public SbrProducer apply(SbrProducer producer) {
                return producer.sortPackagesCount(packagesOrder == SortOrder.ascending);
            }
        });
    }

}
