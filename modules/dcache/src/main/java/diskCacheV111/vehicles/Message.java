// $Id: Message.java,v 1.5 2004-11-05 12:07:19 tigran Exp $

package diskCacheV111.vehicles;

import dmg.cells.nucleus.HasDiagnosticContext;
import org.dcache.util.ReflectionUtils;
import diskCacheV111.util.PnfsId;
import javax.security.auth.Subject;
import org.dcache.auth.Subjects;

// Base class for all Messages

public class Message
    implements java.io.Serializable,
               HasDiagnosticContext
{
    private boolean _replyRequired = false;
    private boolean _isReply       = false;
    private int     _returnCode    = 0;
    private Object  _errorObject   = null;
    private long    _id            = 0 ;
    private Subject _subject;

    private static final long serialVersionUID = 2056896713066252504L;

    public Message(){
    }

    public Message(boolean replyRequired){
	_replyRequired = replyRequired;
    }
    @Override
    public String toString(){
        return _returnCode==0?"":"("+_returnCode+")="+_errorObject ;
    }
    public void setSucceeded(){
	setReply(0,null);
    }

    public void setFailed(int errorCode, Object errorObject){
	setReply(errorCode, errorObject);
    }
    public void setReply(){
        _isReply = true ;
    }
    public void setReply(int returnCode, Object errorObject){
	_isReply     = true;
	_returnCode  = returnCode;
	_errorObject = errorObject;
    }

    public boolean isReply(){
	return _isReply;
    }

    public void clearReply(){
	//allows us to reuse message objects
	_isReply     = false;
	_returnCode  = 0;
	_errorObject = null;
    }

    public int getReturnCode(){
	return _returnCode;
    }

    public Object getErrorObject(){
	return _errorObject;
    }

    public boolean getReplyRequired(){
	return _replyRequired;
    }

    public void setReplyRequired(boolean replyRequired){
	_replyRequired = replyRequired;
    }
    public void setId( long id ){ _id = id ; }
    public long getId(){ return _id ; }

    public void setSubject(Subject subject)
    {
        _subject = subject;
    }

    public Subject getSubject()
    {
        return (_subject == null) ? Subjects.ROOT : _subject;
    }

    /**
     * Returns a human readable name of the message class. By default
     * this is the short class name with the "Message" or "Msg" suffix
     * removed.
     */
    public String getMessageName()
    {
        String name = getClass().getSimpleName();
        int length = name.length();
        if ((length > 7) && name.endsWith("Message")) {
            name = name.substring(0, name.length() - 7);
        } else if ((length > 3) && name.endsWith("Msg")) {
            name = name.substring(0, name.length() - 3);
        }

        return name;
    }

    @Override
    public String getDiagnosticContext()
    {
        String name = getMessageName();
        PnfsId id = ReflectionUtils.getPnfsId(this);
        return (id == null) ? name : (name + " " + id);
    }

    /**
     * Returns true if this message could possibly change the effect
     * or result of <code>message</code>.
     *
     * In a message queue, a message can be used to fold other
     * messages as long as it is not invalidated by any intermediate
     * messages.
     */
    public boolean invalidates(Message message)
    {
        return true;
    }

    /**
     * Folds the reply of another Messages into this Message.
     *
     * For some Messages the correct reply can be derived from the
     * reply of another Message. In those cases processing of this
     * Message can be skipped and instead the reply of the other
     * Message can be folded into this Message.
     *
     * A prerequiste for folding to succeed is that this Message is
     * side effect free. It does however not matter whether the other
     * Message has side effects.
     *
     * This method updates this Message by extracting the correct
     * reply from the Message given as an argument. If successfull,
     * this Message can be send as a valid reply back to the
     * requestor. If not successful, this Message is unmodified.
     *
     * @param message Another Message to fold into this Message
     * @return true if the operation succeeded, false otherwise
     * @see invalidates
     */
    public boolean fold(Message message)
    {
        return false;
    }
}

