package dmg.cells.applets.spy ;

import dmg.util.* ;
 
public class SpyCommandRequest implements CommandRequestable {
   private String    _command ;
   private Object [] _params ;
   public SpyCommandRequest( String command , Object key , Object value ){
       _command   = command ;
       _params    = new Object[2] ;
       _params[0] = key ;
       _params[1] = value ;
   }
   @Override
   public String getRequestCommand(){ return _command ; }
   @Override
   public int    getArgc(){ return 2 ; }
   @Override
   public Object getArgv(int position){ return _params[position] ; }

}
