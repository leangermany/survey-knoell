package com.knoell.survey.components;

import com.knoell.survey.records.EditSurvey;
import com.knoell.survey.store.CreatorHistoryStore;
import com.knoell.survey.store.CreatorHistoryStore.CreatorHistory;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import elemental.json.impl.JreJsonObject;

@Tag("creator-connector")
@NpmPackage(value = "@vaadin/notification", version = "23.0.7")
public class SurveyCreatorConnector extends Component implements ICreatorComponent, HasSize {
	private static final long serialVersionUID = -4222717632133054067L;

	private final CreatorHistory creatorHistory;
	private final String key;
	private EditSurvey editSurvey;

	public SurveyCreatorConnector(String key) {
		this.key = key;
		this.creatorHistory = CreatorHistoryStore.get().getHistory(key);
		init();
		addSaveSurveyListener(ssEvent -> {
			var json = ssEvent.getJson().toJson();
			System.out.println(json);
			ssEvent.getSource().getCreatorHistory().addHistory(json);
		});
	}

	private void init() {
		setId(key);
		var js = """
					Survey.surveyLocalization.supportedLocales = ["en", "de", "es", "fr"];
				    const creatorOptions = { showLogicTab: true, isAutoSave: true, haveCommercialLicense: true,
				        questionTypes: ["text", "checkbox", "radiogroup", "dropdown", "comment", "rating", "boolean", "matrix", "matrixdynamic", "multipletext", "panel", "html" ] };
				    var creator = new SurveyCreator.SurveyCreator(creatorOptions);
				    creator.render($1);
					creator.saveSurveyFunc = (saveNo, callback) => {
				        let completedEvent = new CustomEvent('saveSurvey', { detail: { json: this.creator.JSON, saveNo } });
				        this.dispatchEvent(completedEvent);
				    }
				    this.creator = creator;
				    this.copyToClipboard = function() {
					    let textToCopy = this.creator.text;
					    if (textToCopy) {
					        navigator.clipboard.writeText(textToCopy).then(function () {
					           alert('The configuration was copied to the clipboard');
					        }, function () {
					            alert("The configuration couldn't be copied to the clipboard. Please open the Tab 'JSON Editor' and copy the value manually!");
					        }).catch(error => {
					            alert("The configuration couldn't be copied to the clipboard. Please open the Tab 'JSON Editor' and copy the value manually!");
					        });
					    }
					}
					this.pasteFromClipboard = function() {
					    navigator.clipboard.readText().then(clipText => {
					        try {
					            JSON.parse(clipText);
					            this.creator.changeText(clipText);
					            alert('The configuration was pasted from the clipboard');
					        } catch (error) {
					            alert("The content was no Survey Configuration");
					        }
					    }, onRejected => {
					        alert("Permission to read the clipboard was denied!");
					    }
					    ).catch(error => {
					        alert("The configuration couldn't be pasted to the clipboard. Please open the Tab 'JSON Editor' and paste the value manually!");
					    });
					}
							""";
		getElement().executeJs(js, key);

	}

	public Registration addSaveSurveyListener(ComponentEventListener<SaveSurveyEvent> listener) {
		return addListener(SaveSurveyEvent.class, listener);
	}

	/**
	 * set a survey config into the client sided creator component.
	 * 
	 * @param json
	 */
	public void setJson(String json) {
		getElement().executeJs("this.creator.changeText($0)", json);
	}

	/**
	 * Returns the last saved survey config. Send to the server after a timeout.
	 * 
	 * @return survey config
	 */
	public void recievelientJson(SerializableConsumer<String> resultHandler) {
		getElement().executeJs("return this.creator.text").then(String.class, resultHandler);
	}

	@Override
	public void copyJsonToClipboard() {
		getElement().executeJs("this.copyToClipboard();");

	}

	@Override
	public void pasteJsonFromClipboard() {
		getElement().executeJs("this.pasteFromClipboard();");
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	public CreatorHistory getCreatorHistory() {
		return creatorHistory;
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

	@DomEvent("saveSurvey")
	public static class SaveSurveyEvent extends AbstractSaveSurveyEvent<SurveyCreatorConnector> {
		private static final long serialVersionUID = -3726791481233517207L;

		public SaveSurveyEvent(SurveyCreatorConnector source, boolean fromClient,
				@EventData("event.detail.json") JreJsonObject json, @EventData("event.detail.saveNo") Integer saveNo) {
			super(source, fromClient, json, saveNo);
		}

	}

}
