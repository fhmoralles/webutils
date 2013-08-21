package br.com.webutils;


/**
 * Utility class with functions that are accessible from Facelets files.
 * <p/>
 * Facelets functions need to be registered in a <code>*.taglib.xml</code>
 * descriptor.
 * 
 * @author Anonimo
 */
public final class FaceletsFunctions {

    private FaceletsFunctions() {
    }

    /**
     * Returns the parameter <code>value</code> if it is not null, or the
     * paramenter <code>valueIFNull</code> otherwise.
     * 
     * @param value
     * @param valueIfNull
     * @return value
     */
    public static Object nvl(Object value, String valueIfNull) {
        return (FaceletsFunctions.norm(value) == null) ? valueIfNull : value;
    }

    @SuppressWarnings("unchecked")
    private static <T> T norm(T o) {
        if (o == null) {
            return null;
        }
        if (o instanceof String) {
            String s = (String) o;
            return (T) ((s.trim().length() == 0) ? null : s);
        }
        return o;
    }

    public static String messageEnum(Enum<?> e) {
        return MessageUtil.getMessageEnum(e);
    }

    public static String concat(String str1, String str2, String str3) {
        return MessageUtil.concat(str1, str2, str3);
    }
    
}
