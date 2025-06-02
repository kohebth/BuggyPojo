package me.kohebth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.lang.reflect.Field;

public class Main {
	@Value
	@AllArgsConstructor(onConstructor = @__(@JsonCreator))
	public static class BuggyPojo {
		@JsonProperty("id")
		Long id = -1L;
		@JsonProperty("value")
		String value = "";
	}

	/**
	 * Explain:
	 * <p>
	 *  When Lombok start to generate code, it treated the default value (e.g.: a String "") as a constant value,
	 *  thus it put the value in every place of usage, result into a consistent but compile-time-fixed output.
	 * </p>
	 * <p>
	 *  On the other hand, ObjectMapper uses Java reflection to assign values at runtime, directly to the fields.
	 * </p>
	 * <p>
	 *  Because Lombok and Jackson don't acknowledge each other's behavior, Lombok doesn't generate getter methods
	 *  that actually read from the runtime-assigned fields. Instead, the generated getters return the compile-time
	 *  constant value. This results in unexpected behavior — the value looks correct when debugging the field, but
	 *  the getter still returns the default.
	 * </p>
	 */
	public static void main(String[] args) {
		ObjectMapper objMap = new ObjectMapper();
		String objJson = "{\"id\":\"100\", \"value\":\"Dummy String\"}";

		try {
			BuggyPojo pojo = objMap.readValue(objJson, BuggyPojo.class);
			System.out.println("GetId() = " + pojo.getId()); // 100
			System.out.println("GetValue() = " + pojo.getValue()); // <empty>

			System.out.println("pojo.id = " + pojo.id); // 100
			System.out.println("pojo.value = " + pojo.value); // <empty>

			System.out.println("pojo.id = " + BuggyPojo.class.getDeclaredField("id")); // 100
			System.out.println("pojo.value = " + BuggyPojo.class.getDeclaredField("value").get(pojo)); // Dummy String
		} catch (JsonProcessingException | NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}