package com.redhat.gss.skillmatrix.data.imports.parser;

import java.util.List;

public interface FileParser {
	
	List<?> parse(String[] lines);
	
	
}
