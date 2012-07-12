/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v3.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class fhandle2 implements XdrAble {

    public byte [] value;

    public fhandle2() {
    }

    public fhandle2(byte [] value) {
        this.value = value;
    }

    public fhandle2(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeOpaque(value, mount_prot.FHSIZE2);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        value = xdr.xdrDecodeOpaque(mount_prot.FHSIZE2);
    }

}
// End of fhandle2.java
