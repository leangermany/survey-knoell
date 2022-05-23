package com.knoell.survey.store;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.knoell.survey.api.ISurveyApi;
import com.knoell.survey.exception.ResponseException;
import com.knoell.survey.records.EditSurvey;
import com.knoell.survey.records.Result;
import com.knoell.survey.records.Survey;

@Service
public class DemoSurveyDummyStore implements ISurveyApi {

	private final HashMap<String, Survey> surveys = new HashMap<>();
	private final HashMap<String, ArrayList<Result>> results = new HashMap<>();

	public DemoSurveyDummyStore() {

		// create dummy survey
		try {
			var stream = DemoSurveyDummyStore.class.getClassLoader().getResourceAsStream("demosurvey.json");
			var surveyConfig = new String(stream.readAllBytes(), Charset.forName("UTF-8"));
			var key = "demosurvey";
			surveys.put(key, new Survey(key, surveyConfig.trim()));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Survey checkSurvey(String surveyId) throws ResponseException, Exception {
		try {
			var survey = surveys.get(surveyId);
			var exists = survey == null ? 404 : 200;
			switch (exists) {
			case 200:
				return survey;
			case 404:
			default:
				throw new ResponseException(exists, "The survey was not found.");
			}
		} catch (ResponseException e) {
			throw e;
		}
	}

	@Override
	public boolean submitResult(String surveyId, String resultJson) throws ResponseException {
		assert surveyId != null && !surveyId.isBlank();
		assert resultJson != null && !resultJson.isBlank();
		if (!surveys.containsKey(surveyId)) {
			throw new ResponseException(404, "The survey was not found.");
		}
		var result = new Result(surveyId, resultJson);
		var resultList = results.get(surveyId);
		if (resultList == null) {
			resultList = new ArrayList<Result>();
			results.put(surveyId, resultList);
		}
		resultList.add(result);
		return true;
	}

	@Override
	public EditSurvey editSurvey(String editId) throws ResponseException, Exception {
		try {
			var survey = surveys.get(editId);
			var exists = survey == null ? 404 : 200;
			switch (exists) {
			case 200:
				return new EditSurvey(editId, survey.json());
			case 404:
			default:
				throw new ResponseException(exists, "The survey was not found.");
			}
		} catch (ResponseException e) {
			throw e;
		}
	}

	@Override
	public boolean saveSurvey(String editId, String configJson) throws ResponseException, Exception {
		assert editId != null && !editId.isBlank();
		assert configJson != null && !configJson.isBlank();
		if (!surveys.containsKey(editId)) {
			throw new ResponseException(404, "The survey was not found.");
		}
		surveys.put(editId, new Survey(editId, configJson));
		return true;
	}

}
