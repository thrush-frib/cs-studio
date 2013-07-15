/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.simplepv.pvmanager;

import org.csstudio.simplepv.test.BasicReadTester;
import org.csstudio.simplepv.test.BasicReadWriteTester;
import org.csstudio.simplepv.test.BufferingReadTester;
import org.csstudio.simplepv.test.PVPerformanceTester;
import org.csstudio.simplepv.test.PVPerformanceTester.PVNameProvider;
import org.junit.Before;
import org.junit.Test;

/**
 * Test simple PV Read functionalities. It should run as plugin test.
 * 
 * @author Xihui Chen
 * 
 */
public class PVMangerPVTest {

private static final String PVMANAGER = "pvmanager";

	@Before
	public void setup() {
		// A workaround for problem with AWT code inside PVManager on Mac OS X.
		System.setProperty("java.awt.headless", "true");
		PVManagerPV.setDebug(true);
	}
	
	@Test
	public void testSimpleRead() throws Exception {
		BasicReadTester tester = 
				new BasicReadTester(PVMANAGER, "sim://ramp(0,100,1,0.1)");
		tester.testAll();
		
	}
	
	@Test
	public void testBufferingRead() throws Exception {
		BufferingReadTester tester = 
				new BufferingReadTester(PVMANAGER, "sim://ramp(0,80,1,0.1)");
		tester.testAll();		
	}

	
	@Test
	public void testReadWrite() throws Exception {
		BasicReadWriteTester tester = 
				new BasicReadWriteTester(PVMANAGER, "loc://test(0)");
		tester.testAll();		
	}
	
	@Test
	public void testPerformance() throws Exception{
		PVPerformanceTester tester = new PVPerformanceTester(PVMANAGER, 1000, new PVNameProvider() {

			@Override
			public String getPVName(int index) {
				return "sim://ramp(0," + index + "1, 0.1)";
			}
		});
		tester.testAll();
	}	
	
}
