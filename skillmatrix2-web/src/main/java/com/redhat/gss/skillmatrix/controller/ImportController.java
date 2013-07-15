package com.redhat.gss.skillmatrix.controller;

import com.redhat.gss.skillmatrix.data.imports.diffs.Diff;
import com.redhat.gss.skillmatrix.data.imports.diffs.DiffException;
import com.redhat.gss.skillmatrix.data.imports.parser.ParseException;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.util.DiffHandler;
import com.redhat.gss.skillmatrix.util.DiffHolder;
import com.redhat.gss.skillmatrix.util.ImportFileHandler;
import org.reflections.Reflections;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;

@ViewScoped
@ManagedBean
public class ImportController implements Serializable {
	private static final long serialVersionUID = -816363082121318366L;

	@Inject
	private ImportFileHandler importHandler;

	@Inject
	private DiffHandler diffManager;

	@Inject
	private DiffHolder holder;

	@Inject
	private transient Reflections reflections;

	@Inject
	private transient FacesContext facesContext;

	private Map<String, byte[]> uploadedFiles;
	private List<String> packages;
	private Diff<Package> diff;
	private String clearFileName;



	public List<String> getPackages() {
		return packages;
	}

	public Diff<Package> getDiff() {
		return diff;
	}


	public void uploadListener(FileUploadEvent event) {
		UploadedFile file = event.getUploadedFile();
		uploadedFiles.put(file.getName(),file.getData());
	}

	public String submitFiles() throws InterruptedException {
		if(!uploadedFiles.isEmpty()) {
			// Parsing
			List<List<?>> objects = new ArrayList<List<?>>();

			for (byte[] data : uploadedFiles.values()) {
				try {
					objects.add(importHandler.process(data));
				} catch (ParseException e) {
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Parsing error: " +  e.getMessage(), null));
				}
			}

			//Diff
			List<Diff<?>> diffs = null;
			if(!objects.isEmpty()) {
				try {
					diffs = diffManager.createDiffs(objects);
				} catch (DiffException e) {
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Diff creation error. " +  e.getMessage(), null));
				} 
			} else {
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No files were parsed successfully.", null));
			}

			if(diffs!=null) {
				for (Iterator<Diff<?>> it = diffs.iterator(); it.hasNext();) {// filter out empty diffs
					Diff<?> diff =  it.next();
					if(diff.isEmpty()) {
						facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "No difference found between imported file and data store.", null));
						it.remove();
					}
				}

				if(!diffs.isEmpty()) { 
					holder.start();
					holder.setDiffs(diffs);
					return holder.next();
				}
			}
		} else {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Upload some files first.", null));
		}

		uploadedFiles.clear();
		return "";

	}

	public void clear() {
		uploadedFiles.remove(clearFileName);
		clearFileName = null;
	}


	//helpers
	@PostConstruct
	private void init() {
		uploadedFiles = new HashMap<String, byte[]>();
		packages = new ArrayList<String>();
	}

	public String getClearFileName() {
		return clearFileName;
	}

	public void setClearFileName(String clearFileName) {
		this.clearFileName = clearFileName;
	}



}
