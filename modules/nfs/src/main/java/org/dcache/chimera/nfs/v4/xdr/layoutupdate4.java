/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class layoutupdate4 implements XdrAble {
    public int lou_type;
    public byte [] lou_body;

    public layoutupdate4() {
    }

    public layoutupdate4(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(lou_type);
        xdr.xdrEncodeDynamicOpaque(lou_body);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        lou_type = xdr.xdrDecodeInt();
        lou_body = xdr.xdrDecodeDynamicOpaque();
    }

}
// End of layoutupdate4.java
