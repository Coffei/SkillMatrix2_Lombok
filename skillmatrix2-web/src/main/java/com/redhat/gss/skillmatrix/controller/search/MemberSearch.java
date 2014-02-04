package com.redhat.gss.skillmatrix.controller.search;


import com.redhat.gss.skillmatrix.controller.search.filter.Filter;
import com.redhat.gss.skillmatrix.controller.search.filter.FilterType;
import com.redhat.gss.skillmatrix.controller.search.filter.MemberFilter;
import com.redhat.gss.skillmatrix.controller.search.filter.filters.util.AttributeEncoder;
import com.redhat.gss.skillmatrix.data.dao.producers.util.OperatorEnum;
import com.redhat.gss.skillmatrix.model.GeoEnum;
import org.reflections.Reflections;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 12/3/13
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class MemberSearch {

    @Inject
    private Logger log;

    @Inject
    private Reflections reflections;

    private SortedMap<String, Class<?>> availableBasicFilters;
    //should be list of filters, but we need to have
    private List<Filter> basicFilters;
    private List<String> basicFiltersNames;

    @PostConstruct
    private void init() {
        //create collections
        availableBasicFilters = new TreeMap<String, Class<?>>();
        basicFilters = new ArrayList<Filter>();
        basicFiltersNames = new FilterNameList(basicFilters, availableBasicFilters);

        //find all basic filters on CP
        for(Class<?> c : reflections.getTypesAnnotatedWith(MemberFilter.class)) {
            MemberFilter filter = c.getAnnotation(MemberFilter.class);
            if(filter.type().equals(FilterType.BASIC)) {
                availableBasicFilters.put(filter.name(), c);
            }
        }
    }


    public String submit() {
        StringBuilder targetURL = new StringBuilder("searchResult.jsf?faces-redirect=true");
        int i = 0;
        for (Filter filter : basicFilters) {
            targetURL.append("&filter");
            targetURL.append(i); //&filterX=...
            targetURL.append("=");

            targetURL.append(filter.encode());
            i++;
        }


        log.info("search url = " + targetURL.toString());
        return targetURL.toString();
    }


    public SortedMap<String, Class<?>> getAvailableBasicFilters() {
        return availableBasicFilters;
    }

    public void addBasicFilter() {
        basicFiltersNames.add(null);
    }


    public void removeBasicFilter(int index) {
        basicFiltersNames.remove(index);
    }

    public List<Filter> getBasicFilters() {
        return basicFilters;
    }

    public List<String> getBasicFiltersNames() {
        return basicFiltersNames;
    }



    public String resolve(Filter filter) {
        
        if(filter==null) {
            System.out.println("resolved null");
            return null;
        }

        MemberFilter annt = filter.getClass().getAnnotation(MemberFilter.class);
        System.out.println("resolved filters/" + annt.page());
        return "filters/" + annt.page();
    }

    public Map<String, String> getAllLevels() {
        Map<String, String> levels = new HashMap<String, String>(3);
        levels.put("beginner", "0");
        levels.put("intermediate", "1");
        levels.put("expert", "2");

        return levels;
    }

    public List<OperatorEnum> getAllOperators() {
        return Arrays.asList(OperatorEnum.values());
    }


    private static class FilterNameList extends AbstractList<String> {

        private List<Filter> backingList;
        private Map<String, Class<?>> allFilters;

        public FilterNameList(List<Filter> backingList, Map<String, Class<?>> allFilters) {
            this.backingList = backingList;
            this.allFilters = allFilters;
        }


        @Override
        public String get(int i) {
            Filter filter = backingList.get(i);
            if(filter!=null) {
                MemberFilter annt = filter.getClass().getAnnotation(MemberFilter.class);
                return annt.name();
            } else {
                return null;
            }

        }

        @Override
        public int size() {
            return backingList.size();

        }

        @Override
        public String set(int index, String element) {
            if(element==null || element.trim().isEmpty()) {
                Filter filter = backingList.set(index, null);
                printBackingList();
                if(filter!=null) {
                    MemberFilter annt = filter.getClass().getAnnotation(MemberFilter.class);
                    return annt.name();
                } else {
                    return null;
                }
            }
            Class<?> filterClass = allFilters.get(element);
            if(filterClass!=null) {
                try {
                    Filter filter =  backingList.set(index,(Filter)filterClass.newInstance());
                    printBackingList();
                    if(filter!=null) {
                        MemberFilter annt = filter.getClass().getAnnotation(MemberFilter.class);
                        return annt.name();
                    } else {
                        return null;
                    }
                } catch (InstantiationException e) {
                    //TODO: handle this exception
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    //TODO: handle this exception
                    e.printStackTrace();
                }
            } else {
                //TODO: log this situation, unsupported filter found
            }
            return null;
        }

        @Override
        public void add(int index, String element) {
            if(element==null || element.trim().isEmpty()) {
                backingList.add(index, null);
                return;
            }
            Class<?> filterClass = allFilters.get(element);
            if(filterClass!=null) {
                try {
                    backingList.add(index,(Filter)filterClass.newInstance());
                } catch (InstantiationException e) {
                    //TODO: handle this exception
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    //TODO: handle this exception
                    e.printStackTrace();
                }
            } else {
                //TODO: log this situation, unsupported filter found
            }

        }

        @Override
        public String remove(int index) {
            Filter filter = backingList.remove(index);
            if(filter!=null) {
                MemberFilter annt = filter.getClass().getAnnotation(MemberFilter.class);
                return annt.name();
            } else {
                return null;
            }
        }

        private void printBackingList() {
            System.out.println("Filters:");
            for (Filter filter : backingList) {
                if(filter==null)
                    System.out.println("   null");
                else
                    System.out.println("   " + filter.getClass().getName());
            }
        }
    }



}
