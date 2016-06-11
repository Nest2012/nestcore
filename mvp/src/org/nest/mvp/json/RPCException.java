
package org.nest.mvp.json;


public class RPCException extends Exception
{
   
   /**
     * 
     */
    private static final long serialVersionUID = -1478516811523578039L;


    public RPCException(String msg){
        super(msg);
    }


    public RPCException(Exception e) {
        super(e);
    }

    public RPCException(String msg,Throwable cause){
        super(msg,cause);
    }
}
