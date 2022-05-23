package com.knoell.survey.components;

import com.knoell.survey.records.EditSurvey;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.function.SerializableConsumer;

import elemental.json.impl.JreJsonObject;

public interface ICreatorComponent {

	String getKey();

	void copyJsonToClipboard();

	void pasteJsonFromClipboard();

	void clear();

	void setJson(String json);

	void recievelientJson(SerializableConsumer<String> resultHandler);

	EditSurvey getEditSurvey();

	public static abstract class AbstractSaveSurveyEvent<C extends Component & ICreatorComponent>
			extends ComponentEvent<C> {
		private static final long serialVersionUID = -3726791481233517207L;

		private final JreJsonObject json;
		private final Integer saveNo;

		public AbstractSaveSurveyEvent(C source, boolean fromClient, @EventData("event.detail.json") JreJsonObject json,
				@EventData("event.detail.saveNo") Integer saveNo) {
			super(source, fromClient);
			this.json = json;
			this.saveNo = saveNo;
		}

		public JreJsonObject getJson() {
			return json;
		}

		public Integer getSaveNo() {
			return saveNo;
		}

	}

}
