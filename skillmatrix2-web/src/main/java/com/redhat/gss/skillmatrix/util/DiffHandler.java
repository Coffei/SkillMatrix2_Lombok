package com.redhat.gss.skillmatrix.util;

import com.redhat.gss.skillmatrix.data.imports.diffs.Diff;
import com.redhat.gss.skillmatrix.data.imports.diffs.DiffCreator;
import com.redhat.gss.skillmatrix.data.imports.diffs.DiffCreatorType;
import com.redhat.gss.skillmatrix.data.imports.diffs.DiffException;
import org.reflections.Reflections;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RequestScoped
public class DiffHandler {

	@Inject
	private Reflections reflections;
	
	@Inject
	private EntityManager em;

	@Inject
	private Logger log;

	public List<Diff<?>> createDiffs(List<List<?>> listOfentities) throws DiffException {
		List<Diff<?>> diffs = new ArrayList<Diff<?>>();
		for (List<?> entities : listOfentities) {
			Class<?> entityType = entities.get(0).getClass();

			DiffCreator<?> creator = findDiffCreator(entityType);
			creator.setEntityManager(em);
			diffs.add(creator.createDiff(entities));
		}
		
		
		return diffs;
	}

	private DiffCreator<?>  findDiffCreator(Class<?> entityType) throws DiffException {
		for (Class<?> diffCreator : reflections.getTypesAnnotatedWith(DiffCreatorType.class)) {
			DiffCreatorType annotation = diffCreator.getAnnotation(DiffCreatorType.class);
			log.info("Found class " + diffCreator.getName());
			
			if(annotation.forClass().equals(entityType)) {// found the right creator
				try {
					return (DiffCreator<?>)diffCreator.newInstance();
				} catch (Exception e) {
					throw new DiffException("error creating DiffCreator instance", e);
				}
			}
		}

		return null;
	}

}
