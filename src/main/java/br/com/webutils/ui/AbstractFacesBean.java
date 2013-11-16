package br.com.webutils.ui;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import br.com.webutils.MessageUtil;

public abstract class AbstractFacesBean implements Serializable {

	private static final long serialVersionUID = 1L;

	protected void info(final String key, final Object... params) {
		MessageUtil.addGlobalInfoMessage(key, params);
	}

	protected void error(final String key, final Object... params) {
		MessageUtil.addGlobalErrorMessage(key, params);
	}

	protected void warn(final String key, final Object... params) {
		MessageUtil.addGlobalWarnMessage(key, params);
	}

	public String getRequestURL() {
		
		Object request = FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		if (request instanceof HttpServletRequest) {
			return ((HttpServletRequest) request).getRequestURL().toString();
		} else {
			return "";
		}
		
	}

	public String getRequestParameter(String parameter) {
		
		Object request = FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		if (request instanceof HttpServletRequest) {
			return ((HttpServletRequest) request).getParameter(parameter);
		} else {
			return "";
		}
		
	}
	
}
