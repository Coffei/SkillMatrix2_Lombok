package com.redhat.gss.skillmatrix.controllers.sorthelpers;

import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
import com.redhat.gss.skillmatrix.model.Member;
import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;

import javax.faces.context.FacesContext;
import java.util.List;

/**
 * RichFaces model for Members. This model is very flexible and can be used for any member-related tables. Just modify
 * the {@link MemberProducer} before giving it to this class by {@link #getProducer()}.<br/>
 * User: jtrantin<br/>
 * Date: 8/19/13<br/>
 * Time: 3:16 PM
 */
public abstract class MembersModel extends ExtendedDataModel<Member> {

    private Integer key;

    private MemberProducer producer;

    private List<Member> members;

    private SequenceRange range;

    public MembersModel() {
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

        this.members = producer.getMembers();

        for(int i = 0; i < this.members.size(); i++) {
            dataVisitor.process(facesContext, i, o);
        }
    }

    @Override
    public boolean isRowAvailable() {
        return key!=null;
    }

    @Override
    public int getRowCount() {
        if(producer==null)
            this.producer = getProducer();

        return (int)producer.getCount();
    }

    @Override
    public Member getRowData() {
        return members.get(key);
    }

    @Override
    public int getRowIndex() {
        return -1;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setRowIndex(int i) {
        //noop
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
     * @return range of displayed data.
     */
    public SequenceRange getRange() {
        return range;
    }

    /**
     * Sets the range of displayed data
     * @param range valid sequence range.
     */
    public void setRange(SequenceRange range) {
        this.range = range;
    }

    /**
     * Must return a valid {@link MemberProducer}. The producer can be already modified.
     * @return
     */
    protected abstract MemberProducer getProducer();
}
