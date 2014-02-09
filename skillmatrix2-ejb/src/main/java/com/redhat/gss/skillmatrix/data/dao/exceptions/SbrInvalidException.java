package com.redhat.gss.skillmatrix.data.dao.exceptions;

import lombok.Getter;
import lombok.Setter;

import com.redhat.gss.skillmatrix.model.SBR;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 8/6/13
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class SbrInvalidException extends Exception {

	@Getter @Setter
    private SBR invalidSbr;

    public SbrInvalidException() {
        super();
    }

    public SbrInvalidException(SBR invalidSbr) {
        super();
        this.invalidSbr = invalidSbr;
    }

    public SbrInvalidException(String message) {
        super(message);
    }

    public SbrInvalidException(String message, SBR invalidSbr) {
        super(message);
        this.invalidSbr = invalidSbr;
    }

    public SbrInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public SbrInvalidException(String message, Throwable cause, SBR invalidSbr) {
        super(message, cause);
        this.invalidSbr = invalidSbr;
    }

    public SbrInvalidException(Throwable cause) {
        super(cause);
    }
}
