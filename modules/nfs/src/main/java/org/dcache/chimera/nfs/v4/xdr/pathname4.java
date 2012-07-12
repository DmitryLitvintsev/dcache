/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class pathname4 implements XdrAble {

    public component4 [] value;

    public pathname4() {
    }

    public pathname4(component4 [] value) {
        this.value = value;
    }

    public pathname4(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        { int $size = value.length; xdr.xdrEncodeInt($size); for ( int $idx = 0; $idx < $size; ++$idx ) { value[$idx].xdrEncode(xdr); } }
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        { int $size = xdr.xdrDecodeInt(); value = new component4[$size]; for ( int $idx = 0; $idx < $size; ++$idx ) { value[$idx] = new component4(xdr); } }
    }

}
// End of pathname4.java
