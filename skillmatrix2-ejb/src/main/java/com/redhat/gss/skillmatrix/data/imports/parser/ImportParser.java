package com.redhat.gss.skillmatrix.data.imports.parser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportParser {
	public String nameOfType();
}
