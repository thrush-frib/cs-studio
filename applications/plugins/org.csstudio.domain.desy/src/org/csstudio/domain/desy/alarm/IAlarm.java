/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.domain.desy.alarm;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.ISystemVariable;

/**
 * Interface of an alarm.
 *
 * An alarm is defined as describing characteristic of a identifiable system variable (in form of an
 * distinct value or or state) that yields information about the feature's relevance
 * ({@link IAlarmSeverity} according to a given rule set and/or value range set.
 * An alarm serves as the ultimate cause to propagate (alarm) notifications throughout the system.
 *
 * Sidenote: Whether a control system considers an OK or UNKNOWN state as alarm or not, is up to the
 * implementation.
 *
 * TODO (bknerr, jhatje, jpenning, hrickens) : The 'identifiable system feature' may be
 * {@link IProcessVariable} or {@link IControlSystemItem} or any of the many others. I introduced
 * {@link ISystemVariable} for now, was not sure which one if any to take.
 *
 * @author bknerr
 * @param <T>
 * @since 04.11.2010
 */
public interface IAlarm {

    /**
     * Return the alarm's severity
     * @return the severity
     */
    @Nonnull
    <T extends IAlarmSeverity<T>>
        IAlarmSeverity<T> getSeverity();
}