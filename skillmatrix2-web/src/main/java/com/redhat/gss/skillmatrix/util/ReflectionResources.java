package com.redhat.gss.skillmatrix.util;

import org.jboss.vfs.VirtualFile;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.vfs.SystemDir;
import org.reflections.vfs.Vfs;
import org.reflections.vfs.ZipDir;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URL;
import java.util.jar.JarFile;

@Singleton
public class ReflectionResources {

	@Produces
	public Reflections createReflections() {
		return new Reflections(new ConfigurationBuilder()
		.setUrls(ClasspathHelper.forPackage("com.redhat.gss.skillmatrix"))
		.setScanners(new ResourcesScanner(),
				new TypeAnnotationsScanner(),
				new SubTypesScanner()));

	}

    @PostConstruct
	private void init() {
		Vfs.addDefaultURLTypes(
				new Vfs.UrlType() {
					public boolean matches(URL url) {
						return url.getProtocol().equals("vfs");
					}

					public Vfs.Dir createDir(URL url) {
						VirtualFile content;
						try {
							content = (VirtualFile) url.openConnection().getContent();
						} catch (Throwable e) {
							throw new ReflectionsException("could not open url connection as VirtualFile [" + url + "]", e);
						}

						Vfs.Dir dir = null;
						try {
							dir = createDir(new java.io.File(content.getPhysicalFile().getParentFile(), content.getName()));
						} catch (IOException e) { /*continue*/ }
						if (dir == null) {
							try {
								dir = createDir(content.getPhysicalFile());
							} catch (IOException e) { /*continue*/ }
						}
						return dir;
					}

					Vfs.Dir createDir(java.io.File file) {
						try {
							return file.exists() && file.canRead() ? file.isDirectory() ? new SystemDir(file) : new ZipDir(new JarFile(file)) : null;
						} catch (IOException e) {
							e.printStackTrace();
						}
						return null;
					}
				});
	}

}
