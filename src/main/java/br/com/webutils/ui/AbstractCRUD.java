package br.com.webutils.ui;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import br.com.webutils.ui.filter.Filter;

public abstract class AbstractCRUD<E, F extends Filter> extends
		AbstractFacesBean implements Serializable {

	
	public static final String GLOBAL_MSG_DELETE = "global.msg.delete";
	public static final String GLOBAL_MSG_ERROR_DELETE = "global.msg.error.delete";
	public static final String GLOBAL_MSG_SELECT = StringUtils.EMPTY;
	public static final String GLOBAL_MSG_SAVE = "global.msg.save";
	public static final String GLOBAL_MSG_UPDATE = "global.msg.update";
	public static final String GLOBAL_MSG_SEARCH_NOT_FOUND = "global.msg.search_not_found";
	
	private static final Logger LOG = Logger.getLogger(AbstractCRUD.class);
	
	// -- Constantes

	protected enum Mode {
		SEARCH, CREATE, EDIT, DETAIL, PRE_CREATE
	}

	private static final long serialVersionUID = 1L;

	// -- Atributos

	private F filter;

	private E bean;

	private List<E> rows;

	private Mode mode;

	public AbstractCRUD() {
		this(null);
	}

	protected AbstractCRUD(final F filter) {
		this.filter = filter;
	}

	// -- Actions

	public String backToSearch() {
		mode = Mode.SEARCH;

		return getActionSearch();
	}

	private void cleanUp() {
		bean = null;
		cleanUpImpl();
	}

	/**
	 * Efetua opera��es espec�ficas de cleanUp.
	 */
	protected abstract void cleanUpImpl();

	public String delete() {

		try {
			if (bean != null) {
				deleteImpl(bean);
				info(GLOBAL_MSG_DELETE);
			}
			return prepareSearch();
		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
			error(GLOBAL_MSG_ERROR_DELETE);
			return null;
		}
	}

	protected abstract void deleteImpl(E bean);

	/**
	 * Retorna a action a ser utilizada na inser��o.
	 */
	protected abstract String getActionCreate();

	/**
	 * Retorna a action a ser utilizada na visualiza��o.
	 */
	protected abstract String getActionDetail();

	/**
	 * Retorna a action a ser utilizada na edi��o.
	 */
	protected abstract String getActionEdit();

	/**
	 * Retorna a action a ser utilizada na busca.
	 */
	protected abstract String getActionSearch();

	public E getBean() {
		return bean;
	}

	// -- M�todos Auxiliares

	public F getFilter() {
		return filter;
	}

	// -- M�todos Abstratos

	public List<E> getRows() {
		return rows;
	}

	/**
	 * Indica se a opera��o de remo��o estar� habilitada.
	 * 
	 * @return
	 */
	public abstract boolean isDeletable();

	/**
	 * Indica se as opera��es s�o realizadas em um dialog.
	 * 
	 * @return
	 */
	//public abstract boolean isDialogMode();

	/**
	 * Indica se a opera��o de edi��o estar� habilitada.
	 * 
	 * @return
	 */
	public abstract boolean isEditable();

	public boolean isEditing() {
		return mode == Mode.EDIT;
	}

	/**
	 * Indica se a opera��o de cria��o estar� habilitada.
	 * 
	 * @return
	 */
	public abstract boolean isInsertable();

	public boolean isInserting() {
		return mode == Mode.CREATE;
	}

	/**
	 * Indica se � uma opera��o de inser��o m�ltipla, ou seja, ap�s inserir um
	 * registro se inicia o proceso de inser��o novamente.
	 * 
	 * @return
	 */
	//protected abstract boolean isMultipleInsertion();

	/**
	 * Indica se a opera��o de busca estar� habilitada.
	 * 
	 * @return
	 */
	public abstract boolean isSearchable();

	/**
	 * Indica se o m�todo {@link NewAbstractCrud#prepareSearch()} realizar� uma
	 * busca ao ser executado.
	 */
	protected abstract boolean isSearchOnPrepare();

	/**
	 * Indica se o bean em edi��o � v�lido. Caso seja v�lido, ser� salvo.
	 * 
	 * @return
	 */
	protected abstract boolean isValidBean(E bean);

	/**
	 * Indica se a opera��o de visualiza��o estar� habilitada.
	 * 
	 * @return
	 */
	public abstract boolean isViewable();

	public boolean isViewing() {
		return mode == Mode.DETAIL;
	}

	/**
	 * Cria uma nova inst�ncia para inser��o.
	 * 
	 * @return
	 */
	protected abstract E newInstance();

	public String prepareCreate() {

		cleanUp();
		mode = Mode.CREATE;
		bean = newInstance();
		
		return getActionCreate();
	}

	public String prepareDelete() {
		if (bean == null) {
			info(GLOBAL_MSG_SELECT);
		}
		return null;
	}

	public String prepareDetail() {
		mode = Mode.DETAIL;
		return getActionDetail();
	}

	public String prepareEdit() {
		mode = Mode.EDIT;
		return getActionEdit();
	}

	// -- Getters e Setters

	public String prepareSearch() {

		reset();

		if (!isSearchable() || isSearchOnPrepare()) {
			search();
		}

		mode = Mode.SEARCH;
		return getActionSearch();
	}

	public void reset() {

		if (filter != null) {
			filter.reset();
		}

		resetRows();
		cleanUp();

	}

	/**
	 * Garante que o atributo {@link AbstractCRUD#rows} nunca esteja nulo.
	 */
	private void resetRows() {
		rows = Collections.emptyList();
	}

	public String save() {

		if (isValidBean(bean)) {
			
			try {
				saveImpl(bean);
	
				if (isInserting()) {
					info(GLOBAL_MSG_SAVE);
				} else {
					info(GLOBAL_MSG_UPDATE);
				}
	
				// Realiza a busca novamente e volta para os resultados
				// search();
				return backToSearch();
				
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}

		}

		return null;

	}

	/**
	 * Opera��o espec�fica para que o objeto seja salvo.
	 */
	protected abstract void saveImpl(E bean) throws Exception;

	public void search() {
		if (filter.isValid()) {
			rows = searchImpl(filter);

			if (rows == null || rows.isEmpty()) {
				resetRows();
				if (getMode() == Mode.SEARCH) {
					warn(GLOBAL_MSG_SEARCH_NOT_FOUND);
				}
			}
		} else {
			final String validationMessage = filter.getValidationMessage();
			if (StringUtils.isNotBlank(validationMessage)) {
				error(filter.getValidationMessage());
			}
			resetRows();
		}
	}

	/**
	 * Efetua a busca e retorna a lista contendo o resultado que atende aos
	 * requisitos do filtro.
	 * 
	 * @return
	 */
	protected abstract List<E> searchImpl(F filter);

	public void setBean(final E bean) {
		this.bean = bean;
	}

	public void setFilter(final F filter) {
		this.filter = filter;
	}

	protected Mode getMode() {
		return mode;
	}

	protected void setMode(final Mode mode) {
		this.mode = mode;
	}

}
