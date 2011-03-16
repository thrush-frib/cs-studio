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
package org.csstudio.archive.common.engine;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nonnull;

import org.csstudio.platform.AbstractPreference;
import org.csstudio.platform.logging.CentralLogger;


/**
 * Access to engine related RDB archive preferences.
 *
 * @author bknerr
 * @since 16.11.2010
 * @param <T> the type of the preference
 */
public class ArchiveEnginePreference<T> extends AbstractPreference<T>{

    private static URL DEFAULT_RESCUE_URL;
    static {
        try {
            DEFAULT_RESCUE_URL = new URL("file://./engineRescue");
        } catch (final MalformedURLException e) {
            CentralLogger.getInstance().error(ArchiveEnginePreference.class,
                                              "Default URL preference for rescue dir not well formed.");
        }
    }

    public static final ArchiveEnginePreference<Long> WRITE_PERIOD =
        new ArchiveEnginePreference<Long>("writePeriodInS", Long.valueOf(7));
    public static final ArchiveEnginePreference<URL> DATA_RESCUE_DIR =
        new ArchiveEnginePreference<URL>("dataRescueDir", DEFAULT_RESCUE_URL);

    /**
     * Constructor.
     * @param keyAsString
     * @param defaultValue
     */
    protected ArchiveEnginePreference(@Nonnull final String keyAsString,
                                      @Nonnull final T defaultValue) {
        super(keyAsString, defaultValue);
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    protected Class<? extends AbstractPreference<T>> getClassType() {
        return (Class<? extends AbstractPreference<T>>) ArchiveEnginePreference.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected String getPluginID() {
        return Activator.PLUGIN_ID;
    }
}