package org.bool.rhine.zookeeper;

import org.junit.Before;
import org.junit.Test;

public class ZookeeperTest {
	@Before
	public void init() {
		
	}
	
	@Test
	public void connect() {
		ZKTools.connect();
	}
	
}
