package com.redhat.gss.skillmatrix.data.dao.interfaces;

import com.redhat.gss.skillmatrix.data.dao.exceptions.PackageInvalidException;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.PackageProducer;
import com.redhat.gss.skillmatrix.model.Package;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 8/15/13
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PackageDAO {
    /**
     * Returns {@link PackageProducer} impl, that is compatible with this DAO.
     * @return
     */
    PackageProducer getProducerFactory();

    /**
     * Creates and persists a Package into the datastore. This operation might not be supported.
     * @param pkg Package to be created.
     * @throws com.redhat.gss.skillmatrix.data.dao.exceptions.PackageInvalidException when the pkg is invalid for some
     * reason and cannot be created.
     * @throws UnsupportedOperationException when this operation is not supported by the implementation.
     * @see #canModify()
     */
    void create(Package pkg) throws PackageInvalidException;

    /**
     * Updates a Package in the datastore. This operation might not be supported.
     * @param pkg Package to be updated.
     * @throws PackageInvalidException when the pkg is invalid for some reason and cannot be updated.
     * @throws UnsupportedOperationException when this operation is not supported by the implementation.
     * @see #canModify()
     */
    void update(Package pkg) throws PackageInvalidException;

    /**
     * Deletes a Package from datastore. This operation might not be supported.
     * @param pkg Package to be deleted.
     * @throws PackageInvalidException when the pkg is invalid for some reason and cannot be deleted.
     * @throws UnsupportedOperationException when this operation is not supported by this implementation.
     * @see #canModify()
     */
    void delete(Package pkg) throws PackageInvalidException;

    /**
     * @return true if the content can be modified, false if not.
     * if true is returned, then methods {@link #create(com.redhat.gss.skillmatrix.model.Package)}, {@link #update(com.redhat.gss.skillmatrix.model.Package)} and {@link #delete(com.redhat.gss.skillmatrix.model.Package)}
     * can be called and should not throw {@link UnsupportedOperationException}.
     */
    boolean canModify();

}
