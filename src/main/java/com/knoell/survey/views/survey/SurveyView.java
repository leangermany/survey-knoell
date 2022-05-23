package com.knoell.survey.views.survey;

import org.springframework.beans.factory.annotation.Autowired;

import com.knoell.survey.components.SurveyComponent;
import com.knoell.survey.components.SurveyKeyClientStoreConnector;
import com.knoell.survey.exception.BusinessException;
import com.knoell.survey.exception.ResponseException;
import com.knoell.survey.records.Survey;
import com.knoell.survey.store.DemoSurveyDummyStore;
import com.knoell.survey.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

/**
 * This view only displays a survey component. The RouteParameter "key" is
 * optional, but if not present or the parameter key is not valid, the event is
 * forwarded to the error view. Further will be checked if the survey key is
 * registered client sided, if true a navigation to an error view is triggered.
 * On survey submit the result will be send to the backend.
 * 
 * @author lam
 *
 */
@PageTitle("Survey - knoell")
@Route(value = "survey/:key?", layout = MainLayout.class)
@RouteAlias(value = ":key?", layout = MainLayout.class)
public class SurveyView extends Div implements BeforeEnterObserver, BeforeLeaveObserver {
	private static final long serialVersionUID = 115819994842382311L;
	private static final String idParametername = "key";

	// data
	private final DemoSurveyDummyStore dummyStore;

	// component
	private final H3 loadingCaption = new H3("Loading...");

	public SurveyView(@Autowired DemoSurveyDummyStore dummyStore) {
		this.dummyStore = dummyStore;
		setSizeFull();
		add(loadingCaption);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		var routeParameters = event.getRouteParameters();
		var idParamOpt = routeParameters.get(idParametername);
		if (idParamOpt.isEmpty()) {
			ResponseException ex = new ResponseException(404, "The survey was not found.");
			event.rerouteToError(ex, ex.getMessage());
		} else {
			var paramContent = idParamOpt.get();
			loadSurvey(event, paramContent);
		}

	}

	private void loadSurvey(BeforeEnterEvent event, String paramContent) {
		try {
			Survey survey = null;
			try {
				survey = this.dummyStore.checkSurvey(paramContent);
			} catch (ResponseException e) {
				event.rerouteToError(e, e.getMessage());
				return;
			}
			assert survey != null;
			setupSurveyComponent(survey);
			//checkForExistingKeyInClient(survey);
		} catch (Exception e) {
			e.printStackTrace();
			var be = new BusinessException(e.getMessage(), e);
			event.rerouteToError(be, be.getMessage());
		}
//		ThemeController.setTheme(Lumo.DARK);
	}

	private void checkForExistingKeyInClient(Survey survey) {
		SurveyKeyClientStoreConnector.getCurrent().hasKey(survey.key(), hasKey -> {
			System.out.println("[SurveyView] rpc response is: " + hasKey);
			if (hasKey) {
				UI.getCurrent().navigate(SurveyAlreadySubmittedView.class);
			}
		});
	}

	private void setupSurveyComponent(Survey survey) {
		var comp = new SurveyComponent(survey);
		comp.setSizeFull();
		comp.addCompleteListener(cEvent -> {
			var jsonStr = cEvent.getJson().toJson();
			System.out.println("[SurveyView] " + jsonStr);
			var sourceSurvey = cEvent.getSource().getSurvey();
			try {
				boolean success = this.dummyStore.submitResult(sourceSurvey, jsonStr);
				System.out.println("[SurveyView] submitted result: " + success);
				if (success) {
					System.out.println("[SurveyView] adding key to client store: " + sourceSurvey.key());
					SurveyKeyClientStoreConnector.getCurrent().putKey(sourceSurvey.key(), wasAdded -> {
						System.out.println("[SurveyView] the key was added to the client store: " + wasAdded);
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		removeAll();
		add(comp);
	}

	@Override
	public void beforeLeave(BeforeLeaveEvent event) {

	}

}
