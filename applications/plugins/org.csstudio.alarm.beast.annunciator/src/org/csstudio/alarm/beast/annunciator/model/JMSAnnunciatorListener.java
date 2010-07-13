/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.model;

/** Listener to Annunciator events
 *  @author Kay Kasemir
 */
public interface JMSAnnunciatorListener
{
    /** @param annunciation Message that was just annunciated */
    public void performedAnnunciation(AnnunciationMessage annunciation);
    
    /** @param ex Error detail */
    public void annunciatorError(Exception ex);
}
