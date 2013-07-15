package com.redhat.gss.skillmatrix.util;

import com.redhat.gss.skillmatrix.data.imports.parser.FileParser;
import com.redhat.gss.skillmatrix.data.imports.parser.ImportParser;
import com.redhat.gss.skillmatrix.data.imports.parser.ParseException;
import org.reflections.Reflections;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequestScoped
public class ImportFileHandler {
	
	private static final Pattern HEADER_PATTERN = Pattern.compile("^SM-([a-zA-Z]+)-(\\d.\\d)(-(.+))?$");
	
	@Inject
	private Reflections reflections;

	@Inject
	private Logger log;

	private FileParser parser;
	
	//parsed from header
	private String type;
	private String version;
	private String separator;
	
	
	public String getType() {
		return type;
	}

	public List<?> process(byte[] data) throws ParseException {
		if(data==null)
			throw new ParseException("null data", new NullPointerException("data"));
		
		//revert to initial state
		type = null;
		version = null;
		separator = null;
		parser = null;
		

		String content = new String(data);
		content.replaceAll("\r\n", "\n");

		String[] lines = content.split("\n");

		parseHeader(lines[0]);

		String[] datalines = Arrays.copyOfRange(lines, 1, lines.length);
		
		return parser.parse(datalines);
	}

	private void parseHeader(String line) throws ParseException {
		if(line==null)
			throw new ParseException("null header", new NullPointerException("header"));
		
		Matcher matcher = HEADER_PATTERN.matcher(line);
		
		if(!matcher.matches())
			throw new ParseException("header in wrong format");
		
		type = matcher.group(1);
		version = matcher.group(2);
		separator = matcher.group(4);
		
		if(type==null || type.isEmpty())
			throw new ParseException("missing type in header");
		
		for(Class<?> parserClass : reflections.getTypesAnnotatedWith(ImportParser.class)) {
			ImportParser annotation = parserClass.getAnnotation(ImportParser.class);
			log.info("found annotated class " + parserClass.getSimpleName());
			if(annotation.nameOfType().toLowerCase().equals(type.toLowerCase())) {// found the right parser
				try { // try to make instance
					parser = (FileParser) parserClass.newInstance();
					break;
				} catch (Exception e) {
					throw new ParseException("error when creating parser", e);
				}
			}
		}
		
		if(parser==null)
			throw new ParseException("unsupported type: " + type);
		
	}
	
}
