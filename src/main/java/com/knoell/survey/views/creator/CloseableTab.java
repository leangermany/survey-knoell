package com.knoell.survey.views.creator;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.shared.Registration;

/**
 * a tab with a close button.
 * 
 * @author lam
 *
 */
public class CloseableTab extends Tab implements HasLabel {
	private static final long serialVersionUID = 8284048032450593310L;

	// components
	public Span caption = new Span();
	public Button closeButton = new Button(VaadinIcon.CLOSE_SMALL.create());

	// attributes
	private boolean closable = false;

	public CloseableTab() {
		super();
		setupLayout();
	}

	public CloseableTab(String label) {
		this();
		setCaption(label);
	}

	public CloseableTab(String label, boolean closable) {
		this(label);
		setClosable(closable);
	}

	private void setupLayout() {
		closeButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE);
		closeButton.setVisible(false);
		closeButton.getStyle().set("margin-left", "var(--lumo-space-xs)");
		getElement().appendChild(caption.getElement(), closeButton.getElement());
		getElement().getStyle().set("align-items", "center");
	}

	public boolean isClosable() {
		return closable;
	}

	public void setClosable(boolean closable) {
		this.closable = closable;
		closeButton.setVisible(this.closable);
	}

	public Registration addTabCloseListener(ComponentEventListener<ClickEvent<Button>> listener) {
		return closeButton.addClickListener(listener);
	}

	public void setCaption(String label) {
		this.caption.setText(label);
	}

	public String getCaption() {
		return this.caption.getText();
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
	}

	public boolean isSelected() {
		return super.isSelectedBoolean();
	}

}