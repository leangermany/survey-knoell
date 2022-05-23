package com.knoell.survey.api;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import com.knoell.survey.exception.ResponseException;
import com.knoell.survey.records.EditSurvey;
import com.knoell.survey.records.Survey;

/**
 * A backend api to load and submit Surveys.
 * 
 * @author lam
 *
 */
public interface ISurveyApi {

	/**
	 * checks the backend for the survey id and returns the survey or
	 * 
	 * @param surveyId
	 * @return the survey
	 * @throws ResponseException
	 * @throws Exception
	 */
	Survey checkSurvey(String surveyId) throws ResponseException, Exception;

	/**
	 * submits the survey result linked to the survey id and returns true or throws
	 * an {@link ResponseException}.
	 * 
	 * @param surveyId
	 * @return true if the result was submitted successfully
	 * @throws ResponseException
	 * @throws Exception
	 */
	default boolean submitResult(Survey survey, String resultJson) throws ResponseException, Exception {
		return submitResult(survey.key(), resultJson);
	}

	/**
	 * submits the survey result linked to the survey id and returns true or throws
	 * an {@link ResponseException}.
	 * 
	 * @param surveyId
	 * @param resultJson
	 * @return
	 * @throws ResponseException
	 * @throws Exception
	 */
	boolean submitResult(String surveyId, String resultJson) throws ResponseException, Exception;

	/**
	 * returns the survey config for the given edit id
	 * 
	 * @param editId
	 * @return the survey
	 * @throws ResponseException
	 * @throws Exception
	 */
	EditSurvey editSurvey(String editId) throws ResponseException, Exception;
	

	/**
	 * saves the survey linked to the edit id and returns true or throws an
	 * {@link ResponseException}.
	 * 
	 * @param editId
	 * @param configJson
	 * @return
	 * @throws ResponseException
	 * @throws Exception
	 */
	default boolean saveSurvey(EditSurvey editSurvey, String configJson) throws ResponseException, Exception {
		return saveSurvey(editSurvey.editId(), configJson);
	}

	/**
	 * saves the survey linked to the edit id and returns true or throws an
	 * {@link ResponseException}.
	 * 
	 * @param editId
	 * @param configJson
	 * @return
	 * @throws ResponseException
	 * @throws Exception
	 */
	boolean saveSurvey(String editId, String configJson) throws ResponseException, Exception;

	default HttpURLConnection openConnection(String base, String id) throws IOException {
		final String combinedUrl = String.format("%s/%s", base, id);
		return openConnection(combinedUrl);
	}

	default HttpURLConnection openConnection(String url) throws IOException {
		final URL u = new URL(url);
		return (HttpURLConnection) u.openConnection();
	}

	default String readInputStream(HttpURLConnection connection) throws IOException {
		return readInputStream(connection, Charset.forName("UTF-8"));
	}

	default String readInputStream(HttpURLConnection connection, Charset charset) throws IOException {
		byte[] bytes = connection.getInputStream().readAllBytes();
		return new String(bytes, charset);
	}

	default void writeOutputStream(HttpURLConnection connection, String content) throws IOException {
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/json");
		var os = connection.getOutputStream();
		DataOutputStream out = new DataOutputStream(os);
		out.writeBytes(content);
		out.flush();
		out.close();
	}

}
