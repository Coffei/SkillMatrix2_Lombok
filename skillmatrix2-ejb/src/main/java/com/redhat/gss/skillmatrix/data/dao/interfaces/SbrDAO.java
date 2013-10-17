package com.redhat.gss.skillmatrix.data.dao.interfaces;

import com.redhat.gss.skillmatrix.data.dao.exceptions.SbrInvalidException;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.SbrProducer;
import com.redhat.gss.skillmatrix.model.SBR;

/**
 * SBR DAO interface. Should be used exclusively via this interface.
 * User: jtrantin
 * Date: 8/14/13
 * Time: 9:00 AM
 */
public interface SbrDAO {

    /**
     * Returns SbrProducer impl, that is compatible with this DAO.
     * @return
     */
    SbrProducer getProducerFactory();

    /**
     * Creates and persists an SBR into the datastore. This operation might not be supported.
     * @param sbr SBR to be created.
     * @throws SbrInvalidException when the sbr is invalid for some reason and cannot be created.
     * @throws UnsupportedOperationException when this operation is not supported by the implementation.
     * @see #canModify()
     */
    void create(SBR sbr) throws SbrInvalidException;

    /**
     * Updates an SBR in the datastore. This operation might not be supported.
     * @param sbr SBR to be updated.
     * @throws SbrInvalidException when the sbr is invalid for some reason and cannot be updated.
     * @throws UnsupportedOperationException when this operation is not supported by the implementation.
     * @see #canModify()
     */
    void update(SBR sbr) throws SbrInvalidException;

    /**
     * Deletes an SBR from datastore. This operation might not be supported.
     * @param sbr SBR to be deleted.
     * @throws SbrInvalidException when the sbr is invalid for some reason and cannot be deleted.
     * @throws UnsupportedOperationException when this operation is not supported by this implementation.
     */
    void delete(SBR sbr) throws SbrInvalidException;

    /**
     * @return true if the content can be modified, false if not.
     * if true is returned, then methods {@link #create(com.redhat.gss.skillmatrix.model.SBR)}, {@link #update(com.redhat.gss.skillmatrix.model.SBR)} and {@link #delete(com.redhat.gss.skillmatrix.model.SBR)}
     * can be called and should not throw {@link UnsupportedOperationException}.
     */
    boolean canModify();
}
