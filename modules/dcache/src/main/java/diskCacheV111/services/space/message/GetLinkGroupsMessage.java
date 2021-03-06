//______________________________________________________________________________
//
// Intended usage: specify requested linkgroup id and get a set with one
//                 element back
//                 do not specify requested linkgroup id and get the set
//                 containing all linkgroups
//
//  $Id$
//  $Author$
//______________________________________________________________________________
package diskCacheV111.services.space.message;

import java.util.Collections;
import java.util.Set;

import diskCacheV111.services.space.LinkGroup;
import diskCacheV111.vehicles.Message;

public class GetLinkGroupsMessage extends Message {

	private static final long serialVersionUID = 2889995137324365133L;
	private Long linkGroupId;

	private Set<LinkGroup> list = Collections.emptySet();

	public void setLinkGroupidI(long id) {
		linkGroupId = id;
	}

	public void setLinkGroupId(Long id) {
		linkGroupId = id;
	}

	public Long getLinkgroupidId() {
		return linkGroupId;
	}


	public GetLinkGroupsMessage() {
		setReplyRequired(true);
	}

	public Set<LinkGroup> getLinkGroupSet() {
		return list;
	}

	public void setLinkGroupSet(Set<LinkGroup> lglist) {
		this.list=lglist;
	}

}
