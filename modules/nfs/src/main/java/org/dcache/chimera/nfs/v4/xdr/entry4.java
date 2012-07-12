/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class entry4 implements XdrAble {
    public nfs_cookie4 cookie;
    public component4 name;
    public fattr4 attrs;
    public entry4 nextentry;

    public entry4() {
    }

    public entry4(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        entry4 $this = this;
        do {
            $this.cookie.xdrEncode(xdr);
            $this.name.xdrEncode(xdr);
            $this.attrs.xdrEncode(xdr);
            $this = $this.nextentry;
            xdr.xdrEncodeBoolean($this != null);
        } while ( $this != null );
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        entry4 $this = this;
        entry4 $next;
        do {
            $this.cookie = new nfs_cookie4(xdr);
            $this.name = new component4(xdr);
            $this.attrs = new fattr4(xdr);
            $next = xdr.xdrDecodeBoolean() ? new entry4() : null;
            $this.nextentry = $next;
            $this = $next;
        } while ( $this != null );
    }

}
// End of entry4.java
