/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v3.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class dirlist3 implements XdrAble {
    public entry3 entries;
    public boolean eof;

    public dirlist3() {
    }

    public dirlist3(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        if ( entries != null ) { xdr.xdrEncodeBoolean(true); entries.xdrEncode(xdr); } else { xdr.xdrEncodeBoolean(false); }
        xdr.xdrEncodeBoolean(eof);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        entries = xdr.xdrDecodeBoolean() ? new entry3(xdr) : null;
        eof = xdr.xdrDecodeBoolean();
    }

}
// End of dirlist3.java
