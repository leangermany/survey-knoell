package com.knoell.survey.views.creator;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.knoell.survey.api.KermitSurveyApi;
import com.knoell.survey.components.ICreatorComponent;
import com.knoell.survey.components.ICreatorView;
import com.knoell.survey.components.SurveyCreatorComponent;
import com.knoell.survey.components.SurveyCreatorConnector;
import com.knoell.survey.exception.BusinessException;
import com.knoell.survey.exception.ResponseException;
import com.knoell.survey.views.MainLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;

/**
 * This view contains a {@link CreatorMenuBar} and the
 * {@link SurveyCreatorComponent}.
 * 
 * @author lam
 *
 */
@Route(value = "creator-frame/:key?", layout = MainLayout.class)
@PageTitle("Survey Creator - knoell")
@JsModule("./survey/SurveyStorage.ts")
@CssImport("./survey/defaultV2.min.css")
@CssImport("./survey/survey-creator-core.min.css")
//@JsModule("./survey.core.min.js")
//@JsModule("./survey-knockout-ui.min.js")
//@JsModule("./survey-creator-core.min.js")
//@JsModule("./survey-creator-knockout.min.js")
public class CreatorIFrameView extends VerticalLayout implements ICreatorView, BeforeEnterObserver {
	private static final long serialVersionUID = 4722220173924265837L;

	// data
	private final KermitSurveyApi kermitSurveyApi;

	// components
	private final CreatorMenuBar menuBar = new CreatorMenuBar(this);
	private SurveyCreatorConnector creator;

	public CreatorIFrameView(@Autowired KermitSurveyApi kermitSurveyApi) {
		this.kermitSurveyApi = kermitSurveyApi;
		setupLayout();
	}

	private void setupLayout() {
		add(menuBar);
		setPadding(false);
		setSpacing(false);
		setSizeFull();
		setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
	}

	@Override
	public ICreatorComponent getCurrentCreator() {
		return this.creator;
	}

	public void saveEditSurvey() {
		try {
			this.creator.recievelientJson(jsonText -> {
				try {
					final var editSurvey = this.creator.getEditSurvey();
					this.kermitSurveyApi.saveSurvey(editSurvey, jsonText);
					Notification.show("The survey was successfully saved in the backend!", 5000, Position.BOTTOM_START)
							.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				} catch (ResponseException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {

		if (event.getRouteParameters().get("key").isEmpty()) {
			var key = UUID.randomUUID().toString();
			event.forwardTo(CreatorIFrameView.class, new RouteParameters("key", key));
			return;
		} else {
			var key = event.getRouteParameters().get("key").get();
			this.creator = new SurveyCreatorConnector(key);
			this.creator.setSizeFull();
			add(creator);

			final var queryParameters = event.getLocation().getQueryParameters().getParameters();
			if (queryParameters.containsKey("edit") && queryParameters.get("edit").size() == 1) {
				final var editId = queryParameters.get("edit").get(0);
				if (editId != null) {
					try {
						var editSurvey = kermitSurveyApi.editSurvey(editId);
						this.creator.setEditSurvey(editSurvey, true);
						this.menuBar.setSaveItemVisible(true);
					} catch (ResponseException e) {
						event.rerouteToError(e, e.getMessage());
						return;
					} catch (Exception e) {
						e.printStackTrace();
						var be = new BusinessException(e.getMessage(), e);
						event.rerouteToError(be, be.getMessage());
						return;
					}
				}
			}
		}

	}

}
