/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.chimera.nfs.v4.*;
import org.dcache.xdr.*;
import java.io.IOException;

public class LOCK4args implements XdrAble {
    public int locktype;
    public boolean reclaim;
    public offset4 offset;
    public length4 length;
    public locker4 locker;

    public LOCK4args() {
    }

    public LOCK4args(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(locktype);
        xdr.xdrEncodeBoolean(reclaim);
        offset.xdrEncode(xdr);
        length.xdrEncode(xdr);
        locker.xdrEncode(xdr);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        locktype = xdr.xdrDecodeInt();
        reclaim = xdr.xdrDecodeBoolean();
        offset = new offset4(xdr);
        length = new length4(xdr);
        locker = new locker4(xdr);
    }

}
// End of LOCK4args.java
