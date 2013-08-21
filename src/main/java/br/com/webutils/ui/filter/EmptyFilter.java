package br.com.webutils.ui.filter;

import org.apache.commons.lang.StringUtils;

public class EmptyFilter implements Filter {
    
    private static final long serialVersionUID = -7429827889981732582L;
    
    @Override
    public void reset() {
    }
    
    @Override
    public boolean isValid() {
	return true;
    }
    
    @Override
    public String getValidationMessage() {
	return StringUtils.EMPTY;
    }
    
}
