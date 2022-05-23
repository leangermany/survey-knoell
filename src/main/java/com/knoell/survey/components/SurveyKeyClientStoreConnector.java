package com.knoell.survey.components;

import java.io.Serializable;
import java.util.ArrayList;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.function.SerializableConsumer;

/**
 * stores and checks for key of surveys.
 * 
 * @author lam
 *
 */
@Tag("survey-rpc")
@JsModule("./survey/SurveyKeyClientStore.js")
public class SurveyKeyClientStoreConnector extends Component {
	private static final long serialVersionUID = -5127882129565284741L;

	public SurveyKeyClientStoreConnector() {}

	/**
	 * Supplies <code>true</code> if the client store contains the key,
	 * <code>false</code> if not.
	 * 
	 * @param key
	 * @param resultHandler
	 */
	public void hasKey(String key, SerializableConsumer<Boolean> resultHandler) {
		executeJs("return KnoellSurvey.has($0)", key).then(Boolean.class, resultHandler);
	}

	/**
	 * Puts the given key into the client store,
	 * 
	 * @param key
	 * @param resultHandler
	 */
	public void putKey(String key, SerializableConsumer<Boolean> resultHandler) {
		executeJs("return KnoellSurvey.put($0)", key).then(Boolean.class, resultHandler);
	}

	/**
	 * not tested, returns all keys in the client store. Supplies <code>true</code>
	 * if the operation was successful else <code>false</code>.
	 * 
	 * @param key
	 * @param resultHandler
	 */
	@Deprecated
	@SuppressWarnings("rawtypes")
	public void getAllKeys(String key, SerializableConsumer<ArrayList> resultHandler) {
		executeJs("return KnoellSurvey.getAll()", key).then(ArrayList.class, resultHandler);
	}

	/**
	 * not tested, returns alls keys in the client store and than deletes it.
	 * 
	 * @param key
	 * @param resultHandler
	 */
	@SuppressWarnings("rawtypes")
	public void clear(SerializableConsumer<ArrayList> resultHandler) {
		executeJs("return KnoellSurvey.clear()").then(ArrayList.class, resultHandler);
	}

	private PendingJavaScriptResult executeJs(String expression, Serializable... parameters) {
		return getElement().executeJs(expression, parameters);
	}

	public static SurveyKeyClientStoreConnector getCurrent() {
		var compOpt = UI.getCurrent().getChildren().filter(c -> c instanceof SurveyKeyClientStoreConnector).findFirst();
		if (compOpt.isEmpty()) {
			var rpc = new SurveyKeyClientStoreConnector();
			UI.getCurrent().add(rpc);
			return rpc;
		} else {
			return (SurveyKeyClientStoreConnector) compOpt.get();
		}
	}

}
