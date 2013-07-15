/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.simplepv.utilitypv;


import static org.junit.Assert.assertEquals;

import org.csstudio.simplepv.test.BasicReadTester;
import org.csstudio.simplepv.test.BasicReadWriteTester;
import org.csstudio.simplepv.test.PVPerformanceTester;
import org.csstudio.simplepv.test.PVPerformanceTester.PVNameProvider;
import org.junit.Test;

/**
 * Test UtlityPV implementation.
 * @author Xihui Chen
 * 
 */
public class UtilityPVTest {

	private static final String UTILITY_PV = "utility_pv";


	@Test
	public void testSimpleRead() throws Exception {
		BasicReadTester tester = 
				new BasicReadTester(UTILITY_PV, "sim://ramp(0,100,1,0.1)");
		tester.testAll();
		
	}	
	
	
	@Test
	public void testReadWrite() throws Exception {
		BasicReadWriteTester tester = 
				new BasicReadWriteTester(UTILITY_PV, "loc://test(0)");
		tester.testAll();		
	}
	
	@Test
	public void testConvertPMPVToUtilityPVName(){
		assertEquals("123.45678",
				UtilityPV.convertPMPVToUtilityPVName("=123.45678"));
		
		assertEquals("\"abcd\"",
				UtilityPV.convertPMPVToUtilityPVName("=\"abcd\""));
		
		assertEquals("const://array(1, 23, 34,12.345)",
				UtilityPV.convertPMPVToUtilityPVName("sim://const(1, 23, 34,12.345)"));	
		
		
		//regular pv
		assertEquals("fred:current",
				UtilityPV.convertPMPVToUtilityPVName("fred:current"));	
				
	}
	
	@Test
	public void testPerformance() throws Exception{
		PVPerformanceTester tester = new PVPerformanceTester(UTILITY_PV, 1000, new PVNameProvider() {

			@Override
			public String getPVName(int index) {
				return "sim://ramp(0," + index + "1, 0.1)";
			}
		});		tester.testAll();
	}
}
