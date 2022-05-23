package com.knoell.survey.exception;

import com.knoell.survey.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.ParentLayout;

@ParentLayout(MainLayout.class)
public class BusinessExceptionView extends VerticalLayout implements HasErrorParameter<BusinessException> {
	private static final long serialVersionUID = 5760676793542870998L;

	private final Image image = new Image("./crosscirclelinear_106172.svg", "");
	private final H2 caption = new H2("Oh no...");
	private final Paragraph cause = new Paragraph("An unexpected error occured.");

	public BusinessExceptionView() {
		image.setMaxHeight("150px");
		image.setMaxWidth("150px");
		cause.getElement().getStyle().set("text-align", "center");
		add(image, caption, cause);
		getStyle().set("padding-top", "var(--lumo-space-xl)");
		setDefaultHorizontalComponentAlignment(Alignment.CENTER);
	}

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<BusinessException> parameter) {

		return 500;
	}

}
