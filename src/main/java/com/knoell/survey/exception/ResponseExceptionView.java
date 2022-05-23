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
public class ResponseExceptionView extends VerticalLayout implements HasErrorParameter<ResponseException> {
	private static final long serialVersionUID = 243358429569254966L;

	private final Image image = new Image("./crosscirclelinear_106172.svg", "Not found image");
	private final H2 caption = new H2("Not Found");
	private final Paragraph cause = new Paragraph("The survey was not found.");

	public ResponseExceptionView() {
		image.setMaxHeight("150px");
		image.setMaxWidth("150px");
		cause.getElement().getStyle().set("text-align", "center");
		add(image, caption, cause);
		getStyle().set("padding-top", "var(--lumo-space-xl)");
		setDefaultHorizontalComponentAlignment(Alignment.CENTER);
	}

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<ResponseException> parameter) {
		cause.setText(parameter.getCustomMessage());
		return parameter.getException().getResponseCode();
	}

}
