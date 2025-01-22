package com.sk.skala.quizapi.tools;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapTool {
	@SuppressWarnings("unchecked")
	public static void copyProperties(Map<String, Object> source, Map<String, Object> target) {
		if (source == null || target == null) {
			log.error("SagaTool.copyProperties: source or target is null");
			return;
		}

		for (Map.Entry<String, Object> entry : source.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			if (value == null) {
				target.put(key, null);
			} else if (value.getClass().isArray()) {
				target.put(key, copyArray(value));
			} else if (value instanceof Map) {
				Map<String, Object> nestedTargetMap = target.containsKey(key) && target.get(key) instanceof Map
						? (Map<String, Object>) target.get(key)
						: new HashMap<>();
				copyProperties((Map<String, Object>) value, nestedTargetMap);
				target.put(key, nestedTargetMap);
			} else {
				target.put(key, value);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static Object copyArray(Object sourceArray) {
		int length = Array.getLength(sourceArray);
		Class<?> componentType = sourceArray.getClass().getComponentType();
		Object copiedArray = Array.newInstance(componentType, length);

		for (int i = 0; i < length; i++) {
			Object element = Array.get(sourceArray, i);
			if (componentType.isArray()) {
				Array.set(copiedArray, i, copyArray(element));
			} else if (element instanceof Map) {
				Map<String, Object> nestedMap = new HashMap<>();
				copyProperties((Map<String, Object>) element, nestedMap);
				Array.set(copiedArray, i, nestedMap);
			} else {
				Array.set(copiedArray, i, element);
			}
		}

		return copiedArray;
	}

}
