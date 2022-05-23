package com.knoell.survey.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;

import com.knoell.survey.api.KermitSurveyApi;
import com.knoell.survey.components.SurveyComponent;
import com.knoell.survey.exception.ResponseException;
import com.knoell.survey.records.Survey;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(registerAtStartup = false)
@Deprecated
public class ApiView extends VerticalLayout {

	private static final String survey = "92a3439e-87b5-4d6c-855f-a638ace02354";
	private static final String jochen = "6ed085f5-96a7-4765-a924-931398d8dae2";
	private static final String leander = "ff3b7c1e-2e88-4187-a229-654491902b22";
	private static final String KERMIT = "https://kermit.knoell.com/a83f440b-127b-4e81-a4ab-87b85485deab/survey/read/";

	private final KermitSurveyApi kermitSurveyApi;

	public ApiView(@Autowired KermitSurveyApi kermitSurveyApi) {
		this.kermitSurveyApi = kermitSurveyApi;

		var urlString = String.format(KERMIT + "%s", leander);

		try {
			Survey survey = this.kermitSurveyApi.checkSurvey(leander);
			var comp = new SurveyComponent(survey);
			add(comp);
		} catch (IOException | ResponseException e) {
			e.printStackTrace();
		}

//				HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
//				System.out.println(id + " " + connection.getResponseCode());
//				if (connection.getResponseCode() == 200) {
//					byte[] bytes = connection.getInputStream().readAllBytes();
//					var json1 = new String(bytes, Charset.forName("UTF-8"));
//					System.out.println(id + " " + json1);
//					add(new Span(id + " " + json1));
//				}

	}

	public static String readJsonFromUrl(String url) throws IOException {
		URL url2 = new URL(url);
		URLConnection openConnection = url2.openConnection();
		Object content = openConnection.getContent();
		System.out.println(content);
		InputStream is = url2.openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			StringBuilder sb = new StringBuilder();
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}
			return sb.toString();
		} finally {
			is.close();
		}
	}

}
