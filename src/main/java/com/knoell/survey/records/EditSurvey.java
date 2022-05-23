package com.knoell.survey.records;

import java.io.Serializable;

public record EditSurvey(String editId, String json) implements Serializable {

}
