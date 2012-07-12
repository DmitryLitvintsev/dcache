/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v3.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class devicedata3 implements XdrAble {
    public sattr3 dev_attributes;
    public specdata3 spec;

    public devicedata3() {
    }

    public devicedata3(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        dev_attributes.xdrEncode(xdr);
        spec.xdrEncode(xdr);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        dev_attributes = new sattr3(xdr);
        spec = new specdata3(xdr);
    }

}
// End of devicedata3.java
