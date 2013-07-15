package com.redhat.gss.skillmatrix.data.imports.diffs;

import com.redhat.gss.skillmatrix.model.Package;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@DiffCreatorType(forClass=Package.class)
@Stateless
public class PackageDiffCreator implements DiffCreator<Package> {

	private static final int BATCH_SIZE = 100;

	private EntityManager em;


	@Override
	public Diff<Package> createDiff(List<?> entities) {


		long pkgCount = getPackagesCount();
		int steps = (int)(pkgCount / BATCH_SIZE) + ((pkgCount % BATCH_SIZE) == 0? 0 : 1);

		List<Package> newPackages = convertList(entities);
		Diff<Package> diff = new Diff<Package>();
		diff.setAdded(new ArrayList<Package>(newPackages));
		diff.setDeprecated(new ArrayList<Package>());
		diff.setUndeprecated(new ArrayList<Package>());
		for(int i = 0; i <  steps; i++) {
			List<Package> oldPackages = fetchPackages(i * BATCH_SIZE, BATCH_SIZE);
			for (Package pkg : oldPackages) {
				if(isInList(newPackages, pkg)) {
					removeFromList(diff.getAdded(), pkg);
					if(pkg.isDeprecated()) {
						diff.getUndeprecated().add(pkg);
					}
				} else {
					if(!pkg.isDeprecated()) {
						diff.getDeprecated().add(pkg);
					}
				}
			}
		}

		return diff;
	}

	private long getPackagesCount() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);

		Root<Package> pkg = criteria.from(Package.class);
		criteria.select(cb.count(pkg));

		return em.createQuery(criteria).getSingleResult();
	}

	private List<Package> convertList(List<?> entities) {
		List<Package> packages = new ArrayList<Package>();
		for (Object object : entities) {
			if(object instanceof Package) {
				packages.add((Package) object);
			}
		}

		return packages;
	}

	private List<Package> fetchPackages(int from, int howmany) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Package> criteria = cb.createQuery(Package.class);

		Root<Package> pkg = criteria.from(Package.class);
		criteria.select(pkg);

		TypedQuery<Package> query = em.createQuery(criteria);
		query.setFirstResult(from);
		query.setMaxResults(howmany);

		return query.getResultList();
	}

	private boolean isInList(List<Package> list, Package element) {
		for (Package pkg : list) {
			if(pkg.getName().equals(element.getName()))
				return true;
		}

		return false;
	}

	private void removeFromList(List<Package> list, Package element) {
		for (Iterator<Package> iterator = list.iterator(); iterator.hasNext();) {
			Package pkg = iterator.next();

			if(pkg.getName().equals(element.getName())) {
				iterator.remove();
				return;
			}
		}
	}

	@Override
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}



}
