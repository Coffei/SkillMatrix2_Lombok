package com.redhat.gss.skillmatrix.data.imports.diffs;

import javax.persistence.EntityManager;
import java.util.List;


public interface DiffCreator<T> {

	Diff<T> createDiff(List<?> entities);
	
	void setEntityManager(EntityManager em);
	
}
