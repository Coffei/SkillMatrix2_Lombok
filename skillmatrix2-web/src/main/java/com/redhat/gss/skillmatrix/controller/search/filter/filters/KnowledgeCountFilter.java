package com.redhat.gss.skillmatrix.controller.search.filter.filters;

import com.redhat.gss.skillmatrix.controller.search.filter.Filter;
import com.redhat.gss.skillmatrix.controller.search.filter.FilterType;
import com.redhat.gss.skillmatrix.controller.search.filter.MemberFilter;
import com.redhat.gss.skillmatrix.controller.search.filter.exeptions.TypeMismatchException;
import com.redhat.gss.skillmatrix.controller.search.filter.filters.util.AttributeEncoder;
import com.redhat.gss.skillmatrix.controllers.sorthelpers.MemberModelHelper;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
import com.redhat.gss.skillmatrix.data.dao.producers.util.OperatorEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 2/4/14
 * Time: 8:32 AM
 * To change this template use File | Settings | File Templates.
 */
@MemberFilter(id ="knowCount",
        type = FilterType.ADVANCED,
        page = "knowCount.xhtml",
        name = "knowledge count")
public class KnowledgeCountFilter implements Filter {

    private int level;
    private int amount;
    private OperatorEnum operator;


    @Override
    public String encode() {
        Map<String, String> data = new HashMap<String, String>(3);
        data.put("level", String.valueOf(level));
        data.put("amount", String.valueOf(amount));
        data.put("operator", operator.toString());

        return AttributeEncoder.encodeFromMap("knowCount", data);
    }

    @Override
    public void decode(String filter) throws TypeMismatchException, IllegalArgumentException {
        Map<String, String> data = AttributeEncoder.decodeToMap(filter, "knowCount");
        if(data==null || data.isEmpty())
            throw new IllegalArgumentException("no parameters specified");
        if(data.get("level")==null)
            throw new IllegalArgumentException("missing parameter- level");
        if(data.get("amount") == null)
            throw new IllegalArgumentException("missing parameter- amount");
        if(data.get("operator")==null)
            throw new IllegalArgumentException("missing parameter- operator");

        //all data should be present
        try {
            this.level = Integer.parseInt(data.get("level"));
            this.amount = Integer.parseInt(data.get("amount"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("wrong parameter format", e);
        }

        this.operator = OperatorEnum.valueOf(data.get("operator"));
        if(this.operator==null)
            throw new IllegalArgumentException("wrong parameter format- operator");
    }

    @Override
    public boolean apply(MemberModelHelper modelHelper) {
        return false;  // cannot be applied to a model
    }

    @Override
    public void applyOnProducer(MemberProducer producer) {
        producer.filterKnowledgeLevelCount(level, amount, operator);
    }

    @Override
    public String explain() {
        return String.format("number of knowledges at %s level is %s %d", (level==0? "beginner" : (level==1? "intermediate" : "expert")), operator.toReadableText(), amount);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public OperatorEnum getOperator() {
        return operator;
    }

    public void setOperator(OperatorEnum operator) {
        this.operator = operator;
    }
}
