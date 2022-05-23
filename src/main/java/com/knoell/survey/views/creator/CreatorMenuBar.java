package com.knoell.survey.views.creator;

import java.util.UUID;

import com.knoell.survey.components.ICreatorView;
import com.knoell.survey.components.SurveyCreatorComponent;
import com.knoell.survey.store.CreatorHistoryStore;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouteParameters;

/**
 * This Component is displayed above the {@link SurveyCreatorComponent} in the
 * {@link CreatorView}. The user can interact via {@link MenuBar} with the
 * creator.
 * 
 * @author lam
 *
 */
public class CreatorMenuBar extends Div {
	private static final long serialVersionUID = 406734517165465964L;

	// data
	private ICreatorView creatorView;

	// components
	private final MenuBar menuBar = new MenuBar();

	// menu items
	private MenuItem newItem;
	private MenuItem saveItem;
	private MenuItem copyItem;
	private MenuItem pasteItem;
	private MenuItem clearItem;

	// layouts
	private final FlexLayout buttonLayout = new FlexLayout();

	public CreatorMenuBar(ICreatorView creatorView) {
		super();
		this.creatorView = creatorView;
		setupLayout();
		setupMenuBar();
	}

	private void setupLayout() {
		buttonLayout.add(menuBar);
		buttonLayout.setAlignItems(Alignment.CENTER);
		buttonLayout.getElement().getStyle().set("gap", "var(--lumo-space-m)");
		buttonLayout.getElement().getStyle().set("margin", "var(--lumo-space-xs) var(--lumo-space-m)");
		getStyle().set("box-shadow", "inset 0 -1px 0 0 var(--lumo-contrast-10pct)");
		add(buttonLayout);
	}

	private void setupMenuBar() {
		menuBar.addThemeVariants(MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_PRIMARY);
		menuBar.setOpenOnHover(true);
		menuBar.setMaxWidth("100%");

		// open new tab
		newItem = addMenuItem("New Tab", VaadinIcon.PLUS.create(), __ -> {

			UI.getCurrent().getPage().executeJs("window.open($0, '_blank').focus();", setupNewTabLink());

		});
		newItem.getElement().setAttribute("title", "(Alt + N) Creates a new Survey.");
		newItem.addClickShortcut(Key.KEY_N, KeyModifier.ALT);

		// open new tab
		saveItem = addMenuItem("Save", VaadinIcon.CHECK.create(), __ -> {

			openDialog("Save",
					"Do you want to save the current configuration into the backend? "
							+ "This action overrides the old configuration of the survey.",
					this.creatorView::saveEditSurvey);

		});
		saveItem.getElement().setAttribute("title", "(Alt + S) Saves the survey into the backend.");
		saveItem.addClickShortcut(Key.KEY_S, KeyModifier.ALT);
		saveItem.setVisible(false);
		saveItem.setEnabled(false);

		// copy config to clipboard
		copyItem = addMenuItem("Copy Config", VaadinIcon.COPY_O.create(), __ -> {

			creatorView.getCurrentCreator().copyJsonToClipboard();

		});
		copyItem.getElement().setAttribute("title", "(Alt + C) Copy the survey configuration to your clipboard.");
		SubMenu copyItemSubMenu = copyItem.getSubMenu();

		// copy config to clipboard (submenu)
		MenuItem copySubItem = copyItemSubMenu.addItem("Copy Config to Clipboard", __ -> {

			creatorView.getCurrentCreator().copyJsonToClipboard();

		});
		copySubItem.getElement().setAttribute("title", "(Alt + C) Copy the survey configuration to your clipboard.");
		copySubItem.addClickShortcut(Key.KEY_C, KeyModifier.ALT);

		// open a new tab and copy the config (submenu)
		MenuItem copyToNewTabSubItem = copyItemSubMenu.addItem("Copy Config into new Tab", __ -> {

			creatorView.getCurrentCreator().recievelientJson(jsonText -> {
				String newKey = UUID.randomUUID().toString();
				var store = CreatorHistoryStore.get().getHistory(newKey);
				store.addHistory(jsonText);
				UI.getCurrent().getPage().executeJs("window.open($0, '_blank').focus();",
						setupNewTabLinkWithParameter(newKey));
			});

		});
		copyToNewTabSubItem.getElement().setAttribute("title",
				"(Alt + T) Open a new Tab with a copy of the survey configuration.");
		copyToNewTabSubItem.addClickShortcut(Key.KEY_T, KeyModifier.ALT);

		// paste config
		pasteItem = addMenuItem("Paste Config", VaadinIcon.PASTE.create(), __ -> {

			openDialog("Paste", "Do you want to override the current configuration?",
					() -> creatorView.getCurrentCreator().pasteJsonFromClipboard());

		});
		pasteItem.getElement().setAttribute("title", "(Alt + V) Paste the survey configuration from your clipboard.");
		pasteItem.addClickShortcut(Key.KEY_V, KeyModifier.ALT);

		// apply the default config
		clearItem = addMenuItem("Clear", VaadinIcon.TRASH.create(), __ -> {

			openDialog("Clear", "Do you want to delete the current configuration? All data will be lost.",
					() -> creatorView.getCurrentCreator().clear());

		});
		clearItem.getElement().setAttribute("title", "(Alt + R) Clears all data.");
		clearItem.addClickShortcut(Key.KEY_R, KeyModifier.ALT);
	}

	private MenuItem addMenuItem(String text, Component icon,
			ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
		icon.getElement().getStyle().set("height", "var(--lumo-space-m)");
		icon.getElement().getStyle().set("width", "var(--lumo-space-m)");
		var layout = new HorizontalLayout(icon, new Span(text));
		layout.getElement().getStyle().set("gap", "var(--lumo-space-s)");
		return menuBar.addItem(layout, clickListener);
	}

	private String setupNewTabLink() {
		final String path = RouteConfiguration.forApplicationScope().getUrl(CreatorView.class);
		return path;
	}

	private String setupNewTabLinkWithParameter(String key) {
		final String path = RouteConfiguration.forApplicationScope().getUrl(CreatorView.class,
				new RouteParameters("key", key));
		return path;
	}

	private void openDialog(String caption, String text, Runnable confirmAction) {
		var d = new ConfirmDialog(caption, text, "OK", e -> confirmAction.run());
		d.setCancelable(true);
		d.setCancelText("Abort");
		d.open();
	}

	public void setNavigationEnable(boolean enabled) {
		newItem.setEnabled(true);
		saveItem.setEnabled(true);
		copyItem.setEnabled(true);
		pasteItem.setEnabled(true);
		clearItem.setEnabled(true);
	}

	public void setSaveItemVisible(boolean visible) {
		this.saveItem.setVisible(visible);
		this.saveItem.setEnabled(visible);
	}

}
