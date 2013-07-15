package com.redhat.gss.skillmatrix.util;

import com.google.common.collect.Lists;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.util.datamodels.AllPackagesModel;
import org.richfaces.component.SortOrder;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Holder for memberform. Used to hold certain values across requests.
 * @author jtrantin
 *
 */
@ManagedBean
@ViewScoped
public class MemberformHolder implements Serializable {
	private static final long serialVersionUID = -3396633999804175984L;

	@Inject
	private transient List<Package> packages;

	@Inject
	private transient Logger log;

	private Map<Package, Integer> knowledge;
	
	private List<String> languages;

	private List<Long> sbrs;


	// getters, setters
	/**
	 * Returns map of package and corresponding selected knowledge.
	 * @return
	 */
	public Map<Package, Integer> getKnowledge() {
		if(knowledge == null)
			reload(null);

        return knowledge;
	}

	/**
	 * Setter for {@code getKnowledge() }
	 * @param knowledge
	 * @see #getKnowledge()
	 */
	public void setKnowledge(Map<Package, Integer> knowledge) {
		this.knowledge = knowledge;
	}

	/**
	 * Returns list of Packages that have associated valid knowledge in
	 * the knowledge map.
	 * @return
	 */
	public List<Package> getPackagesWithKnowledge() {
		if(knowledge == null)
			reload(null);
		
		List<Package> result = Lists.newArrayList();
      	for (Map.Entry<Package, Integer> entry : knowledge.entrySet()) {
      		if(entry.getValue()!=null && entry.getValue() >= 0) {
      			result.add(entry.getKey());
			}
		}

		return result;
	}

	/**
	 * Returns members SBRs sorted by level (at index zero is first SBR,...).
	 * @return list of SBR IDs, can be empty
	 */
	public List<Long> getSbrs() {
		if(sbrs==null)
			reload(null);

		return sbrs;
	}

	//actions

	/**
	 * Loads holder with values of specified member.
	 * @param member member to load, if null values for 'new member' are loaded.
	 */
	public void loadWith(Member member) {
		reload(member);
	}

	/**
	 * Sort package by level of knowledge, ordering is not complete, 
	 * those with some knowledge (at any level) are preferred.
	 * @param model model to which should this be applied, not null.
	 */
	public void sortByLevel(AllPackagesModel model) {
		if(model!=null) { 
			model.setPreferred(getPackagesWithKnowledge());
            log.info(getPackagesWithKnowledge().toString());
			model.doSort("pref", SortOrder.ascending);
		}
	}

	/**
	 * Resets the holder, equivalent to {@code this.loadWith(null)}
	 * @see #loadWith(Member)
	 */
	public void reset() {
		reload(null);
	}


	// helpers

	private void reload(Member member) {
        log.info("reloading member: " + member);
		sbrs = Lists.newArrayList();
		knowledge = new HashMap<Package, Integer>();
		languages = Lists.newArrayList();

		if(member!=null) {
			if(member!=null && member.getKnowledges()!=null) { 

				for (Knowledge k : member.getKnowledges()) {
					if(k instanceof PackageKnowledge) {
						knowledge.put(((PackageKnowledge)k).getPackage(), k.getLevel());
					} else if (k instanceof LanguageKnowledge) {
						languages.add(((LanguageKnowledge)k).getLanguage());
					}

				}
			}

			Collections.sort(member.getMembersbrs(), new Comparator<MemberSbr>(){ //sort membersbrs to be in the order of ascending level
				@Override
				public int compare(MemberSbr arg0, MemberSbr arg1) {
					return arg0.getLevel() - arg1.getLevel();
				}
			});


			for (MemberSbr memberSbr : member.getMembersbrs()) {
				sbrs.add(memberSbr.getSbr().getId());
			}

		}

		if(sbrs.isEmpty())
			sbrs.add(null);
	}

	/**
	 * @return the languages
	 */
	public List<String> getLanguages() {
		if(languages==null)
			reload(null);
		return languages;
	}

	/**
	 * @param languages the languages to set
	 */
	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}


    private static class LogMap<K,V> implements Map<K,V> {

        private HashMap<K,V> backingMap;
        private Logger log;

        public LogMap(Logger log) {
            backingMap = new HashMap<K, V>();
            this.log = log;
        }

        @Override
        public int size() {
            return backingMap.size();
        }

        @Override
        public boolean isEmpty() {
            return backingMap.isEmpty();
        }

        @Override
        public boolean containsKey(Object o) {
            return backingMap.containsKey(o);
        }

        @Override
        public boolean containsValue(Object o) {
            return backingMap.containsValue(o);
        }

        @Override
        public V get(Object o) {
            return backingMap.get(o);
        }

        @Override
        public V put(K k, V v) {
            log.info("put " + k + ": " + v);
            return backingMap.put(k,v);
        }

        @Override
        public V remove(Object o) {
            return backingMap.remove(o);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> map) {
            backingMap.putAll(map);
        }

        @Override
        public void clear() {
            backingMap.clear();
        }

        @Override
        public Set<K> keySet() {
            return backingMap.keySet();
        }

        @Override
        public Collection<V> values() {
            return backingMap.values();
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return backingMap.entrySet();
        }
    }
	
}
