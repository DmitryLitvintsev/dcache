/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class nfsv4_1_file_layout_ds_addr4 implements XdrAble {
    public uint32_t [] nflda_stripe_indices;
    public multipath_list4 [] nflda_multipath_ds_list;

    public nfsv4_1_file_layout_ds_addr4() {
    }

    public nfsv4_1_file_layout_ds_addr4(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        { int $size = nflda_stripe_indices.length; xdr.xdrEncodeInt($size); for ( int $idx = 0; $idx < $size; ++$idx ) { nflda_stripe_indices[$idx].xdrEncode(xdr); } }
        { int $size = nflda_multipath_ds_list.length; xdr.xdrEncodeInt($size); for ( int $idx = 0; $idx < $size; ++$idx ) { nflda_multipath_ds_list[$idx].xdrEncode(xdr); } }
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        { int $size = xdr.xdrDecodeInt(); nflda_stripe_indices = new uint32_t[$size]; for ( int $idx = 0; $idx < $size; ++$idx ) { nflda_stripe_indices[$idx] = new uint32_t(xdr); } }
        { int $size = xdr.xdrDecodeInt(); nflda_multipath_ds_list = new multipath_list4[$size]; for ( int $idx = 0; $idx < $size; ++$idx ) { nflda_multipath_ds_list[$idx] = new multipath_list4(xdr); } }
    }

}
// End of nfsv4_1_file_layout_ds_addr4.java
