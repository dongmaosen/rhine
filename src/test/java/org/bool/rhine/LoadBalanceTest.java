package org.bool.rhine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 *
 *
 * Author: 不二   
 *
 * Copyright @ 2019
 * 
 */
public class LoadBalanceTest {
	@Test
	public void loadBalanceTest() {
		
		List<String> nodes = new ArrayList<String>();
		nodes.add("node1");
		nodes.add("node2");
		nodes.add("node3");
		String currentNode = "node3";
		
		int nodeCount = nodes.size();
		int nodeSequence = 0;
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).equals(currentNode)) {
				nodeSequence = i;
				break;
			}
			
		}
		
		Map<String, String> strategyMap = new HashMap<String, String>();
		strategyMap.put("strategy1", "v1");
		strategyMap.put("strategy2", "v2");
		strategyMap.put("strategy3", "v3");
		strategyMap.put("strategy4", "v4");
		strategyMap.put("strategy5", "v5");
		strategyMap.put("strategy6", "v6");
		strategyMap.put("strategy7", "v7");
		strategyMap.put("strategy8", "v8");
		
		Iterator<String> keys = strategyMap.keySet().iterator();
		int i = 0;
		while (keys.hasNext()) {
			if (i++ % nodeCount == nodeSequence) {
				System.out.println("YES : " + keys.next());				
			} else {
				System.out.println("NO  : " + keys.next());
			}
		}
	}
}
