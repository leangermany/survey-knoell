package com.knoell.survey.views.creator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.knoell.survey.components.ICreatorComponent;
import com.knoell.survey.components.ICreatorView;
import com.knoell.survey.components.SurveyCreatorComponent;
import com.knoell.survey.exception.BusinessException;
import com.knoell.survey.exception.ResponseException;
import com.knoell.survey.records.Survey;
import com.knoell.survey.store.DemoSurveyDummyStore;
import com.knoell.survey.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
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
@Route(value = "creator/:key?", layout = MainLayout.class)
@PageTitle("Survey Creator - knoell")
@JsModule("./survey/SurveyStorage.ts")
public class CreatorView extends VerticalLayout implements ICreatorView, BeforeEnterObserver {
	private static final long serialVersionUID = 4722220173924265837L;

	// data
	private final DemoSurveyDummyStore dummyStore;
//	private Registration browserWindowResizeListenerRegistration;
	@Deprecated
	private final Map<Tab, Component> tabContent = new HashMap<>();
	private int counter = 1;

	// components
	private SurveyCreatorComponent creator;
	private final CreatorMenuBar menuBar = new CreatorMenuBar(this);
	@Deprecated
	private Tabs tabBar = new Tabs(false);

	public CreatorView(@Autowired DemoSurveyDummyStore dummyStore) {
		this.dummyStore = dummyStore;
		setupLayout();
//		setupTabs();
//		UI.getCurrent().getElement().executeJs("Survey.surveyLocalization.supportedLocales = [\"en\", \"de\"];");
	}

	private void setupLayout() {
		add(menuBar);
		setPadding(false);
		setSpacing(false);
		setSizeFull();
		setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
	}

	private void setupSingleTab() {
		remove(tabBar);
		add(creator);
		menuBar.setNavigationEnable(true);
		creator.setSizeFull();
		creator.setMinHeight("500px");
	}

	@SuppressWarnings("unused")
	private void setupTabs() {
		tabBar.setWidthFull();
		tabBar.setAutoselect(true);
		tabBar.addThemeVariants(TabsVariant.LUMO_SMALL);
		add(tabBar);
		Tab tab = new Tab("Info");
		this.tabContent.put(tab, new Span("Here will be some info"));
		tabBar.add(tab);
		tabBar.addSelectedChangeListener(listener -> {
			if (listener.getPreviousTab() != null) {
				var oldComp = tabContent.get(listener.getPreviousTab());
				remove(oldComp);
			}
			Component c = tabContent.get(tabBar.getSelectedTab());
			if (c != null) {
				add(c);
			}
			menuBar.setNavigationEnable(c instanceof SurveyCreatorComponent);
		});
		addTab(null);
	}

	public Tab addTab(Survey survey) {
		String caption = "Survey " + counter++;
		CloseableTab tab = new CloseableTab(caption, true);
		tab.addTabCloseListener(e -> removeTab(tab));
		var creator = new SurveyCreatorComponent(UUID.randomUUID().toString());
		creator.setSizeFull();
		creator.addSaveSurveyListener(ssEvent -> {
			var jsonObject = ssEvent.getJson();
			var titleObject = jsonObject.get("title");
			if (titleObject != null) {
				var titleString = titleObject.asString();
				if (titleString.length() > 10) {
					titleString = titleString.substring(0, 10) + "...";
				}
				tab.setCaption(titleString);
			}
		});
		this.tabContent.put(tab, creator);
		tabBar.add(tab);
		return tab;
	}

	public void removeTab(Tab tab) {
		tabBar.remove(tab);
		this.tabContent.remove(tab);
	}

	public ICreatorComponent getCurrentCreator() {
		return creator;
//		Tab selectedTab = tabBar.getSelectedTab();
//		return (SurveyCreatorComponent) tabContent.get(selectedTab);
	}

	public void saveEditSurvey() {
		try {
			this.creator.recievelientJson(jsonText -> {
				try {
					final var editSurvey = this.creator.getEditSurvey();
					this.dummyStore.saveSurvey(editSurvey, jsonText);
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
			event.forwardTo(CreatorView.class, new RouteParameters("key", key));
			return;
		} else {
			var key = event.getRouteParameters().get("key").get();
			this.creator = new SurveyCreatorComponent(key);
			this.menuBar.setSaveItemVisible(false);
			setupSingleTab();

			final var queryParameters = event.getLocation().getQueryParameters().getParameters();
			if (queryParameters.containsKey("edit") && queryParameters.get("edit").size() == 1) {
				final var editId = queryParameters.get("edit").get(0);
				System.out.println("[CreatorView] has edit parameter: " + editId);
				if (editId != null) {
					try {
						var editSurvey = dummyStore.editSurvey(editId);
						System.out.println("[CreatorView] loaded config by edit id: " + editSurvey);
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

		UI.getCurrent().getPage().retrieveExtendedClientDetails(clientDetails -> {
			if (clientDetails.getBodyClientWidth() <= 600) {
				// notify user that under 600 Pixels the creator is wrong diplayed
				openWidthDialog("Please use a browser window width of at least 600 Pixel to avaid misrepresentation.");
			} else if (clientDetails.getBodyClientWidth() <= 1200) {
				// notify user that under 1200 Pixels the ui is compact
				openWidthDialog(
						"We recommend to use a browser window width of at least 1200 Pixel for the best experience.");
			}
		});
//		if (browserWindowResizeListenerRegistration != null) {
//			browserWindowResizeListenerRegistration.remove();
//		}
//		browserWindowResizeListenerRegistration = UI.getCurrent().getPage().addBrowserWindowResizeListener(bwrEvent -> {
//			openWidthDialog(
//					"Due to a bug in the creator, the toolbox on the left disappears after the window is resized. Refresh the browser tab to redisplay the Toolbox.");
//			browserWindowResizeListenerRegistration.remove();
//			browserWindowResizeListenerRegistration = null;
//		});

	}

	private void openWidthDialog(String text) {
		var d = new ConfirmDialog();
		d.setHeader("Attention");
		d.setText(text);
		d.setConfirmText("OK");
		d.open();
	}

}
