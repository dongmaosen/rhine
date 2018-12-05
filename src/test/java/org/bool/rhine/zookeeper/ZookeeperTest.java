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
	
}
