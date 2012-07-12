/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class state_protect_ops4 implements XdrAble {
    public bitmap4 spo_must_enforce;
    public bitmap4 spo_must_allow;

    public state_protect_ops4() {
    }

    public state_protect_ops4(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        spo_must_enforce.xdrEncode(xdr);
        spo_must_allow.xdrEncode(xdr);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        spo_must_enforce = new bitmap4(xdr);
        spo_must_allow = new bitmap4(xdr);
    }

}
// End of state_protect_ops4.java
