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
	public void registJobTest() {
		RhineStrategy rs = new RhineStrategy();
		rs.setCreateTime("");
		rs.setTaskName("rhineTestJob");
		rs.setCrontab("0/3 * * * * ?");
		rs.setStrategyName("rhineTestJobStrategy_01");
		RhineStrategyManager.registStrategy(rs);
	}
}
