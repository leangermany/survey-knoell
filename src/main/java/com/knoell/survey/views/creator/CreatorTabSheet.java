package com.knoell.survey.views.creator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.SelectedChangeEvent;
import com.vaadin.flow.component.tabs.TabsVariant;

@Deprecated
public class CreatorTabSheet extends Composite<FlexLayout> implements HasSize {
	private static final long serialVersionUID = 5190769084220838959L;

	private Map<Tab, Component> tabContent = new HashMap<>();

	private Tabs tabBar = new Tabs(false);
	private FlexLayout contentContainer = new FlexLayout();

	public CreatorTabSheet() {
		setupLayout();
		setupLogic();
	}

	private void setupLayout() {
		tabBar.setWidthFull();
		contentContainer.setSizeFull();
		getElement().appendChild(tabBar.getElement(), contentContainer.getElement());
		getContent().setFlexDirection(FlexDirection.COLUMN);
	}

	private void setupLogic() {
		tabBar.addSelectedChangeListener(listener -> {
			contentContainer.removeAll();
			Component c = tabContent.get(tabBar.getSelectedTab());
			if (c != null) {
				contentContainer.add(c);
			}
		});
	}

	public CloseableTab addTab(String caption, Component content) {
		CloseableTab tab = new CloseableTab(caption);
		tab.addTabCloseListener(e -> removeTab(tab));
		addTab(tab, content);
		return tab;
	}

	public CloseableTab addTab(String caption, Component content, boolean closable) {
		CloseableTab tab = new CloseableTab(caption, closable);
		tab.addTabCloseListener(e -> removeTab(tab));
		addTab(tab, content);
		return tab;
	}

	public Tab addTab(Tab tab, Component content) {
		Objects.requireNonNull(tab, "added Tab is null");
		tabBar.add(tab);
		this.tabContent.put(tab, content);
		return tab;
	}

	public void add(Component component) {
		tabBar.add(component);
	}

	public void removeTab(Tab tab) {
		tabBar.remove(tab);
		this.tabContent.remove(tab);
	}

	public void remove(Component component) {
		if (component instanceof Tab) {
			removeTab((Tab) component);
		} else {
			tabBar.remove(component);
		}
	}

	public void selectTab(Tab tab) {
		tabBar.setSelectedTab(tab);
	}

	public Tab getSelectedTab() {
		return tabBar.getSelectedTab();
	}

	public void addSelectedChangeListener(ComponentEventListener<SelectedChangeEvent> listener) {
		tabBar.addSelectedChangeListener(listener);
	}

	public void addThemeVariants(TabsVariant... variants) {
		tabBar.addThemeVariants(variants);
	}

	public void removeThemeVariants(TabsVariant... variants) {
		tabBar.removeThemeVariants(variants);
	}

	public void setAutoselect(boolean autoselect) {
		tabBar.setAutoselect(autoselect);
	}

	public boolean isAutoselect() {
		return tabBar.isAutoselect();
	}

}
