package com.redhat.gss.skillmatrix.controllers.sorthelpers;

import java.util.List;

import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;

import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
import com.redhat.gss.skillmatrix.model.Member;
import lombok.NonNull;

/**
 * RichFaces model for Members. This model is very flexible and can be used for any member-related tables. Just modify
 * the {@link MemberProducer} before giving it to this class by {@link #getProducer()}.<br/>
 * User: jtrantin<br/>
 * Date: 8/19/13<br/>
 * Time: 3:16 PM
 */
public abstract class MembersModel extends ExtendedDataModel<Member> {

	@Getter
    private Integer rowKey;

	
    private MemberProducer producer;

    private List<Member> members;

    /**
	 * Range of displayed data.
	 * 
	 * @return range of currently displayed data
	 * @param range valid sequence range
	 */
	@Getter @Setter
        @NonNull
    private SequenceRange range;
    

    public MembersModel() {
        this.range = new SequenceRange(0, Integer.MAX_VALUE);
    }

    @Override
    public void setRowKey(Object o) {
        if(o instanceof Integer) {
            rowKey = (Integer)o;
        }
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
        return rowKey!=null;
    }

    @Override
    public int getRowCount() {
        this.producer = getProducer();

        return (int)producer.getCount();
    }

    @Override
    public Member getRowData() {
        return members.get(rowKey);
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
     * Must return a valid {@link MemberProducer}. The producer can be already modified.
     * @return
     */
    protected abstract MemberProducer getProducer();
}
