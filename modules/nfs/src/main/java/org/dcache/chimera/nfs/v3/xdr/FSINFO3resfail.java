/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v3.xdr;
import org.dcache.chimera.nfs.v3.*;
import org.dcache.xdr.*;
import java.io.IOException;

public class FSINFO3resfail implements XdrAble {
    public post_op_attr obj_attributes;

    public FSINFO3resfail() {
    }

    public FSINFO3resfail(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        obj_attributes.xdrEncode(xdr);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        obj_attributes = new post_op_attr(xdr);
    }

}
// End of FSINFO3resfail.java
