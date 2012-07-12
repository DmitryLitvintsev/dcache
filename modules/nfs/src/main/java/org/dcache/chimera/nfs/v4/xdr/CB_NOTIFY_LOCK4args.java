/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.chimera.nfs.v4.*;
import org.dcache.xdr.*;
import java.io.IOException;

public class CB_NOTIFY_LOCK4args implements XdrAble {
    public nfs_fh4 cnla_fh;
    public lock_owner4 cnla_lock_owner;

    public CB_NOTIFY_LOCK4args() {
    }

    public CB_NOTIFY_LOCK4args(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        cnla_fh.xdrEncode(xdr);
        cnla_lock_owner.xdrEncode(xdr);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        cnla_fh = new nfs_fh4(xdr);
        cnla_lock_owner = new lock_owner4(xdr);
    }

}
// End of CB_NOTIFY_LOCK4args.java
