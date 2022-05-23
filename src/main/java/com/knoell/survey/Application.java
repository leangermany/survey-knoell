package com.knoell.survey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@SpringBootApplication
@Push
@Theme(value = "survey", variant = Lumo.LIGHT)
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {
	private static final long serialVersionUID = -2997665962667785229L;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
