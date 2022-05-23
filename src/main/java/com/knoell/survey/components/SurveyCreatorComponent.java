package com.knoell.survey.components;

import java.util.ArrayList;
import java.util.Collections;

import com.knoell.survey.records.EditSurvey;
import com.knoell.survey.store.CreatorHistoryStore;
import com.knoell.survey.store.CreatorHistoryStore.CreatorHistory;
import com.knoell.survey.store.CreatorHistoryStore.HistoryEntry;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import elemental.json.impl.JreJsonObject;

/**
 * Server side component of <code>survey-creator-impl</code>. This LitElement
 * includes the <code>survey-creator-knockout</code> npm package.
 * 
 * @author lam
 *
 */
@Tag("survey-creator-impl")
@JsModule("./survey/survey-creator-impl.ts")
@NpmPackage(value = "survey-creator-knockout", version = "1.9.31")
public class SurveyCreatorComponent extends Component implements ICreatorComponent, HasSize, HasStyle {
	private static final long serialVersionUID = 5869607009381008106L;

	private final CreatorHistory creatorHistory;
	private final String key;
	private String lastJson;
	private EditSurvey editSurvey;

	/**
	 * creates a new component which history will be initialised with the given key
	 * 
	 * @param key
	 */
	public SurveyCreatorComponent(String key) {
		this.key = key;
		this.creatorHistory = CreatorHistoryStore.get().getHistory(key);
		addSaveSurveyListener(ssEvent -> {
			var json = ssEvent.getJson().toJson();
			this.lastJson = json;
			ssEvent.getSource().getCreatorHistory().addHistory(json);
		});
		setId(key);
		getElement().getStyle().set("overflow", "hidden");
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		// set latest config from is history, if present
		ArrayList<HistoryEntry> history = creatorHistory.getHistory();
		if (!history.isEmpty()) {
			history.removeAll(Collections.singleton(null));
			var entry = history.get(history.size() - 1);
			setJson(entry.getJson());
		}
	}

	/**
	 * Returns the {@link CreatorHistory} object linked with the key of the creator.
	 * 
	 * @return CreatorHistory
	 */
	public CreatorHistory getCreatorHistory() {
		return creatorHistory;
	}

	/**
	 * copies the config client sided
	 */
	public void copyJsonToClipboard() {
		getElement().callJsFunction("copyToClipboard");
	}

	/**
	 * paste the config client sided
	 */
	public void pasteJsonFromClipboard() {
		getElement().callJsFunction("pasteFromClipboard");
	}

	/**
	 * clears the current config and sets the default config.
	 */
	public void clear() {
		getElement().callJsFunction("clear");
		Notification.show("Data cleared.").addThemeVariants(NotificationVariant.LUMO_PRIMARY);
	}

	/**
	 * Adds a {@link ComponentEventListener} to the client sided event 'saveSurvey'
	 * that is triggered after a timeout and contains the save number (saveNo) and
	 * the survey configuration json.
	 * 
	 * @param listener
	 * @return the registration to remove the listener
	 */
	public Registration addSaveSurveyListener(ComponentEventListener<SaveSurveyEvent> listener) {
		return addListener(SaveSurveyEvent.class, listener);
	}

	/**
	 * set a survey config into the client sided creator component.
	 * 
	 * @param json
	 */
	public void setJson(String json) {
		getElement().callJsFunction("setText", json);
		this.lastJson = json;
	}

	/**
	 * Returns the last saved survey config. Send to the server after a timeout.
	 * 
	 * @return survey config
	 */
	public String getLastJson() {
		return this.lastJson;
	}

	/**
	 * Returns the last saved survey config. Send to the server after a timeout.
	 * 
	 * @return survey config
	 */
	public void recievelientJson(SerializableConsumer<String> resultHandler) {
		getElement().callJsFunction("getText").then(String.class, resultHandler);
	}

	public String getKey() {
		return key;
	}

	public EditSurvey getEditSurvey() {
		return editSurvey;
	}

	public void setEditSurvey(EditSurvey editSurvey, boolean applyConfig) {
		this.editSurvey = editSurvey;
		if (editSurvey.json() != null && !editSurvey.json().isBlank()) {
			setJson(editSurvey.json());
		}
	}

	/**
	 * The client component uses this as callback to notify the user if the 'copy to
	 * clipboard' action was successfull.
	 * 
	 * @deprecated typescript Notification component is used to prevent client
	 *             server roundtrip
	 * 
	 * @param success
	 */
	@ClientCallable
	private void notifyClipboardCopySuccess(String success) {
		if (success != null && success.equals(Boolean.TRUE.toString())) {
			Notification.show("The configuration was copied to the clipboard")
					.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
		} else {
			Notification.show(
					"The configuration couldn't be copied to the clipboard. Please open the Tab 'JSON Editor' and copy the value manually!")
					.addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
	}

	/**
	 * The client component uses this as callback to notify the user if the 'paste
	 * from clipboard' action was successfull.
	 * 
	 * @deprecated typescript Notification component is used to prevent client
	 *             server roundtrip
	 * 
	 * @param success
	 */
	@ClientCallable
	private void notifyClipboardPasteSuccess(String success) {
		if (success != null && success.equals(Boolean.TRUE.toString())) {
			Notification.show("The configuration was pasted from the clipboard")
					.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
		} else if (success != null && success.equals("parse")) {
			// not parseable -> no json
			Notification.show("The content was no Survey Configuration")
					.addThemeVariants(NotificationVariant.LUMO_ERROR);
		} else {
			Notification.show(
					"The configuration couldn't be pasted to the clipboard. Please open the Tab 'JSON Editor' and paste the value manually!")
					.addThemeVariants(NotificationVariant.LUMO_ERROR);
			;
		}
	}

	@DomEvent("saveSurvey")
	public static class SaveSurveyEvent extends AbstractSaveSurveyEvent<SurveyCreatorComponent> {
		private static final long serialVersionUID = -3726791481233517207L;

		public SaveSurveyEvent(SurveyCreatorComponent source, boolean fromClient,
				@EventData("event.detail.json") JreJsonObject json, @EventData("event.detail.saveNo") Integer saveNo) {
			super(source, fromClient, json, saveNo);
		}

	}

}