package com.redhat.gss.skillmatrix.controllers.sorthelpers;

import com.redhat.gss.skillmatrix.controllers.sorthelpers.util.Filter;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.PackageProducer;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.SbrProducer;
import com.redhat.gss.skillmatrix.util.PaginationHelper;
import org.ajax4jsf.model.SequenceRange;
import org.richfaces.component.SortOrder;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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


    private SortOrder nameOrder = SortOrder.unsorted;
    private SortOrder membersOrder = SortOrder.unsorted;
    private SortOrder packagesOrder = SortOrder.unsorted;

    public HashMap<String, Filter<SbrProducer>> filtersOrders;

    //richfaces model
    private SbrsModel model;
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

        this.pagination = new PaginationHelper(recordsPerPage, model.getRowCount());
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

        if(pagination!=null)
            pagination.setMaxRecords((int)producer.getCount());
        return producer;
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
     * @return members sort order
     */
    public SortOrder getMembersOrder() {
        return membersOrder;
    }

    /**
     * Sets members sort order
     * @param membersOrder
     */
    public void setMembersOrder(SortOrder membersOrder) {
        this.membersOrder = membersOrder;
    }

    /**
     * @return packages sort order
     */
    public SortOrder getPackagesOrder() {
        return packagesOrder;
    }

    /**
     * Sets packages sort order
     * @param packagesOrder
     */
    public void setPackagesOrder(SortOrder packagesOrder) {
        this.packagesOrder = packagesOrder;
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
        if(nameFilter.isEmpty())
            return;

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

    /**
     *
     * @return RichFaces model.
     */
    public SbrsModel getModel() {
        return model;
    }

    /**
     *
     * @return Pagination helper.
     */
    public PaginationHelper getPagination() {
        return pagination;
    }
}
