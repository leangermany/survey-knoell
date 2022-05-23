package com.knoell.survey.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.knoell.survey.exception.ResponseException;
import com.knoell.survey.records.EditSurvey;
import com.knoell.survey.records.Survey;

/**
 * Implementation to communicate with the kermit api. The spring boot annotation
 * {@link Service} is used to use {@link Autowired} in vaadin views.
 * 
 * @author lam
 *
 */
@Service
public class KermitSurveyApi implements ISurveyApi {

	/*
	 * @Value loads the value from the application.properties
	 */

	@Value("${surveyapi.kermit.read}")
	private String baseUrlRead;
	@Value("${surveyapi.kermit.submit}")
	private String baseUrlSubmit;
	@Value("${surveyapi.kermit.edit}")
	private String baseUrlEdit;
	@Value("${surveyapi.kermit.save}")
	private String baseUrlSave;

	@Override
	public Survey checkSurvey(String id) throws ResponseException, IOException {
		try {
			var con = openConnection(baseUrlRead, id);
			int responseCode = con.getResponseCode();
			switch (responseCode) {
			case 200:
				var json = readInputStream(con);
				return new Survey(id, json);
			case 404:
				throw new ResponseException(responseCode, "The survey was not found.");
			case 409:
				throw new ResponseException(responseCode, "The survey was already submitted.");
			case 410:
				throw new ResponseException(responseCode, "The survey has not yet started or has expired.");
			case 418:
				throw new ResponseException(responseCode, "Unfortunately, the survey fell into a tea kettle.");
			default:
				Logger logger = Logger.getLogger(KermitSurveyApi.class.getName());
				logger.log(Level.SEVERE, "The backend responded with error code: " + responseCode);
				throw new ResponseException(responseCode, "An unexpected error occured.");
			}
		} catch (ResponseException e) {
			throw e;
		} catch (IOException e) {
			System.out.println("[KermitBackend#checkSurvey] An IOException occured: " + e.getMessage());
			throw e;
		}
	}

	@Override
	public boolean submitResult(String surveyId, String resultJson) throws ResponseException, IOException {
		assert surveyId != null && !surveyId.isBlank();
		assert resultJson != null && !resultJson.isBlank();
		HttpURLConnection con = null;
		try {
			con = openConnection(baseUrlSubmit, surveyId);
			con.setRequestMethod("POST");
			writeOutputStream(con, resultJson);
			int responseCode = con.getResponseCode();
			switch (responseCode) {
			case 200:
				return true;
			case 404:
				throw new ResponseException(responseCode, "The survey was not found.");
			case 409:
				throw new ResponseException(responseCode, "The survey was already submitted.");
			case 410:
				throw new ResponseException(responseCode, "The survey has not yet started or has expired.");
			case 418:
				throw new ResponseException(responseCode, "Unfortunately, the survey fell into a tea kettle.");
			default:
				Logger logger = Logger.getLogger(KermitSurveyApi.class.getName());
				logger.log(Level.SEVERE, "The backend responded with error code: " + responseCode);
				throw new ResponseException(responseCode, "An unexpected error occured.");
			}
		} catch (ResponseException e) {
			throw e;
		} catch (IOException e) {
			System.out.println("[KermitBackend#submitResult] An IOException occured: " + e.getMessage());
			throw e;
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	@Override
	public EditSurvey editSurvey(String editId) throws ResponseException, Exception {
		assert editId != null && !editId.isBlank();
		HttpURLConnection con = null;
		try {
			con = openConnection(baseUrlEdit, editId);
			int responseCode = con.getResponseCode();
			switch (responseCode) {
			case 200:
				var json = readInputStream(con);
				return new EditSurvey(editId, json);
			case 404:
				throw new ResponseException(responseCode, "The survey was not found.");
			case 418:
				throw new ResponseException(responseCode, "Unfortunately, the survey fell into a tea kettle.");
			default:
				Logger logger = Logger.getLogger(KermitSurveyApi.class.getName());
				logger.log(Level.SEVERE, "The backend responded with error code: " + responseCode);
				throw new ResponseException(responseCode, "An unexpected error occured.");
			}
		} catch (ResponseException e) {
			throw e;
		} catch (IOException e) {
			System.out.println("[KermitBackend#editSurvey] An IOException occured: " + e.getMessage());
			throw e;
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	@Override
	public boolean saveSurvey(String editId, String configJson) throws ResponseException, Exception {
		assert editId != null && !editId.isBlank();
		HttpURLConnection con = null;
		try {
			con = openConnection(baseUrlSave, editId);
			con.setRequestMethod("POST");
			writeOutputStream(con, configJson);
			int responseCode = con.getResponseCode();
			switch (responseCode) {
			case 200:
				return true;
			case 404:
				throw new ResponseException(responseCode, "The survey was not found.");
			case 418:
				throw new ResponseException(responseCode, "Unfortunately, the survey fell into a tea kettle.");
			default:
				Logger logger = Logger.getLogger(KermitSurveyApi.class.getName());
				logger.log(Level.SEVERE, "The backend responded with error code: " + responseCode);
				throw new ResponseException(responseCode, "An unexpected error occured.");
			}
		} catch (ResponseException e) {
			throw e;
		} catch (IOException e) {
			System.out.println("[KermitBackend#saveSurvey] An IOException occured: " + e.getMessage());
			throw e;
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

}
