/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class RECLAIM_COMPLETE4args implements XdrAble {
    public boolean rca_one_fs;

    public RECLAIM_COMPLETE4args() {
    }

    public RECLAIM_COMPLETE4args(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeBoolean(rca_one_fs);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        rca_one_fs = xdr.xdrDecodeBoolean();
    }

}
// End of RECLAIM_COMPLETE4args.java
