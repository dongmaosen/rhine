package org.bool.rhine.zookeeper;

import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.junit.Before;
import org.junit.Test;

public class ZookeeperTest {
	@Before
	public void init() {
		
	}
	
	@Test
	public void connect() {
		ZKManager.connect();
	}
	
	@Test
	public void printTree() throws Exception {
		List<String> paths = ZKUtility.getPathTree("/");
		for (int i = 0; i < paths.size(); i++) {
			System.out.println(paths.get(i));
		}
	}
}
