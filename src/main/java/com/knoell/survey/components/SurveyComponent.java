package com.knoell.survey.components;

import com.knoell.survey.records.Survey;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.shared.Registration;

import elemental.json.impl.JreJsonObject;

/**
 * Server side component of <code>survey-impl</code>.
 * 
 * @author lam
 *
 */
@Tag("survey-impl")
@JsModule("./survey/survey-impl.js")
@CssImport("./survey/defaultV2.min.css")
public class SurveyComponent extends Component implements HasSize, HasStyle {
	private static final long serialVersionUID = -2176675327523778046L;

	private final Survey survey;

	public SurveyComponent(Survey survey) {
		assert survey != null;
		this.survey = survey;
		setupElement();
		setupSurvey();
	}

	private void setupElement() {
		getElement().getStyle().set("display", "inline-block");
		getElement().getStyle().set("width", "100%");
	}

	private void setupSurvey() {
		setId(String.format("survey-%s", this.survey.key()));
		getElement().callJsFunction("setSurvey", this.survey.json());
	}

	/**
	 * Adds a {@link ComponentEventListener} to the client sided event 'complete'
	 * that return the result json.
	 * 
	 * @param listener
	 * @return the registration to remove the listener
	 */
	public Registration addCompleteListener(ComponentEventListener<CompleteEvent> listener) {
		return addListener(CompleteEvent.class, listener);
	}

	/**
	 * The survey configures the client component.
	 * 
	 * @return
	 */
	public Survey getSurvey() {
		return survey;
	}

	@DomEvent("complete")
	public static class CompleteEvent extends ComponentEvent<SurveyComponent> {
		private static final long serialVersionUID = -3726791481233517207L;

		private final JreJsonObject json;

		public CompleteEvent(SurveyComponent source, boolean fromClient,
				@EventData("event.detail.json") JreJsonObject json) {
			super(source, fromClient);
			this.json = json;
		}

		/**
		 * the json object of the survey result
		 * 
		 * @return
		 */
		public JreJsonObject getJson() {
			return json;
		}

	}

}
