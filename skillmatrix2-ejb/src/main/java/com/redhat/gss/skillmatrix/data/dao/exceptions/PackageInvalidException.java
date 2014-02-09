package com.redhat.gss.skillmatrix.data.dao.exceptions;

import lombok.Getter;
import lombok.Setter;

import com.redhat.gss.skillmatrix.model.Package;

/**

 * User: jtrantin
 * Date: 8/15/13
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class PackageInvalidException extends Exception {

	@Getter @Setter
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
}
