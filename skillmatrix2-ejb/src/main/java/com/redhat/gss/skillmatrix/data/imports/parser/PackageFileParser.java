package com.redhat.gss.skillmatrix.data.imports.parser;

import com.redhat.gss.skillmatrix.model.Package;

import java.util.ArrayList;
import java.util.List;

@ImportParser(nameOfType = "package")
public class PackageFileParser implements FileParser {

	@Override
	public List<Package> parse(String[] lines) {
		List<Package> result = new ArrayList<Package>();
		
		for (String line : lines) {
			Package p  = new Package();
			p.setName(line);
			result.add(p);
		}
		
		return result;
	}

}
