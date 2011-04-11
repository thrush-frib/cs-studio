/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

/** Base for archived channels.
 *
 *  @author Kay Kasemir
 *  @author Bastian Knerr
 *  @param <V> the basic value type
 *  @param <T> the system variable for the basic value type
 */
@SuppressWarnings("nls")
public class ArchiveChannel<V, T extends ISystemVariable<V>> {

    private static final Logger LOG = CentralLogger.getInstance().getLogger(PVListener.class);

    /** Channel name.
     *  This is the name by which the channel was created,
     *  not the PV name that might include decorations.
     */
    private final String _name;

    private final ArchiveChannelId _id;

    /** Control system PV */
    private final PV _pv;


    /** Buffer of received samples, periodically written */
    final SampleBuffer<V, T, IArchiveSample<V, T>> _buffer;


    /** Is this channel currently running?
     *  <p>
     *  PV sends another 'disconnected' event
     *  as the result of 'stop', but we don't
     *  want to log that, so we keep track of
     *  the 'running' state.
     */
    @GuardedBy("this")
    volatile boolean _isStarted = false;

    /** Most recent value of the PV.
     *  <p>
     *  This is the value received from the PV,
     *  is is not necessarily written to the archive.
     *  <p>
     */
    @GuardedBy("this") T _mostRecentSysVar;

    /**
     * The most recent value send to the archive.
     */
    @GuardedBy("this")
    protected T _lastArchivedSample;

    /** Counter for received values (monitor updates) */
    long _receivedSampleCount = 0;

    //volatile boolean _connected = false;


    private IServiceProvider _provider = new IServiceProvider() {
        @Override
        @Nonnull
        public IArchiveEngineFacade getEngineFacade() throws OsgiServiceUnavailableException {
            throw new OsgiServiceUnavailableException("This is a stub. The service provider for this channel has not been set to a real implementation.");
        }
    };

    private final DesyArchivePVListener<V, T> _listener;

    /**
     * Constructor
     * @throws EngineModelException on failure while creating PV
     */
    public ArchiveChannel(@Nonnull final String name,
                          @Nonnull final ArchiveChannelId id,
                          @Nullable final Class<V> clazz) throws EngineModelException {
        _name = name;
        _id = id;
        _buffer = new SampleBuffer<V, T, IArchiveSample<V, T>>(name);

        try {
            _pv = PVFactory.createPV(name);
        } catch (final Exception e) {
            throw new EngineModelException("Creation of pv failed for channel " + name, e);
        }

        _listener = new DesyArchivePVListener<V, T>(_provider, name, id, clazz) {
                        @Override
                        protected void addSampleToBuffer(@Nonnull final IArchiveSample<V, T> sample) {
                            synchronized (this) {
                                ++_receivedSampleCount;
                                _mostRecentSysVar = sample.getSystemVariable();
                            }
                            _buffer.add(sample);
                        }
                    };
        _pv.addListener(_listener);
    }

    /** @return Name of channel */
    @Nonnull
    public String getName() {
        return _name;
    }

    /** @return Short description of sample mechanism */
    @Nonnull
    public String getMechanism() {
        return "MONITOR (on change)";
    }

    /** @return <code>true</code> if connected */
    public boolean isConnected() {
        return _pv.isConnected();
    }

    /** @return Human-readable info on internal state of PV */
    @CheckForNull
    public String getInternalState() {
        return _pv.getStateInfo();
    }

    /**
     * Start archiving this channel.
     * @throws EngineModelException
     */
    public void start(@Nonnull final String info) throws EngineModelException {
        try {
            if (_isStarted) {
                return;
            }
            _listener.setStartInfo(info);
            synchronized (this) {
                _pv.start();
                _isStarted = true;
            }
        } catch (final Exception e) {
            LOG.error("PV " + _pv.getName() + " could not be started with state info " + _pv.getStateInfo(), e);
            throw new EngineModelException("Something went wrong within Kasemir's PV stuff on channel/PV startup", e);
        }

    }


    /**
     * Stop archiving this channel
     */
    public void stop(@Nonnull final String info) {
    	if (!_isStarted) {
            return;
        }
    	_listener.setStopInfo(info);
    	synchronized (this) {
    	    _isStarted = false;
    	}
        _pv.stop();
    }

    /** @return Most recent value of the channel's PV */
    @Nonnull
    public synchronized String getCurrentValueAsString() {
        if (_mostRecentSysVar == null) {
            return "null"; //$NON-NLS-1$
        }
        return _mostRecentSysVar.getData().getValueData().toString();
    }

    @Nonnull
    public T getMostRecentValue() {
        return _mostRecentSysVar;
    }

    /** @return Count of received values */
    public synchronized long getReceivedValues() {
        return _receivedSampleCount;
    }

    /** @return Last value written to archive */
    @Nonnull
    public final String getLastArchivedValue() {
        synchronized (this) {
            if (_lastArchivedSample == null) {
                return "null"; //$NON-NLS-1$
            }
            return _lastArchivedSample.getData().getValueData().toString();
        }
    }

    /** @return Sample buffer */
    @Nonnull
    public final SampleBuffer<V, T, IArchiveSample<V, T>> getSampleBuffer() {
        return _buffer;
    }


    /** Reset counters */
    public void reset() {
        _buffer.statsReset();
        synchronized (this) {
            _receivedSampleCount = 0;
        }
    }

    @Override
    @Nonnull
    public String toString() {
        return "Channel " + getName() + ", " + getMechanism();
    }

    @Deprecated
    public boolean isEnabled() {
        return true;
    }

    public void setServiceProvider(@Nonnull final IServiceProvider provider) {
        _provider = provider;
        _listener.setProvider(provider);
    }
}