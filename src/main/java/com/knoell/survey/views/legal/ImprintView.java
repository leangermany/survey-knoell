package com.knoell.survey.views.legal;

import java.io.InputStream;

import com.knoell.survey.views.MainLayout;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "legal/imprint", layout = MainLayout.class)
@PageTitle("Imprint - Survey - knoell")
public class ImprintView extends VerticalLayout {
	private static final long serialVersionUID = 7028068947067612420L;

	public ImprintView() {
		InputStream resourceAsStream = ImprintView.class.getClassLoader().getResourceAsStream("legal/imprint.html");
		Html html = new Html(resourceAsStream);
		add(html);
	}

}
