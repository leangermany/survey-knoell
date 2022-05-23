package com.knoell.survey.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.router.Route;

@Route("dev/richtexteditor")
public class DevRichTextFieldView extends VerticalLayout {
	private static final long serialVersionUID = 950493839793185243L;

	RichTextEditor editor = new RichTextEditor();
	Div div = new Div();

	public DevRichTextFieldView() {
		editor.asHtml().addValueChangeListener(vcEvent -> {
			div.setText(vcEvent.getValue());
		});
		editor.setWidthFull();
		add(editor, div);
		setSizeFull();
	}

}
