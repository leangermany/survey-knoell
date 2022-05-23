package com.knoell.survey.views;

import java.util.List;
import java.util.Objects;

import com.knoell.survey.controller.ThemeController;
import com.knoell.survey.views.legal.ImprintView;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLayout;

/**
 * the main routerlayout of the application. It contains a header, a content
 * wrapper and a footer. If on enter the theme is applied on the optional query
 * parameter.
 * 
 * @author lam
 *
 */
public class MainLayout extends Div implements RouterLayout, BeforeEnterObserver {
	private static final long serialVersionUID = 1413516251367726843L;

	// header
	private final Header header = new Header();
	private final Span span = new Span();
	private final Image logo = new Image();
	private final Div links = new Div();
	private final Button themeButton = new Button(VaadinIcon.ADJUST.create());

	// main
	private final Main main = new Main();

	// footer
	private final Footer footer = new Footer();

	public MainLayout() {
		setupLayout();
		setupHeader();
		span.setText("knoell survey");
	}

	private void setupLayout() {
		header.addClassName("ks-header");
		main.addClassName("ks-main");
		footer.addClassName("ks-footer");
		addClassName("ks-router");
		add(header, main);
	}

	private void setupHeader() {
		header.add(logo, span, links);
		logo.addClassNames("ks-logo", "ks-header-logo");
		span.addClassNames("ks-header-caption");
		links.addClassNames("ks-header-links");
		themeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL,
				ButtonVariant.LUMO_TERTIARY_INLINE);
		themeButton.addClickListener(__ -> ThemeController.toggleTheme());
		logo.setSrc("./Knoell_Logo_4c3_slim.png");
		logo.setAlt("logo");
		var impressumLink = RouteConfiguration.forApplicationScope().getUrl(ImprintView.class);
		var dataLink = RouteConfiguration.forApplicationScope().getUrl(ImprintView.class);
		var impressumAnchor = new Anchor(impressumLink, "Impressum – Legal Notice", AnchorTarget.BLANK);
		impressumAnchor.addClassName("ks-header-link");
		var dataAnchor = new Anchor(dataLink, "Data policy", AnchorTarget.BLANK);
		dataAnchor.addClassName("ks-header-link");
		links.add(themeButton, impressumAnchor, dataAnchor);
	}

	@Override
	public void showRouterLayoutContent(HasElement content) {
		main.getElement().appendChild(Objects.requireNonNull(content.getElement()));
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		var queryParameters = event.getLocation().getQueryParameters();
		List<String> list = queryParameters.getParameters().get("theme");
		if (list != null && !list.isEmpty()) {
			String themeName = list.get(0);
			ThemeController.setTheme(themeName);
		} else if (!getElement().getNode().isAttached()) {
			ThemeController.setThemeByCookie();
		}
	}

}
