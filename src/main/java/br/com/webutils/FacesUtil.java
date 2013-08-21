package br.com.webutils;

import java.util.Locale;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

public final class FacesUtil {

    // -- Construtores ------------------------------------------------------ //

    private FacesUtil() {
    }

    // -- Métodos ----------------------------------------------------------- //

    public static FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    public static Locale getLocale() {
        Locale locale = null;

        UIViewRoot viewRoot = FacesUtil.getFacesContext().getViewRoot();

        if (viewRoot != null) {
            locale = viewRoot.getLocale();
        }

        if (locale == null) {
            locale = Locale.getDefault();
        }

        return locale;
    }

}
