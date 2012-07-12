/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.chimera.nfs.v4.*;
import org.dcache.xdr.*;
import java.io.IOException;

public class CB_NOTIFY4args implements XdrAble {
    public stateid4 cna_stateid;
    public nfs_fh4 cna_fh;
    public notify4 [] cna_changes;

    public CB_NOTIFY4args() {
    }

    public CB_NOTIFY4args(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        cna_stateid.xdrEncode(xdr);
        cna_fh.xdrEncode(xdr);
        { int $size = cna_changes.length; xdr.xdrEncodeInt($size); for ( int $idx = 0; $idx < $size; ++$idx ) { cna_changes[$idx].xdrEncode(xdr); } }
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        cna_stateid = new stateid4(xdr);
        cna_fh = new nfs_fh4(xdr);
        { int $size = xdr.xdrDecodeInt(); cna_changes = new notify4[$size]; for ( int $idx = 0; $idx < $size; ++$idx ) { cna_changes[$idx] = new notify4(xdr); } }
    }

}
// End of CB_NOTIFY4args.java
