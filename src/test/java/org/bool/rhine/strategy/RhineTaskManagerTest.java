package org.bool.rhine.strategy;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

/**
 *
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineTaskManagerTest {
	@Test
	public void registJobTest() {
		RhineStrategy rs = new RhineStrategy();
		rs.setCreateTime("");
		rs.setTaskName("rhineTestJob");
		rs.setCrontab("0/5 * * * * ?");
		rs.setStrategyName("rhineTestJobStrategy");
		RhineStrategyManager.registStrategy(rs);
	}
}
