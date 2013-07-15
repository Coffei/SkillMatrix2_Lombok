package com.redhat.gss.skillmatrix.data;

import com.redhat.gss.skillmatrix.model.Knowledge;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * CRUD Manager for Knowledge.
 * @author jtrantin
 *
 */
public class KnowledgeManager {

		@Inject
		private EntityManager em;

		@Inject
		private Event<Knowledge> event;


		/**
		 * Writes unmanaged entity to context, and makes it managed and persistent.
		 * @param know unmanaged entity.
		 */
		public void create(Knowledge know) {
			if(know==null)
				throw new NullPointerException("know");
			
			em.persist(know);
			event.fire(know);
		}

		/**
		 * Updates existing managed (or creates unmanaged) entity.
		 * @param know managed or unmanaged entity
		 */
		public void update(Knowledge know) {
			if(know==null)
				throw new NullPointerException("know");
			
			em.merge(know);
			event.fire(know);
		}

		/**
		 * Deletes managed or unmanaged (then it is merged) entity from persistence context.
		 * @param know entity to be deleted
		 */
		public void delete(Knowledge know) {
			if(know==null)
				throw new NullPointerException("know");
			
			if(!em.contains(know)) {
				know = em.merge(know);
			}
			em.remove(know);
			event.fire(know);
		}
		
		/**
		 * Retrieves Knowledge by its ID
		 * @param id id of package to retrieve
		 * @return Knowledge instance or null if not found
		 */
		public Knowledge getByID(long id) {
			return em.find(Knowledge.class, id);
		}

}
