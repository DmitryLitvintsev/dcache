/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class fs_locations_info4 implements XdrAble {
    public uint32_t fli_flags;
    public int32_t fli_valid_for;
    public pathname4 fli_fs_root;
    public fs_locations_item4 [] fli_items;

    public fs_locations_info4() {
    }

    public fs_locations_info4(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        fli_flags.xdrEncode(xdr);
        fli_valid_for.xdrEncode(xdr);
        fli_fs_root.xdrEncode(xdr);
        { int $size = fli_items.length; xdr.xdrEncodeInt($size); for ( int $idx = 0; $idx < $size; ++$idx ) { fli_items[$idx].xdrEncode(xdr); } }
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        fli_flags = new uint32_t(xdr);
        fli_valid_for = new int32_t(xdr);
        fli_fs_root = new pathname4(xdr);
        { int $size = xdr.xdrDecodeInt(); fli_items = new fs_locations_item4[$size]; for ( int $idx = 0; $idx < $size; ++$idx ) { fli_items[$idx] = new fs_locations_item4(xdr); } }
    }

}
// End of fs_locations_info4.java
