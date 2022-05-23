package com.knoell.survey.views.survey;

import com.knoell.survey.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * routing via SurveyJS completedUrl property to this view, will result in a
 * client sided navigation and no vaadin navigation, what reloads the framework.
 * 
 * @author lam
 *
 */
@Deprecated
@Route(value = "completed", layout = MainLayout.class, registerAtStartup = false)
public class CompletedView extends VerticalLayout {
	private static final long serialVersionUID = -8310481287283925842L;

	private final Image image = new Image("./tickcirclelinear_106244.svg", "completed image");
	private final H2 caption = new H2("Survey completed");
	private final Paragraph thanks = new Paragraph("Thanks for completing this survey provided by knoell.");
	private final Paragraph info = new Paragraph("You do not need to do anything else and you can close this page.");

	public CompletedView() {
		image.setMaxHeight("150px");
		image.setMaxWidth("150px");
		add(image, caption, thanks, info);
		getStyle().set("padding-top", "var(--lumo-space-xl)");
		thanks.getElement().getStyle().set("text-align", "center");
		info.getElement().getStyle().set("text-align", "center");
		setDefaultHorizontalComponentAlignment(Alignment.CENTER);
	}

}
