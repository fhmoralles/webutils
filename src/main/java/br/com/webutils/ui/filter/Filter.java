package br.com.webutils.ui.filter;

import java.io.Serializable;

public interface Filter extends Serializable {
    
    void reset();
    
    boolean isValid();
    
    public String getValidationMessage();
    
}
