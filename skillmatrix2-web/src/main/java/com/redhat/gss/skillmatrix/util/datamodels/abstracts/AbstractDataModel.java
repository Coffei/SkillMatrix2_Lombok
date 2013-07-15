package com.redhat.gss.skillmatrix.util.datamodels.abstracts;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.richfaces.component.SortOrder;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Abstract model class for easier implementation of ExtendedDataModel<T>.
 * @author jtrantin
 *
 * @param <T> type parameter of ExtendedDataModel class, type of the result object.
 * @param <V> type parameter representing return type of CriteriaQueries (eg. query only for ids,...).
 */
public abstract class AbstractDataModel<T,V> extends ExtendedDataModel<T>  {
	private static final Predicate[] PREDICATE_EMPTY_ARRAY = new Predicate[0];

	private EntityManager em;

	private Object rowKey;

	private Boolean isEmpty;

	//order and filter
	private String orderAttrName;
	private SortOrder order;
	private Map<String, Object> filters;

	private boolean nonString;

	/**
	 * Constructor
	 * @param em entity manager
	 */
	public AbstractDataModel(EntityManager em) {
		this.em = em;
	}


	/**
	 * True if this model is empty- if it never returns a record when no filtering is applied.
	 * @return
	 */
	public Boolean isEmpty() {
		if(isEmpty == null)
			evaluateEmpty();

		return isEmpty;
	}

	private void evaluateEmpty() {
		CriteriaQuery<Long> criteria = createCountCriteriaQuery();

		List<Predicate> predicates = getExistingPredicates();
		if(predicates!=null && !predicates.isEmpty()) {
			criteria.where(predicates.toArray(PREDICATE_EMPTY_ARRAY));
		}

		isEmpty = em.createQuery(criteria).getSingleResult() == 0;		
	}

	@Override
	public Object getRowKey() {
		return rowKey;
	}

	@Override
	public void setRowKey(Object arg0) {
		this.rowKey = arg0;
	}

	private void addFilterPredicates(List<Predicate> predicates) {

		if(filters!=null) {
			for(Map.Entry<String, Object> entry : filters.entrySet()) {

				if(!entry.getKey().isEmpty() && entry.getValue() != null && (!(entry.getValue() instanceof String) || !((String)entry.getValue()).trim().isEmpty())) { // ensures that some filtering is needed
					predicates.add(createPredicate(entry));
				}
			}
		}
	}

	protected Predicate createPredicate(Map.Entry<String, Object> values) {
		Object filterValue = values.getValue();
		Root<T> root = getRoot();
		CriteriaBuilder cb = em.getCriteriaBuilder();

		String attrName = values.getKey();

		if(filterValue instanceof String) {
			String filter = (String)filterValue;

			Predicate p = cb.like(cb.lower(findPath(root, attrName).as(String.class)), "%" + filter.toLowerCase(Locale.ENGLISH) + "%");

			return p;
		} else {
			Predicate p = cb.equal(findPath(root, attrName), filterValue);

			return p;
		}
	}

	/**
	 * Adds ordering to specified criteria Query.
	 * @param query
	 */
	private <K> void addOrdering(CriteriaQuery<K> query) {
		if(orderAttrName != null && !orderAttrName.isEmpty() && order != null && !order.equals(SortOrder.unsorted)) { // ensures that some ordering is needed
			Root<T> root = getRoot();
			String attrName = this.orderAttrName;

			Path<?> attribute = findPath(root, attrName);
			CriteriaBuilder cb = em.getCriteriaBuilder();

			if(this.order.equals(SortOrder.ascending)) { 
				if(nonString) {
					query.orderBy(cb.asc(attribute));
				} else {
					query.orderBy(cb.asc(cb.lower(attribute.as(String.class))));
				}
			} else if (this.order.equals(SortOrder.descending)) {
				if(nonString) {
					query.orderBy(cb.desc(attribute));
				} else {
					query.orderBy(cb.desc(cb.lower(attribute.as(String.class))));
				}
			}
		}
	}

	private Path<?> findPath(Root<T> root, String attrName) {
		Path<?> path = null;
		for (String attr : attrName.split("\\.")) {

			if(path==null) {
				path = root.get(attr);
			} else {
				path = path.get(attr);
			}
		}

		return path;
	}

	private <K> TypedQuery<K> processCriteriaQuery(CriteriaQuery<K> criteria, boolean isCount) {
		if(criteria==null)
			throw new NullPointerException("criteria");

		List<Predicate> predicates = new ArrayList<Predicate>(getExistingPredicates());
		addFilterPredicates(predicates); // load predicates, add filtering

		criteria.where(predicates.toArray(PREDICATE_EMPTY_ARRAY)); // create query

		if(!isCount) {
			addOrdering(criteria);
			postProcessCriteria((CriteriaQuery<V>) criteria);
		}

		return em.createQuery(criteria);
	}

	/**
	 * Adds predicates and ordering to criteria query, returns typed query created from criteria query.
	 * @param criteria
	 * @return
	 */
	private <K> TypedQuery<K> processCriteriaQuery(CriteriaQuery<K> criteria) {
		return processCriteriaQuery(criteria, false);
	}

	@Override
	public void walk(FacesContext ctx, DataVisitor visitor, Range rangeObject,
			Object arg) {

		TypedQuery<V> query = processCriteriaQuery(createCriteriaQuery());

		//set pagination params
		SequenceRange range = (SequenceRange) rangeObject;
		if(range.getFirstRow() >= 0 && range.getRows() > 0) {
			query.setMaxResults(range.getRows());
			query.setFirstResult(range.getFirstRow());
		}

		// perform visit
		List<V> results = query.getResultList();
		for (V v : results) {
			visitor.process(ctx, getId(v), arg);
		}

	}

	/**
	 * Sort results by {@code attrName} attribute in the order of {@code order}
	 * @param attrName name of entities attribute to sort by
	 * @param order order of sort
	 */
	public void doSort(String attrName, SortOrder order) {
		doSort(attrName, order, false);
	}

	public void doSort(String attrName, SortOrder order, boolean nonString) {
		this.orderAttrName = attrName;
		this.order = order;
		this.nonString = nonString;
	}

	/**
	 * Filter results by {@code attrName} which must contain {@code filter}
	 * @param attrName name of entities attribute to filter
	 * @param filter filter content
	 */
	public void doFilter(Map<String, Object> filters) {
		this.filters = filters;
	}

	@Override
	public int getRowCount() {
		return processCriteriaQuery(createCountCriteriaQuery(), true).getSingleResult().intValue();
	}

	@Override
	public int getRowIndex() {
		return -1;
	}

	@Override
	public Object getWrappedData() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRowAvailable() {
		return rowKey!=null;
	}

	@Override
	public void setRowIndex(int arg0) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setWrappedData(Object arg0) {
		throw new UnsupportedOperationException();

	}

	//abstracts
	/**
	 * Creates CriteriaQuery for record retrieval.
	 * CriteriaQuery must have select and from set up, all added predicates will be replaced.
	 * Use {@code getExistingPredicates()} to add predicates.
	 * @return valid CriteriaQuery with select and from set up.
	 * @see #getExistingPredicates()
	 */
	protected abstract CriteriaQuery<V> createCriteriaQuery();

	/**
	 * Creates CriteriaQuery for number of records.
	 * CriteriaQuery must have select and from set up, all added predicates will be replaced.
	 * Use {@code getExistingPredicates()} to add predicates.
	 * @return valid CriteriaQuery with select and from set up.
	 * @see #getExistingPredicates()
	 */
	protected abstract CriteriaQuery<Long> createCountCriteriaQuery();

	/**
	 * Returns root of the last retrieved query. All filter and ordering operations are run on this root.
	 * @return root of query
	 */
	protected abstract Root<T> getRoot();

	/**
	 * Returns list of predicates, that should be applied to the last retrieved query.
	 * @return list of predicates, may not be null
	 */
	protected abstract List<Predicate> getExistingPredicates();

	/**
	 * Returns entities ID.
	 * @param t entity
	 * @return ID, primary key
	 */
	protected abstract Object getId(V v);

	/**
	 * Method called before the query is computed. 
	 * By implementing this method, child classes can process, modify the criteria
	 * just before evaluation, after everything is applied and the criteria is complete. 
	 * @param criteria complete criteriaquery
	 */
	protected abstract void postProcessCriteria(CriteriaQuery<V> criteria);


}