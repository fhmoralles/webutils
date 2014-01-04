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
	public static final String GLOBAL_MSG_ERROR = "global.msg.error";
	public static final String GLOBAL_MSG_UPDATE = "global.msg.update";
	public static final String GLOBAL_MSG_SEARCH_NOT_FOUND = "global.msg.search_not_found";
	public static final String GLOBAL_MSG_INVALIDBEAN = "global.msg.invalidbean";

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
		return prepareSearch();
	}

	private void cleanUp() {
		bean = null;
		cleanUpImpl();
	}

	/**
	 * Efetua operações específicas de cleanUp.
	 */
	protected abstract void cleanUpImpl();

	public String delete() {
		
		try {
			if (bean != null) {
				deleteImpl(bean);
				info(GLOBAL_MSG_DELETE);
			}
			return prepareSearch();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			error(GLOBAL_MSG_ERROR_DELETE);
			return null;
		}
	}

	protected abstract void deleteImpl(E bean) throws Exception;

	/**
	 * Retorna a menagem de exclusão.
	 */
	protected abstract String getMsgDelete();

	/**
	 * Retorna a action a ser utilizada na inserção.
	 */
	protected abstract String getActionCreate();

	/**
	 * Retorna a action a ser utilizada na visualização.
	 */
	protected abstract String getActionDetail();

	/**
	 * Retorna a action a ser utilizada na edição.
	 */
	protected abstract String getActionEdit();

	/**
	 * Retorna a action a ser utilizada na busca.
	 */
	protected abstract String getActionSearch();

	public E getBean() {
		return bean;
	}

	// -- Métodos Auxiliares

	public F getFilter() {
		return filter;
	}

	// -- Métodos Abstratos

	public List<E> getRows() {
		return rows;
	}

	/**
	 * Indica se a operação de remoção estará habilitada.
	 * 
	 * @return
	 */
	public abstract boolean isDeletable();

	/**
	 * Indica se as operações são realizadas em um dialog.
	 * 
	 * @return
	 */
	// public abstract boolean isDialogMode();

	/**
	 * Indica se a operação de edição estará habilitada.
	 * 
	 * @return
	 */
	public abstract boolean isEditable();

	public boolean isEditing() {
		return mode == Mode.EDIT;
	}

	/**
	 * Indica se a operação de criação estará habilitada.
	 * 
	 * @return
	 */
	public abstract boolean isInsertable();

	public boolean isInserting() {
		return mode == Mode.CREATE;
	}

	/**
	 * Indica se é uma operação de inserção múltipla, ou seja, após inserir um
	 * registro se inicia o proceso de inserção novamente.
	 * 
	 * @return
	 */
	// protected abstract boolean isMultipleInsertion();

	/**
	 * Indica se a operação de busca estará habilitada.
	 * 
	 * @return
	 */
	public abstract boolean isSearchable();

	/**
	 * Indica se o método {@link NewAbstractCrud#prepareSearch()} realizará uma
	 * busca ao ser executado.
	 */
	protected abstract boolean isSearchOnPrepare();

	/**
	 * Indica se o bean em edição é válido. Caso seja válido, será salvo.
	 * 
	 * @return
	 */
	protected abstract boolean isValidBean(E bean);

	/**
	 * Indica se a operação de visualização estará habilitada.
	 * 
	 * @return
	 */
	public abstract boolean isViewable();

	public boolean isViewing() {
		return mode == Mode.DETAIL;
	}

	/**
	 * Cria uma nova instância para inserção.
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

		mode = Mode.SEARCH;

		if (!isSearchable() || isSearchOnPrepare()) {
			search();
		} else {
			reset();			
		}

		return getActionSearch();

	}

	public void reset() {

		if (filter != null) {
			filter.reset();
		}

		resetRows();
		cleanUp();

		System.out.println("***** reset: " + rows + " filter: " + filter );
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

				return backToSearch();

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				error(GLOBAL_MSG_INVALIDBEAN);
			}

		} else {
			error(GLOBAL_MSG_INVALIDBEAN);
		}

		return null;

	}

	/**
	 * Operação específica para que o objeto seja salvo.
	 */
	protected abstract void saveImpl(E bean) throws Exception;

	public void search() {

//		LOG.info("1");
		if (filter.isValid()) {
//			LOG.info("2");
			rows = searchImpl(filter);
//			LOG.info("3");
			if (rows == null || rows.isEmpty()) {
//				LOG.info("4");
				resetRows();
//				LOG.info("5");
				if (getMode() == Mode.SEARCH) {
//					LOG.info("6");
					warn(GLOBAL_MSG_SEARCH_NOT_FOUND);
//					LOG.info("7");
				}
//				LOG.info("8");
			}
//			LOG.info("9");
		} else {
//			LOG.info("10");
			final String validationMessage = filter.getValidationMessage();
//			LOG.info("11");
			if (StringUtils.isNotBlank(validationMessage)) {
//				LOG.info("12");
				error(filter.getValidationMessage());
//				LOG.info("13");
			}
//			LOG.info("14");
			resetRows();
//			LOG.info("15");
		}
//		LOG.info("16");
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
