package org.bool.rhine.strategy;

import org.junit.Test;

/**
 *
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineStrategyManagerTest {
	@Test
	public void registStrategyTest() {
		RhineStrategy rs = new RhineStrategy();
		rs.setCreateTime("");
		rs.setTaskName("rhineTestJob");
		rs.setCrontab("*/15 * * * * ? *");
		rs.setStrategyName("rhineTestJobStrategy_03");
		RhineStrategyManager.registStrategy(rs);
	}
//	@Test
	public void deleteStrategyTest() throws Exception {
		RhineStrategyManager.removeStrategy("rhineTestJobStrategy_01");
	}
}
