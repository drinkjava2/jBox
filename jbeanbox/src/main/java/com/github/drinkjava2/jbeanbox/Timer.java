/**
 * Copyright (C) 2016 Yong Zhu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.drinkjava2.jbeanbox;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is a debug tool to to calculate method execute time, use
 * Timer.set("SomeTimeMark") to set a time mark, use TimeCountUtils.print() to
 * print summary time be used between each time mark
 * 
 * @author Yong Zhu
 *
 */
public class Timer {//NOSONAR
	private static String lastMark = "start";
	private static long lastTime = System.nanoTime();
	private static final Map<String, Long> timeMap = new LinkedHashMap<String, Long>();
	private static final Map<String, Long> timeHappenCount = new LinkedHashMap<String, Long>();

	public static void set(int mark) {
		set("" + mark);
	};

	public static void set(String mark) {
		long thisTime = System.nanoTime();
		String key = lastMark + "->" + mark;
		Long lastSummary = timeMap.get(key);
		if (lastSummary == null)
			lastSummary = 0l;

		timeMap.put(key, System.nanoTime() - lastTime + lastSummary);
		Long lastCount = timeHappenCount.get(key);
		if (lastCount == null)
			lastCount = 0L;

		timeHappenCount.put(key, ++lastCount);
		lastTime = thisTime;
		lastMark = mark;
	};

	public static void print() {
		for (Entry<String, Long> entry : timeMap.entrySet()) {
			System.out.println(// NOSONAR
					String.format("%20s, Total times:%20s,  Repeat times:%20s, Avg times:%20s ", entry.getKey(),
							entry.getValue(), timeHappenCount.get(entry.getKey()),

							entry.getValue() / timeHappenCount.get(entry.getKey())

					));
		}
	}

}
