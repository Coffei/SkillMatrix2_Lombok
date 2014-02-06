package com.redhat.gss.skillmatrix.controllers.sorthelpers;

import com.redhat.gss.skillmatrix.controllers.sorthelpers.util.Filter;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
import com.redhat.gss.skillmatrix.model.GeoEnum;
import com.redhat.gss.skillmatrix.util.PaginationHelper;
import org.ajax4jsf.model.SequenceRange;
import org.richfaces.component.SortOrder;

import java.util.HashMap;

/**
 * Model helper class for {@link com.redhat.gss.skillmatrix.model.Member}. This class has two functions:
 * <ul>
 *     <li>contains a RichFaces model and pagination helper and enables the bond between the two</li>
 *     <li>takes care of orders and filters</li>
 * </ul>
 *
 * User: jtrantin
 * Date: 8/19/13
 * Time: 2:13 PM
 */
public abstract class MemberModelHelper {

    private HashMap<String, Filter<MemberProducer>> filtersOrders;

    private MembersModel model;
    private PaginationHelper pagination;


    // sorting
    private SortOrder name = SortOrder.unsorted;
    private SortOrder nick = SortOrder.unsorted;
    private SortOrder email = SortOrder.unsorted;
    private SortOrder experts = SortOrder.unsorted;
    private SortOrder role = SortOrder.unsorted;
    private SortOrder geo = SortOrder.unsorted;
    private SortOrder extension = SortOrder.unsorted;

    /**
     * This method must return valid {@link MemberProducer} implementation. The producer can already be modified (e.g.
     * some filters can be added).
     * @return valid MemberProducer impl.
     */
    protected abstract MemberProducer getProducerFactory();

    public MemberModelHelper(int recordsPerPage) {
        filtersOrders = new HashMap<String, Filter<MemberProducer>>();
        model = new MembersModel() {
            @Override
            protected MemberProducer getProducer() {
                return createMemberProducer();
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
                // set the right range after each change (like current page has changed, etc.)
                MemberModelHelper.this.model.setRange(range);
            }
        });


        //set initial range
        this.model.setRange(pagination.getRange());
    }


    private MemberProducer createMemberProducer() {
        MemberProducer producer = getProducerFactory();
        for(Filter filterOrder : filtersOrders.values()) {
            filterOrder.apply(producer); //apply all filters and orders
        }

        return producer;
    }


    /**
     *
     * @return filter for name
     */
    public String getNameFilter() {
        Filter nameFilter = filtersOrders.get("nameFilter");
        if(nameFilter == null)
            return "";

        return nameFilter.getValue();
    }

    /**
     * Sets filter for name.
     * @param nameFilter
     */
    public void setNameFilter(String nameFilter) {
        if(nameFilter==null)
            return;
        nameFilter = nameFilter.trim();
        if(nameFilter.isEmpty()) {
            filtersOrders.remove("nameFilter");
            return;
        }

        Filter filter = new Filter<MemberProducer>() {
            @Override
            public MemberProducer apply(MemberProducer producer) {
               return producer.filterName(this.value);
            }
        };
        filter.setValue(nameFilter);

        filtersOrders.put("nameFilter", filter);
    }

    /**
     *
     * @return filter for nick
     */
    public String getNickFilter() {
        Filter nickFilter = filtersOrders.get("nickFilter");
        if(nickFilter == null)
            return "";

        return nickFilter.getValue();
    }

    /**
     * Sets filter for nick
     * @param nickFilter
     */
    public void setNickFilter(String nickFilter) {
        if(nickFilter==null)
            return;
        nickFilter = nickFilter.trim();
        if(nickFilter.isEmpty()){
            filtersOrders.remove("nickFilter");
            return;
        }

        Filter filter = new Filter<MemberProducer>() {
            @Override
            public MemberProducer apply(MemberProducer producer) {
                return producer.filterNick(this.value);
            }
        };
        filter.setValue(nickFilter);

        filtersOrders.put("nickFilter", filter);
    }

    /**
     *
     * @return filter for email
     */
    public String getEmailFilter() {
        Filter emailFilter = filtersOrders.get("emailFilter");
        if(emailFilter == null)
            return "";

        return emailFilter.getValue();
    }

    /**
     * Sets filter for email.
     * @param emailFilter
     */
    public void setEmailFilter(String emailFilter) {
        if(emailFilter==null)
            return;
        emailFilter = emailFilter.trim();
        if(emailFilter.isEmpty()){
            filtersOrders.remove("emailFilter");
            return;
        }

        Filter filter = new Filter<MemberProducer>() {
            @Override
            public MemberProducer apply(MemberProducer producer) {
                return producer.filterEmail(this.value);
            }
        };
        filter.setValue(emailFilter);

        filtersOrders.put("emailFilter", filter);
    }

    public void setLanguagesFilter(String langFilter) {
        if(langFilter==null)
            return;
        langFilter = langFilter.trim();
        if(langFilter.isEmpty()){
            filtersOrders.remove("langFilter");
            return;
        }

        Filter filter = new Filter<MemberProducer>() {
            @Override
            public MemberProducer apply(MemberProducer producer) {
                String[] langs = this.value.split(",");
                for(String lang : langs) {
                    if(!lang.trim().isEmpty()) // split languages into separate strings and filter them individually
                        producer.filterLanguage(lang.trim());
                }

                return producer;
            }
        };
        filter.setValue(langFilter);

        filtersOrders.put("langFilter", filter);
    }

    public String getLanguagesFilter() {
        Filter langFilter = filtersOrders.get("langFilter");
        if(langFilter == null)
            return "";

        return langFilter.getValue();
    }

    public void setRoleFilter(String roleFilter) {
        if(roleFilter==null)
            return;
        roleFilter = roleFilter.trim();
        if(roleFilter.isEmpty()){
            filtersOrders.remove("roleFilter");
            return;
        }

        Filter filter = new Filter<MemberProducer>() {
            @Override
            public MemberProducer apply(MemberProducer producer) {
                return producer.filterRole(this.value);
            }
        };
        filter.setValue(roleFilter);

        filtersOrders.put("roleFilter", filter);
    }

    public String getRoleFilter() {
        Filter roleFilter = filtersOrders.get("roleFilter");
        if(roleFilter == null)
            return "";

        return roleFilter.getValue();
    }

    public void setGeoFilter(String filterGeostr) {
        if(filterGeostr==null || GeoEnum.parseGeo(filterGeostr)==null){
            filtersOrders.remove("geoFilter");
            return;
        }

        Filter filter = new Filter<MemberProducer>() {
            @Override
            public MemberProducer apply(MemberProducer producer) {
                return producer.filterGeo(GeoEnum.parseGeo(this.value));
            }
        };
        filter.setValue(filterGeostr);

        filtersOrders.put("geoFilter", filter);
    }

    public String getGeoFilter() {
        Filter geoFilter = filtersOrders.get("geoFilter");
        if(geoFilter == null)
            return null;

        return geoFilter.getValue();
    }

    public void setExtensionFilter(String extFilter) {
        if(extFilter==null)
            return;
        extFilter = extFilter.trim();
        if(extFilter.isEmpty()){
            filtersOrders.remove("extFilter");
            return;
        }

        Filter filter = new Filter<MemberProducer>() {
            @Override
            public MemberProducer apply(MemberProducer producer) {
                return producer.filterExtension(this.value);
            }
        };
        filter.setValue(extFilter);

        filtersOrders.put("extFilter", filter);
    }

    public String getExtensionFilter() {
        Filter extFilter = filtersOrders.get("extFilter");
        if(extFilter == null)
            return "";

        return extFilter.getValue();
    }



    //sorting
    /**
     * Sorts by name, sets all corresponding sort orders.
     */
    public void sortByName() {
        nick = email = experts = role = geo = extension = SortOrder.unsorted;
        if(SortOrder.ascending.equals(name)) {
            setName(SortOrder.descending);
        } else {
            setName(SortOrder.ascending);
        }

        this.filtersOrders.put("order", new Filter<MemberProducer>() {
            @Override
            public MemberProducer apply(MemberProducer producer) {
                return producer.sortName(name==SortOrder.ascending);
            }
        });

    }

    public void sortByRole() {
        name = nick = experts = geo = extension = email = SortOrder.unsorted;
        if(SortOrder.ascending.equals(role)) {
            setRole(SortOrder.descending);
        } else {
            setRole(SortOrder.ascending);
        }

        this.filtersOrders.put("order", new Filter<MemberProducer>() {
            @Override
            public MemberProducer apply(MemberProducer producer) {
                return producer.sortRole(role == SortOrder.ascending);
            }
        });
    }

    public void sortByGeo() {
        name = nick = experts = role = extension = email = SortOrder.unsorted;
        if(SortOrder.ascending.equals(geo)) {
            setGeo(SortOrder.descending);
        } else {
            setGeo(SortOrder.ascending);
        }

        this.filtersOrders.put("order", new Filter<MemberProducer>() {
            @Override
            public MemberProducer apply(MemberProducer producer) {
                return producer.sortGeo(geo == SortOrder.ascending);
            }
        });
    }

    public void sortByExtension() {
        name = nick = experts = geo = role = email = SortOrder.unsorted;
        if(SortOrder.ascending.equals(extension)) {
            setExtension(SortOrder.descending);
        } else {
            setExtension(SortOrder.ascending);
        }

        this.filtersOrders.put("order", new Filter<MemberProducer>() {
            @Override
            public MemberProducer apply(MemberProducer producer) {
                return producer.sortExtension(extension == SortOrder.ascending);
            }
        });
    }

    /**
     * Sorts by nick, sets all corresponding sort orders.
     */
    public void sortByNick() {
        name = email = experts = role = geo = extension =  SortOrder.unsorted;
        if(SortOrder.ascending.equals(nick)) {
            setNick(SortOrder.descending);
        } else {
            setNick(SortOrder.ascending);
        }

        this.filtersOrders.put("order", new Filter<MemberProducer>() {
            @Override
            public MemberProducer apply(MemberProducer producer) {
                return producer.sortNick(nick == SortOrder.ascending);
            }
        });
    }

    /**
     * Sorts by email, sets all corresponding sort orders.
     */
    public void sortByEmail()  {
        name = nick = experts = role = geo = extension =  SortOrder.unsorted;
        if(SortOrder.ascending.equals(email)) {
            setEmail(SortOrder.descending);
        } else {
            setEmail(SortOrder.ascending);
        }

        this.filtersOrders.put("order", new Filter<MemberProducer>() {
            @Override
            public MemberProducer apply(MemberProducer producer) {
                return producer.sortEmail(email == SortOrder.ascending);
            }
        });
    }

    /**
     * Sorts by experts, sets all corresponding sort orders.
     */
    public void sortByExperts() {
        name = nick = email = role = geo = extension =  SortOrder.unsorted;
        if(SortOrder.ascending.equals(experts)) {
            setExperts(SortOrder.descending);
        } else {
            setExperts(SortOrder.ascending);
        }

        this.filtersOrders.put("order", new Filter<MemberProducer>() {
            @Override
            public MemberProducer apply(MemberProducer producer) {
                return producer.sortKnowledgesAtLevel(2, experts == SortOrder.ascending);
            }
        });
    }


    //getters, setters, uninteresting
    /**
     * @return sort order for name.
     */
    public SortOrder getName() {
        return name;
    }
    /**
     * Sets sort order for name
     * @param name
     */
    public void setName(SortOrder name) {
        this.name = name;
    }

    /**
     * @return sort order for nick.
     */
    public SortOrder getNick() {
        return nick;
    }
    /**
     * Sets sort order for nick
     * @param nick
     */
    public void setNick(SortOrder nick) {
        this.nick = nick;
    }

    /**
     * @return sort order for email.
     */
    public SortOrder getEmail() {
        return email;
    }
    /**
     * Sets sort order for email
     * @param email
     */
    public void setEmail(SortOrder email) {
        this.email = email;
    }

    /**
     * @return sort order for experts.
     */
    public SortOrder getExperts() {
        return experts;
    }
    /**
     * Sets sort order for experts
     * @param experts
     */
    public void setExperts(SortOrder experts) {
        this.experts = experts;
    }

    /**
     * @return the role
     */
    public SortOrder getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(SortOrder role) {
        this.role = role;
    }

    /**
     * @return the geo
     */
    public SortOrder getGeo() {
        return geo;
    }

    /**
     * @param geo the geo to set
     */
    public void setGeo(SortOrder geo) {
        this.geo = geo;
    }

    /**
     * @return the extension
     */
    public SortOrder getExtension() {
        return extension;
    }

    /**
     * @param extension the extension to set
     */
    public void setExtension(SortOrder extension) {
        this.extension = extension;
    }

    /**
     *
     * @return RichFaces model.
     */
    public MembersModel getModel() {
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
