package com.knoell.survey.store;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import com.knoell.survey.components.SurveyCreatorComponent;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;

/**
 * Atores the {@link CreatorHistory} object linked to the key a
 * {@link SurveyCreatorComponent}.
 * 
 * @author lam
 *
 */
public class CreatorHistoryStore {

	/*
	 * 
	 * stored in session
	 * 
	 */

	public static CreatorHistoryStore get() {
		return get(VaadinSession.getCurrent());
	}

	public static CreatorHistoryStore get(VaadinSession session) {
		var store = session.getAttribute(CreatorHistoryStore.class);
		if (store == null) {
			store = new CreatorHistoryStore();
			session.setAttribute(CreatorHistoryStore.class, store);
		}
		return store;
	}

	;

	/*
	 * 
	 * the store
	 * 
	 */

	private final HashMap<String, CreatorHistory> creators = new HashMap<>();

	private CreatorHistoryStore() {}

	public CreatorHistory getHistory(String key) {
		var history = creators.get(key);
		if (history == null) {
			history = new CreatorHistory(key);
			creators.put(key, history);
		}
		return history;
	}

	/**
	 * Stores the last 20 changes of a survey config.
	 * 
	 * @author lam
	 * 
	 */
	public static class CreatorHistory {

		private final String key;
		private final ArrayList<HistoryEntry> history = new ArrayList<>();
		private Registration componentListenerRegistration;

		public CreatorHistory(String key) {
			super();
			this.key = key;
		}

		public String getKey() {
			return key;
		}

		public ArrayList<HistoryEntry> getHistory() {
			return history;
		}

		public void addHistory(String json) {
			this.history.add(new HistoryEntry(json));
			if (history.size() > 20) {
				history.remove(0);
			}
		}

		public Registration getComponentListenerRegistration() {
			return componentListenerRegistration;
		}

		public void setComponentListenerRegistration(Registration componentListenerRegistration) {
			this.componentListenerRegistration = componentListenerRegistration;
		}

	}

	/**
	 * contains a timestamp and the survey config
	 * 
	 * @author lam
	 *
	 */
	public static class HistoryEntry implements Serializable {
		private static final long serialVersionUID = 1273544560332835040L;

		private final LocalDateTime creation;
		private final String json;

		public HistoryEntry(String jsonValue) {
			super();
			this.creation = LocalDateTime.now();
			this.json = jsonValue;
		}

		public LocalDateTime getCreation() {
			return creation;
		}

		public String getJson() {
			return json;
		}

	}

}
