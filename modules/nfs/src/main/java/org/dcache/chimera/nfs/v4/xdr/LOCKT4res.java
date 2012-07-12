/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;

import org.dcache.chimera.nfs.nfsstat;
import org.dcache.chimera.nfs.v4.xdr.LOCK4denied;
import org.dcache.xdr.*;
import java.io.IOException;

public class LOCKT4res implements XdrAble {
    public int status;
    public LOCK4denied denied;

    public LOCKT4res() {
    }

    public LOCKT4res(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(status);
        switch ( status ) {
        case nfsstat.NFSERR_DENIED:
            denied.xdrEncode(xdr);
            break;
        case nfsstat.NFS_OK:
            break;
        default:
            break;
        }
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        status = xdr.xdrDecodeInt();
        switch ( status ) {
        case nfsstat.NFSERR_DENIED:
            denied = new LOCK4denied(xdr);
            break;
        case nfsstat.NFS_OK:
            break;
        default:
            break;
        }
    }

}
// End of LOCKT4res.java
