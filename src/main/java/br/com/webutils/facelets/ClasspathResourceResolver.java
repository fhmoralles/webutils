package br.com.webutils.facelets;

import java.net.URL;

import javax.faces.view.facelets.ResourceResolver;

import org.apache.log4j.Logger;

public class ClasspathResourceResolver extends ResourceResolver {

    private static final Logger LOG = Logger.getLogger(ClasspathResourceResolver.class);
    private ResourceResolver parent;

    public ClasspathResourceResolver(ResourceResolver parent) {
        this.parent = parent;
    }
    
	@Override
	public URL resolveUrl(String resource) {

		LOG.debug("Resolving facelets " + resource);
		URL resourceUrl = parent.resolveUrl(resource);
        
		if (resourceUrl == null) {
            if (resource.startsWith("/")) {
                resource = resource.substring(1);
            }
            resourceUrl = getLoader().getResource(resource);
        }
        
		LOG.debug("resourceUrl=" + resourceUrl);
        return resourceUrl;
	}
	
    private ClassLoader getLoader() {
        return Thread.currentThread().getContextClassLoader();
    }	

}
