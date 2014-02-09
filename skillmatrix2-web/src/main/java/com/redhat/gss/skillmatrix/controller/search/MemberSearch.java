package com.redhat.gss.skillmatrix.controller.search;


import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import lombok.Getter;

import org.reflections.Reflections;

import com.redhat.gss.skillmatrix.controller.search.filter.Filter;
import com.redhat.gss.skillmatrix.controller.search.filter.FilterType;
import com.redhat.gss.skillmatrix.controller.search.filter.MemberFilter;
import com.redhat.gss.skillmatrix.data.dao.producers.util.OperatorEnum;

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

    @Getter
    private SortedMap<String, Class<?>> availableBasicFilters;
    @Getter
    private SortedMap<String, Class<?>> availableAdvancedFilters;
    //should be list of filters, but we need to have
    @Getter
    private List<Filter> basicFilters;
    @Getter
    private List<String> basicFiltersNames;

    @Getter
    private List<Filter> advancedFilters;
    @Getter
    private List<String> advancedFiltersNames;



    @PostConstruct
    private void init() {
        //create collections- basic
        availableBasicFilters = new TreeMap<String, Class<?>>();
        basicFilters = new ArrayList<Filter>();
        basicFiltersNames = new FilterNameList(basicFilters, availableBasicFilters);
        //advanced
        availableAdvancedFilters = new TreeMap<String, Class<?>>();
        advancedFilters = new ArrayList<Filter>();
        advancedFiltersNames = new FilterNameList(advancedFilters, availableAdvancedFilters);

        //find all basic filters on CP
        for(Class<?> c : reflections.getTypesAnnotatedWith(MemberFilter.class)) {
            MemberFilter filter = c.getAnnotation(MemberFilter.class);
            if(filter.type().equals(FilterType.BASIC)) {
                availableBasicFilters.put(filter.name(), c);
            } else if (filter.type().equals(FilterType.ADVANCED)) {
                availableAdvancedFilters.put(filter.name(), c);
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
        for (Filter filter : advancedFilters) {
            targetURL.append("&filter");
            targetURL.append(i); //&filterX=...
            targetURL.append("=");

            targetURL.append(filter.encode());
            i++;
        }


        log.info("search url = " + targetURL.toString());
        return targetURL.toString();
    }



    public void addBasicFilter() {
        basicFiltersNames.add(null);
    }
    public void addAdvancedFilter() {
        advancedFiltersNames.add(null);
    }


    public void removeBasicFilter(int index) {
        basicFiltersNames.remove(index);
    }

    public void removeAdvancedFilter(int index) {
        advancedFiltersNames.remove(index);
    }

   


    public String resolve(Filter filter) {
        
        if(filter==null) {
            return null;
        }

        MemberFilter annt = filter.getClass().getAnnotation(MemberFilter.class);
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
        private final Logger log = Logger.getLogger(getClass().getName());

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
                //check if there already isn't the correct element, in that case do not recreate
                Filter oldFilter = backingList.get(index);
                if(oldFilter!=null && filterClass.equals(oldFilter.getClass()))
                    return element;

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
                    log.severe(String.format("Unable to instantiate a filter. %s\n%s",e.toString(),Arrays.toString(e.getStackTrace())));
                } catch (IllegalAccessException e) {
                    log.severe(String.format("Not allowed to instantiate a filter. %s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
                }
            } else {
                log.severe(String.format("Unsupported filter found! Filter type name: %s", element));
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
                    log.severe(String.format("Unable to instantiate a filter. %s\n%s",e.toString(),Arrays.toString(e.getStackTrace())));
                } catch (IllegalAccessException e) {
                    log.severe(String.format("Not allowed to instantiate a filter. %s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
                }
            } else {
                log.severe(String.format("Unsupported filter found! Filter type name: %s", element));
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
            log.info("Filters:");
            for (Filter filter : backingList) {
                if(filter==null)
                    log.info("    null");
                else
                    log.info("    " + filter.getClass().getName());
            }
        }
    }



}
