package com.redhat.gss.skillmatrix.data.dao.exceptions;

import com.redhat.gss.skillmatrix.model.Member;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 8/6/13
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class MemberInvalidException extends Exception {

    private Member invalidMember;

    public MemberInvalidException() {
        super();
    }

    public MemberInvalidException(Member invalidMember) {
        super();
        this.invalidMember = invalidMember;
    }

    public MemberInvalidException(String message) {
        super(message);
    }

    public MemberInvalidException(String message, Member invalidMember) {
        super(message);
        this.invalidMember = invalidMember;
    }

    public MemberInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberInvalidException(String message, Throwable cause, Member invalidMember) {
        super(message, cause);
        this.invalidMember = invalidMember;
    }

    public MemberInvalidException(Throwable cause) {
        super(cause);
    }

    public Member getInvalidMember() {
        return invalidMember;
    }

    public void setInvalidMember(Member invalidMember) {
        this.invalidMember = invalidMember;
    }
}
