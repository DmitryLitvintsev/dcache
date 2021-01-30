package org.dcache.xrootd.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.dcache.pool.movers.NettyTransferService;
import org.dcache.vehicles.XrootdProtocolInfo;
import org.dcache.xrootd.protocol.messages.OkResponse;
import org.dcache.xrootd.protocol.messages.SyncRequest;
import org.dcache.xrootd.protocol.messages.XrootdResponse;
import org.dcache.xrootd.util.ByteBuffersProvider;

/**
 * Encapsulates an open file for reading in the xrootd data server.
 */
public class ReadDescriptor implements FileDescriptor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadDescriptor.class);

    /**
     * Update mover meta-information
     */
    protected            NettyTransferService<XrootdProtocolInfo>.NettyMoverChannel _channel;

    public ReadDescriptor(NettyTransferService<XrootdProtocolInfo>.NettyMoverChannel channel)
    {
        _channel = channel;
    }

    @Override
    public void read(ByteBuffer buffer, long position) throws IOException
    {
        try {
            while (buffer.hasRemaining()) {
                /* use position independent thread safe call */
                int bytes = _channel.read(buffer, position);
                if (bytes < 0) {
                    break;
                }
                position += bytes;
            }
        } catch (RuntimeException | IOException e) {
            Throwable t = e.getCause();
            LOGGER.error("Intercepting error thrown during buffer read: {}; cause {}.",
                e.getMessage(), t == null ? "cause not reported" : t.getMessage());
            throw e;
        }
    }

    @Override
    public XrootdResponse<SyncRequest> sync(SyncRequest msg) throws IOException,
                    InterruptedException
    {
        /* As this is a read only file, there is no reason to sync
         * anything.
         */
        return new OkResponse<>(msg);
    }

    @Override
    public void write(ByteBuffersProvider msg)
        throws IOException
    {
        throw new IOException("File is read only");
    }

    @Override
    public NettyTransferService<XrootdProtocolInfo>.NettyMoverChannel getChannel()
    {
        return _channel;
    }

    @Override
    public boolean isPersistOnSuccessfulClose()
    {
        return false;
    }
}

