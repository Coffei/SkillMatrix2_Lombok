package com.redhat.gss.skillmatrix.controllers.sorthelpers;

import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.PackageProducer;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.SbrProducer;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;
import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;

import javax.faces.context.FacesContext;
import java.util.List;

/**
 * RichFaces model for {@link Package}. This model is very flexible and can be used for any package-related tables. Just modify
 * the {@link PackageProducer} before giving it to this class by {@link #getProducer()}.<br/>
 * User: jtrantin<br/>
 * Date: 9/16/13<br/>
 * Time: 10:10 AM
 */
public abstract class PackagesModel extends ExtendedDataModel<Package> {

    private Integer key;

    private PackageProducer producer;

    private List<Package> pkgs;

    private SequenceRange range;

    public PackagesModel() {
        this.range = new SequenceRange(0, Integer.MAX_VALUE);
    }


    @Override
    public void setRowKey(Object o) {
        if(o instanceof Integer) {
            key = (Integer)o;
        }
    }

    @Override
    public Object getRowKey() {
        return key;
    }

    @Override
    public void walk(FacesContext facesContext, DataVisitor dataVisitor, Range range, Object o) {
        this.producer = getProducer();

        producer.recordsCount(this.range.getRows())
                .recordsStart(this.range.getFirstRow());

        this.pkgs = producer.getPackages();

        for(int i = 0; i < this.pkgs.size(); i++) {
            dataVisitor.process(facesContext, i, o);
        }
    }

    @Override
    public boolean isRowAvailable() {
        return key!=null;
    }

    @Override
    public int getRowCount() {
        this.producer = getProducer();

        return (int)producer.getCount();
    }

    @Override
    public Package getRowData() {
        return pkgs.get(key);
    }

    @Override
    public int getRowIndex() {
        return -1;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setRowIndex(int i) {
        // noop
    }

    @Override
    public Object getWrappedData() {
        return null;
    }

    @Override
    public void setWrappedData(Object o) {
        // noop
    }

    /**
     *
     * @return range of displayed data
     */
    public SequenceRange getRange() {
        return range;
    }

    /**
     * Sets the range of displayed data.
     * @param range
     */
    public void setRange(SequenceRange range) {
        this.range = range;
    }

    /**
     * Must return a valid {@link PackageProducer}. The producer can be already modified.
     * @return
     */
    protected abstract PackageProducer getProducer();
}
