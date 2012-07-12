/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class SEQUENCE4args implements XdrAble {
    public sessionid4 sa_sessionid;
    public sequenceid4 sa_sequenceid;
    public slotid4 sa_slotid;
    public slotid4 sa_highest_slotid;
    public boolean sa_cachethis;

    public SEQUENCE4args() {
    }

    public SEQUENCE4args(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        sa_sessionid.xdrEncode(xdr);
        sa_sequenceid.xdrEncode(xdr);
        sa_slotid.xdrEncode(xdr);
        sa_highest_slotid.xdrEncode(xdr);
        xdr.xdrEncodeBoolean(sa_cachethis);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        sa_sessionid = new sessionid4(xdr);
        sa_sequenceid = new sequenceid4(xdr);
        sa_slotid = new slotid4(xdr);
        sa_highest_slotid = new slotid4(xdr);
        sa_cachethis = xdr.xdrDecodeBoolean();
    }

}
// End of SEQUENCE4args.java
