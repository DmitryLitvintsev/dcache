/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v3.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class nfs_fh3 implements XdrAble {
    public byte [] data;

    public nfs_fh3() {
    }

    public nfs_fh3(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeDynamicOpaque(data);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        data = xdr.xdrDecodeDynamicOpaque();
    }

}
// End of nfs_fh3.java
