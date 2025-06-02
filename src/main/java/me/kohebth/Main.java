package me.kohebth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Value;

public class Main {
	@Value
	@AllArgsConstructor(onConstructor = @__(@JsonCreator))
	public static class BuggyPojo {
		@JsonProperty("id")
		Long id = -1L;
		@JsonProperty("value")
		String value = "";
	}

	public static void main(String[] args) {
		ObjectMapper objMap = new ObjectMapper();
		String objJson = "{\"id\":\"100\", \"value\":\"Dummy String\"}";

		try {
			BuggyPojo pojo = objMap.readValue(objJson, BuggyPojo.class);
			System.out.println("GetId() = " + pojo.getId());
			System.out.println("GetValue() = " + pojo.getValue());

			System.out.println("pojo.id = " + pojo.id);
			System.out.println("pojo.value = " + pojo.value);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}