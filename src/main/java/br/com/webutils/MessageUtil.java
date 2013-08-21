package br.com.webutils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;

public final class MessageUtil {

	private static final String RESOURCE_BUNDLE_ID = "messages";

	// -- Construtores ------------------------------------------------------ //

	private MessageUtil() {
	}

	// -- Métodos ----------------------------------------------------------- //

	public static void addGlobalInfoMessage(final String key, final Object... params) {
		MessageUtil.addComponentInfoMessage(null, key, params);
	}

	public static void addGlobalErrorMessage(final String key, final Object... params) {
		MessageUtil.addComponentErrorMessage(null, key, params);
	}

	public static void addGlobalWarnMessage(final String key, final Object... params) {
		MessageUtil.addComponentWarnMessage(null, key, params);
	}

	public static void addComponentInfoMessage(final String componentId,
			final String key, final Object... params) {

		MessageUtil.addMessage(FacesMessage.SEVERITY_INFO, componentId, key,
				params);

	}

	public static void addComponentErrorMessage(final String componentId,
			final String key, final Object... params) {

		MessageUtil.addMessage(FacesMessage.SEVERITY_ERROR, componentId, key,
				params);

	}

	public static void addComponentWarnMessage(final String componentId,
			final String key, final Object... params) {

		MessageUtil.addMessage(FacesMessage.SEVERITY_WARN, componentId, key,
				params);

	} 

	private static void addMessage(final FacesMessage.Severity severity,
			final String componentId, final String key, final Object... params) {

		final String text = MessageUtil.getMessage(key, params);

		final FacesMessage message = new FacesMessage(severity, text, null);
		FacesUtil.getFacesContext().addMessage(componentId, message);
	}

	/**
	 * Obtém uma messagem do resource bundle.
	 * 
	 * @param key
	 *            A chave da mensagem
	 * @return A mensagem
	 */
	public static String getMessage(final String key, final Object... params) {

		String text = null;
		final Locale locale = FacesUtil.getLocale();

		try {
			text = MessageUtil.getResourceBundle(locale).getString(key);
		} catch (final MissingResourceException e) {
			text = "//" + key + " BUNDLE NOT FOUND//";
		}

		if (params != null && params.length > 0) {
			final MessageFormat mf = new MessageFormat(text, locale);
			text = mf.format(params, new StringBuffer(), null).toString();
		}

		return text;
	}

	public static String getMessageEnum(final Enum<?> e) {

		if (e != null) {
			final String key = "enum." + e.getClass().getSimpleName() + "."
					+ e.name();
			return MessageUtil.getMessage(key);
		}

		return "// BUNDLE NOT FOUND//";

	}

	public static String concat(final String... strs) {
		String strConcat = "";
		for (final String str : strs) {
			strConcat = strConcat.concat(str);
		}
		return strConcat;

	}

	private static ResourceBundle getResourceBundle(final Locale locale) {

		final ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();

		return ResourceBundle.getBundle(MessageUtil.RESOURCE_BUNDLE_ID, locale,
				classLoader);

	}

}
