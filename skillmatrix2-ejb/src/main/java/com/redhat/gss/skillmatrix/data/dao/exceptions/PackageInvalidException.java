package com.redhat.gss.skillmatrix.data.dao.exceptions;

import com.redhat.gss.skillmatrix.model.Member;

/**

 * User: jtrantin
 * Date: 8/15/13
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class PackageInvalidException extends Exception {

    private Package pkg;

    public PackageInvalidException() {
        super();
    }

    public PackageInvalidException(Package pkg) {
        super();
        this.pkg = pkg;
    }

    public PackageInvalidException(String message) {
        super(message);
    }

    public PackageInvalidException(String message, Package pkg) {
        super(message);
        this.pkg = pkg;
    }

    public PackageInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public PackageInvalidException(String message, Throwable cause, Package pkg) {
        super(message, cause);
        this.pkg = pkg;
    }

    public PackageInvalidException(Throwable cause) {
        super(cause);
    }

    public Package getPkg() {
        return pkg;
    }

    public void setPkg(Package pkg) {
        this.pkg = pkg;
    }
}
